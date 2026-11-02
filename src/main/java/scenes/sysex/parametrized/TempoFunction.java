package scenes.sysex.parametrized;

import entity.NameEntity;

import java.util.function.Function;

public class TempoFunction extends NameEntity implements Function<Integer, String> {
    public TempoFunction(String name) {
        this.name = name;
    }

    @Override
    public String apply(Integer tempo) {
        int microsecondValue = Math.round(60_000_000F / tempo);

        byte t1 = (byte)(microsecondValue & 0x7F);
        byte t2 = (byte)((microsecondValue >> 7) & 0x7F);
        byte t3 = (byte)((microsecondValue >> 14) & 0x7F);
        byte t4 = (byte)((microsecondValue >> 21) & 0x7F);

        return String.format("%02X %02X %02X %02X", t4, t3, t2, t1);
    }
}
