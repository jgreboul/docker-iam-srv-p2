package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.NoArgsConstructor; // https://projectlombok.org/features/constructor
import lombok.Setter; // https://projectlombok.org/features/GetterSetter

// Internal Objects
import jgr.iam.model.dto.MetadataDTO;

// Metadata
@Getter
@Setter
public class Metadata {
    private String name;
    private String value;
    private boolean archived;

    // Constructor
    public Metadata(MetadataDTO meta) {
        if (meta!= null) {
            this.name = meta.getName();
            this.value = meta.getValue();
            this.archived = meta.isArchived();
        }
    }

    // Constructor (to use only for unit test purpose)
    public Metadata(String name) {
        this.name = name;
        this.value = null;
        this.archived = false;
    }
}
