package util.midi;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MidiFileWriter {
    public void writeMidi(Sequence sequence, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            // 1. Write the header chunk (MThd)
            fos.write("MThd".getBytes());
            write32BitInt(fos, 6); // is always 6 bytes

            // Keep type 1, set nr of tracks, and the ppq (resolution)
            write16BitInt(fos, 1);
            write16BitInt(fos, sequence.getTracks().length);
            write16BitInt(fos, sequence.getResolution());

            // 2. write the track chuks (MTrk)
            for (Track track : sequence.getTracks()) {
                java.io.ByteArrayOutputStream trackBuffer = new java.io.ByteArrayOutputStream();
                long previousTick = 0;

                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage msg = event.getMessage();

                    // calculate the delta time (tome since previous event on this track)
                    long deltaTime = event.getTick() - previousTick;
                    writeVariableLengthInt(trackBuffer, deltaTime);

                    // retrieve the raw bytes if the MIDI-message
                    byte[] messageBytes = msg.getMessage();
                    int length = msg.getLength();

                    // Java's MetaMessage.getMessage() doesn't always pass the status byte.
                    // therefore we check this to prevent byte-shifting:
                    if (length > 0 && (messageBytes[0] & 0xFF) != (msg.getStatus() & 0xFF)) {
                        // if the status byte is missing (ofter with Meta Messages), write it first
                        trackBuffer.write(msg.getStatus());
                    }

                    // writhe the bytes of the message into the track buffer
                    if (length > 0) {
                        trackBuffer.write(messageBytes, 0, length);
                    }

                    previousTick = event.getTick();
                }

                // write the track header (MTrk) and the exact length to the file
                fos.write("MTrk".getBytes());
                write32BitInt(fos, trackBuffer.size());

                // write the track content to the file
                fos.write(trackBuffer.toByteArray());
            }
        }
    }

    private void write32BitInt(FileOutputStream fos, int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(value).array();
        fos.write(bytes);
    }

    private void write16BitInt(FileOutputStream fos, int value) throws IOException {
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) value).array();
        fos.write(bytes);
    }

    private void writeVariableLengthInt(java.io.ByteArrayOutputStream stream, long value) throws IOException {
        long buffer = value & 0x7F;
        while ((value >>= 7) > 0) {
            buffer <<= 8;
            buffer |= ((value & 0x7F) | 0x80);
        }
        while (true) {
            stream.write((int) (buffer & 0xFF));
            if ((buffer & 0x80) != 0) {
                buffer >>= 8;
            } else {
                break;
            }
        }
    }
}