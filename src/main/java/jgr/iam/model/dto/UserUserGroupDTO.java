package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS User_UserGroup (
//        userId INT NOT NULL,
//        userGroupId INT NOT NULL,
//        archived BOOLEAN NOT NULL DEFAULT FALSE,
//        PRIMARY KEY (userId, userGroupId),
// FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
// FOREIGN KEY (userGroupId) REFERENCES UserGroup(id) ON DELETE CASCADE);
@Entity
@Getter
@Setter
public class UserUserGroupDTO {
    private int userId;
    private int userGroupId;
    private boolean archived;

    // Constructor
    public UserUserGroupDTO(int userId, int userGroupId, boolean archived) {
        this.userId = userId;
        this.userGroupId = userGroupId;
        this.archived = archived;
    }
}
