package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.GeneratedValue; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.GenerationType; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html
import javax.persistence.Id; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS Feature (
//    id INT AUTO_INCREMENT PRIMARY KEY,
//    name VARCHAR(100) NOT NULL,
//    description VARCHAR(255),
//    applicationId INT NOT NULL,
//    archived BOOLEAN NOT NULL DEFAULT FALSE,
//    FOREIGN KEY (applicationId) REFERENCES Application(id) ON DELETE CASCADE);
@Entity
@Getter
@Setter
public class FeatureDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private int applicationId;
    private boolean archived;

    // Constructor with known id
    public FeatureDTO(int id, String name, String description, int applicationId, boolean archived) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.applicationId = applicationId;
        this.archived = archived;
    }

    // Constructor with parameters
    public FeatureDTO(String name, String description, int applicationId) {
        this.name = name;
        this.description = description;
        this.applicationId = applicationId;
        this.archived = false;
    }
}

