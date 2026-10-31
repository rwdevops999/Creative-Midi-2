package util.voice;

import java.io.File;

public class InsProvider implements InstrumentProvider {
    public InsProvider(File insFile) {
    }

    public InsProvider(String filename) {
        this(new File(filename));
    }

    @Override
    public String getSourceType() {
        return "INS";
    }
}
