package entity.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterBlock {
    private boolean filterMidiOut = false;
    private boolean filterMetaOut = false;
    private boolean filterSysexOut = false;
    private int filterChannels = 0x0;
}
