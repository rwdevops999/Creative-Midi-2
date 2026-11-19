package util.midi;

import org.apache.commons.io.FilenameUtils;
import util.app.AppInfo;

import javax.sound.midi.MidiSystem;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static util.Util.insertTimestamp;

public class MidiSplitter {
    public record SplitResult(byte[] midiBytes, byte[] nonMidiBytes) {}

    public String splitMidi(String styleFileName) {
        File styleFile = new File(styleFileName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String splitFileName = insertTimestamp(styleFileName, timestamp);

        try {
            byte[] data = Files.readAllBytes(styleFile.toPath());
            SplitResult result = splitMidiAndNonMidi(data);

            File tempMid = new File(FilenameUtils.removeExtension(splitFileName) + ".mid");
            Files.write(tempMid.toPath(), result.midiBytes);
            File tempNmi = new File(FilenameUtils.removeExtension(splitFileName) + ".nmi");
            Files.write(tempNmi.toPath(), result.nonMidiBytes());
        } catch (IOException e) {
            System.out.println("IOException");
        }

        return splitFileName;
    }

    public SplitResult splitMidiAndNonMidi(byte[] fileBytes) throws IOException {
        ByteArrayOutputStream midiOut = new ByteArrayOutputStream();
        ByteArrayOutputStream nonMidiOut = new ByteArrayOutputStream();

        int i = 0;
        while (i + 8 <= fileBytes.length) {
            String chunkId = new String(fileBytes, i, 4, StandardCharsets.US_ASCII);
            int chunkLength = ByteBuffer.wrap(fileBytes, i + 4, 4).getInt();

            boolean isStandardMidiChunk = ("MThd".equals(chunkId) || "MTrk".equals(chunkId))
                    && chunkLength >= 0
                    && (long) i + 8 + chunkLength <= fileBytes.length;

            if (isStandardMidiChunk) {
                int totalChunkSize = 8 + chunkLength;
                midiOut.write(fileBytes, i, totalChunkSize);
                i += totalChunkSize;
            } else {
                nonMidiOut.write(fileBytes, i, 1);
                i++;
            }
        }

        if (i < fileBytes.length) {
            nonMidiOut.write(fileBytes, i, fileBytes.length - i);
        }

        return new SplitResult(midiOut.toByteArray(), nonMidiOut.toByteArray());
    }

    public void spliceFiles(String midiFileName, String splitFileName, String styleFileName) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String newStyleName = insertTimestamp(styleFileName, timestamp);

        File mid = new File(midiFileName);
        File nmi = new File(FilenameUtils.removeExtension(splitFileName) + ".nmi");
        File mergedOutput = new File(newStyleName);

        try (OutputStream out = new FileOutputStream(mergedOutput);
             // TODO Convert midi to type 0 if type 1
             InputStream inMid = new FileInputStream(mid);
             InputStream inNmi = new FileInputStream(nmi)) {

            inMid.transferTo(out);
            inNmi.transferTo(out);
        }

        // TODO if dnot debugmode remove temp files
        if (!AppInfo.INSTANCE.isDebugMode()) {
            File tempMid = new File(FilenameUtils.removeExtension(splitFileName) +".mid");
            Files.deleteIfExists(tempMid.toPath());
            File tempNmi = new File(FilenameUtils.removeExtension(splitFileName) +".nmi");
            Files.deleteIfExists(tempNmi.toPath());
        }
    }
}
