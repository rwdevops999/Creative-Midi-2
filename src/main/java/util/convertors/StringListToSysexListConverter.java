package util.convertors;

import com.fasterxml.jackson.databind.util.StdConverter;
import entity.sysex.Sysex;

import java.util.ArrayList;
import java.util.List;

public class StringListToSysexListConverter extends StdConverter<List<String>, List<Sysex>> {

    @Override
    public List<Sysex> convert(List<String> stringList) {
        if (stringList == null) {
            return new ArrayList<>();
        }

        // Zet elke String (naam) om naar een nieuw Sysex object
        return stringList.stream()
                .map(name -> {
                    Sysex sysex = new Sysex();
                    sysex.setName(name); // Of gebruik een constructor: new Sysex(name)
                    return sysex;
                })
                .toList();
    }
}