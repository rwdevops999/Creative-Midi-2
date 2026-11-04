package entity.voice;

import entity.NameEntity;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import util.voice.VoiceType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Patch extends NameEntity implements Serializable {
    private static final long serialVersionUID = 1L; //

    @Getter
    private String type;

    @Getter
    @Setter
    private Integer msb;

    @Getter
    @Setter
    private Integer lsb;

    @Getter
    private transient Integer bank;

    @Getter
    @Setter
    private Integer pc;

    @Getter
    private Group parent;

    public Patch() {
    }

    public Patch(Group group, String type, String name, String msb, String lsb, String pc) {
        this();

        this.name = name;
        this.type = type;
        this.parent = group;
        this.msb = Integer.parseInt(msb);
        this.lsb = Integer.parseInt(lsb);
        this.pc = Integer.parseInt(pc);
        this.bank = (this.msb << 7) | this.lsb;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String patch) {
        this.name = patch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(name)
                .append("[")
                .append("BANK=").append(bank)
                .append(", MSB=").append(msb)
                .append(", LSB=").append(lsb)
                .append(", PC=").append(pc)
                .append("]");

        return sb.toString();
    }

    private String getParentGroups(Group group) {
        StringBuilder result = new StringBuilder();

        if (group != null) {
            result.append(getParentGroups(group.getParent())).append("/").append(group.getName());
        }

        return result.toString();
    }

    public String getPath() {
        StringBuilder sb = new StringBuilder();

        if (parent != null) {
            sb.append(getParentGroups(parent));
        }

        return sb.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        // 1. Schrijf de niet-transiente velden (zoals long serialVersionUID indien van toepassing)
        out.defaultWriteObject();

        // 2. Schrijf de objecten en de ruwe waarden uit de transiente properties weg
        out.writeObject(parent);               // Schrijft Group object
        out.writeObject(name);          // Schrijft String object
        out.writeInt(msb);               // Schrijft primitieve int (efficiënter)
        out.writeInt(lsb);               // Schrijft primitieve int
        if (pc == null) {
            out.writeInt(-1);
        } else {
            out.writeInt(pc);
        }
    }

    // Aangeroepen door ObjectInputStream achter de schermen
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // 1. Lees de standaard niet-transiente velden
        in.defaultReadObject();

        // 2. Lees exact in dezelfde volgorde de velden terug!
        this.parent = (Group) in.readObject();  // Cast terug naar Group

        this.name = (String) in.readObject();

        int rawMsb = in.readInt();
        this.msb = rawMsb;

        int rawLsb = in.readInt();
        this.lsb = rawLsb;

        int rawPc = in.readInt();
        if (rawPc == -1) {
            this.pc = null;
        } else {
            this.pc = rawPc;
        }

        // 3. Vergeet niet de bank property opnieuw te berekenen en te initialiseren!
        this.bank = (rawMsb << 7) | rawLsb;
    }}