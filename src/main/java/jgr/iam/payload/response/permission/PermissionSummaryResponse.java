package jgr.iam.payload.response.permission;

// External Objects
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

// Internal Objects
import jgr.iam.model.bo.Permission;
import jgr.iam.payload.response.ObjectResponse;

// Permission Summary Response
@Getter
public class PermissionSummaryResponse extends ObjectResponse {
    // Constructor
    public PermissionSummaryResponse(Permission perm, HttpStatusCode status, String response)
    {
        super(status, response);
        if(perm != null) {
            super.setItems(new PermissionSummaryValue(perm));
            super.setCount(1);
        }
    }
}
