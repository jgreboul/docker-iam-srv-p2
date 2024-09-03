package jgr.iam.payload.request.permission;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PermissionCreateRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
}
