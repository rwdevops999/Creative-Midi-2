package util.keyboards.oldyamaha;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static util.keyboards.yamaha.General.*;

public class KeyboardCommunication {
    private static final Logger logger = LoggerFactory.getLogger(KeyboardCommunication.class);

    @Getter
    private static KeyboardCommunication instance = new KeyboardCommunication();

    private final List<byte[]> responses = new CopyOnWriteArrayList<>();

    private final BlockingQueue<byte[]> midiHandshake = new LinkedBlockingQueue<>();

    private MidiDevice output, input;
    private Receiver sender;
    private Transmitter incoming;

    private void onBytesReceived(byte[] rawResponse) {
        // poll() of offer() zorgt voor de directe overdracht naar de wachtende thread
        logger.debug("RECEIVED FROM PSR: {}", asHex(rawResponse));
        boolean accepted = midiHandshake.offer(rawResponse);
        if (!accepted) {
            logger.debug("No thread is waiting on these bytes at the moment: ignore them.");
        }
    }

    private boolean isInitialised = false;

    public boolean isSetup() {
        return isInitialised;
    }

    private KeyboardCommunication() {
        isInitialised = false;

        MidiDevice out = null, in = null;

        out = AppInfo.INSTANCE.getSelectedDevice().getMidiOutputDevice();
        in = AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();

        if (out == null || in == null) {
            logger.error("Midi device not selected");
        } else {
            output = in; input = out;

            try {
                if (!output.isOpen()) {
                    output.open();
                }
            } catch (MidiUnavailableException e) {
                logger.error("Exception. CAUSE {}", e.getMessage());
            }

            try { input.open(); }
            catch (MidiUnavailableException e) {
                output.close();
                logger.error("Exception. CAUSE {}", e.getMessage());
            }

            try {
                sender = output.getReceiver();
                transfer = new YamahaTransfer(sender);
                incoming = input.getTransmitter();

                incoming.setReceiver(new Receiver() {
                    public void send(MidiMessage msg, long stamp) {
                        if (msg instanceof SysexMessage) {
                            onBytesReceived(msg.getMessage().clone());
                        }
                    }
                    public void close() { }
                });

                isInitialised = true;
            } catch (MidiUnavailableException e) {
                input.close();
                output.close();
                logger.error("Exception. CAUSE {}", e.getMessage());
            }
        }
    }

    private YamahaTransfer transfer;

    private void send(byte[] bytes) throws InvalidMidiDataException {
        transfer.sendSysex(bytes);
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

            logger.debug("Queue received: {}", asHex(response));
            responses.add(response);

            // Expected response
            if (startsWith(response, expectedPrefix)) {
                return response;
            }

            // Directory end/status response
            if (startsWith(response,
                    new byte[]{(byte) 0xF0, 0x43, 0x50, 0x00, 0x05, 0x7F})) {
                return response;
            }

            logger.debug("Ignoring unexpected response: {}", asHex(response));
        }
    }

    /**
     * The 3 messages included here test for a response. It is a connection check.
     *
     * @throws Exception
     */
    public void begin() throws Exception {
        responses.clear();
        logger.debug("BEGIN");

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

    /**
     * Here we connect to the keyboard; The keyboard changes its display
     * to 'Connected to a computer or Smart device'
     *
     * @throws Exception
     */
    public void makeConnection() throws Exception {
        byte[] expected4 = {
                (byte) 0xF0,0x43,0x50,0x00,0x00,0x01,0x02,0x01
        };
        sendAndWaitForResponse("F0 43 50 00 00 01 00 01 F7", expected4, 1000);
    }

    public void end() throws Exception {
        logger.debug("ENDING TRANSFER MODE");

        byte[] expected = {
                (byte) 0xF0,
                0x43, 0x50, 0x00, 0x00,
                0x01, 0x02, 0x00
        };

        long start = System.currentTimeMillis();

        sendAndWaitForResponse(
                "F0 43 50 00 00 01 00 00 F7",
                expected,
                10000
        );

        logger.debug("END WAIT RETURNED after {} ms", (System.currentTimeMillis() - start));
    }

    public void identity() throws Exception {
        logger.debug("IDENTIFY");
/*        byte[] expected = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x05, 0x0B, 0x01
        };
        sendAndWaitForResponse("F0 43 50 00 05 0B 00 01 F7", expected, 1000);

        byte[] expected2 = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x00, 0x07, 0x02
        };
        sendAndWaitForResponse("F0 43 50 00 00 07 01 F7", expected2, 1000);
    } */
        byte[] expected = {
                (byte) 0xF0,
                0x7E, 0x7F, 0x06,
                0x02, 0x43, 0x00
        };
        sendAndWaitForResponse("F0 7E 7F 06 01 F7", expected, 1000);
    }
}
