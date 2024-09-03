package jgr.iam.model.dto;

// External Objects
import lombok.Data; // https://projectlombok.org/features/Data
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import javax.persistence.Entity; // https://docs.oracle.com/javaee%2F7%2Fapi%2F%2F/javax/persistence/package-summary.html

// CREATE TABLE IF NOT EXISTS Role_Permission (
//        roleId INT NOT NULL,
//        permissionId INT NOT NULL,
//        archived BOOLEAN NOT NULL DEFAULT FALSE,
//        PRIMARY KEY (roleId, permissionId),
// FOREIGN KEY (roleId) REFERENCES Role(id) ON DELETE CASCADE,
// FOREIGN KEY (permissionId) REFERENCES Permission(id) ON DELETE CASCADE);
@Entity
@Getter
@Setter
public class RolePermissionDTO {
    private int roleId;
    private int permissionId;
    private boolean archived;

    // Constructor
    public RolePermissionDTO(int roleId, int permissionId, boolean archived) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.archived = archived;
    }
}
