package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.ApplicationDTO;

// Application
@Getter
@Setter
public class Application {
    // Core Attributes
    private int id;
    private String name;
    private String description;
    private boolean archived;

    // Advanced Attributes
    private int featureCount;
    private List<Feature> featureList;
    private int metadataCount;
    private List<Metadata> metadataList;

    // Default Constructor
    public Application(ApplicationDTO appDTO) {
        if(appDTO != null) {
            // Set Core Attributes
            id = appDTO.getId();
            name = appDTO.getName();
            description = appDTO.getDescription();
            archived = appDTO.isArchived();

            // Set Features List
            featureCount = 0;
            featureList = new ArrayList<>();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();
        }
    }
}
