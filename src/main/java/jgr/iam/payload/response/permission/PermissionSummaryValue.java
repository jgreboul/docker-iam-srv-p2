package jgr.iam.payload.response.permission;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// Internal Objects
import jgr.iam.model.bo.Permission;

// Permission Summary Value
@Getter
public class PermissionSummaryValue {
    // Core Attributes
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("archived")
    private boolean archived;
    @JsonProperty("role_count")
    private int roleCount;
    @JsonProperty("metadata_count")
    private int metadataCount;

    // Constructor
    public PermissionSummaryValue(Permission perm) {
        id = perm.getId();
        name = perm.getName();
        description = perm.getDescription();
        archived = perm.isArchived();
        roleCount = perm.getRoleCount();
        metadataCount = perm.getMetadataCount();
    }
}
