package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.PermissionDTO;

// Permission
@Getter
@Setter
public class Permission {
    // Core Attributes
    private int id;
    private String name;
    private String description;
    private boolean archived;

    // Advanced Attributes
    private int metadataCount;
    private List<Metadata> metadataList;
    private int roleCount;
    private List<String> roleExtendedNameList;

    // Constructor
    public Permission(PermissionDTO permDTO) {
        if(permDTO != null) {
            this.id = permDTO.getId();
            this.name = permDTO.getName();
            this.description = permDTO.getDescription();
            this.archived = permDTO.isArchived();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();

            // Set Roles List
            roleCount = 0;
            roleExtendedNameList = new ArrayList<>();
        }
    }
}
