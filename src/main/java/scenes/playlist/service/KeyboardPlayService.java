package scenes.playlist.service;

import entity.midi.Midi;
import entity.playlist.Song;
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

    private String currentPlayingSong = "";
    private boolean songIsPlaying = false;
    private boolean preMappingsSent = false;
    /*
     * followSong is false if the song doesn't exist in the CM playlist.
     * There is no song information, so we don't need to follow it.
     */
    private boolean followSong = true;

    private List<Sysex> preMappings = new ArrayList<>();
    private List<Sysex> postMappings = new ArrayList<>();
    private Map<ByteBuffer, ByteBuffer> mappingsMap = new HashMap<>();
    private Map<ByteBuffer, String> mappingsNameMap = new HashMap<>();

    private void processSongInformation(String songName) {
        Song song = playlistContainer.getSongByName(songName);

        currentPlayingSong = "";

        if (song != null) {
            if (! songName.equals(currentPlayingSong)) {
                currentPlayingSong = songName;

                // TODO setup song information (such as mappings)...
                if (! song.getPreMappings().isEmpty()) {
                    preMappings.clear();
                    preMappingsSent = false;
                    for (Sysex sysex : song.getPreMappings()) {
                        if (sysex.getContent().getParametrized()) {
                            Function<Integer, Parameter> function = ParametrizedFunctionFactory.getInstance().getParametrizedFunction(sysex.getContent().getParametrizedFunction());
                            if (function instanceof TempoFunction tempoFunction) {
                                Parameter result = tempoFunction.apply(song.getSongInfo().getTempo());
                                sysex.getContent().setParametrized(false);
                                String tempoSysex = sysex.getContent().getSysex().replace(result.getParameterString(), result.getReplacementString());
                                sysex.getContent().setSysex(tempoSysex);

                                preMappings.add(sysex);
//                                preMappings.add(ByteBuffer.wrap(sysex.getContent().getBytes()));
                            }
                        } else {
                            preMappings.add(sysex);
//                            preMappings.add(ByteBuffer.wrap(sysex.getContent().getBytes()));
                        }
                    }
                }

                if (! song.getMappings().isEmpty()) {
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
                    postMappings.clear();
                    postMappings.addAll(song.getPostMappings());
                }

                // Execute pre-mappings
//                sendPreMappings();
            }
        } else {
            followSong = false;
        }
    }

    private void sendPreMappings () {
        // Execute pre-mappings
        try {
            for (Sysex sysex : preMappings) {
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
            for (Sysex sysex : postMappings) {
                sysexWriter.send(sysex);
            }
/*            for (ByteBuffer buffer : postMappings) {
                sysexWriter.sendBytes(buffer.array());
            } */
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            logger.error("Exception. CAUSE {}", e.getMessage());
        }
    }

    private void handleSysexMessage(SysexMessage message) {
        String hexMessage = SysexToHexStringConvertor.convertToHexString(message.getMessage());

        if (hexMessage.startsWith("F0 43 73 01 52 26")) {
            // It is a song name => use songResolver
            String songName = songResolver.resolveSongName(hexMessage);

            songIsPlaying = false;

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
            if (rawBytes[0] == START && followSong) {
                comm.showMessage(CommunicationType.INBOUND, "Start");
                songIsPlaying = true;
            } else if (rawBytes[0] == STOP && songIsPlaying) {
                songIsPlaying = false;
                comm.showMessage(CommunicationType.INBOUND, "Stop");
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
}
