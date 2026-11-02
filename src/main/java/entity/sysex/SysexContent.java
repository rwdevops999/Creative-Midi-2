package entity.sysex;

import app.CreativeApp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;

public class SysexContent {
    private static final Logger logger = LoggerFactory.getLogger(SysexContent.class);

    @Getter
    private String sysex;

    private byte[] bytes;

    public SysexContent() {
        this.sysex = "";
        this.bytes = new byte[0];
    }

    public SysexContent(String sysex) {
        this.sysex = sysex;
        this.bytes = setBytes(sysex);
    }

    public void setSysex(String sysex) {
        this.sysex = sysex;
        this.bytes = setBytes(sysex);
    }

    public SysexContent(SysexContent content) {
        this.sysex = content.getSysex();
        this.bytes = null;
        if (content.getBytes() != null) {
            this.bytes = content.getBytes().clone();
        }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SysexContent other)) {
            return false;
        }

        // Compare the String with Objects.equals and the byte array with Arrays.equals
        return Objects.equals(this.sysex, other.sysex) &&
                Arrays.equals(this.bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        // For an array we need to use Arrays.hashCode instead of Objects.hash
        int result = Objects.hashCode(sysex);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }}
