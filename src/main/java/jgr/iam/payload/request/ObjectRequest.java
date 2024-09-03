package jgr.iam.payload.request;

// External Objects
import com.fasterxml.jackson.annotation.JsonProperty; // https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/com/fasterxml/jackson/annotation/JsonProperty.html
import com.fasterxml.jackson.annotation.JsonInclude; // https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/com/fasterxml/jackson/annotation/JsonProperty.html
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.NoArgsConstructor; // https://projectlombok.org/features/NoArgsConstructor
import lombok.Setter; // https://projectlombok.org/features/GetterSetter

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectRequest {
    private String name;
    @JsonProperty
    private String request;

    // Constructor
    public ObjectRequest(String name, String request) {
        this.name = name;
        this.request = request;
    }

}
