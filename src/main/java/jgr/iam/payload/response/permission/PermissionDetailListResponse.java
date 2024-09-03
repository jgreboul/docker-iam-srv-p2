package jgr.iam.payload.response.permission;

// External Objects
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

// Internal Objects
import jgr.iam.model.bo.Permission;
import jgr.iam.payload.response.ObjectResponse;


// Permission Detail List Response
@Getter
public class PermissionDetailListResponse extends ObjectResponse {

    // Constructor
    public PermissionDetailListResponse(List<Permission> perms, HttpStatusCode status, String response)
    {
        super(status, response);
        List<PermissionDetailValue> values = new ArrayList<>();
        // List is not null
        if(perms != null) {
            for (Permission perm : perms) {
                values.add(new PermissionDetailValue(perm));
            }
            super.setCount(perms.size());
        }
        else {
            super.setCount(0);
        }
        super.setItems(values);
    }
}