package util.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import entity.sysex.Sysex;
import scenes.sysex.container.SysexContainer;

import java.util.ArrayList;
import java.util.List;

public class StringToSysexConverter extends StdConverter<String, Sysex> {

    @Override
    public Sysex convert(String str) {
        if (str == null) {
            return null;
        }

        // Convert each name to a Sysex object (they are loaded in the SysexContainer)
        return SysexContainer.getInstance().getSysexByName(str);
    }
}