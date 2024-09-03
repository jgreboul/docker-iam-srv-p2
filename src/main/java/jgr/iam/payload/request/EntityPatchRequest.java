package jgr.iam.payload.request;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class EntityPatchRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Object value;

    // Constructor
    public EntityPatchRequest(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
