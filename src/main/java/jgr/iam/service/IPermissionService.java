package jgr.iam.service;

// External Objects
import java.util.List;

// Internal Objects
import jgr.iam.payload.request.EntityPatchRequest;
import jgr.iam.payload.response.EntityDeleteResponse;
import jgr.iam.payload.response.EntityPatchResponse;
import jgr.iam.payload.response.permission.PermissionDetailListResponse;
import jgr.iam.payload.response.permission.PermissionDetailResponse;
import jgr.iam.payload.response.permission.PermissionSummaryListResponse;
import jgr.iam.payload.response.permission.PermissionSummaryResponse;


public interface IPermissionService {

    // Get Permissions (Summary View)
    PermissionSummaryListResponse getPermissionSummaryList(boolean archived);

    // Get Permissions (Detail View)
    PermissionDetailListResponse getPermissionDetailList(boolean archived);

    // Get Permission Summary Information
    PermissionSummaryResponse getPermissionSummary(String id);

    // Get Permission Detail Information
    PermissionDetailResponse getPermissionDetail(String id);

    // Create Permission
    PermissionSummaryResponse createPermission(String name, String description);

    // Delete Permission
    EntityDeleteResponse deletePermission(String id);

    // Update Permission
    EntityPatchResponse updatePermission(String id, List<EntityPatchRequest> requests);

    // Create Metadata
    PermissionSummaryResponse createMetadata(String id, String name, String value);

    // Delete Metadata
    EntityDeleteResponse deleteMetadata(String id, String name);

    // Update Metadata
    EntityPatchResponse updateMetadata(String id, String Name, List<EntityPatchRequest> requests);
}
