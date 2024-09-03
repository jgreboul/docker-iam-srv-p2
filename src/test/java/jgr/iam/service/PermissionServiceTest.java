package jgr.iam.service;

// External Objects
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// External Static Objects
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// Internal Objects
import jgr.iam.constant.dto.MetadataDTOTestConstant;
import jgr.iam.constant.iamDBUpdatableFields;
import jgr.iam.constant.iamServiceResponseCode;
import jgr.iam.model.bo.Metadata;
import jgr.iam.model.dto.MetadataDTO;
import jgr.iam.payload.request.EntityPatchRequest;
import jgr.iam.payload.response.EntityDeleteResponse;
import jgr.iam.payload.response.EntityPatchResponse;
import jgr.iam.payload.response.permission.*;
import jgr.iam.model.bo.Permission;
import jgr.iam.model.dto.PermissionDTO;
import jgr.iam.enums.RequestType;
import jgr.iam.manager.PermissionManager;
import jgr.iam.service.impl.PermissionService;
import org.springframework.http.HttpStatus;

public class PermissionServiceTest {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionServiceTest.class.getCanonicalName());

    @Mock
    private PermissionManager permissionManager;

    @InjectMocks
    private PermissionService permissionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionSummaryList() {
        List<Permission> mockPermissions = Arrays.asList(
                new Permission(new PermissionDTO(1, "Permission1", "Description1", false)),
                new Permission(new PermissionDTO(2, "Permission2", "Description2", false))
        );
        when(permissionManager.getPermissions(RequestType.SUMMARY, false)).thenReturn(mockPermissions);

        // Call
        PermissionSummaryListResponse result = permissionService.getPermissionSummaryList(false);

        // Validate
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getResponse());
        assertEquals(2, result.getCount());

        // Cast Response
        List<PermissionSummaryValue> castResult = (List<PermissionSummaryValue>)result.getItems();
        assertEquals("Permission1", castResult.get(0).getName());
        assertEquals(1, castResult.get(0).getId());
        assertEquals("Permission2", castResult.get(1).getName());
        assertEquals(2, castResult.get(1).getId());
    }

    @Test
    public void negativeTestNullResultGetPermissionSummaryList() {
        when(permissionManager.getPermissions(RequestType.SUMMARY, false)).thenReturn(null);

        // Call
        PermissionSummaryListResponse result = permissionService.getPermissionSummaryList(false);

        // Validate
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestEmptyResultGetPermissionSummaryList() {
        List<Permission> mockPermissions = new ArrayList<>();
        when(permissionManager.getPermissions(RequestType.SUMMARY, false)).thenReturn(mockPermissions);

        // Call
        PermissionSummaryListResponse result = permissionService.getPermissionSummaryList(false);

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionDetailList() {
        List<Permission> mockPermissions = Arrays.asList(
                new Permission(new PermissionDTO(1, "Permission1", "Description1", false)),
                new Permission(new PermissionDTO(2, "Permission2", "Description2", false))
        );
        when(permissionManager.getPermissions(RequestType.DETAIL, false)).thenReturn(mockPermissions);

        // Call
        PermissionDetailListResponse result = permissionService.getPermissionDetailList(false);

        // Validate
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getResponse());
        assertEquals(2, result.getCount());

        // Cast Response
        List<PermissionDetailValue> castResult = (List<PermissionDetailValue>)result.getItems();
        assertEquals("Permission1", castResult.get(0).getName());
        assertEquals(1, castResult.get(0).getId());
        assertEquals("Permission2", castResult.get(1).getName());
        assertEquals(2, castResult.get(1).getId());
    }

    @Test
    public void negativeTestNullResultGetPermissionDetailList() {
        when(permissionManager.getPermissions(RequestType.DETAIL, false)).thenReturn(null);

        // Call
        PermissionDetailListResponse result = permissionService.getPermissionDetailList(false);

        // Validate
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestEmptyResultGetPermissionDetailList() {
        List<Permission> mockPermissions = new ArrayList<>();
        when(permissionManager.getPermissions(RequestType.DETAIL, false)).thenReturn(mockPermissions);

        // Call
        PermissionDetailListResponse result = permissionService.getPermissionDetailList(false);

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionSummary() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.SUMMARY)).thenReturn(mockPermission);

        // Call
        PermissionSummaryResponse result = permissionService.getPermissionSummary("1");

        // Validate
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getResponse());
        assertEquals(1, result.getCount());

        // Cast Response
        PermissionSummaryValue castResult = (PermissionSummaryValue)result.getItems();
        assertEquals("TestPermission", castResult.getName());
    }

    @Test
    public void negativeTestNullResultGetPermissionSummary() {
        when(permissionManager.getPermission(1, RequestType.SUMMARY)).thenReturn(null);

        // Call
        PermissionSummaryResponse result = permissionService.getPermissionSummary("1");

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestExceptionGetPermissionSummary() {
        when(permissionManager.getPermission(1, RequestType.SUMMARY)).thenReturn(null);

        // Call
        PermissionSummaryResponse result = permissionService.getPermissionSummary("BLAH");

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionDetail() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.DETAIL)).thenReturn(mockPermission);

        // Call
        PermissionDetailResponse result = permissionService.getPermissionDetail("1");

        // Validate
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getResponse());
        assertEquals(1, result.getCount());

        // Cast Response
        PermissionDetailValue castResult = (PermissionDetailValue)result.getItems();
        assertEquals("TestPermission", castResult.getName());
    }

    @Test
    public void negativeTestNullResultGetPermissionDetail() {
        when(permissionManager.getPermission(1, RequestType.DETAIL)).thenReturn(null);

        // Call
        PermissionDetailResponse result = permissionService.getPermissionDetail("1");

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestExceptionGetPermissionDetail() {
        when(permissionManager.getPermission(1, RequestType.DETAIL)).thenReturn(null);

        // Call
        PermissionDetailResponse result = permissionService.getPermissionDetail("BLAH");

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestCreatePermission() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission("TestPermission", RequestType.SUMMARY)).thenReturn(null);
        when(permissionManager.createPermission("TestPermission", "Test Description")).thenReturn(mockPermission);

        // Call
        PermissionSummaryResponse result = permissionService.createPermission("TestPermission", "Test Description");

        // Validate
        assertEquals(HttpStatus.CREATED, result.getStatus());
        assertEquals(iamServiceResponseCode.CREATED, result.getResponse());
        assertEquals(1, result.getCount());

        // Cast Response
        PermissionSummaryValue castResult = (PermissionSummaryValue)result.getItems();
        assertEquals("TestPermission", castResult.getName());
        assertEquals("Test Description", castResult.getDescription());
    }

    @Test
    public void negativeTestNullInputsCreatePermission() {
        // Call with null Name
        PermissionSummaryResponse result = permissionService.createPermission(null, "Test Description");
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());

        // Call with null Description
        result = permissionService.createPermission("TestPermission", null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void negativeTestNullResultCreatePermission() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission("TestPermission", RequestType.SUMMARY)).thenReturn(mockPermission);

        // Call
        PermissionSummaryResponse result = permissionService.createPermission("TestPermission", "Test Description");

        // Validate
        assertEquals(HttpStatus.FOUND, result.getStatus());
        assertEquals(iamServiceResponseCode.ALREADY_EXISTING, result.getResponse());
        assertEquals(1, result.getCount());

        // Cast Response
        PermissionSummaryValue castResult = (PermissionSummaryValue)result.getItems();
        assertEquals("TestPermission", castResult.getName());
        assertEquals("Test Description", castResult.getDescription());
    }

    @Test
    public void negativeTestNullCreatePermission() {
        when(permissionManager.getPermission("TestPermission", RequestType.SUMMARY)).thenReturn(null);
        when(permissionManager.createPermission("TestPermission", "Test Description")).thenReturn(null);

        // Call
        PermissionSummaryResponse result = permissionService.createPermission("TestPermission", "Test Description");

        // Validate
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void smokeTestDeletePermission() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.deletePermission(1)).thenReturn(true);

        // Call
        EntityDeleteResponse result = permissionService.deletePermission("1");

        // Validate
        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(iamServiceResponseCode.DELETED, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestNullIdDeletePermission() {
        // Call
        EntityDeleteResponse result = permissionService.deletePermission(null);
        // Validate
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestInvalidIdDeletePermission() {
        // Call
        EntityDeleteResponse result = permissionService.deletePermission("BLAH");

        // Validate
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestNoContentDeletePermission() {
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(null);

        // Call
        EntityDeleteResponse result = permissionService.deletePermission("1");

        // Validate
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestInternalErrorDeletePermission() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.deletePermission(1)).thenReturn(false);

        // Call
        EntityDeleteResponse result = permissionService.deletePermission("1");

        // Validate
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestNullRequestsUpdatePermission() {
        // Call
        EntityPatchResponse result = permissionService.updatePermission("1", null);

        // Validate
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestDescriptionExceptionUpdatePermission() {
        List<EntityPatchRequest> mockRequests = Arrays.asList(
                new EntityPatchRequest("description", (Object) "Test Description Updated"));
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.updatePermissionDescription(1, "Test Description Updated")).thenThrow(new RuntimeException("Test Exception"));

        // Call
        EntityPatchResponse result = permissionService.updatePermission("1", mockRequests);

        // Validate
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
        assertEquals(null, result.getItems());
    }

    @Test
    public void negativeTestNullInputsCreateMetadata() {
        // Call with null id
        PermissionSummaryResponse result = permissionService.createMetadata(null, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());

        // Call with null name
        result = permissionService.createMetadata("1", null, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());

        // Call with null value
        result = permissionService.createMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestInvalidPermissionCreateMetadata() {
        // Call
        PermissionSummaryResponse result = permissionService.createMetadata("BLAH", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNoContentPermissionCreateMetadata() {
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(null);

        // Call
        PermissionSummaryResponse result = permissionService.createMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestInvalidMetadataCreateMetadata() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new RuntimeException("Test Exception"));

        // Call
        PermissionSummaryResponse result = permissionService.createMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestFoundMetadataCreateMetadata() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        Metadata mockMetadata = new Metadata(new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false));
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(mockMetadata);

        // Call
        PermissionSummaryResponse result = permissionService.createMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.FOUND, result.getStatus());
        assertEquals(iamServiceResponseCode.ALREADY_EXISTING, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNullResultCreateMetadata() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(null);
        when(permissionManager.createMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE)).thenReturn(null);

        // Call
        PermissionSummaryResponse result = permissionService.createMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNullInputsDeleteMetadata() {
        // Call with null id
        EntityDeleteResponse result = permissionService.deleteMetadata(null, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());

        // Call with null name
        result = permissionService.deleteMetadata("1", null);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestInvalidPermissionDeleteMetadata() {
        // Call
        EntityDeleteResponse result = permissionService.deleteMetadata("BLAH", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNoContentPermissionDeleteMetadata() {
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(null);

        // Call
        EntityDeleteResponse result = permissionService.deleteMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestExceptionMetadataDeleteMetadata() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new RuntimeException("Test Exception"));

        // Call
        EntityDeleteResponse result = permissionService.deleteMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNullResultDeleteMetadata() {
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        Metadata mockMetadata = new Metadata(new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false));
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(mockMetadata);
        when(permissionManager.deleteMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(false);

        // Call
        EntityDeleteResponse result = permissionService.deleteMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestInvalidPermissionUpdateMetadata() {
        List<EntityPatchRequest> mockRequests = Arrays.asList(
                new EntityPatchRequest(iamDBUpdatableFields.VALUE, (Object) MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        // Call
        EntityPatchResponse result = permissionService.updateMetadata("BLAH", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, mockRequests);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestNoContentPermissionUpdateMetadata() {
        List<EntityPatchRequest> mockRequests = Arrays.asList(
                new EntityPatchRequest(iamDBUpdatableFields.VALUE, (Object) MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(null);

        // Call
        EntityPatchResponse result = permissionService.updateMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, mockRequests);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestExceptionMetadataUpdateMetadata() {
        List<EntityPatchRequest> mockRequests = Arrays.asList(
                new EntityPatchRequest(iamDBUpdatableFields.VALUE, (Object) MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new RuntimeException("Test Exception"));

        // Call
        EntityPatchResponse result = permissionService.updateMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, mockRequests);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, result.getResponse());
        assertEquals(0, result.getCount());
    }

    @Test
    public void negativeTestExceptionUpdateValueUpdateMetadata() {
        List<EntityPatchRequest> mockRequests = Arrays.asList(
                new EntityPatchRequest(iamDBUpdatableFields.VALUE, (Object) MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        Permission mockPermission = new Permission(new PermissionDTO(1, "TestPermission", "Test Description", false));
        when(permissionManager.getPermission(1, RequestType.INTERNAL)).thenReturn(mockPermission);
        Metadata mockMetadata = new Metadata(new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false));
        when(permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(mockMetadata);
        when(permissionManager.updateMetadataValue(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED)).thenThrow(new RuntimeException("Test Exception"));

        // Call
        EntityPatchResponse result = permissionService.updateMetadata("1", MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, mockRequests);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, result.getResponse());
        assertEquals(0, result.getCount());
    }
}
