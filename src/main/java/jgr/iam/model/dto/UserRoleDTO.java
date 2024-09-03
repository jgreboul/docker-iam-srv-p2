package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS User_Role (
//        userId INT NOT NULL,
//        roleId INT NOT NULL,
//        archived BOOLEAN NOT NULL DEFAULT FALSE,
//        PRIMARY KEY (userId, roleId),
// FOREIGN KEY (userId) REFERENCES User(id) ON DELETE CASCADE,
// FOREIGN KEY (roleId) REFERENCES Role(id) ON DELETE CASCADE);
@Entity
@Getter
@Setter
public class UserRoleDTO {
    private int userId;
    private int roleId;
    private boolean archived;

    // Constructor
    public UserRoleDTO(int userId, int roleId, boolean archived) {
        this.userId = userId;
        this.roleId = roleId;
        this.archived = archived;
    }
}
