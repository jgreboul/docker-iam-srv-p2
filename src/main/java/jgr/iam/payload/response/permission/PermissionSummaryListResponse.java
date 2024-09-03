package jgr.iam.payload.response.permission;

// External Objects
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

// Internal Objects
import jgr.iam.model.bo.Permission;
import jgr.iam.payload.response.ObjectResponse;

// Permission Summary List Response
@Getter
public class PermissionSummaryListResponse extends ObjectResponse {

    // Constructor
    public PermissionSummaryListResponse(List<Permission> perms, HttpStatusCode status, String response)
    {
        super(status, response);
        List<PermissionSummaryValue> values = new ArrayList<>();
        // List is not null
        if(perms != null) {
            for (Permission perm : perms) {
                values.add(new PermissionSummaryValue(perm));
            }
            super.setCount(perms.size());
        }
        else {
            super.setCount(0);
        }
        super.setItems(values);
    }
}