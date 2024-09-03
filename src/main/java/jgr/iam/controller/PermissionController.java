package jgr.iam.controller;

// External Objects
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Internal Objects
import jgr.iam.payload.request.EntityPatchRequest;
import jgr.iam.payload.request.metadata.MetadataCreateRequest;
import jgr.iam.payload.request.permission.PermissionCreateRequest;
import jgr.iam.payload.response.EntityPatchResponse;
import jgr.iam.payload.response.EntityDeleteResponse;
import jgr.iam.payload.response.permission.PermissionDetailListResponse;
import jgr.iam.payload.response.permission.PermissionDetailResponse;
import jgr.iam.payload.response.permission.PermissionSummaryListResponse;
import jgr.iam.payload.response.permission.PermissionSummaryResponse;
import jgr.iam.service.impl.PermissionService;

// GET
// - /manage/permission: Get all non-archived permissions (summary view)
// - /manage/permission?archived=true: Get all archived permissions (summary view)
// - /manage/permission/detail: Get all non-archived permissions (detail view)
// - /manage/permission/detail?archived=true: Get all archived permissions (summary view)
// - /manage/permission/{id}: Get permission (summary view)
// - /manage/permission/{id}/detail: Get permission (detail view)

// POST
// - /manage/permission/: Create new Permission
// - /manage/permission/{id}: Create new Metadata for identified permission

// DELETE
// - /manage/permission/{id}: Delete identified Permission
// - /manage/permission/{id}/{name}: Delete named Metadata of identified Permission

// PATCH
// - /manage/permission/{id}: Update identified Permission
// - /manage/permission/{id}/{name}: Update named Metadata of identified Permission

@RestController
@RequestMapping("/manage/permission")
public class PermissionController {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionController.class.getCanonicalName());

    // Permission Management Service
    private PermissionService permService;

    // As an Admin, I want to see all Non-archived Permissions (Summary View)
    // As an Admin, I want to see all Archived Permissions (Summary View)
    @GetMapping
    public ResponseEntity<PermissionSummaryListResponse> getPermissionSummaryList(@RequestParam(required = false, defaultValue = "false") boolean archived) {
        logger.info("getPermissionSummaryList(" + archived + ").");
        if(permService == null)
            permService = new PermissionService();
        PermissionSummaryListResponse result = permService.getPermissionSummaryList(archived);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(permService.getPermissionSummaryList(archived), status);
    }

    // As an Admin, I want to create a new Permission
    @PostMapping
    public ResponseEntity<PermissionSummaryResponse> createPermission(@RequestBody PermissionCreateRequest request) {
        if(permService == null)
            permService = new PermissionService();
        if(request == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        logger.info("createPermission(" + request.toString() + ").");
        PermissionSummaryResponse result = permService.createPermission(request.getName(), request.getDescription());
        HttpStatusCode status = result == null ? HttpStatus.BAD_REQUEST : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to see all Non-Archived Permissions (Detail View)
    // As an Admin, I want to see all Archived Permissions (Detail View)
    @GetMapping("/detail")
    public ResponseEntity<PermissionDetailListResponse> getPermissionDetailList(@RequestParam(required = false, defaultValue = "false") boolean archived) {
        logger.info("getPermissionDetailList(" + archived + ").");
        if(permService == null)
            permService = new PermissionService();
        PermissionDetailListResponse result = permService.getPermissionDetailList(archived);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to get a Permission Summary Information
    @GetMapping("/{id}")
    public ResponseEntity<PermissionSummaryResponse> getPermissionSummary(@PathVariable String id) {
        logger.info("getPermissionSummary(" + id + ").");
        if(permService == null)
            permService = new PermissionService();
        PermissionSummaryResponse result = permService.getPermissionSummary(id);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to delete a Permission
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityDeleteResponse> deletePermission(@PathVariable String id) {
        logger.info("deletePermission(" + id + ").");
        if(permService == null)
            permService = new PermissionService();
        EntityDeleteResponse result  = permService.deletePermission(id);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to update a Permission basic information: description
    // As an Admin, I want to update archive or undo-Archive a Permission basic
    @PatchMapping("/{id}")
    public ResponseEntity<EntityPatchResponse> updatePermission(@PathVariable String id, @RequestBody List<EntityPatchRequest> requests) {
        logger.info("updatePermission(" + id + ", [" + requests + "]).");
        if(permService == null)
            permService = new PermissionService();
        if(requests == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        EntityPatchResponse result  = permService.updatePermission(id, requests);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to create a new Metadata for the identified Permission
    @PostMapping("/{id}")
    public ResponseEntity<PermissionSummaryResponse> createMetadata(@PathVariable String id, @RequestBody MetadataCreateRequest request) {
        if(permService == null)
            permService = new PermissionService();
        if(request == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        logger.info("createMetadata(" + id + "," + request.toString() + ").");
        PermissionSummaryResponse result = permService.createMetadata(id, request.getName(), request.getValue());
        HttpStatusCode status = result == null ? HttpStatus.BAD_REQUEST : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to update a Metadata for the identified Permission
    // As an Admin, I want to archive or undo-Archive a Metadata for the identified Permission
    @PatchMapping("/{id}/{name}")
    public ResponseEntity<EntityPatchResponse> updateMetadata(@PathVariable String id, @PathVariable String name, @RequestBody List<EntityPatchRequest> requests) {
        logger.info("updateMetadata(" + id + ", " + name + ", [" + requests + "]).");
        if(permService == null)
            permService = new PermissionService();
        EntityPatchResponse result  = permService.updateMetadata(id, name, requests);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to delete a Metadata for the identified Permission
    @DeleteMapping("/{id}/{name}")
    public ResponseEntity<EntityDeleteResponse> deleteMetadata(@PathVariable String id, @PathVariable String name) {
        logger.info("deleteMetadata(" + id + ", " + name + ").");
        if(permService == null)
            permService = new PermissionService();
        EntityDeleteResponse result  = permService.deleteMetadata(id, name);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }

    // As an Admin, I want to get a Permission Detail Information
    @GetMapping("/{id}/detail")
    public ResponseEntity<PermissionDetailResponse> getPermissionDetail(@PathVariable String id) {
        logger.info("getPermissionDetail(" + id + ").");
        if(permService == null)
            permService = new PermissionService();
        PermissionDetailResponse result  = permService.getPermissionDetail(id);
        HttpStatusCode status = result == null ? HttpStatus.INTERNAL_SERVER_ERROR : result.getStatus();
        return new ResponseEntity<>(result, status);
    }
}