package jgr.iam.payload.response;

//External Objects
import org.springframework.http.HttpStatus;

// Internal Objects

// Admin Root Response
public class RootDefaultResponse extends ObjectResponse {

    // Constructor
    public RootDefaultResponse(String response) {
        super(HttpStatus.NOT_FOUND, response);
    }
}
