package jgr.iam.controller;

// External Libraries
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.ArrayList;

// External Libraries (Static)
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

// Internal Libraries
import jgr.iam.util.ExceptionHandlerTestUtil;
import jgr.iam.payload.request.EntityPatchRequest;
import jgr.iam.payload.request.metadata.MetadataCreateRequest;
import jgr.iam.payload.request.permission.PermissionCreateRequest;
import jgr.iam.payload.response.EntityDeleteResponse;
import jgr.iam.payload.response.EntityPatchResponse;
import jgr.iam.constant.data.AdvancedAppDataTestConstant;
import jgr.iam.constant.iamServiceResponseCode;
import jgr.iam.model.dto.PermissionDTO;
import jgr.iam.payload.response.metadata.MetadataValue;
import jgr.iam.payload.response.permission.*;
import jgr.iam.model.bo.Permission;
import jgr.iam.service.impl.PermissionService;

// Permission Controller Test
public class PermissionControllerTest {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionControllerTest.class.getCanonicalName());

    // MockMVC
    private MockMvc mockMvc;

    @Mock
    private PermissionService permService;

    @InjectMocks
    private PermissionController permissionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(permissionController).build();
    }

    // As an Admin, I want to see all Non-archived Permissions (Summary View)
    // As an Admin, I want to see all Archived Permissions (Summary View)
    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionSummaryList() {
        // Create Expected Response
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(new PermissionDTO(1, AdvancedAppDataTestConstant.PERM1_NAME, AdvancedAppDataTestConstant.PERM1_DESCRIPTION, false)));
        PermissionSummaryListResponse response = new PermissionSummaryListResponse(perms, HttpStatus.OK, iamServiceResponseCode.CONTENT_FOUND);
        // Set Expected Response
        when(permService.getPermissionSummaryList(false)).thenReturn(response);
        // Check getPermissionSummaryList
        ResponseEntity<PermissionSummaryListResponse> result = permissionController.getPermissionSummaryList(false);
        // Check Result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, result.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getBody().getResponse());
        assertEquals(1, result.getBody().getCount());
        try {
            // Items should be a List<PermissionSummaryValue>
            List<PermissionSummaryValue> values = (List<PermissionSummaryValue>) result.getBody().getItems();
            List<String> roles = null;
            List<MetadataValue> metas = null;
            Object object = result.getBody().getItems();
            // Permission 0
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, values.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, values.get(0).getDescription());
            assertEquals(false, values.get(0).isArchived());
            assertEquals(0, values.get(0).getRoleCount());
            assertEquals(0, values.get(0).getMetadataCount());
        } catch (Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetPermissionSummaryList", e);
            fail("smokeTestGetPermissionSummaryList: " + e.getClass().getCanonicalName() + ".");
        }
    }

    @Test
    public void negativeTestGetPermissionSummaryList() {
        doReturn(null).when(permService).getPermissionSummaryList(anyBoolean());
        // Call
        ResponseEntity<PermissionSummaryListResponse> result = permissionController.getPermissionSummaryList(false);
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestCreatePermission() {
        doReturn(null).when(permService).createPermission(anyString(), anyString());
        // Call
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setName("Test");
        request.setDescription("Test Description");
        ResponseEntity<PermissionSummaryResponse> result = permissionController.createPermission(request);
        // Check Result
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    // As an Admin, I want to see all Non-Archived Permissions (Detail View)
    // As an Admin, I want to see all Archived Permissions (Detail View)
    @Test
    @SuppressWarnings("unchecked")
    public void smokeTestGetPermissionDetailList() {
        // Create Expected Response
        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(new PermissionDTO(1, AdvancedAppDataTestConstant.PERM1_NAME, AdvancedAppDataTestConstant.PERM1_DESCRIPTION, false)));
        PermissionDetailListResponse response = new PermissionDetailListResponse(perms, HttpStatus.OK, iamServiceResponseCode.CONTENT_FOUND);
        // Set Expected Response
        when(permService.getPermissionDetailList(false)).thenReturn(response);
        // Check getPermissionSummaryList
        ResponseEntity<PermissionDetailListResponse> result = permissionController.getPermissionDetailList(false);
        // Check Result
        assertEquals(HttpStatus.OK, result.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, result.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, result.getBody().getResponse());
        assertEquals(1, result.getBody().getCount());
        try {
            // Items should be a List<PermissionDetailValue>
            List<PermissionDetailValue> values = (List<PermissionDetailValue>) result.getBody().getItems();
            List<String> roles = null;
            List<MetadataValue> metas = null;
            Object object = result.getBody().getItems();
            // Permission 0
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, values.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, values.get(0).getDescription());
            assertEquals(false, values.get(0).isArchived());
            assertEquals(0, values.get(0).getRoleCount());
            assertEquals(0, values.get(0).getMetadataCount());
        } catch (Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetPermissionDetailList", e);
            fail("smokeTestGetPermissionDetailList: " + e.getClass().getCanonicalName() + ".");
        }
    }

    @Test
    public void negativeTestGetPermissionDetailList() {
        doReturn(null).when(permService).getPermissionDetailList(anyBoolean());
        // Call
        ResponseEntity<PermissionDetailListResponse> result = permissionController.getPermissionDetailList(false);
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestGetPermissionSummary() {
        doReturn(null).when(permService).getPermissionSummary(anyString());
        // Call
        ResponseEntity<PermissionSummaryResponse> result = permissionController.getPermissionSummary("1");
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestDeletePermission() {
        doReturn(null).when(permService).deletePermission(anyString());
        // Call
        ResponseEntity<EntityDeleteResponse> result = permissionController.deletePermission("1");
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestUpdatePermission() {
        doReturn(null).when(permService).updatePermission(anyString(), any());
        // Call
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest("test", "value"));
        ResponseEntity<EntityPatchResponse> result = permissionController.updatePermission("1", requests);
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestCreateMetadata() {
        doReturn(null).when(permService).createMetadata(anyString(), anyString(), anyString());
        // Call
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setName("Name");
        request.setValue("Value");
        ResponseEntity<PermissionSummaryResponse> result = permissionController.createMetadata("1", request);
        // Check Result
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void negativeTestUpdateMetadata() {
        doReturn(null).when(permService).updateMetadata(anyString(), anyString(), any());
        // Call
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest("test", "value"));
        ResponseEntity<EntityPatchResponse> result = permissionController.updateMetadata("1", "name", requests);
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestDeleteMetadata() {
        doReturn(null).when(permService).deleteMetadata(anyString(), anyString());
        // Call
        ResponseEntity<EntityDeleteResponse> result = permissionController.deleteMetadata("1", "name");
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void negativeTestGetPermissionDetail() {
        doReturn(null).when(permService).getPermissionDetail(anyString());
        // Call
        ResponseEntity<PermissionDetailResponse> result = permissionController.getPermissionDetail("1");
        // Check Result
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}