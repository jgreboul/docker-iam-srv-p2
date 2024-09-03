package jgr.iam.model.bo;

// External Objects
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.UserDTO;

// User
@Getter
@Setter
public class User {
    // Core Attributes
    private int id;
    private String username;
    private String email;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private String firstName;
    private String lastName;
    private boolean active = true;
    private boolean archived = false;

    // Advanced Attributes
    private int userGroupCount;
    private List<UserGroup> userGroupList;
    private int roleCount;
    private List<Role> roleList;
    private int metadataCount;
    private List<Metadata> metadataList;

    // Default Constructor
    public User(UserDTO userDTO) {
        if(userDTO != null) {
            // Set Core Attributes
            id = userDTO.getId();
            username = userDTO.getUsername();
            email = userDTO.getEmail();
            passwordHash = userDTO.getPasswordHash();
            passwordSalt = userDTO.getPasswordSalt();
            firstName = userDTO.getFirstName();
            lastName = userDTO.getLastName();
            active = userDTO.isActive();
            archived = userDTO.isArchived();

            // Set UserGroup List
            userGroupCount = 0;
            userGroupList = new ArrayList<>();

            // Set Role List
            roleCount = 0;
            roleList = new ArrayList<>();

            // Set Metadata List
            metadataCount = 0;
            metadataList = new ArrayList<>();
        }
    }
}
