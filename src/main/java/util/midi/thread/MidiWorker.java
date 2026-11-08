package util.midi.thread;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ToggleButton;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.app.AppInfo;
import util.eventhandlers.IThreadEventHandler;
import util.voice.receiver.MultiChannelMidiReceiver;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

@Setter
public class MidiWorker implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MidiWorker.class);

    public IThreadEventHandler onSucceededHandler;
    public IThreadEventHandler onInterruptedHandler;

    private static Sequencer sequencer = null;
    private static File midiFile = null;

    private ToggleButton toggle = null;

    public MidiWorker() {
    }

    public MidiWorker (MidiDevice device, MultiChannelMidiReceiver multiChannelMidiReceiver, ToggleButton btn, Long fromTick) {
        toggle = btn;
        try {
            if (!device.isOpen()) {
                device.open();
            }

            sequencer = MidiSystem.getSequencer(false);
            sequencer.open();

            Transmitter transmitter = sequencer.getTransmitter();

            // --- DE FIX: WRAP DE HARDWARE RECEIVER OM DE INTERNE BUFFER TE OMZEILEN ---
            final Receiver hardwareReceiver = device.getReceiver();
            Receiver safeReceiver = new Receiver() {
                @Override
                public void send(MidiMessage message, long timeStamp) {
                    // Stuur het bericht puur en direct door naar de hardware
                    hardwareReceiver.send(message, timeStamp);
                }

                @Override
                public void close() {
                    hardwareReceiver.close();
                }
            };

            // Koppel nu de VEILIGE wrapper aan jouw multiChannelMidiReceiver
            multiChannelMidiReceiver.setKeyboardReceiver(safeReceiver);

            // Koppel de sequencer transmitter aan jouw filter
            transmitter.setReceiver(multiChannelMidiReceiver);

            midiFile = AppInfo.INSTANCE.getMidiToPlay();
            if (midiFile != null) {
                Sequence sequence = MidiSystem.getSequence(midiFile);
                sequencer.setSequence(sequence);

                if (fromTick != null) {
                    sequencer.setTickPosition(fromTick);
                }
            }
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            logger.error("Exception. CAUSE: {}", e.getMessage());
        }
    }

    private static final Object lock = new Object();

    private final Task<String> workerTask = new Task<>() {
        @Override
        protected String call() throws Exception {
            sequencer.addMetaEventListener(meta -> {
                if (meta.getType() == 47) {
                    sequencer.stop();
                    sequencer.close();
                    if (onSucceededHandler != null) {
                        onSucceededHandler.handle(toggle);
                    }
                }
            });

            try {
                sequencer.start();
                sequencer.addMetaEventListener(new MetaEventListener() {
                    @Override
                    public void meta(MetaMessage meta) {
                        // Type 47 staat voor het einde van de MIDI-track
                        if (meta.getType() == 47) {
                            synchronized (lock) {
                                lock.notify(); // Maak de hoofdthread wakker
                            }
                        }
                    }
                });

                synchronized (lock) {
                    while (sequencer.isRunning()) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException ie) {
                logger.debug("Exception. CAUSE: {}", ie.getMessage());
                if (onInterruptedHandler != null) {
                    onInterruptedHandler.handle(toggle);
                }
            } finally {
                sequencer.close();
            }

            return null;
        }
    };

    private Thread workerThread = null;

    @Override
    public void run() {
        workerThread = new Thread(workerTask);
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void interrupt() {
        if (sequencer != null && sequencer.isRunning()) {
            sequencer.stop();
            try {
                sequencer.setSequence((Sequence) null);
            } catch (Exception e) {
                System.out.println("EXCEPTION");
            }
        }
        workerThread.interrupt();
    }

    public boolean isAlive() {
        if (workerThread != null) {
            return workerThread.isAlive();
        }

        return false;
    }

    public boolean readyToRun() {
        return (sequencer != null);
    }
}
