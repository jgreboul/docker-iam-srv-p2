package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.RoleDTO;

// Role
@Getter
@Setter
public class Role {
    // Core Attributes
    private int id;
    private String name;
    private String description;
    private boolean archived;

    // Advanced Attributes
    private int permissonCount;
    private List<Permission> permissonList;
    private int metadataCount;
    private List<Metadata> metadataList;
    private int userGroupCount;
    private List<UserGroup> userGroupList;
    private int userCount;
    private List<User> userList;

    // Default Constructor
    public Role(RoleDTO roleDTO) {
        if(roleDTO != null) {
            // Set Core Attributes
            id = roleDTO.getId();
            name = roleDTO.getName();
            description = roleDTO.getDescription();
            archived = roleDTO.isArchived();

            // Set Permission List
            permissonCount = 0;
            permissonList = new ArrayList<>();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();

            // Set UserGroup List
            userGroupCount = 0;
            userGroupList = new ArrayList<>();

            // Set User List
            userCount = 0;
            userList = new ArrayList<>();
        }
    }
}
