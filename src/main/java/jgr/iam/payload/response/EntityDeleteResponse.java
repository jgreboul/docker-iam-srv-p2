package jgr.iam.payload.response;

// External Objects
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

// Internal Objects


@Getter
public class EntityDeleteResponse extends ObjectResponse {
    // Constructor
    public EntityDeleteResponse(HttpStatusCode status, String response)
    {
        super(status, response);
    }
}
