package jgr.iam.payload.response;

// External Objects
import com.fasterxml.jackson.annotation.JsonInclude; // https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/com/fasterxml/jackson/annotation/JsonProperty.html
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import lombok.NoArgsConstructor; // https://projectlombok.org/features/NoArgsConstructor
import com.fasterxml.jackson.annotation.JsonProperty; // https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-annotations/latest/com/fasterxml/jackson/annotation/JsonProperty.html
import org.springframework.http.HttpStatusCode; // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpStatusCode.html

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ObjectResponse {
    @JsonProperty
    private HttpStatusCode status;
    @JsonProperty
    private String response;
    @JsonProperty
    private int count;
    @JsonProperty
    private Object items;

    // Constructor
    public ObjectResponse(HttpStatusCode status, String response) {
        this.status = status;
        this.response = response;
        this.count = 0;
        this.items = null;
    }
}
