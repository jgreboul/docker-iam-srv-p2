package jgr.iam.payload.response.permission;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import jgr.iam.payload.response.metadata.MetadataValue;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

// Internal Objects
import jgr.iam.model.bo.Permission;
import jgr.iam.model.bo.Metadata;

// Permission Detail Value
@Getter
public class PermissionDetailValue
{
    // Core Attributes
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("archived")
    private boolean archived;
    // Advanced Attributes
    @JsonProperty("role_count")
    private int roleCount;
    @JsonProperty("role_items")
    private List<String> roleNameList;
    @JsonProperty("metadata_count")
    private int metadataCount;
    @JsonProperty("metadata_items")
    private List<MetadataValue> metadataList;

    // Constructor
    public PermissionDetailValue(Permission perm) {
        id = perm.getId();
        name = perm.getName();
        description = perm.getDescription();
        archived = perm.isArchived();
        roleCount = perm.getRoleCount();
        roleNameList = perm.getRoleExtendedNameList();
        // Set Metadata List
        metadataCount = perm.getMetadataCount();
        metadataList = new ArrayList<>();
        for(Metadata meta: perm.getMetadataList()) {
            metadataList.add(new MetadataValue(meta));
        }
    }
}