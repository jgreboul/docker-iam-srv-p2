package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.NoArgsConstructor;
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS <TABLE>Metadata (
//        refid INT NOT NULL,
//        name VARCHAR(100) NOT NULL,
//        value JSON NOT NULL,
//        archived BOOLEAN NOT NULL DEFAULT FALSE,
//        PRIMARY KEY (refid, name),
//        FOREIGN KEY (refid) REFERENCES <TABLE>(id) ON DELETE CASCADE );
@Entity
@Getter
@Setter
@NoArgsConstructor
public class MetadataDTO {
    private int refid;
    private String name;
    private String value;
    private boolean archived = false;

    // Constructor with parameters
    public MetadataDTO(int refid, String name, String value, Boolean archived) {
        this.refid = refid;
        this.name = name;
        this.value = value;
        this.archived = archived;
    }
}
