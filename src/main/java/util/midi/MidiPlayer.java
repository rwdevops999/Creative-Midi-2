package util.midi;

import entity.midi.Midi;
import fx.component.ChannelIndicator;
import fx.dialog.DialogFactory;
import javafx.concurrent.Worker;
import lombok.Getter;
import util.app.AppInfo;
import util.eventhandlers.IThreadEventHandler;
import util.midi.thread.MidiWorker;
import util.voice.receiver.MultiChannelMidiReceiver;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.Map;

public class MidiPlayer {
    private static MidiWorker worker;

    @Getter
    private static boolean isPlaying = false;

    public static void playMidi(IThreadEventHandler successHandler, IThreadEventHandler interruptedHandler, MultiChannelMidiReceiver multiChannelMidiReceiver) {
        worker = new MidiWorker(AppInfo.INSTANCE.getSelectedDevice().getMidiInputDevice(), multiChannelMidiReceiver);
        worker.setOnSucceededHandler(successHandler);
        worker.setOnInterruptedHandler(interruptedHandler);

        if (worker.readyToRun()) {
            worker.run();
            isPlaying = true;
        } else {
            DialogFactory.getInstance().renderErrorDialog("Device not ready !!!!");
            if (interruptedHandler != null) {
                interruptedHandler.handle();
            }
        }
    }

    public static void stopDemo(Map<Integer, ChannelIndicator> channelIndicators) {
        if (worker != null && worker.isAlive()) {
            worker.interrupt();
            isPlaying = false;
            if (channelIndicators != null) {
                for (ChannelIndicator channelIndicator : channelIndicators.values()) {
                    if (channelIndicator != null) {
                        channelIndicator.setActive(false);
                    }
                }
            }
        }
    }

    public static void testPlayer() {
        try {
            Midi midi = new Midi("Note On", 0, 0x90, 0X3C, 0x64);
            MidiWriter.getInstance().send(midi);
        } catch (InvalidMidiDataException | MidiUnavailableException e) {
            System.out.println("Exception");
        }
    }
}
