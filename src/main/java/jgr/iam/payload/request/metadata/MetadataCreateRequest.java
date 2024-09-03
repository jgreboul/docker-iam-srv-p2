package jgr.iam.payload.request.metadata;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MetadataCreateRequest {
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
}
