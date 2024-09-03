package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.FeatureDTO;

// Feature
@Getter
@Setter
public class Feature {
    // Core Attributes
    private int id;
    private String name;
    private String description;
    private boolean archived;

    // Advanced Attributes
    private int roleCount;
    private List<Role> roleList;
    private int metadataCount;
    private List<Metadata> metadataList;

    // Default Constructor
    public Feature(FeatureDTO featDTO) {
        if(featDTO != null) {
            // Set Core Attributes
            id = featDTO.getId();
            name = featDTO.getName();
            description = featDTO.getDescription();
            archived = featDTO.isArchived();

            // Set Roles List
            roleCount = 0;
            roleList = new ArrayList<>();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();
        }
    }
}
