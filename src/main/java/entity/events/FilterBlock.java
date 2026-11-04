package entity.events;

import lombok.Getter;
import lombok.Setter;
import util.midi.EventKeyType;

import java.util.ArrayList;
import java.util.List;

public class FilterBlock {
    @Getter
    @Setter
    private boolean filterMidiOut = false;

    @Getter
    @Setter
    private boolean filterMetaOut = false;

    @Getter
    @Setter
    private boolean filterSysexOut = false;

    @Getter
    @Setter
    private int filterChannels = 0x0;

    private List<EventKeyType> midiEventsToFilter = new ArrayList<>();
    private List<EventKeyType> metaEventsToFilter = new ArrayList<>();
    private List<EventKeyType> sysexEventsToFilter = new ArrayList<>();

    public List<EventKeyType> getEventsToFilter(String type) {
        if ("midi".equals(type)) {
            return midiEventsToFilter;
        }

        if ("meta".equals(type)) {
            return metaEventsToFilter;
        }

        return sysexEventsToFilter;
    }

    public void setEventsToFilter(String type, List<EventKeyType> eventsToFilter) {
        if ("midi".equals(type)) {
            midiEventsToFilter = eventsToFilter;
        } else if ("meta".equals(type)) {
            metaEventsToFilter = eventsToFilter;
        } else {
            sysexEventsToFilter = eventsToFilter;
        }
    }
}
