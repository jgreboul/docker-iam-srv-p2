package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS UserGroup_Role (
//        userGroupId INT NOT NULL,
//        roleId INT NOT NULL,
//        archived BOOLEAN NOT NULL DEFAULT FALSE,
//        PRIMARY KEY (userGroupId, roleId),
// FOREIGN KEY (userGroupId) REFERENCES UserGroup(id) ON DELETE CASCADE,
// FOREIGN KEY (roleId) REFERENCES Role(id) ON DELETE CASCADE);
@Entity
@Getter
@Setter
public class UserGroupRoleDTO {
    private int userGroupId;
    private int roleId;
    private boolean archived;

    // Constructor
    public UserGroupRoleDTO(int userGroupId, int roleId, boolean archived) {
        this.userGroupId = userGroupId;
        this.roleId = roleId;
        this.archived = archived;
    }
}
