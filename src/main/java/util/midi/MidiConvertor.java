package util.midi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiConvertor {
    private static final Logger logger = LoggerFactory.getLogger(MidiConvertor.class);

    public MidiConvertor() {
    }

    public Sequence convertType1ToType0(File midiFile) {
        try {
            Sequence sourceSequence = MidiSystem.getSequence(midiFile);

            MidiFileFormat format = MidiSystem.getMidiFileFormat(midiFile);
            boolean isType1 = format.getType() == 1;

            if (isType1) {
                // 1. Create a new Sequence with the same timing resolution (PPQ or SMPTE)
                Sequence targetSequence = new Sequence(sourceSequence.getDivisionType(), sourceSequence.getResolution());

                // 2. Create one single track for the Type 0 Sequence
                Track mainTrack = targetSequence.createTrack();

                // 3. Walk through all the tracks of the original file
                for (Track track : sourceSequence.getTracks()) {
                    for (int i = 0; i < track.size(); i++) {
                        MidiEvent event = track.get(i);

                        // Add each event to the master track
                        // Java's Track class sorts the events automatically based on their tick value.
                        mainTrack.add(event);
                    }
                }

                return targetSequence;
            }
        } catch (Exception e) {
            logger.error("Exception. CAUSE {}",e.getMessage());
        }

        return null;
    }

    public void convertMidiFileToType0(File inFile, File outFile) throws IOException, InvalidMidiDataException {
        Sequence originalSequence = MidiSystem.getSequence(inFile);
        Sequence outputSequence;

        outputSequence = convertToType0(originalSequence);

        int bytesWritten = MidiSystem.write(outputSequence, 0, outFile);
        if (bytesWritten == -1) {
            throw new IOException("The system architecture failed to write the requested MIDI format.");
        }
    }

    public void convertMidiFileToType1(File inFile, File outFile) throws IOException, InvalidMidiDataException {
        Sequence originalSequence = MidiSystem.getSequence(inFile);
        Sequence outputSequence;

        outputSequence = convertToType1(originalSequence);

        int bytesWritten = MidiSystem.write(outputSequence, 1, outFile);
        if (bytesWritten == -1) {
            throw new IOException("The system architecture failed to write the requested MIDI format.");
        }
    }

    public Sequence convertToType0(Sequence source) throws InvalidMidiDataException {
        Sequence type0Seq = new Sequence(source.getDivisionType(), source.getResolution());
        Track unifiedTrack = type0Seq.createTrack();

        // Iterate through all tracks and merge their events into one unified track
        for (Track track : source.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                unifiedTrack.add(event);
            }
        }
        return type0Seq;
    }

    public Sequence convertToType1(Sequence source) throws InvalidMidiDataException {
        // If the source already possesses multiple tracks, it's safe to write it as-is
        if (source.getTracks().length > 1) {
            return source;
        }

        Sequence type1Seq = new Sequence(source.getDivisionType(), source.getResolution());
        Track sourceTrack = source.getTracks()[0];

        // Track 0 in a Type 1 file traditionally stores global meta-information (Tempo, Time Sig)
        Track metaTrack = type1Seq.createTrack();

        // Create an array of 16 individual tracks assigned to each MIDI channel (0-15)
        Track[] channelTracks = new Track[16];

        for (int i = 0; i < sourceTrack.size(); i++) {
            MidiEvent event = sourceTrack.get(i);
            MidiMessage message = event.getMessage();

            if (message instanceof ShortMessage sm) {
                // Route voice/note events to their respective channel track layout
                int channel = sm.getChannel();

                if (channelTracks[channel] == null) {
                    channelTracks[channel] = type1Seq.createTrack();
                }
                channelTracks[channel].add(event);
            } else {
                // Route global meta/sysex commands to the primary master track
                metaTrack.add(event);
            }
        }
        return type1Seq;
    }

    /**
     * Start with Sequence midiInSeq = MidiSystem.getSequence(midiFile);
     * And call then Sequence midiSeq = convertType1ToType0(midiInSeq);
     *
     * @param sourceSequence the sequence containing the type 1 file
     * @return a sequence containing type 0 file
     */
    public Sequence convertType1ToType0(Sequence sourceSequence) {
        try {
            // 1. Create a new Sequence with the same timing resolution (PPQ or SMPTE)
            Sequence targetSequence = new Sequence(sourceSequence.getDivisionType(), sourceSequence.getResolution());

            // 2. Create one single track for Type 0 Sequence
            Track mainTrack = targetSequence.createTrack();

            // 3. Walk through all tracks of the original file
            for (Track track : sourceSequence.getTracks()) {
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);

                    // Add each event to the new master track.
                    // Java's Track class sorts the events automatically based on their tick value.
                    mainTrack.add(event);
                }
            }

            return targetSequence;
        } catch (Exception e) {
            logger.error("Exception. CAUSE: {}", e.getMessage());
        }

        return null;
    }
}
