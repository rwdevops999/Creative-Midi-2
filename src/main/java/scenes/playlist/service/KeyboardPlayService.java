package scenes.playlist.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.communication.CommunicationType;
import util.registry.AppEvent;
import util.registry.CommunicationMessage;
import util.registry.Registry;

import javax.sound.midi.*;

import static util.registry.AppEvent.AppEventType.MONITOR;

public class KeyboardPlayService extends Service<Void> {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardPlayService.class);

    private static final long FOREVER = Long.MAX_VALUE;

    /*
     * To use showMessage which transmits the message to the monitor
     */
    private final CommunicationMessage comm = CommunicationMessage.getInstance();

    private MidiDevice midiOutputDevice;

    private void handleSysexMessage(SysexMessage message) {
        System.out.println("SysexMessage: " + message.getMessage());
        /*
         * This message is send to monitor
         */
        comm.showMessage(CommunicationType.INBOUND, "SysexMessage: " + message.getMessage());
    };

    private void handleMidiMessage(ShortMessage message) {
        System.out.println("MidiMessage: " + message.getMessage());
        comm.showMessage(CommunicationType.INBOUND, "MidiMessage: " + message.getMessage());
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
            midiOutputDevice =  AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();
            if (midiOutputDevice == null) {
                return null;
            }

            if (! midiOutputDevice.isOpen()) {
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
                            System.out.println("CLOSE RECEIVER -> CLOSE MIDI OUTPUT DEVICE");
                            if (midiOutputDevice.isOpen()) {
                                midiOutputDevice.close();
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
