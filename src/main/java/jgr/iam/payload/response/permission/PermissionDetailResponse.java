package jgr.iam.payload.response.permission;

// External Objects
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

// Internal Objects
import jgr.iam.model.bo.Permission;
import jgr.iam.payload.response.ObjectResponse;

// Permission Detail Response
@Getter
public class PermissionDetailResponse extends ObjectResponse {

    // Constructor
    public PermissionDetailResponse(Permission perm, HttpStatusCode status, String response)
    {
        super(status, response);
        if(perm!= null) {
            super.setItems(new PermissionDetailValue(perm));
            super.setCount(1);
        }
    }
}