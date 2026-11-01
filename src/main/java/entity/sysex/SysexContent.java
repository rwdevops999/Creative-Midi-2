package entity.sysex;

import app.CreativeApp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HexFormat;

public class SysexContent {
    private static final Logger logger = LoggerFactory.getLogger(SysexContent.class);

    @Getter
    private String sysex;

    private byte[] bytes;

    public SysexContent() {
    }

    public SysexContent(String sysex) {
        this.sysex = sysex;
        this.bytes = setBytes(sysex);
    }

    public void setSysex(String sysex) {
        this.sysex = sysex;
        this.bytes = setBytes(sysex);
    }

    private byte[] setBytes(String sysex) {
        if (sysex != null) {
            if (sysex.contains(" ")) {
                String cleanHex = sysex.replace(" ", "");

                // Convert to byte array
                byte[] bytes = new byte[2];
                try {
                    return HexFormat.of().parseHex(cleanHex);
                } catch (Exception e) {
                    logger.error("Exception. CAUSE {}", e.getMessage());
                }

                return bytes;
            }
        }

        return new byte[0];
    }

    @JsonIgnore
    public byte[] getBytes() {
        return bytes;
    }
}
