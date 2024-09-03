package jgr.iam.service.impl;

// External Objects
import jgr.iam.model.bo.Metadata;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import static java.lang.Integer.parseInt;
import java.util.List;

// Internal Objects
import jgr.iam.constant.iamDBUpdatableFields;
import jgr.iam.constant.iamServiceResponseCode; // iam Service Response Code
import jgr.iam.enums.RequestType; // Request Type
import jgr.iam.manager.PermissionManager; // Permission Manager
import jgr.iam.model.bo.Permission; // Permission Business Object
import jgr.iam.payload.request.EntityPatchRequest;
import jgr.iam.payload.response.EntityDeleteResponse;
import jgr.iam.payload.response.EntityPatchResponse;
import jgr.iam.payload.response.permission.PermissionDetailListResponse;
import jgr.iam.payload.response.permission.PermissionDetailResponse;
import jgr.iam.payload.response.permission.PermissionSummaryListResponse;
import jgr.iam.payload.response.permission.PermissionSummaryResponse;
import jgr.iam.service.IPermissionService;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PermissionService implements IPermissionService {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionService.class.getCanonicalName());

    // Permission Manager
    private PermissionManager permManager;

    // Self
    @Autowired
    @Lazy
    private PermissionService it;

    // Get Permissions (Summary View)
    @Override
    public PermissionSummaryListResponse getPermissionSummaryList(boolean archived) {
        if(permManager == null)
            permManager = new PermissionManager();
        List<Permission> result = permManager.getPermissions(RequestType.SUMMARY, archived);
        String response = result == null ? iamServiceResponseCode.SOMETHING_WENT_WRONG : result.size() > 0 ? iamServiceResponseCode.CONTENT_FOUND : iamServiceResponseCode.NO_CONTENT;
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        return new PermissionSummaryListResponse(result, status, response);
    }

    // Get Permissions (Detail View)
    @Override
    public PermissionDetailListResponse getPermissionDetailList(boolean archived) {
        if(permManager == null)
            permManager = new PermissionManager();
        List<Permission> result = permManager.getPermissions(RequestType.DETAIL, archived);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.size() > 0 ? HttpStatus.OK : HttpStatus.NO_CONTENT;
        String response = result == null ? iamServiceResponseCode.SOMETHING_WENT_WRONG : result.size() > 0 ? iamServiceResponseCode.CONTENT_FOUND : iamServiceResponseCode.NO_CONTENT;
        return new PermissionDetailListResponse(result, status, response);
    }

    // Get Permission Summary Information
    @Override
    public PermissionSummaryResponse getPermissionSummary(String id) {
        Permission result = _getPermission(id, RequestType.SUMMARY);
        HttpStatusCode status = result == null ?  HttpStatus.NO_CONTENT: HttpStatus.OK;
        String response = result == null ? iamServiceResponseCode.NO_CONTENT : iamServiceResponseCode.CONTENT_FOUND;
        return new PermissionSummaryResponse(result, status, response);
    }

    // Get Permission Detail Information
    @Override
    public PermissionDetailResponse getPermissionDetail(String id) {
        Permission result = _getPermission(id, RequestType.DETAIL);
        HttpStatusCode status = result == null ?  HttpStatus.NO_CONTENT: HttpStatus.OK;
        String response = result == null ? iamServiceResponseCode.NO_CONTENT : iamServiceResponseCode.CONTENT_FOUND;
        return new PermissionDetailResponse(result, status, response);
    }

    // Get Permission
    private Permission _getPermission(String id, RequestType requestType) {
        if(permManager == null)
            permManager = new PermissionManager();
        // Check Input
        try {
            int permId  = parseInt(id);
            // Proceed
            return permManager.getPermission(permId, requestType);
        }
        catch(Exception e) {
            logger.error("_getPermission(" + id + ", " + requestType.toString() + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            return null;
        }
    }

    // Create Permission
    @Override
    public PermissionSummaryResponse createPermission(String name, String description) {
        // Check Inputs
        if ((name == null) || (description == null)) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new PermissionSummaryResponse(null, status, response);
        }

        // Proceed
        if(permManager == null)
            permManager = new PermissionManager();

        // Check if already exists
        Permission result = permManager.getPermission(name, RequestType.SUMMARY);
        if(result != null) {
            HttpStatusCode status = HttpStatus.FOUND;
            String response = iamServiceResponseCode.ALREADY_EXISTING;
            return new PermissionSummaryResponse(result, status, response);
        }

        // Create Permission
        result = permManager.createPermission(name, description);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.CREATED;
        String response = result == null ? iamServiceResponseCode.SOMETHING_WENT_WRONG : iamServiceResponseCode.CREATED;
        return new PermissionSummaryResponse(result, status, response);
    }

    // Delete Permission
    @Override
    public EntityDeleteResponse deletePermission(String id) {
        // Check Input
        if (id == null) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new EntityDeleteResponse(status, response);
        }

        // Check Permission Id
        int permId = _checkPermissionId(id);
        switch(permId)
        {
            case -1: { // Invalid
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityDeleteResponse(status, response);
            }
            case 0: { // Not Found
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityDeleteResponse(status, response);
            }
            default: // Found
        }

        // Proceed with deletion
        if(permManager.deletePermission(permId)) {
            HttpStatusCode status = HttpStatus.OK;
            String response = iamServiceResponseCode.DELETED;
            return new EntityDeleteResponse(status, response);
        }

        // Something went wrong
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        String response = iamServiceResponseCode.SOMETHING_WENT_WRONG;
        return new EntityDeleteResponse(status, response);
    }

    // Update Permission
    @Override
    public EntityPatchResponse updatePermission(String id, List<EntityPatchRequest> requests) {
        // Check Inputs
        if ((id == null) || (requests == null)) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new EntityPatchResponse(status, response);
        }

        // Check if there are any updates to make
        if (requests.isEmpty()) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.NO_UPDATE_REQUESTED;
            return new EntityPatchResponse(status, response);
        }

        // Check Permission Id
        int permId = _checkPermissionId(id);
        switch(permId)
        {
            case -1: {
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityPatchResponse(status, response);
            }
            case 0: {
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityPatchResponse(status, response);
            }
            default: // Found
        }

        // Loop through the fields to update: Permission only has 2 fields that can be updated
        boolean bResult = true;
        boolean bUnknown = false;
        // Set Response
        String response = iamServiceResponseCode.UPDATED;
        HttpStatusCode status = HttpStatus.OK;
        // Proceed
        for(EntityPatchRequest request: requests) {
            String name = request.getName();

            // Update Field
            boolean intermediaryResult = false;
            // Update Description
            if (iamDBUpdatableFields.DESCRIPTION.compareToIgnoreCase(name) == 0) {
                try {
                    intermediaryResult = permManager.updatePermissionDescription(permId, request.getValue().toString());
                }
                catch(Exception e)
                {
                    logger.error("updatePermission(" + id + ")-description: Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
                }
            }
            // Update Archived
            else if (iamDBUpdatableFields.ARCHIVED.compareToIgnoreCase(name) == 0) {
                try {
                    intermediaryResult = permManager.updatePermissionArchived(permId, ((boolean)request.getValue()));
                }
                catch(Exception e) {
                    logger.error("updatePermission(" + id + ")-archived: Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
                }
            }
            // Unknown Fields
            else {
                logger.warn("updatePermission(" + id + "): Unknown Fields: [" + name + "].");
                response = iamServiceResponseCode.UPDATED_WARNING;
                status = HttpStatus.BAD_REQUEST;
                bUnknown = true;
            }

            // Check result
            if(!intermediaryResult)
                bResult = false;
        }

        // Check result
        if(!bResult && !bUnknown)  {
            response = iamServiceResponseCode.SOMETHING_WENT_WRONG;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Return result
        return new EntityPatchResponse(status, response);
    }

    // Check Permission Identifier: return -1 (error), 0 (not found) or identifier
    private int _checkPermissionId(String id) {
        // Set Perm Server
        if(permManager == null)
            permManager = new PermissionManager();

        int permId = 0;
        try {
            // Parse Id
            permId = parseInt(id);
            // Check if Permission actually exists
            Permission result = permManager.getPermission(permId, RequestType.INTERNAL);
            if(result == null) {
                return 0;
            }
        }
        catch(Exception e) {
            logger.error("_checkPermissionId(" + id + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            return -1;
        }
        return permId;
    }

    // Create Metadata
    @Override
    public PermissionSummaryResponse createMetadata(String id, String name, String value) {
        // Check Inputs
        if ((id == null) || (name == null) || (value == null)) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new PermissionSummaryResponse(null, status, response);
        }

        // Check Permission Id
        int permId = _checkPermissionId(id);
        switch(permId)
        {
            case -1: { // Invalid
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new PermissionSummaryResponse(null, status, response);
            }
            case 0: { // Not Found
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new PermissionSummaryResponse(null, status, response);
            }
            default: // Found: we can proceed
        }

        // Check if Metadata name already exists
        switch(_checkMetadata(permId, name)) {
            case -1: { // Invalid
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new PermissionSummaryResponse(null, status, response);
            }
            case 1: { // Found
                HttpStatusCode status = HttpStatus.FOUND;
                String response = iamServiceResponseCode.ALREADY_EXISTING;
                return new PermissionSummaryResponse(permManager.getPermission(permId, RequestType.SUMMARY), status, response);
            }
            default: // Not Found: we can proceed
        }

        // Proceed with the creation
        Permission result = permManager.createMetadata(permId, name, value);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.CREATED;
        String response = result == null ? iamServiceResponseCode.SOMETHING_WENT_WRONG : iamServiceResponseCode.CREATED;
        return new PermissionSummaryResponse(result, status, response);
    }

    // Check Permission Identifier: return -1 (error), 0 (not found) or identifier
    private int _checkMetadata(int permId, String name) {
        try {
            // Check if Metadata actually exists
            Metadata result = permManager.getMetadata(permId, name);
            return result == null ? 0 : 1;
        } catch (Exception e) {
            logger.error("_checkMetadata(" + permId + ", " + name + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            return -1;
        }
    }

    // Delete Metadata
    @Override
    public EntityDeleteResponse deleteMetadata(String id, String name) {
        // Check Input
        if ((id == null) || (name == null)) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new EntityDeleteResponse(status, response);
        }

        // Check Permission Id
        int permId = _checkPermissionId(id);
        switch(permId)
        {
            case -1: { // Invalid
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityDeleteResponse(status, response);
            }
            case 0: { // Not Found
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityDeleteResponse(status, response);
            }
            default: // Found
        }

        // Check Metadata
        switch(_checkMetadata(permId, name))
        {
            case -1: { // Invalid
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityDeleteResponse(status, response);
            }
            case 0: { // Not Found
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityDeleteResponse(status, response);
            }
            default: // Found
        }

        // Proceed with deletion
        if(permManager.deleteMetadata(permId, name)) {
            HttpStatusCode status = HttpStatus.OK;
            String response = iamServiceResponseCode.DELETED;
            return new EntityDeleteResponse(status, response);
        }

        // Something went wrong
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        String response = iamServiceResponseCode.SOMETHING_WENT_WRONG;
        return new EntityDeleteResponse(status, response);
    }

    // Update Metadata
    @Override
    public EntityPatchResponse updateMetadata(String id, String name, List<EntityPatchRequest> requests) {
        // Check Inputs
        if ((id == null) || (name == null) || (requests == null)) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.BAD_REQUEST;
            return new EntityPatchResponse(status, response);
        }

        // Check if there are any updates to make
        if (requests.isEmpty()) {
            HttpStatusCode status = HttpStatus.BAD_REQUEST;
            String response = iamServiceResponseCode.NO_UPDATE_REQUESTED;
            return new EntityPatchResponse(status, response);
        }

        // Check Permission Id
        int permId = _checkPermissionId(id);
        switch(permId)
        {
            case -1: {
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityPatchResponse(status, response);
            }
            case 0: {
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityPatchResponse(status, response);
            }
            default: // Found
        }

        // Check Metadata
        switch(_checkMetadata(permId, name))
        {
            case -1: {
                HttpStatusCode status = HttpStatus.BAD_REQUEST;
                String response = iamServiceResponseCode.BAD_REQUEST;
                return new EntityPatchResponse(status, response);
            }
            case 0: {
                HttpStatusCode status = HttpStatus.NO_CONTENT;
                String response = iamServiceResponseCode.NO_CONTENT;
                return new EntityPatchResponse(status, response);
            }
            default: // Found
        }

        // Loop through the fields to update: Metadata only has 2 fields that can be updated
        boolean bResult = true;
        boolean bUnknown = false;
        // Set Response
        String response = iamServiceResponseCode.UPDATED;
        HttpStatusCode status = HttpStatus.OK;
        // Proceed
        for(EntityPatchRequest request: requests) {
            String keyname = request.getName();

            // Update Field
            boolean intermediaryResult = false;
            // Update Description
            if (iamDBUpdatableFields.VALUE.compareToIgnoreCase(keyname) == 0) {
                try {
                    intermediaryResult = permManager.updateMetadataValue(permId, name, request.getValue().toString());
                }
                catch(Exception e)
                {
                    logger.error("updateMetadata(" + id + "," + name + ")-value: Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
                }
            }
            // Update Archived
            else if (iamDBUpdatableFields.ARCHIVED.compareToIgnoreCase(keyname) == 0) {
                try {
                    intermediaryResult = permManager.updateMetadataArchived(permId, name, ((boolean)request.getValue()));
                }
                catch(Exception e) {
                    logger.error("updateMetadata(" + id + "," + name + ")-archived: Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
                }
            }
            // Unknown Fields
            else {
                logger.warn("updateMetadata(" + id + "," + name + "): Unknown Fields: [" + keyname + "].");
                response = iamServiceResponseCode.UPDATED_WARNING;
                status = HttpStatus.BAD_REQUEST;
                bUnknown = true;
            }

            // Check result
            if(!intermediaryResult)
                bResult = false;
        }

        // Check result
        if(!bResult && !bUnknown)  {
            response = iamServiceResponseCode.SOMETHING_WENT_WRONG;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Return result
        return new EntityPatchResponse(status, response);
    }
}
