package util.keyboards.yamaha;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.keyboards.yamaha.entity.*;
import util.sysex.SysexWriter;

import javax.sound.midi.*;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static util.keyboards.yamaha.KbUtil.extractName;
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
    private MidiDevice keyboardIn, keyboardOut;

    private Receiver receiver;
    private Transmitter transmitter;

    /**
     * Sets-up MidiDevices (input and output as we need both)
     */
    private KbCommunication() {
        if (AppInfo.INSTANCE.getSelectedDevice() == null) {
            // TODO Something
            logger.error("No device selected");
        } else {
            keyboardOut = AppInfo.INSTANCE.getSelectedDevice().getMidiOutputDevice();
            keyboardIn = AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice();

            /*
             * We need both, so neither one may be null
             */
            if (keyboardOut == null || keyboardIn == null) {
                logger.error("MidiDevice not selected");
            } else {
                try {
                    if (!keyboardIn.isOpen()) {
                        keyboardIn.open();
                    }
                } catch (MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }

                try {
                    keyboardOut.open();
                } catch (MidiUnavailableException e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }

                /*
                 * Now both should be open
                 */

                try {
                    /* keyboardInput has a receiver, keyboardOutput has a transmitter */
                    receiver = keyboardIn.getReceiver();
                    transmitter = keyboardOut.getTransmitter();
                    transmitter.setReceiver(new Receiver() {
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
    public final BlockingQueue<byte[]> handshakeQueue = new LinkedBlockingQueue<>();

    /**
     * Sysex received from keyboard and put in Q
     *
     * @param rawResponse
     * @param rawResponse
     */
    public void onSysexReceived(byte[] rawResponse) {

        logMidi("RECEIVED", rawResponse);

        // Bestaande overdracht naar de wachtende thread
        boolean accepted = handshakeQueue.offer(rawResponse);

        if (!accepted) {
            logger.debug("No thread is waiting on these bytes at the moment: ignore them.");
        }
    }

    /**
     * Sending sysex bytes to keyboard
     *
     * @param bytes
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    public void send(byte[] bytes)
            throws MidiUnavailableException, InvalidMidiDataException {

        logMidi("SENT", bytes);

        sysexWriter.sendBytes(bytes);
    }

    public byte[] waitForResponse(
            byte[] expectedPrefix,
            long timeoutMs) throws InterruptedException {

        long deadline = System.nanoTime()
                + TimeUnit.MILLISECONDS.toNanos(timeoutMs);

        while (true) {
            long remaining = deadline - System.nanoTime();

            if (remaining <= 0) {
                logger.error("Timeout waiting for: {}", asHex(expectedPrefix));
                return null;
            }

            byte[] response = handshakeQueue.poll(
                    remaining, TimeUnit.NANOSECONDS);

            if (response == null) {
                logger.error("Timeout waiting for: {}", asHex(expectedPrefix));
                return null;
            }

            logger.debug("Received response: {}", asHex(response));

            // Alle Yamaha style-transferstatussen teruggeven.
            if (starts(response, 0xF0, 0x43, 0x50, 0x00, 0x03)) {
                return response;
            }

            if (startsWith(response, expectedPrefix)) {
                return response;
            }

            logger.debug("Ignoring unexpected response: {}", asHex(response));
        }
    }

        /**
         * Send and wait for response
         *
         * @param s : string to send
         * @param expectedPrefix :  bytes array of expected response
         * @param timeoutMs : tome to wait
         * @return byte array, the response or null
         *
         * @throws Exception
         */
    public byte[] sendAndWaitForResponse(
            String s, byte[] expectedPrefix, long timeoutMs) throws Exception {

        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs must be > 0, got: " + timeoutMs);
        }

        /* clear the handschake Q */
        handshakeQueue.clear();

        /* send the string */
        long sendStart = System.nanoTime();
        send(hex(s));

        return waitForResponse(expectedPrefix, timeoutMs);
    }

    /*
     * The 3 messages included here test for a response. It is for a connection check.
     */
    public void checkCommunication() throws Exception {
        logger.debug("Test Communication");

        byte[] expected1 = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x00, 0x00
        };

        byte[] response1 = sendAndWaitForResponse("F0 43 50 00 00 00 01 F7", expected1, 1000);
        if (response1 == null) {
            throw new IOException("Connection test 1 failed");
        }


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
            throw new IOException("Connection test 2 failed");
        }

        byte[] expected3 = {
                (byte) 0xF0,0x43,0x50,0x00,0x00,0x01,0x02,0x00
        };
        byte[] response3 = sendAndWaitForResponse("F0 43 50 00 00 01 01 F7", expected3, 1000);
        if (response3 == null) {
            throw new IOException("Connection test 3 failed");
        }
    }

    /**
     * request identification
     *
     * @throws Exception
     */
    public void identity() throws Exception {
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

    /**
     * Here we disconnect from the keyboard; The keyboard changes its display
     * back to normal functionality
     *
     * @throws Exception
     */
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

    /**
     * retrieve the drives (USER and USB1 if connected)
     *
     * @return Keyboard instance containing a list of present drives
     *
     * @throws Exception
     */
    public Keyboard getKeyboardDrives() throws Exception {
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
        } else {
            KbDrive kbUserDrive = new KbDrive(0, "USER");
            keyboard.getDrives().add(kbUserDrive);
        }

        return keyboard;
    }

    /**
     * retrieve files and/or folders
     *
     * @param entity (a Drive or Folder instance)
     * @param sysex The sysex
     *
     * @throws Exception
     */
    public void retrieveData(DriveEntity entity, String sysex) throws Exception {
        byte[] expected = {
                (byte) 0xF0,
                0x43, 0x50, 0x00,
                0x05, 0x04, 0x01
        };

        byte[] response = sendAndWaitForResponse(sysex, expected, 10000);

        if (response != null) {
            // The response if a file or folder
            if (! starts(response, 0xF0, 0x43, 0x50,0x00, 0x05, 0x7F)) {
                extractName(response, entity);

                /* Look for the next items */
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

                    /* until NO MORE DATA is sent */
                    // F0 43 50 00 05 7F 01 01 01 42 00 00 F7
                    if (starts(response, 0xF0, 0x43, 0x50,
                            0x00, 0x05, 0x7F)) {
                        break;
                    }

                    /* extract file or folder from response and add it to instance */
                    extractName(response, entity);
                }
            }
        }
    }

    public byte[] sendMessageAndWait(String s, long timeout, boolean sendFirst) throws MidiUnavailableException {
        try {
            if (sendFirst) {
                send(hex(s));
            }

            // 3. HIER BLOKKEERT DE THREAD: We wachten maximaal 3 seconden.
            // Omdat de listener de array maakt, maakt de variabele lengte hier niks uit!
            byte[] responseBytes = handshakeQueue.poll(timeout, TimeUnit.MILLISECONDS);

            if (responseBytes != null) {
                // 5. Geef de byte-array terug als resultaat van de functie
                return responseBytes;
            } else {
                logger.error("[SendThread] Timeout: MIDI device didin't react in the time limit");
            }

        } catch (InvalidMidiDataException e) {
            logger.error("Error in MIDI data format: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while waiting.");
        }

        return null; // Retourneer null als er een timeout of fout optreedt
    }

    public void clearHandshakeQueue() {
        handshakeQueue.clear();
    }

    public void close() {
//        try { keyboardIn.close(); } catch (Exception ignored) {}
//        try { keyboardOut.close(); } catch (Exception ignored) {}
//        try { receiver.close(); } catch (Exception ignored) {}
//        try { transmitter.close(); } catch (Exception ignored) {}
    }

    private synchronized void logMidi(String direction, byte[] bytes) {
        if (bytes == null) return;

        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(String.format("%02X ", b & 0xFF));
        }

        logger.info("MIDI {} [{} bytes]: {}",
                direction, bytes.length, hex.toString().trim());
    }}
