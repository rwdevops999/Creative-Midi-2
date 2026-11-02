package util.midi;

import entity.midi.MidiFileEventInfo;
import entity.midi.MidiSequence;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MidifileParser {
    private static final Logger logger = LoggerFactory.getLogger(MidifileParser.class);

    @Getter
    private MidiSequence customSequence;

    public MidifileParser(File midiFile) {
        customSequence = parseMidi(midiFile);
    }

    public MidiSequence parseMidi(File midiFile) {
        MidiSequence sequence = new MidiSequence();

        // read the raw bytes from the (binary) file
        try (FileInputStream fis = new FileInputStream(midiFile)) {
            if (processHeaderChunck(fis, sequence)) {
                // The header is processed and is OK, now we are going to read the tracks
                for (int i = 0; i < sequence.getTrackCount(); i++) {
                    // Read the track ID
                    byte[] trackId = new byte[4];
                    int bytesRead = fis.read(trackId);
                    if (bytesRead == -1) break; // Reached EOF

                    // And the track length
                    int trackLength = read32BitInt(fis);

                    // read the raw data of the track
                    byte[] trackData = new byte[trackLength];
                    int totalRead = 0;
                    while (totalRead < trackLength) {
                        int read = fis.read(trackData, totalRead, trackLength - totalRead);
                        if (read == -1) break;
                        totalRead += read;
                    }

                    sequence.getTracks().put(i, parseTrack(trackData, i));
                }
            }
        } catch (IOException ioe) {
            logger.error("Exception: CAUSE {}", ioe.getMessage());
        }

        return sequence;
    }

    /**
     * Process the header in the midi file
     *
     * @param fis      : the FileInputStream of the file
     * @param sequence : the custom midi sequence
     * @return true if chunks correct processed, otherwise false
     * @throws IOException
     */
    private boolean processHeaderChunck(FileInputStream fis, MidiSequence sequence) throws IOException {
        try {
            // the header id is 4 bytes long
            byte[] headerId = new byte[4];
            fis.read(headerId);
            String headerStr = new String(headerId);

            if (!headerStr.equals("MThd")) {
                logger.error("Error. This is not a valid Midi file");
                return false;
            }

            /**
             * It is a valid midi file
             */
            int headerLength = read32BitInt(fis);

            /**
             * Set values in Custom Sequence
             */
            sequence.setFileType(read16BitInt(fis));
            sequence.setTrackCount(read16BitInt(fis));
            sequence.setPpq(read16BitInt(fis));
            MBTCalculator.setPPQ(sequence.getPpq());
        } catch (IOException ioe) {
            logger.error("[CM_MIDI_PARSER_ERROR] EXCEPTION: CAUSE {}", ioe.getMessage());
        }

        return true;
    }

    /**
     * Read an integer as 4 bytes(int) from the input stream
     *
     * @param fis The file input stream
     * @return the integer respresented by the 4 bytes
     * @throws IOException
     */
    private int read32BitInt(FileInputStream fis) throws IOException {
        byte[] bytes = new byte[4];
        fis.read(bytes);
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Read a short (2 bytes) from the input stream
     *
     * @param fis The file input stream
     * @return the short (as integer) respresented by the 2 bytes
     * @throws IOException
     */
    private static int read16BitInt(FileInputStream fis) throws IOException {
        byte[] bytes = new byte[2];
        fis.read(bytes);
        return ByteBuffer.wrap(bytes).getShort() & 0xFFFF; // & 0xFFFF to convert from signed to unsigned
    }

    /**
     * A track consists of a lot of midi events. So we are going to parse the track events
     *
     * @param trackData  : the raw bytes of the track
     * @param trackIndex : the index to the beginning of the track
     * @return List of midi events
     * @throws IOException
     */
    private List<MidiFileEventInfo> parseTrack(byte[] trackData, int trackIndex) throws IOException {
        List<MidiFileEventInfo> events = new ArrayList<>();

        ByteArrayInputStream stream = new ByteArrayInputStream(trackData);

        long currentTick = 0;
        int runningStatus = 0;

        while (stream.available() > 0) {
            long deltaTime = readVariableLengthInt(stream);
            currentTick += deltaTime;

            stream.mark(1);
            int status = stream.read();

            if (status < 0x80) {
                stream.reset();
                status = runningStatus;
            } else {
                runningStatus = status;
            }

            if (status == 0xFF) {
                // This is a META Event

                runningStatus = 0;

                int metaType = stream.read();
                long length = readVariableLengthInt(stream);

                if (length < 0 || length > 65535) {
                    length = 0;
                }

                byte[] data = new byte[(int) length];
                if (length > 0) {
                    stream.read(data);
                }

                /**
                 * Store the event
                 */
                MidiFileEventInfo info = new MidiFileEventInfo(currentTick, status, data, metaType);
                info.setTrack(trackIndex); // Or pass it in the constructor if possible
                events.add(info);
            } else if (status == 0xF0 || status == 0xF7) {
                // This is a SYSEX Event
                runningStatus = 0;

                int length = (int) readVariableLengthInt(stream);
                byte[] data = new byte[length];
                stream.read(data);

                /**
                 * Store the event
                 */
                MidiFileEventInfo info = new MidiFileEventInfo(currentTick, status, data);
                info.setTrack(trackIndex); // store track id
                events.add(info);
            } else {
                // This is a MIDI Event
                int eventType = status & 0xF0;
                int dataLength = 2;

                if (eventType == 0xC0 || eventType == 0xD0) {
                    dataLength = 1;
                }

                byte[] data = new byte[dataLength];

                if (status == runningStatus && stream.markSupported()) {
                    // read first byte
                    data[0] = (byte) stream.read();

                    if (dataLength == 2) {
                        // read evt second byte
                        data[1] = (byte) stream.read();
                    }
                } else {
                    stream.read(data);
                }

                /**
                 * Store the event
                 */
                MidiFileEventInfo info = new MidiFileEventInfo(currentTick, status, data);
                info.setTrack(trackIndex);
                events.add(info);
            }
        }

        /**
         * We have parsed all events
         */
        return events;
    }

    /**
     * Read a long from the input stream (more than 4 bytes)
     *
     * @param stream the byte array stream on the raw data
     * @return a long value
     *
     * @throws IOException
     */
    private long readVariableLengthInt(ByteArrayInputStream stream) throws IOException {
        long value = 0;
        int b;
        do {
            b = stream.read();
            value = (value << 7) | (b & 0x7F);
        } while ((b & 0x80) != 0);
        return value;
    }
}
