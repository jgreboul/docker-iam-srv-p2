package jgr.iam.model.bo;

// External Objects
import jgr.iam.model.dto.UserDTO;
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter

import java.util.ArrayList;
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.UserGroupDTO;

// User Group
@Getter
@Setter
public class UserGroup {
    // Core Attributes
    private int id;
    private String name;
    private String description;
    private boolean archived;

    // Advanced Attributes
    private int userCount;
    private List<User> userList;
    private int roleCount;
    private List<Role> roleList;
    private int metadataCount;
    private List<Metadata> metadataList;


    // Default Constructor
    public UserGroup(UserGroupDTO usergroupDTO) {
        if(usergroupDTO != null) {
            // Set Core Attributes
            id = usergroupDTO.getId();
            name = usergroupDTO.getName();
            description = usergroupDTO.getDescription();
            archived = usergroupDTO.isArchived();

            // Set User List
            userCount = 0;
            userList = new ArrayList<>();

            // Set Role List
            roleCount = 0;
            roleList = new ArrayList<>();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();
        }
    }
}
