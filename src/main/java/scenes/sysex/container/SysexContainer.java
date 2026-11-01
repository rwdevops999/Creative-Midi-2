package scenes.sysex.container;

import entity.sysex.Sysex;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SysexContainer {
    @Getter
    private static SysexContainer instance = new SysexContainer();

    @Getter
    private List<Sysex> sysexList = new ArrayList<>();

    private SysexContainer() {
    }
}
