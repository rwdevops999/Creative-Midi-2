package util.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import entity.sysex.Sysex;

import java.util.List;

public class SysexToStringConverter extends StdConverter<Sysex, String> {
    @Override
    public String convert(Sysex value) {
        if (value == null) {
            return null;
        }

        return value.getName();
    }
}
