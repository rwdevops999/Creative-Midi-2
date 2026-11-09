package util.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import entity.sysex.Sysex;

import java.util.List;

public class SysexListToStringListConverter extends StdConverter<List<Sysex>, List<String>> {
    @Override
    public List<String> convert(List<Sysex> value) {
        if (value == null) {
            return null;
        }

        return value.stream().map(Sysex::getName).toList();
    }
}
