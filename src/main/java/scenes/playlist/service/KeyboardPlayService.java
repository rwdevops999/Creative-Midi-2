package scenes.playlist.service;

import entity.midi.Midi;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.playlist.service.helper.LoadedSongResolver;
import util.SysexToHexStringConvertor;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.midi.MidiWriter;
import util.registry.AppEvent;
import util.registry.CommunicationMessage;
import util.registry.Registry;

import javax.sound.midi.*;

import static util.communication.CommunicationType.SELECT_SONG;
import static util.registry.AppEvent.AppEventType.MONITOR;

public class KeyboardPlayService extends Service<Void> {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardPlayService.class);

    private static final long FOREVER = Long.MAX_VALUE;

    /*
     * To use showMessage which transmits the message to the monitor
     */
    private final CommunicationMessage comm = CommunicationMessage.getInstance();
    private final LoadedSongResolver songResolver = LoadedSongResolver.getInstance();
    private final MidiWriter midiWriter = MidiWriter.getInstance();

    private MidiDevice midiOutputDevice;
    private MidiDevice midiInputDevice; // for writing to keyboard

    private boolean songIsPlaying = false;

    private void handleSysexMessage(SysexMessage message) {
        String hexMessage = SysexToHexStringConvertor.convertToHexString(message.getMessage());

        if (hexMessage.startsWith("F0 43 73 01 52 26")) {
            // It is a song name => use songResolver
            String songName = songResolver.resolveSongName(hexMessage);

            try {
                Midi midi = new Midi("Stop", 0, 0xFC);
                midiWriter.send(midi);
            } catch (InvalidMidiDataException | MidiUnavailableException e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }

            songIsPlaying = false;

            comm.showMessage(SELECT_SONG, songName);
        }

/*        if (! songIsPlaying) {

        } */
        /*
         * This message is send to monitor
         */
        comm.showMessage(CommunicationType.INBOUND, hexMessage);
    };

    private static final byte ACTIVE_SENSING = -2;

    private void handleMidiMessage(ShortMessage message) {
        byte[] rawBytes = message.getMessage();

        /*
         * I'm not interested in ACTIVE SENSING messages
         */
        if (rawBytes[0] != ACTIVE_SENSING) {
            int status = message.getStatus();
            if (status == ShortMessage.START) {
                comm.showMessage(CommunicationType.INBOUND, "Start");
                songIsPlaying = true;
            } else if (status == ShortMessage.STOP) {
                songIsPlaying = false;
                comm.showMessage(CommunicationType.INBOUND, "Stop");
            }
        }
    };

    /**
     * This is the entry point of the service (called when service.start() is executed
     *
     * @return nothing
     */
    @Override
    protected Task<Void> createTask() {
        System.out.println("CREATE KEYBOARD SERVICE");

        try {
            midiOutputDevice =  AppInfo.INSTANCE.getSelectedDevice().getMidiOutputDevice();
            if (midiOutputDevice == null) {
                return null;
            }

            if (! midiOutputDevice.isOpen()) {
                midiOutputDevice.open();
            }

            midiInputDevice =  AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();
            if (! midiInputDevice.isOpen()) {
                midiOutputDevice.open();
            }


            return new Task() {

                /**
                 * Entry point of the task itself. Here the thread starts executing
                 * @return
                 * @throws Exception
                 */
                @Override
                protected Object call() throws Exception {

                    /**
                     * We start up the receiver of messages from the keyboard
                     */
                    Receiver receiver = new Receiver() {
                        @Override
                        public void send(MidiMessage message, long timeStamp) {
                            if (message instanceof SysexMessage sysexMessage) {
                                handleSysexMessage(sysexMessage);
                                return;
                            }

                            if (message instanceof ShortMessage midiMessage) {
                                handleMidiMessage(midiMessage);
                                return;
                            }

                            // We don't treat META messages
                        }

                        /**
                         * Called when the receiver closes
                         */
                        @Override
                        public void close() {
                            System.out.println("CLOSE RECEIVER -> CLOSE MIDI INPUT & OUTPUT DEVICE");
                            if (midiOutputDevice.isOpen()) {
                                midiOutputDevice.close();
                            }
                            if (midiInputDevice.isOpen()) {
                                midiInputDevice.close();
                            }
                        }
                    };

                    Transmitter transmitter = midiOutputDevice.getTransmitter();
                    transmitter.setReceiver(receiver);

                    while (!isCancelled()) {
                        try {
                            Thread.sleep(FOREVER); // is about 26 million years
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                break;
                            }
                        }
                    }

                    // We get here after the break in the interrupt execution
                    // or when the thread awakens
                    receiver.close();

                    return null;
                }
            };
        } catch(MidiUnavailableException mue) {
            logger.error("Exception. CAUSE {}", mue.getMessage());
        }

/*        setOnSucceeded(event -> {
            System.out.println("KEYBOARD SERVICE SUCCESS");
        });

        setOnFailed(event -> {
            System.out.println("KEYBOARD SERVICE FAILED");
        });

        setOnCancelled(event -> {
            System.out.println("KEYBOARD SERVICE CANCELLED");
        }); */

        return null;
    }
}
