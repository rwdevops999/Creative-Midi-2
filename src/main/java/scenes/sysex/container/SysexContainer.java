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

    public void addSysex(Sysex sysex) {
        // If we add a sysex already in the list, it replaces the sysex
        Sysex sysexInList = sysexList.stream().filter(s -> s.getName().equals(sysex.getName())).findFirst().orElse(null);
        if (sysexInList != null) {
            int index = sysexList.indexOf(sysexInList);
            sysexList.set(index, sysex);
        } else {
            sysexList.add(sysex);
        }
    }

    public void deleteSysex(Sysex sysex) {
        sysexList.remove(sysex);
    }
}
