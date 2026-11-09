package util.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import entity.sysex.Sysex;
import scenes.sysex.container.SysexContainer;

import java.util.ArrayList;
import java.util.List;

public class StringListToSysexListConverter extends StdConverter<List<String>, List<Sysex>> {

    @Override
    public List<Sysex> convert(List<String> stringList) {
        if (stringList == null) {
            return new ArrayList<>();
        }

        // Convert each name to a Sysex object (they are loaded in the SysexContainer)
        return stringList.stream()
                .map(s -> SysexContainer.getInstance().getSysexByName(s))
                .toList();
    }
}