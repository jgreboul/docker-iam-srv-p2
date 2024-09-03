package jgr.iam.payload.response.metadata;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

// Internal Objects
import jgr.iam.model.bo.Metadata;

// Metadata Value
@Getter
public class MetadataValue {
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
    @JsonProperty("archived")
    private boolean archived;

    // Constructor
    public MetadataValue(Metadata value) {
        this.name = value.getName();
        this.value = value.getValue();
        this.archived = value.isArchived();
    }
}
