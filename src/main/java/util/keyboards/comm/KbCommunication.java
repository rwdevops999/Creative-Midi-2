package util.keyboards.comm;

import entity.sysex.Sysex;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.keyboards.yamaha.KeyboardCommunication;
import util.keyboards.yamaha.entity.*;
import util.sysex.SysexWriter;

import javax.sound.midi.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static util.keyboards.comm.KbUtil.extractName;
import static util.keyboards.yamaha.General.*;
import static util.keyboards.yamaha.General.startsWith;

public class KbCommunication {
    private static final Logger logger = LoggerFactory.getLogger(KbCommunication.class);

    @Getter
    private static KbCommunication instance = new KbCommunication();

    private static SysexWriter sysexWriter = SysexWriter.getInstance();

    @Getter
    private boolean isInitialised = false;

    private MidiDevice pcInput, pcOutput;

    private Receiver keyboardReceiver;
    private Transmitter keyboardTransmitter;

    private KbCommunication() {
        if (AppInfo.INSTANCE.getSelectedDevice() == null) {
            // TODO Something
            logger.error("No device selected");
        } else {
            MidiDevice keyboardOutput = AppInfo.INSTANCE.getSelectedDevice().getMidiOutputDevice();
            MidiDevice keyboardInput = AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();

            /*
             * We need both, so neither one may be null
             */
            if (keyboardOutput == null || keyboardInput == null) {
                logger.error("MidiDevice not selected");
            } else {
                pcInput = keyboardOutput;
                pcOutput = keyboardInput;

                try {
                    if (!pcOutput.isOpen()) {
                        pcOutput.open();
                    }
                } catch (MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }

                try {
                    pcInput.open();
                } catch (MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }

                /*
                 * Now both should be open
                 */

                try {
                    /* keyboardInput has a receiver, keyboardOutput has a transmitter */
                    keyboardReceiver = keyboardInput.getReceiver();
                    keyboardTransmitter = keyboardOutput.getTransmitter();
                    keyboardTransmitter.setReceiver(new Receiver() {
                        public void send(MidiMessage msg, long stamp) {
                            if (msg instanceof SysexMessage) {
                                onSysexReceived(msg.getMessage().clone());
                            }
                        }

                        public void close() {
                        }
                    });
                    isInitialised = true;
                } catch (MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }
            }
        }
    }

    /*
     * Unbounded blocking queue (for producer - consumer)
     */
    private final BlockingQueue<byte[]> midiHandshake = new LinkedBlockingQueue<>();

    private void onSysexReceived(byte[] rawResponse) {
        // poll() of offer() zorgt voor de directe overdracht naar de wachtende thread
        boolean accepted = midiHandshake.offer(rawResponse);
        if (!accepted) {
            logger.debug("No thread is waiting on these bytes at the moment: ignore them.");
        }
    }

    private void send(byte[] bytes) throws MidiUnavailableException, InvalidMidiDataException {
        sysexWriter.sendBytes(bytes);
    }

    public byte[] sendAndWaitForResponse(
            String s, byte[] expectedPrefix, long timeoutMs) throws Exception {

        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs must be > 0, got: " + timeoutMs);
        }

        midiHandshake.clear();

        long sendStart = System.nanoTime();
        send(hex(s));
        long sendDurationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - sendStart);

        if (sendDurationMs > 10) {
            logger.warn("send() took {} ms", sendDurationMs);
        }

        // Start the response timeout AFTER send() completes
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        while (true) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                logger.error("Timeout waiting for: {} (timeoutMs={}, sendDurationMs={})",
                        asHex(expectedPrefix), timeoutMs, sendDurationMs);
                return null;
            }

            byte[] response = midiHandshake.poll(
                    remaining, TimeUnit.NANOSECONDS);

            if (response == null) {
                logger.error("Timeout waiting for: {}", asHex(expectedPrefix));
                return null;
            }


            if (starts(response, 0xF0, 0x43, 0x50,
                    0x00, 0x05, 0x7F)) {
                return response;
            }

            // Expected response
            if (startsWith(response, expectedPrefix)) {
                return response;
            }

  /*          // Directory end/status response
            if (startsWith(response,
                    new byte[]{(byte) 0xF0, 0x43, 0x50, 0x00, 0x05, 0x7F})) {
                return response;
            }
*/
            logger.debug("Ignoring unexpected response: {}", asHex(response));
        }
    }

    /*
     * The 3 messages included here test for a response. It is a connection check.
     */
    public void testCommunication() throws Exception {
        logger.debug("Test Communication");

        byte[] expected1 = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x00, 0x00, 0x02, 0X01, 0X01
        };
        sendAndWaitForResponse("F0 43 50 00 00 00 01 F7", expected1, 1000);

        byte[] expected2 = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x00, 0x02, 0x02
        };

        byte[] response2 = sendAndWaitForResponse(
                "F0 43 50 00 00 02 01 F7",
                expected2,
                1000
        );

        if (response2 == null) {
            throw new IOException("Timeout waiting for device information");
        }

        byte[] expected3 = {
                (byte) 0xF0,0x43,0x50,0x00,0x00,0x01,0x02,0x00
        };
        sendAndWaitForResponse("F0 43 50 00 00 01 01 F7", expected3, 1000);
    }

    public void identity() throws Exception {
        logger.debug("IDENTIFY");

        byte[] expected = {
                (byte) 0xF0,
                0x7E, 0x7F, 0x06,
                0x02, 0x43, 0x00
        };
        sendAndWaitForResponse("F0 7E 7F 06 01 F7", expected, 1000);
    }

    /**
     * Here we connect to the keyboard; The keyboard changes its display
     * to 'Connected to a computer or Smart device'
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        byte[] expected4 = {
                (byte) 0xF0,0x43,0x50,0x00,0x00,0x01,0x02,0x01
        };
        sendAndWaitForResponse("F0 43 50 00 00 01 00 01 F7", expected4, 1000);
    }

    public void disconnect() throws Exception {
        byte[] expected = {
                (byte) 0xF0,0x43,0x50,0x00,0x00,0x01,0x02,0x00
        };

        sendAndWaitForResponse(
                "F0 43 50 00 00 01 00 00 F7",
                expected,
                10000
        );
    }

    public Keyboard getKeyboardSetup() throws Exception {
        byte[] expected = {
                (byte) 0xF0, 0x43, 0x50, 0x00, 0x05, 0x0B, 0x01
        };

        Keyboard keyboard = new Keyboard();

        // Lookup drive 0
        byte[] response = sendAndWaitForResponse(SysexBuilder.getDrive(0), expected, 1000);
        String resp1 = new String(response);
        if (resp1.contains("USB1")) {
            KbDrive kbUsbDrive = new KbDrive(0, "USB1");
            keyboard.getDrives().add(kbUsbDrive);
            keyboard.setHasUsbDrive(true);

            KbDrive kbUserDrive = new KbDrive(1, "USER");
            response = sendAndWaitForResponse(SysexBuilder.getDrive(1), expected, 1000);
            keyboard.getDrives().add(kbUserDrive);

//            asHex = asHex(response);
//            drives.put(1, new CommEntity("USER", asHex.substring(35, asHex.length() - 2)));
        } else {
            KbDrive kbUserDrive = new KbDrive(0, "USER");
            keyboard.getDrives().add(kbUserDrive);

//            String asHex = asHex(response);
//            drives.put(0, new CommEntity("USER", asHex.substring(35, asHex.length() - 2)));
        }

        return keyboard;
    }

    public void retrieveData(DriveEntity entity, String sysex) throws Exception {
        byte[] expected = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x05, 0x04, 0x01
        };

        byte[] response = sendAndWaitForResponse(sysex, expected, 10000);

        if (response != null) {
            if (! starts(response, 0xF0, 0x43, 0x50,0x00, 0x05, 0x7F)) {
                extractName(response, entity);

                for (int i = 0; i < 10000; i++) {
                    response = sendAndWaitForResponse(
                            "F0 43 50 00 05 05 00 F7",
                            new byte[]{
                                    (byte) 0xF0, 0x43, 0x50, 0x00, 0x05
                            },
                            3000
                    );

                    if (response == null) {
                        break;
                    }

                    /* NO MORE DATA */
                    // F0 43 50 00 05 7F 01 01 01 42 00 00 F7
                    if (starts(response, 0xF0, 0x43, 0x50,
                            0x00, 0x05, 0x7F)) {
                        break;
                    }

                    extractName(response, entity);
                }
            }
        }
    }
}
