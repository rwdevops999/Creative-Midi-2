package util.keyboards.yamaha;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.midi.MidiConvertor;

import javax.sound.midi.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static util.Util.insertTimestamp;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StyleMerger {

    private static final Logger logger = LoggerFactory.getLogger(StyleMerger.class);

    private static final int MAX_MIDI_CHANNELS = 16;
    private static final int CH_1 = 0;
    private static final int CH_9 = 8;    // Yamaha RHY1 (drums)
    private static final int CH_10 = 9;   // Yamaha RHY2 (drums)
    private static final int CH_11 = 10;  // Yamaha BASS
    private static final int CH_15 = 14;  // Yamaha PHR1
    private static final int CH_16 = 15;  // Yamaha PHR2

    private static final int KEY_SIG_SHARPS_FLATS_IDX = 0;
    private static final int KEY_SIG_MINOR_IDX = 1;

    public String merge(String styleFileName, String midiFileName) throws Exception {
        File origStyleFile = new File(styleFileName);
        byte[] styleBytes = Files.readAllBytes(origStyleFile.toPath());
        SplitResult split = splitMidiAndNonMidi(styleBytes);

        File newMidiFile = new File(midiFileName);
        byte[] newMidiBytes = Files.readAllBytes(newMidiFile.toPath());

        byte[] mergedType0Midi = mergeMidiKeepingStyleSetup(split.midiBytes(), newMidiBytes);
        byte[] updatedNonMidi = rebuildCasmWithAllChannelsAndCMajor(split.nonMidiBytes());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String newStylePath = insertTimestamp(styleFileName, timestamp);
        File outputFile = new File(newStylePath);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            out.write(mergedType0Midi);
            out.write(updatedNonMidi);
        }

        return outputFile.getAbsolutePath();
    }

    private byte[] mergeMidiKeepingStyleSetup(byte[] origStyleMidiBytes, byte[] newMidiBytes) throws Exception {
        try (ByteArrayInputStream origIn = new ByteArrayInputStream(origStyleMidiBytes);
             ByteArrayInputStream newIn = new ByteArrayInputStream(newMidiBytes);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sequence origSeq = MidiSystem.getSequence(origIn);
            Sequence newSeq = MidiSystem.getSequence(newIn);

            int targetResolution = origSeq.getResolution();
            int sourceResolution = newSeq.getResolution();
            int globalTransposeToC = calculateSemitonesToC(newSeq);

            Sequence targetSeq = new Sequence(origSeq.getDivisionType(), targetResolution);
            Track mainTrack = targetSeq.createTrack();

            List combinedEvents = new ArrayList();
            boolean hasSffMarker = false;
            boolean hasSintMarker = false;
            boolean origHasCh16Setup = false;

            // STAP A: Setup-events op tick 0 uit de originele stijl
            for (Track track : origSeq.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    if (event.getTick() > 0) {
                        break;
                    }

                    MidiMessage msg = event.getMessage();
                    if (msg instanceof ShortMessage sm && sm.getChannel() == CH_16) {
                        origHasCh16Setup = true;
                    }
                    if (msg instanceof MetaMessage meta) {
                        if (meta.getType() == 0x2F || meta.getType() == 0x58) {
                            continue; // Laat de nieuwe MIDI de maatsoort bepalen
                        }

                        if (meta.getType() == 0x2F) {
                            continue;
                        }
                        String text = new String(meta.getData(), StandardCharsets.US_ASCII).trim();
                        if ((meta.getType() == 0x03 || meta.getType() == 0x06)
                                && ("SFF1".equals(text) || "SFF2".equals(text))) {
                            hasSffMarker = true;
                        }
                        if (meta.getType() == 0x06) {
                            if ("SInt".equals(text)) {
                                hasSintMarker = true;
                            } else if (!"SFF1".equals(text) && !"SFF2".equals(text)) {
                                break;
                            }
                        }
                    }
                    combinedEvents.add(new MidiEvent((MidiMessage) msg.clone(), 0));
                }
            }

            if (!hasSffMarker) {
                byte[] sffData = "SFF2".getBytes(StandardCharsets.US_ASCII);
                combinedEvents.add(new MidiEvent(new MetaMessage(0x03, sffData, sffData.length), 0));
            }
            if (!hasSintMarker) {
                byte[] sintData = "SInt".getBytes(StandardCharsets.US_ASCII);
                combinedEvents.add(new MidiEvent(new MetaMessage(0x06, sintData, sintData.length), 0));
            }

            // STAP B: Check welke kanalen noten bevatten en haal eventuele ontbrekende CH 16 voice op
            boolean[] channelHasNotes = (boolean[]) Array.newInstance(boolean.class, MAX_MIDI_CHANNELS);
            int ch16Msb = 0;
            int ch16Lsb = 0;
            int ch16Prog = -1;
            int ch16Vol = 100;

            for (Track track : newSeq.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    if (track.get(i).getMessage() instanceof ShortMessage sm) {
                        int ch = sm.getChannel();
                        if (sm.getCommand() == ShortMessage.NOTE_ON && ch >= CH_1 && ch <= CH_16) {
                            Array.setBoolean(channelHasNotes, ch, true);
                        }
                        if (ch == CH_16) {
                            if (sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == 0) {
                                ch16Msb = sm.getData2();
                            } else if (sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == 32) {
                                ch16Lsb = sm.getData2();
                            } else if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE && ch16Prog == -1) {
                                ch16Prog = sm.getData1();
                            } else if (sm.getCommand() == ShortMessage.CONTROL_CHANGE && sm.getData1() == 7 && sm.getData2() > 0) {
                                ch16Vol = sm.getData2();
                            }
                        }
                    }
                }
            }

            // Als de originele stijl op tick 0 geen setup had voor CH 16 (PHR2), voeg die nu op tick 0 toe
            if (!origHasCh16Setup && Array.getBoolean(channelHasNotes, CH_16)) {
                combinedEvents.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, CH_16, 0, ch16Msb), 0));
                combinedEvents.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, CH_16, 32, ch16Lsb), 0));
                combinedEvents.add(new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE, CH_16, Math.max(0, ch16Prog), 0), 0));
                combinedEvents.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, CH_16, 7, ch16Vol), 0));
                combinedEvents.add(new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, CH_16, 11, 127), 0));
            }

            int targetChannelForCh1 = CH_1;
            if (Array.getBoolean(channelHasNotes, CH_1)) {
                if (!Array.getBoolean(channelHasNotes, CH_15)) {
                    targetChannelForCh1 = CH_15;
                } else if (!Array.getBoolean(channelHasNotes, CH_16)) {
                    targetChannelForCh1 = CH_16;
                }
            }

            // STAP C: Kopieer alle events van de nieuwe MIDI
            for (Track track : newSeq.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage msg = event.getMessage();
                    long scaledTick = (event.getTick() * targetResolution) / sourceResolution;

                    if (msg instanceof MetaMessage meta) {
                        if (meta.getType() == 0x2F || meta.getType() == 0x03) {
                            continue;
                        }
                        if (meta.getType() == 0x59) {
                            byte[] origKeyData = meta.getData();
                            byte minorFlag = (origKeyData.length >= 2)
                                    ? Array.getByte(origKeyData, KEY_SIG_MINOR_IDX)
                                    : 0;
                            byte[] cKeyData = new byte[]{0x00, minorFlag};
                            combinedEvents.add(new MidiEvent(new MetaMessage(0x59, cKeyData, 2), scaledTick));
                            continue;
                        }
                        String text = new String(meta.getData(), StandardCharsets.US_ASCII).trim();
                        if (meta.getType() == 0x06 && ("SFF1".equals(text) || "SFF2".equals(text) || "SInt".equals(text))) {
                            continue;
                        }
                        combinedEvents.add(new MidiEvent((MidiMessage) msg.clone(), scaledTick));
                        continue;
                    }

                    if (msg instanceof SysexMessage) {
                        continue;
                    }

                    if (msg instanceof ShortMessage sm) {
                        int ch = sm.getChannel();
                        int destCh;

                        if (ch == CH_1) {
                            destCh = targetChannelForCh1;
                        } else if (ch >= CH_9 && ch <= CH_16) {
                            destCh = ch;
                        } else {
                            continue;
                        }
                        int cmd = sm.getCommand();
                        int data1 = sm.getData1();
                        int data2 = sm.getData2();
                        if (cmd == ShortMessage.PITCH_BEND) {
                            continue;
                        }
                        if (cmd == ShortMessage.CONTROL_CHANGE
                                && (data1 == 100 || data1 == 101 || data1 == 6 || data1 == 38)) {
                            continue;
                        }
                        if (cmd == ShortMessage.CONTROL_CHANGE && (data1 == 7 || data1 == 11) && data2 == 0) {
                            continue;
                        }
                        combinedEvents.add(new MidiEvent(new ShortMessage(cmd, destCh, data1, data2), scaledTick));
                    }
                }
            }
            combinedEvents.sort(Comparator.comparingLong(ev -> ((MidiEvent) ev).getTick()));
// STAP D: Transponeer Intro/Ending globaal naar C, en normaliseer Main/Fill per maat naar C Majeur
            transposeAndNormalizeSections(combinedEvents, targetResolution, globalTransposeToC);

            for (Object obj : combinedEvents) {
                MidiEvent ev = (MidiEvent) obj;
                if (ev.getMessage() instanceof MetaMessage meta && meta.getType() == 0x06) {
                    String markerName = new String(meta.getData(), StandardCharsets.US_ASCII).trim();
                    long bar = (ev.getTick() / (targetResolution * 4L)) + 1;
                    logger.info("Marker '{}' staat op tick {} (maat ~{})", markerName, ev.getTick(), bar);
                }
            }

// STAP E: Schrijf alles naar mainTrack
            for (Object obj : combinedEvents) {
                mainTrack.add((MidiEvent) obj);
            }
            MidiSystem.write(targetSeq, 0, out);
            return out.toByteArray();
        }
    }
    private void transposeAndNormalizeSections(List events, int resolution, int globalTransposeToC)
            throws InvalidMidiDataException {
        int numerator = 4;
        int denominator = 4;
        long maxTick = 0;
        List fixedSections = new ArrayList();
        long currentFixedStart = -1;
        for (Object obj : events) {
            MidiEvent ev = (MidiEvent) obj;
            if (ev.getTick() > maxTick) {
                maxTick = ev.getTick();
            }
            if (ev.getMessage() instanceof MetaMessage meta) {
                if (meta.getType() == 0x58) {
                    byte[] d = meta.getData();
                    if (d.length >= 2) {
                        numerator = Byte.toUnsignedInt(Array.getByte(d, 0));
                        denominator = 1 << Byte.toUnsignedInt(Array.getByte(d, 1));
                    }
                } else if (meta.getType() == 0x06) {
                    String marker = new String(meta.getData(), StandardCharsets.US_ASCII).trim();
                    if ("SInt".equals(marker) || "SFF1".equals(marker) || "SFF2".equals(marker)) {
                        continue;
                    }
                    boolean isIntroOrEnding = marker.startsWith("Intro") || marker.startsWith("Ending");
                    if (isIntroOrEnding) {
                        if (currentFixedStart == -1) {
                            currentFixedStart = ev.getTick();
                        }
                    } else {
                        if (currentFixedStart != -1) {
                            fixedSections.add(new SectionRange(currentFixedStart, ev.getTick()));
                            currentFixedStart = -1;
                        }
                    }
                }
            }
        }
        if (currentFixedStart != -1) {
            fixedSections.add(new SectionRange(currentFixedStart, maxTick + 1));
        }
        long ticksPerBar = (long) resolution * 4 * numerator / denominator;
        if (ticksPerBar <= 0) {
            ticksPerBar = resolution * 4L;
        }
        int totalBars = (int) (maxTick / ticksPerBar) + 1;
        List pitchClassCounts = new ArrayList(totalBars);
        for (int b = 0; b < totalBars; b++) {
            pitchClassCounts.add(Array.newInstance(int.class, 12));
        }
        int[] lowestNotePerBar = (int[]) Array.newInstance(int.class, totalBars);
        int[] lowestPitchClassPerBar = (int[]) Array.newInstance(int.class, totalBars);
        Arrays.fill(lowestNotePerBar, 128);
        Arrays.fill(lowestPitchClassPerBar, -1);
        for (Object obj : events) {
            MidiEvent ev = (MidiEvent) obj;
            if (ev.getMessage() instanceof ShortMessage sm
                    && sm.getCommand() == ShortMessage.NOTE_ON
                    && sm.getData2() > 0) {
                int ch = sm.getChannel();
                if (ch == CH_9 || ch == CH_10 || isTickInFixedSection(ev.getTick(), fixedSections)) {
                    continue;
                }
                int bar = (int) (ev.getTick() / ticksPerBar);
                int note = sm.getData1();
                int pc = note % 12;
                int weight = (ch == CH_11) ? 4 : 1;
                int[] barCounts = (int[]) pitchClassCounts.get(bar);
                Array.setInt(barCounts, pc, Array.getInt(barCounts, pc) + weight);
                if (note < Array.getInt(lowestNotePerBar, bar)) {
                    Array.setInt(lowestNotePerBar, bar, note);
                    Array.setInt(lowestPitchClassPerBar, bar, pc);
                }
            }
        }
        int[] barRoot = (int[]) Array.newInstance(int.class, totalBars);
        boolean[] barIsMinor = (boolean[]) Array.newInstance(boolean.class, totalBars);
        int lastKnownRoot = 0;
        boolean lastKnownMinor = false;
        for (int b = 0; b < totalBars; b++) {
            int bestRoot = -1;
            int bestScore = -1;
            boolean bestMinor = false;
            int[] barCounts = (int[]) pitchClassCounts.get(b);
            for (int candidateRoot = 0; candidateRoot < 12; candidateRoot++) {
                int rootHits = Array.getInt(barCounts, candidateRoot);
                int fifthHits = Array.getInt(barCounts, (candidateRoot + 7) % 12);
                int majThirdHits = Array.getInt(barCounts, (candidateRoot + 4) % 12);
                int minThirdHits = Array.getInt(barCounts, (candidateRoot + 3) % 12);
                if (rootHits == 0 && fifthHits == 0 && majThirdHits == 0 && minThirdHits == 0) {
                    continue;
                }
                boolean isMin = minThirdHits > majThirdHits;
                int thirdHits = isMin ? minThirdHits : majThirdHits;
                int bassBonus = (Array.getInt(lowestPitchClassPerBar, b) == candidateRoot) ? 5 : 0;
                int score = (rootHits * 3) + (fifthHits * 2) + (thirdHits * 2) + bassBonus;
                if (score > bestScore) {
                    bestScore = score;
                    bestRoot = candidateRoot;
                    bestMinor = isMin;
                }
            }
            if (bestRoot != -1) {
                Array.setInt(barRoot, b, bestRoot);
                Array.setBoolean(barIsMinor, b, bestMinor);
                lastKnownRoot = bestRoot;
                lastKnownMinor = bestMinor;
            } else {
                Array.setInt(barRoot, b, lastKnownRoot);
                Array.setBoolean(barIsMinor, b, lastKnownMinor);
            }
        }
        for (int i = 0; i < events.size(); i++) {
            MidiEvent ev = (MidiEvent) events.get(i);
            if (ev.getMessage() instanceof ShortMessage sm) {
                int ch = sm.getChannel();
                int cmd = sm.getCommand();
                boolean isNote = (cmd == ShortMessage.NOTE_ON
                        || cmd == ShortMessage.NOTE_OFF
                        || cmd == ShortMessage.POLY_PRESSURE);
                if (!isNote || ch == CH_9 || ch == CH_10) {
                    continue;
                }
                int origNote = sm.getData1();
                int newNote;
                if (isTickInFixedSection(ev.getTick(), fixedSections)) {
                    newNote = Math.max(0, Math.min(127, origNote + globalTransposeToC));
                } else {
                    int bar = (int) (ev.getTick() / ticksPerBar);
                    int root = Array.getInt(barRoot, bar);
                    boolean isMinor = Array.getBoolean(barIsMinor, bar);
                    int shift = -root;
                    if (shift < -5) {
                        shift += 12;
                    }
                    int intervalFromRoot = Math.floorMod(origNote - root, 12);
                    int minorToMajorAdjustment = (isMinor && intervalFromRoot == 3) ? 1 : 0;
                    newNote = Math.max(0, Math.min(127, origNote + shift + minorToMajorAdjustment));
                }
                ShortMessage newMsg = new ShortMessage(cmd, ch, newNote, sm.getData2());
                events.set(i, new MidiEvent(newMsg, ev.getTick()));
            }
        }
    }
    private boolean isTickInFixedSection(long tick, List fixedSections) {
        for (Object obj : fixedSections) {
            SectionRange r = (SectionRange) obj;
            if (tick >= r.startTick && tick < r.endTick) {
                return true;
            }
        }
        return false;
    }
    private byte[] rebuildCasmWithAllChannelsAndCMajor(byte[] nonMidiBytes) throws IOException {
        int casmPos = indexOfAscii(nonMidiBytes, "CASM", 0);
        if (casmPos == -1 || casmPos + 8 > nonMidiBytes.length) {
            return nonMidiBytes;
        }
        int oldCasmLen = ByteBuffer.wrap(nonMidiBytes, casmPos + 4, 4).getInt();
        int casmDataStart = casmPos + 8;
        int casmDataEnd = Math.min(nonMidiBytes.length, casmDataStart + oldCasmLen);
        List csegBlocks = new ArrayList();
        int cursor = casmDataStart;
        while (cursor + 8 <= casmDataEnd) {
            String tag = new String(nonMidiBytes, cursor, 4, StandardCharsets.US_ASCII);
            int len = ByteBuffer.wrap(nonMidiBytes, cursor + 4, 4).getInt();
            if ("Cseg".equals(tag) && len >= 0 && cursor + 8 + len <= casmDataEnd) {
                csegBlocks.add(Arrays.copyOfRange(nonMidiBytes, cursor, cursor + 8 + len));
                cursor += 8 + len;
            } else {
                break;
            }
        }
        if (csegBlocks.isEmpty()) {
            return nonMidiBytes;
        }
        Map masterChannelChunks = new LinkedHashMap();
        for (Object obj : csegBlocks) {
            extractAndNormalizeChannelChunks((byte[]) obj, masterChannelChunks, false);
        }
// Als CH_16 (index 15 = PHR2) in geen enkel Cseg-blok bestond, maak hem dan aan op basis van een bestaand kanaal
        if (!masterChannelChunks.containsKey(CH_16) && !masterChannelChunks.isEmpty()) {
            byte[] template = (byte[]) masterChannelChunks.values().iterator().next();
            byte[] phr2Chunk = template.clone();
            String subTag = new String(phr2Chunk, 0, 4, StandardCharsets.US_ASCII);
            Array.setByte(phr2Chunk, 8, (byte) CH_16);  // Source Channel = 15 (CH 16)
            byte[] phr2Name = "PHR2    ".getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(phr2Name, 0, phr2Chunk, 9, 8);
            Array.setByte(phr2Chunk, 17, (byte) CH_16); // Dest Channel = 15 (CH 16)
            normalizeChannelSubChunk(subTag, phr2Chunk);
            masterChannelChunks.put(CH_16, phr2Chunk);
        }
        ByteArrayOutputStream newCasmPayload = new ByteArrayOutputStream();
        for (Object obj : csegBlocks) {
            byte[] rebuiltCseg = rebuildSingleCseg((byte[]) obj, masterChannelChunks);
            newCasmPayload.write(rebuiltCseg);
        }
        byte[] newCasmBody = newCasmPayload.toByteArray();
        ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
        finalOut.write(nonMidiBytes, 0, casmPos);
        finalOut.write("CASM".getBytes(StandardCharsets.US_ASCII));
        finalOut.write(ByteBuffer.allocate(4).putInt(newCasmBody.length).array());
        finalOut.write(newCasmBody);
        if (casmDataEnd < nonMidiBytes.length) {
            finalOut.write(nonMidiBytes, casmDataEnd, nonMidiBytes.length - casmDataEnd);
        }
        return finalOut.toByteArray();
    }
    private void extractAndNormalizeChannelChunks(byte[] cseg, Map targetMap, boolean overwrite) {
        int pos = 8;
        while (pos + 8 <= cseg.length) {
            String subTag = new String(cseg, pos, 4, StandardCharsets.US_ASCII);
            int subLen = ByteBuffer.wrap(cseg, pos + 4, 4).getInt();
            if (subLen < 0 || pos + 8 + subLen > cseg.length) {
                break;
            }
            if (("Ctab".equals(subTag) || "Cntt".equals(subTag)) && subLen >= 20) {
                byte[] chunkCopy = Arrays.copyOfRange(cseg, pos, pos + 8 + subLen);
                int srcCh = Byte.toUnsignedInt(Array.getByte(chunkCopy, 8));
                normalizeChannelSubChunk(subTag, chunkCopy);
                if (overwrite || !targetMap.containsKey(srcCh)) {
                    targetMap.put(srcCh, chunkCopy);
                }
            }
            pos += 8 + subLen;
        }
    }
    private byte[] rebuildSingleCseg(byte[] cseg, Map masterChannelChunks) throws IOException {
        ByteArrayOutputStream csegBody = new ByteArrayOutputStream();
        Set presentChannels = new HashSet();
        int pos = 8;
        while (pos + 8 <= cseg.length) {
            String subTag = new String(cseg, pos, 4, StandardCharsets.US_ASCII);
            int subLen = ByteBuffer.wrap(cseg, pos + 4, 4).getInt();
            if (subLen < 0 || pos + 8 + subLen > cseg.length) {
                break;
            }
            byte[] subChunk = Arrays.copyOfRange(cseg, pos, pos + 8 + subLen);
            if (("Ctab".equals(subTag) || "Cntt".equals(subTag)) && subLen >= 20) {
                int srcCh = Byte.toUnsignedInt(Array.getByte(subChunk, 8));
                presentChannels.add(srcCh);
                normalizeChannelSubChunk(subTag, subChunk);
            }
            csegBody.write(subChunk);
            pos += 8 + subLen;
        }
        for (Object entryObj : masterChannelChunks.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObj;
            if (!presentChannels.contains(entry.getKey())) {
                csegBody.write((byte[]) entry.getValue());
            }
        }
        byte[] bodyBytes = csegBody.toByteArray();
        ByteArrayOutputStream rebuilt = new ByteArrayOutputStream();
        rebuilt.write("Cseg".getBytes(StandardCharsets.US_ASCII));
        rebuilt.write(ByteBuffer.allocate(4).putInt(bodyBytes.length).array());
        rebuilt.write(bodyBytes);
        return rebuilt.toByteArray();
    }
    private void normalizeChannelSubChunk(String subTag, byte[] subChunk) {
        if ("Ctab".equals(subTag) && subChunk.length >= 25) {
            Arrays.fill(subChunk, 19, 23, (byte) 0xFF);
            Array.setByte(subChunk, 23, (byte) 0x00);
            Array.setByte(subChunk, 24, (byte) 0x00);
        } else if ("Cntt".equals(subTag) && subChunk.length >= 27) {
            Arrays.fill(subChunk, 19, 25, (byte) 0xFF);
            Array.setByte(subChunk, 25, (byte) 0x00);
            Array.setByte(subChunk, 26, (byte) 0x00);
        }
    }
    private int indexOfAscii(byte[] data, String target, int fromIndex) {
        byte[] targetBytes = target.getBytes(StandardCharsets.US_ASCII);
        outer:
        for (int i = fromIndex; i <= data.length - targetBytes.length; i++) {
            for (int j = 0; j < targetBytes.length; j++) {
                if (Array.getByte(data, i + j) != Array.getByte(targetBytes, j)) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
    private int calculateSemitonesToC(Sequence seq) {
        for (Track track : seq.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                if (track.get(i).getMessage() instanceof MetaMessage meta && meta.getType() == 0x59) {
                    byte[] data = meta.getData();
                    if (data.length >= 2) {
                        int sharpsOrFlats = Array.getByte(data, KEY_SIG_SHARPS_FLATS_IDX);
                        int isMinor = Array.getByte(data, KEY_SIG_MINOR_IDX);
                        int majorRoot = Math.floorMod(sharpsOrFlats * 7, 12);
                        int root = (isMinor == 1) ? Math.floorMod(majorRoot - 3, 12) : majorRoot;
                        if (root == 0) {
                            return 0;
                        }
                        int shift = -root;
                        if (shift < -5) {
                            shift += 12;
                        }
                        return shift;
                    }
                }
            }
        }
        return 0;
    }
    private SplitResult splitMidiAndNonMidi(byte[] fileBytes) {
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
    private record SectionRange(long startTick, long endTick) {}
    private record SplitResult(byte[] midiBytes, byte[] nonMidiBytes) {}
}