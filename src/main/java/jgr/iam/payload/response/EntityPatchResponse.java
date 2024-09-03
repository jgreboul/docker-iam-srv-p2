package jgr.iam.payload.response;

// External Objects
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class EntityPatchResponse extends ObjectResponse {
    // Constructor
    public EntityPatchResponse(HttpStatusCode status, String response)
    {
        super(status, response);
    }
}
