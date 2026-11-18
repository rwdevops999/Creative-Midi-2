package scenes.playlist.service;

import entity.midi.Midi;
import entity.playlist.Song;
import entity.playlist.SongControl;
import entity.playlist.SongMapping;
import entity.sysex.Sysex;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scenes.playlist.container.PlaylistContainer;
import scenes.playlist.service.helper.LoadedSongResolver;
import scenes.sysex.parametrized.Parameter;
import scenes.sysex.parametrized.ParametrizedFunctionFactory;
import scenes.sysex.parametrized.TempoFunction;
import util.SysexToHexStringConvertor;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.midi.MidiWriter;
import util.registry.AppEvent;
import util.registry.CommunicationMessage;
import util.registry.Registry;
import util.sysex.SysexWriter;

import javax.sound.midi.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;

import static util.communication.CommunicationType.*;
import static util.keyboards.yamaha.General.asHex;
import static util.keyboards.yamaha.General.hex;
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
    private final SysexWriter sysexWriter = SysexWriter.getInstance();
    private final PlaylistContainer playlistContainer = PlaylistContainer.getInstance();

    private MidiDevice midiOutputDevice;
    private MidiDevice midiInputDevice; // for writing to keyboard

    private String selectedSong = "";
    private boolean songIsValid = false;
    private boolean songIsPlaying = false;

    private boolean preMappingsSent = false;

    private SongControl songControl = null;

//    private List<Sysex> preMappings = new ArrayList<>();
//    private List<Sysex> postMappings = new ArrayList<>();
    private Map<ByteBuffer, ByteBuffer> mappingsMap = new HashMap<>();
    private Map<ByteBuffer, String> mappingsNameMap = new HashMap<>();

    private void processSongInformation(String songName) {
        logger.debug("PROCESSING SONG: {}", songName);

        if (! songName.equals(selectedSong)) {
            logger.debug("THIS IS NOT THE SAME SONG AS IS PLAYING: {}", songName);

            songControl = new SongControl(songName);

            logger.debug("STOP STYLE: {}", songName);
            sendToKeyboard("F0 43 60 7D F7");

            logger.debug("LOOKING UP SONG: {}", songName);
            Song song = playlistContainer.getSongByName(songName);
            song.getPreMappings().stream().filter(s -> s.getContent().getParametrized() != false).findFirst().ifPresent(System.out::println);

            if (song != null) {
                logger.debug("SONG INFORMATION IS AVAILABLE: {}", songName);
                logger.debug("SONG TEMPO: {}", song.getSongInfo().getTempo());

                if (! song.getPreMappings().isEmpty()) {
                    logger.debug("SETTING UP PRE-MAPPINGS: {}", songName);
                    songControl.setPreMappings(new ArrayList<>());
                    System.out.println(">>> PREMAPPINGS: " + Integer.toHexString(System.identityHashCode(songControl.getPreMappings())));

                    preMappingsSent = false;
                    for (Sysex sysex : song.getPreMappings()) {
                        Sysex newSysex = new Sysex(sysex);
                        songControl.getPreMappings().add(newSysex);

                        if (newSysex.getContent().getParametrized()) {
                            Function<Integer, Parameter> function = ParametrizedFunctionFactory.getInstance().getParametrizedFunction(newSysex.getContent().getParametrizedFunction());
                            if (function instanceof TempoFunction tempoFunction) {
                                Parameter result = tempoFunction.apply(song.getSongInfo().getTempo());
                                newSysex.getContent().setParametrized(false);
                                String tempoSysex = sysex.getContent().getSysex().replace(result.getParameterString(), result.getReplacementString());
                                System.out.println("TEMPO SYSEX = " + tempoSysex);

                                newSysex.getContent().setSysex(tempoSysex);

//                                songControl.getPreMappings().add(new Sysex(sysex));
                            }
                        }
/*                        else {
                            songControl.getPreMappings().add(new Sysex(sysex));
                        } */
                    }
                }

                if (! song.getMappings().isEmpty()) {
                    logger.debug("SETTING UP MAPPINGS: {}", songName);
                    for (SongMapping songMapping : song.getMappings()) {
                        mappingsMap.put(
                                ByteBuffer.wrap(songMapping.getReceive().getContent().getBytes()),
                                ByteBuffer.wrap(songMapping.getReply().getContent().getBytes()));
                        mappingsNameMap.put(
                                ByteBuffer.wrap(songMapping.getReceive().getContent().getBytes()), songMapping.getReceive().getName()
                        );
                        mappingsNameMap.put(
                                ByteBuffer.wrap(songMapping.getReply().getContent().getBytes()), songMapping.getReceive().getName()
                        );
                    }
                }

                if (! song.getPostMappings().isEmpty()) {
                    logger.debug("SETTING UP POST-MAPPINGS: {}", songName);
                    songControl.setPostMappings(new ArrayList<>());
                    songControl.getPostMappings().addAll(song.getPostMappings());
                }

                logger.debug("SELECT REG 1: {}", songName);
                sendToKeyboard("F0 43 73 01 52 25 11 00 02 00 00 F7");

                logger.debug("SENDING PRE-MAPPINGS: {}", songName);
                sendPreMappings();

                songIsValid = true;
                logger.debug("SET SONG AS CURRENT SELECTED: {}", songName);
                selectedSong = songName;
            }
        }
    }

    private void sendPreMappings () {
        System.out.println("SENDING PRE-MAPPINGS of " + songControl != null ? songControl.getSongName() : "UNDEFINED");
        // Execute pre-mappings
        try {
            System.out.println(">>> PREMAPPINGS: " + Integer.toHexString(System.identityHashCode(songControl.getPreMappings())));
            for (Sysex sysex : songControl.getPreMappings()) {
                System.out.println("SENDING SYSEX: " + sysex.getContent().getSysex());
                sysexWriter.send(sysex);
                Thread.sleep(100);
            }
            preMappingsSent = true;
/*            for (ByteBuffer buffer : preMappings) {
                System.out.println("Sending : " + buffer);
                sysexWriter.sendBytes(buffer.array());
            } */
        } catch (InterruptedException | InvalidMidiDataException | MidiUnavailableException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }

    private void sendPostMappings () {
        // Execute post-mappings
        try {
            for (Sysex sysex : songControl.getPostMappings()) {
                sysexWriter.send(sysex);
            }
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }

    private void handleSysexMessage(SysexMessage message) {
        String hexMessage = SysexToHexStringConvertor.convertToHexString(message.getMessage());

        if (hexMessage.startsWith("F0 43 73 01 52 26")) {
            // It is a song name => use songResolver
            String songName = songResolver.resolveSongName(hexMessage);

            comm.showMessage(SELECT_SONG, songName);

            processSongInformation(songName);
        } else if (songIsPlaying) {
            // we examine only sysex messages when song is playing
            ByteBuffer inbound = ByteBuffer.wrap(message.getMessage());
            ByteBuffer outbound = mappingsMap.get(inbound);
            if (outbound != null) {
                try {
                    sysexWriter.sendBytes(outbound.array());
                    comm.showMessage(INBOUND, mappingsNameMap.get(inbound));
                    comm.showMessage(OUTBOUND, mappingsNameMap.get(outbound));
                } catch  (InvalidMidiDataException | MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }
            } else {
                // There is no mapping for this sysex, so send it raw to monitor

                /*
                 * This message is send raw to the monitor as is...
                 */
                if (! songIsPlaying) {
                    comm.showMessage(CommunicationType.INBOUND, hexMessage);
                }
            }
        } else if (hexMessage.startsWith("F0 43 7E 01") && ! preMappingsSent) {
            logger.debug("SENDING PREMAPPINGS");
            sendPreMappings();
        }
    };

    private static final byte ACTIVE_SENSING = -2;
    private static final byte STOP = -4;
    private static final byte START = -6;

    private void handleMidiMessage(ShortMessage message) {
        byte[] rawBytes = message.getMessage();

        /*
         * I'm not interested in ACTIVE SENSING messages
         */
        if (rawBytes[0] != ACTIVE_SENSING) {
            if (rawBytes[0] == START && songIsValid) {
                logger.debug("STARTED SONG: {}", selectedSong);
                comm.showMessage(CommunicationType.INBOUND, "Start");
                songIsPlaying = true;
                logger.debug("SENDING PRE MAPPINGS");
                sendPreMappings();
            } else if (rawBytes[0] == STOP && songIsPlaying) {
                logger.debug("STOPPED SONG: {}", selectedSong);

                songIsPlaying = false;
                comm.showMessage(CommunicationType.INBOUND, "Stop");
                logger.debug("SENDING POST MAPPINGS");
                sendPostMappings();
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
        selectedSong = "";

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
                            } else if (message instanceof ShortMessage midiMessage) {
                                handleMidiMessage(midiMessage);
                            }

                            // We don't treat META messages
                        }

                        /**
                         * Called when the receiver closes
                         */
                        @Override
                        public void close() {
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

    private void sendToKeyboard(String sysex) {
        try {
            sysexWriter.sendBytes(hex(sysex));
//            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }
}
