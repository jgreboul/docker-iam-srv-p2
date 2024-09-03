package jgr.iam.controller;

// External Objects
import jgr.iam.constant.dto.MetadataDTOTestConstant;
import jgr.iam.constant.iamDBUpdatableFields;
import jgr.iam.payload.request.metadata.MetadataCreateRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

// Internal Objects
import jgr.iam.constant.dto.PermissionDTOTestConstant;
import jgr.iam.util.ControllerTestDataUtil;
import jgr.iam.util.ExceptionHandlerTestUtil;
import jgr.iam.constant.iamServiceResponseCode;
import jgr.iam.constant.data.AdvancedAppDataTestConstant;
import jgr.iam.payload.request.*;
import jgr.iam.payload.request.permission.*;
import jgr.iam.payload.response.*;
import jgr.iam.payload.response.permission.*;
import jgr.iam.payload.response.metadata.*;

import static org.junit.jupiter.api.Assertions.*;

// AdminPermissionController Test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PermissionControllerE2ETest {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionControllerE2ETest.class.getCanonicalName());

    // Controller Test Data Utility
    private static ControllerTestDataUtil ctdUtil;

    // Admission Permission Controller
    private PermissionController adminPermController;

    // Insert Data
    @BeforeEach
    void setUp() {
        adminPermController = new PermissionController();
        ctdUtil = new ControllerTestDataUtil();
        // Setup
        ctdUtil.setUp();
        // Create Data
        ctdUtil.createTestData();
    }

    // Tear down Test Data
    @AfterEach
    void tearDown() {
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        //Delete Data
        ctdUtil.deleteTestData();
        // Disconnect
        ctdUtil.tearDown();
    }

    // getPermissionSummaryList (Archived: false)
    @Order(1)
    @SuppressWarnings("unchecked")
    @Test
    void smokeTestGetNonArchivedPermissionSummaryList() {
        ResponseEntity<PermissionSummaryListResponse> response = adminPermController.getPermissionSummaryList(false);
        // HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, response.getBody().getResponse());
        assertEquals(5, response.getBody().getCount());
        try {
            // Items should be a List<PermissionSummaryValue>
            List<PermissionSummaryValue> values = (List<PermissionSummaryValue>) response.getBody().getItems();
            Object object = response.getBody().getItems();
            // Permission 0
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, values.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, values.get(0).getDescription());
            assertEquals(false, values.get(0).isArchived());
            assertEquals(5, values.get(0).getRoleCount());
            assertEquals(1, values.get(0).getMetadataCount());
            // Permission 1
            assertEquals(AdvancedAppDataTestConstant.PERM2_NAME, values.get(1).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM2_DESCRIPTION, values.get(1).getDescription());
            assertEquals(false, values.get(1).isArchived());
            assertEquals(6, values.get(1).getRoleCount());
            assertEquals(1, values.get(1).getMetadataCount());
            // Permission 2
            assertEquals(AdvancedAppDataTestConstant.PERM3_NAME, values.get(2).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM3_DESCRIPTION, values.get(2).getDescription());
            assertEquals(false, values.get(2).isArchived());
            assertEquals(5, values.get(2).getRoleCount());
            assertEquals(1, values.get(2).getMetadataCount());
            // Permission 3
            assertEquals(AdvancedAppDataTestConstant.PERM4_NAME, values.get(3).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM4_DESCRIPTION, values.get(3).getDescription());
            assertEquals(false, values.get(3).isArchived());
            assertEquals(3, values.get(3).getRoleCount());
            assertEquals(1, values.get(3).getMetadataCount());
            // Permission 4
            assertEquals(AdvancedAppDataTestConstant.PERM5_NAME, values.get(4).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM5_DESCRIPTION, values.get(4).getDescription());
            assertEquals(false, values.get(4).isArchived());
            assertEquals(2, values.get(4).getRoleCount());
            assertEquals(1, values.get(4).getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetNonArchivedPermissionSummaryList", e);
            fail("smokeTestGetNonArchivedPermissionSummaryList: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // getPermissionDetailList (Archived: false)
    @Order(2)
    @Test
    void smokeTestGetNonArchivedPermissionDetailList() {
        ResponseEntity<PermissionDetailListResponse> response = adminPermController.getPermissionDetailList(false);
        // HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, response.getBody().getResponse());
        assertEquals(5, response.getBody().getCount());
        try {
            // Items should be a List<PermissionDetailValue>
            List<PermissionDetailValue> values = (List<PermissionDetailValue>) response.getBody().getItems();
            List<String> roles = null;
            List<MetadataValue> metas = null;
            Object object = response.getBody().getItems();
            // Permission 0
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, values.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, values.get(0).getDescription());
            assertEquals(false, values.get(0).isArchived());
            assertEquals(5, values.get(0).getRoleCount());
            // Permission 0: Roles
            roles = values.get(0).getRoleNameList();
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT1_NAME, AdvancedAppDataTestConstant.ROLE1_NAME), roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(1));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE4_NAME), roles.get(2));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE5_NAME), roles.get(3));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(4));
            assertEquals(1, values.get(0).getMetadataCount());
            // Permission 0: Meta
            metas = values.get(0).getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META5_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META5_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
            // Permission 1
            assertEquals(AdvancedAppDataTestConstant.PERM2_NAME, values.get(1).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM2_DESCRIPTION, values.get(1).getDescription());
            assertEquals(false, values.get(1).isArchived());
            assertEquals(6, values.get(1).getRoleCount());
            // Permission 1: Roles
            roles = values.get(1).getRoleNameList();
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT1_NAME, AdvancedAppDataTestConstant.ROLE1_NAME), roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT1_NAME, AdvancedAppDataTestConstant.ROLE2_NAME), roles.get(1));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(2));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE4_NAME), roles.get(3));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE5_NAME), roles.get(4));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(5));
            assertEquals(1, values.get(1).getMetadataCount());
            // Permission 1: Meta
            metas = values.get(1).getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META6_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META6_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
            // Permission 2
            assertEquals(AdvancedAppDataTestConstant.PERM3_NAME, values.get(2).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM3_DESCRIPTION, values.get(2).getDescription());
            assertEquals(false, values.get(2).isArchived());
            assertEquals(5, values.get(2).getRoleCount());
            // Permission 2: Roles
            roles = values.get(2).getRoleNameList();
            assertEquals("IAM.USER.STANDARD", roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(1));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE4_NAME), roles.get(2));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE5_NAME), roles.get(3));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(4));
            assertEquals(1, values.get(2).getMetadataCount());
            // Permission 2: Meta
            metas = values.get(2).getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META7_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META7_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
            // Permission 3
            assertEquals(AdvancedAppDataTestConstant.PERM4_NAME, values.get(3).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM4_DESCRIPTION, values.get(3).getDescription());
            assertEquals(false, values.get(3).isArchived());
            assertEquals(3, values.get(3).getRoleCount());
            // Permission 3: Roles
            roles = values.get(3).getRoleNameList();
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE5_NAME), roles.get(1));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(2));
            assertEquals(1, values.get(3).getMetadataCount());
            // Permission 3: Meta
            metas = values.get(3).getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META8_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META8_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
            // Permission 4
            assertEquals(AdvancedAppDataTestConstant.PERM5_NAME, values.get(4).getName());
            assertEquals(AdvancedAppDataTestConstant.PERM5_DESCRIPTION, values.get(4).getDescription());
            assertEquals(false, values.get(4).isArchived());
            assertEquals(2, values.get(4).getRoleCount());
            // Permission 4: Roles
            roles = values.get(4).getRoleNameList();
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(1));
            assertEquals(1, values.get(4).getMetadataCount());
            // Permission 4: Meta
            metas = values.get(4).getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META9_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META9_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetNonArchivedPermissionDetailList", e);
            fail("smokeTestGetNonArchivedPermissionDetailList: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // getPermissionSummary (smoke test)
    @Order(3)
    @Test
    void smokeTestGetPermissionSummary() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestGetPermissionSummary: Permission Not Found.");
        }
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.getPermissionSummary(String.format("%d", permId));
        // HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionSummaryValue
            PermissionSummaryValue value = (PermissionSummaryValue) response.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, value.getDescription());
            assertEquals(false, value.isArchived());
            assertEquals(5, value.getRoleCount());
            assertEquals(1, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetPermissionSummary", e);
            fail("smokeTestGetPermissionSummary: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // getPermissionSummary (negative test: NumberFormatException)
    @Order(4)
    @Test
    void negativeExceptionTestGetPermissionSummary() {
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.getPermissionSummary("BLAH");
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // getPermissionSummary (negative test: NotFound)
    @Order(5)
    @Test
    void negativeNotFoundTestGetPermissionSummary() {
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.getPermissionSummary("0");
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // getPermissionDetail (smoke test)
    @Order(6)
    @Test
    void smokeTestGetPermissionDetail() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestGetPermissionDetail: Permission Not Found.");
        }
        ResponseEntity<PermissionDetailResponse> response = adminPermController.getPermissionDetail(String.format("%d", permId));
        // HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CONTENT_FOUND, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionDetailValue
            PermissionDetailValue value = (PermissionDetailValue) response.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(AdvancedAppDataTestConstant.PERM1_DESCRIPTION, value.getDescription());
            assertEquals(false, value.isArchived());
            assertEquals(5, value.getRoleCount());
            assertEquals(1, value.getMetadataCount());
            // Roles
            List<String> roles = value.getRoleNameList();
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT1_NAME, AdvancedAppDataTestConstant.ROLE1_NAME), roles.get(0));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.FEAT2_NAME, AdvancedAppDataTestConstant.ROLE3_NAME), roles.get(1));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE4_NAME), roles.get(2));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE5_NAME), roles.get(3));
            assertEquals(String.format("%s.%s.%s", AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.FEAT3_NAME, AdvancedAppDataTestConstant.ROLE6_NAME), roles.get(4));
            assertEquals(1, value.getMetadataCount());
            // Meta
            List<MetadataValue> metas = value.getMetadataList();
            assertEquals(1, metas.size());
            assertEquals(AdvancedAppDataTestConstant.META5_NAME, metas.get(0).getName());
            assertEquals(AdvancedAppDataTestConstant.META5_VALUE, metas.get(0).getValue());
            assertEquals(false, metas.get(0).isArchived());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetPermissionDetail", e);
            fail("smokeTestGetPermissionDetail: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // getPermissionDetail (negative test: NumberFormatException)
    @Order(7)
    @Test
    void negativeExceptionTestGetPermissionDetail() {
        ResponseEntity<PermissionDetailResponse> response = adminPermController.getPermissionDetail("BLAH");
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // getPermissionDetail (negative test: NotFound)
    @Order(8)
    @Test
    void negativeNotFoundTestGetPermissionDetail() {
        ResponseEntity<PermissionDetailResponse> response = adminPermController.getPermissionDetail("0");
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // createPermission (smoke Test)
    @Order(9)
    @Test
    void smokeTestCreatePermission() {
        // Create Request
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
        request.setDescription(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createPermission(request);
        // HttpStatus
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.CREATED, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CREATED, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionSummaryValue
            PermissionSummaryValue value = (PermissionSummaryValue) response.getBody().getItems();
            assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME, value.getName());
            assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, value.getDescription());
            assertEquals(false, value.isArchived());
            assertEquals(0, value.getRoleCount());
            assertEquals(0, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestCreatePermission", e);
            fail("smokeTestCreatePermission: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // createPermission (negative Test: Already Exists)
    @Order(10)
    @Test
    void negativeTestExistingCreatePermission() {
        // Create Request
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
        request.setDescription(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createPermission(request);
        // HttpStatus
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.FOUND, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.ALREADY_EXISTING, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionSummaryValue
            PermissionSummaryValue value = (PermissionSummaryValue) response.getBody().getItems();
            assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME, value.getName());
            assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, value.getDescription());
            assertEquals(false, value.isArchived());
            assertEquals(0, value.getRoleCount());
            assertEquals(0, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestExistingCreatePermission", e);
            fail("negativeTestExistingCreatePermission: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // createPermission (negative Test: Bad Request)
    @Order(11)
    @Test
    void negativeTestBadRequestCreatePermission() {
        // Create Request
        PermissionCreateRequest request = new PermissionCreateRequest();
        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createPermission(request);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // createPermission (negative Test: Null Request)
    @Order(12)
    @Test
    void negativeNullRequestCreatePermission() {
        // Create Request
        PermissionCreateRequest request = new PermissionCreateRequest();
        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createPermission(null);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Response Body
        assertNull(response.getBody());
    }

    // deletePermission (smoke Test)
    @Order(13)
    @Test
    void smokeTestDeletePermission() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
        if(permId == -1) {
            fail("smokeTestDeletePermission: Permission Not Found.");
        }
        // Delete
        ResponseEntity<EntityDeleteResponse> response = adminPermController.deletePermission(String.format("%d",permId));
        // HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.DELETED, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // deletePermission (negative Test: null)
    @Order(14)
    @Test
    void negativeTestNullDeletePermission() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        // Delete
        ResponseEntity<EntityDeleteResponse> response = adminPermController.deletePermission(null);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // deletePermission (negative Test: no record)
    @Order(15)
    @Test
    void negativeTestNoRecordDeletePermission() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        // Delete
        ResponseEntity<EntityDeleteResponse> response = adminPermController.deletePermission("0");
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // updatePermission (smoke Test)
    @Order(16)
    @Test
    void smokeTestUpdatePermission() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestUpdatePermission: Permission Not Found.");
        }

        // Test 1: Update Description
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.DESCRIPTION, AdvancedAppDataTestConstant.PERM1_DESCRIPTION + " (updated)"));
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updatePermission(String.format("%d", permId), requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 1: Clear requests
        requests.clear();

        // Test 2: Update Archived to true
        // 2: Set Request
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, true));
        response = adminPermController.updatePermission(String.format("%d", permId), requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 2: Clear requests
        requests.clear();

        // Test 3: Update Description AND Archived to false
        // 3: Set Request
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.DESCRIPTION, AdvancedAppDataTestConstant.PERM1_DESCRIPTION));
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, false));
        response = adminPermController.updatePermission(String.format("%d", permId), requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.OK, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 3: Clear requests
        requests.clear();
    }

    // updatePermission (negative Test: null id, null requests)
    @Order(17)
    @Test
    void negativeTestNullInputsUpdatePermission() {
        // Test 1: Null Id
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updatePermission(null, requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());

        // Test 2: Null Request
        // 2: Call Update
        response = adminPermController.updatePermission("0", null);
        // 2: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 2: Response Body
        assertNull(response.getBody());
    }

    // updatePermission (negative Test: empty requests)
    @Order(18)
    @Test
    void negativeTestEmptyRequestUpdatePermission() {
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updatePermission("0", requests);
        // 3: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 3: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_UPDATE_REQUESTED, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // updatePermission (negative Test: invalid ID or no record)
    @Order(19)
    @Test
    void negativeTestInvalidIdUpdatePermission() {
        // Test 1: Invalid ID
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.DESCRIPTION, AdvancedAppDataTestConstant.PERM1_DESCRIPTION + " (updated)"));
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updatePermission("BLAH", requests);
        // 3: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 3: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());

        // Test 2: No Record Found
        // 1: Call Update
        response = adminPermController.updatePermission("1", requests);
        // 3: HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // 3: Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // updatePermission (negative Test: unknown field and other errors)
    @Order(20)
    @Test
    void negativeTestInvalidRequestsUpdatePermission() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestInvalidRequestsUpdatePermission: Permission Not Found.");
        }

        // Test 1: Update Unknown Field
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest("unknown", AdvancedAppDataTestConstant.PERM1_DESCRIPTION + " (updated)"));
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updatePermission(String.format("%d", permId), requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED_WARNING, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 1: Clear requests
        requests.clear();

        // Test 2: Update Archived to non-boolean
        // 2: Set Request
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, AdvancedAppDataTestConstant.PERM1_DESCRIPTION));
        response = adminPermController.updatePermission(String.format("%d", permId), requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 2: Clear requests
        requests.clear();
    }

    // createMetadata (smoke Test)
    @Order(21)
    @Test
    void smokeTestCreateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestCreateMetadata: Permission Not Found.");
        }

        // Create  Request
        MetadataCreateRequest metaRequest = new MetadataCreateRequest();
        metaRequest.setName(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        metaRequest.setValue(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);

        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createMetadata(String.format("%d", permId), metaRequest);

        // HttpStatus
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.CREATED, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.CREATED, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionSummaryValue
            PermissionSummaryValue value = (PermissionSummaryValue) response.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(5, value.getRoleCount());
            assertEquals(2, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestCreateMetadata-1", e);
            fail("smokeTestCreateMetadata (1): " + e.getClass().getCanonicalName() + ".");
        }

        // Get Detail Permission Response
        ResponseEntity<PermissionDetailResponse> detailResponse = adminPermController.getPermissionDetail(String.format("%d", permId));
        // HttpStatus
        assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
        // Response Body
        assertEquals(1, detailResponse.getBody().getCount());
        try {
            // Item should be a PermissionDetailValue
            PermissionDetailValue value = (PermissionDetailValue) detailResponse.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(5, value.getRoleCount());
            assertEquals(2, value.getMetadataCount());

            // Retrieve Metadata
            List<MetadataValue> metas = value.getMetadataList();
            assertEquals(2, metas.size());
            assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, metas.get(1).getName());
            assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, metas.get(1).getValue());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestCreateMetadata-2", e);
            fail("smokeTestCreateMetadata (2): " + e.getClass().getCanonicalName() + ".");
        }
    }

    // createMetadata (negative Test: Already Exists)
    @Order(22)
    @Test
    void negativeTestExistingCreateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestExistingCreateMetadata: Permission Not Found.");
        }

        // Create Metadata Request
        MetadataCreateRequest metaRequest = new MetadataCreateRequest();
        metaRequest.setName(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        metaRequest.setValue(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);

        // Call Create #1
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createMetadata(String.format("%d", permId), metaRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Call Create #2
        response = adminPermController.createMetadata(String.format("%d", permId), metaRequest);
        // HttpStatus
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.FOUND, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.ALREADY_EXISTING, response.getBody().getResponse());
        assertEquals(1, response.getBody().getCount());
        try {
            // Items should be a PermissionSummaryValue
            PermissionSummaryValue value = (PermissionSummaryValue) response.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(false, value.isArchived());
            assertEquals(5, value.getRoleCount());
            assertEquals(2, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestExistingCreateMetadata", e);
            fail("negativeTestExistingCreateMetadata: " + e.getClass().getCanonicalName() + ".");
        }
    }

    // createMetadata (negative Test: Bad Request)
    @Order(23)
    @Test
    void negativeTestBadRequestCreateMetadata() {
        // Test 1: Null inputs
        // Call Create
        ResponseEntity<PermissionSummaryResponse> response = adminPermController.createMetadata(null, null);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Test 2: Bad Request
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestBadRequestCreateMetadata: Permission Not Found.");
        }
        // Create Request
        MetadataCreateRequest request = new MetadataCreateRequest();
        // Call Create
        response = adminPermController.createMetadata(String.format("%d", permId), request);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // deleteMetadata (smoke Test)
    @Order(24)
    @Test
    void smokeTestDeleteMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestDeleteMetadata: Permission Not Found.");
        }

        // Create Metadata Request
        MetadataCreateRequest metaRequest = new MetadataCreateRequest();
        metaRequest.setName(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        metaRequest.setValue(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);

        // Call Create
        ResponseEntity<PermissionSummaryResponse> createResponse = adminPermController.createMetadata(String.format("%d", permId), metaRequest);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Call Delete
        ResponseEntity<EntityDeleteResponse> deleteResponse = adminPermController.deleteMetadata(String.format("%d",permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        // HttpStatus
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.OK, deleteResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.DELETED, deleteResponse.getBody().getResponse());
        assertEquals(0, deleteResponse.getBody().getCount());
    }

    // deleteMetadata (negative Test: null)
    @Order(25)
    @Test
    void negativeTestNullDeleteMetadata() {
        // Delete
        ResponseEntity<EntityDeleteResponse> response = adminPermController.deleteMetadata(null, null);
        // HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // deleteMetadata (negative Test: no record)
    @Order(26)
    @Test
    void negativeTestNoRecordDeleteMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestNoRecordDeleteMetadata: Permission Not Found.");
        }

        // Delete
        ResponseEntity<EntityDeleteResponse> response = adminPermController.deleteMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // updateMetadata (smoke Test)
    @Order(27)
    @Test
    void smokeTestUpdateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestUpdateMetadata: Permission Not Found.");
        }
        // Create Metadata Request
        MetadataCreateRequest metaRequest = new MetadataCreateRequest();
        metaRequest.setName(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        metaRequest.setValue(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        // Create
        ResponseEntity<PermissionSummaryResponse> createResponse = adminPermController.createMetadata(String.format("%d", permId), metaRequest);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Test 1: Update Value
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.VALUE, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        ResponseEntity<EntityPatchResponse> updateResponse = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.OK, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());
        // 1: Clear requests
        requests.clear();
        // 1: Get Detail Permission Response
        ResponseEntity<PermissionDetailResponse> detailResponse = adminPermController.getPermissionDetail(String.format("%d", permId));
        // 1: HttpStatus
        assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
        // 1: Response Body
        assertEquals(1, detailResponse.getBody().getCount());
        try {
            // Item should be a PermissionDetailValue
            PermissionDetailValue value = (PermissionDetailValue) detailResponse.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(5, value.getRoleCount());
            assertEquals(2, value.getMetadataCount());

            // Retrieve Metadata
            List<MetadataValue> metas = value.getMetadataList();
            assertEquals(2, metas.size());
            assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED, metas.get(1).getValue());
            assertEquals(false, metas.get(1).isArchived());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdateMetadata-1", e);
            fail("smokeTestUpdateMetadata (1): " + e.getClass().getCanonicalName() + ".");
        }

        // Test 2: Update Archived to true
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, true));
        updateResponse = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.OK, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());
        // 2: Clear requests
        requests.clear();
        // 2: Get Detail Permission Response
        detailResponse = adminPermController.getPermissionDetail(String.format("%d", permId));
        // 2: HttpStatus
        assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
        // 2: Response Body
        assertEquals(1, detailResponse.getBody().getCount());
        try {
            // Item should be a PermissionDetailValue
            PermissionDetailValue value = (PermissionDetailValue) detailResponse.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(5, value.getRoleCount());
            assertEquals(1, value.getMetadataCount());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdateMetadata-2", e);
            fail("smokeTestUpdateMetadata (2): " + e.getClass().getCanonicalName() + ".");
        }

        // Test 3: Update Value AND Archived to false
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.VALUE, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE));
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, false));
        updateResponse = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 3: HttpStatus
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        // 3: Response Body
        assertEquals(HttpStatus.OK, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());
        // 3: Get Detail Permission Response
        detailResponse = adminPermController.getPermissionDetail(String.format("%d", permId));
        // 3: HttpStatus
        assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
        // 3: Response Body
        assertEquals(1, detailResponse.getBody().getCount());
        try {
            // Item should be a PermissionDetailValue
            PermissionDetailValue value = (PermissionDetailValue) detailResponse.getBody().getItems();
            assertEquals(AdvancedAppDataTestConstant.PERM1_NAME, value.getName());
            assertEquals(5, value.getRoleCount());
            assertEquals(2, value.getMetadataCount());

            // Retrieve Metadata
            List<MetadataValue> metas = value.getMetadataList();
            assertEquals(2, metas.size());
            assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, metas.get(1).getValue());
            assertEquals(false, metas.get(1).isArchived());
        }
        catch(Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdateMetadata-3", e);
            fail("smokeTestUpdateMetadata (3): " + e.getClass().getCanonicalName() + ".");
        }
    }

    // updateMetadata (negative Test: null id, null requests)
    @Order(28)
    @Test
    void negativeTestNullInputsUpdateMetadata() {
        // Test 1: Null Id
        List<EntityPatchRequest> requests = new ArrayList<>();
        ResponseEntity<EntityPatchResponse> updateResponse = adminPermController.updateMetadata(null, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());

        // Test 2: Null Name
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("smokeTestUpdateMetadata: Permission Not Found.");
        }
        updateResponse = adminPermController.updateMetadata(String.format("%d", permId), null, requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());

        // Test 3: Null Request
        updateResponse = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, null);
        // 3: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        // 3: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.BAD_REQUEST, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());
    }

    // updateMetadata (negative Test: empty requests)
    @Order(29)
    @Test
    void negativeTestEmptyRequestUpdateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestEmptyRequestUpdateMetadata: Permission Not Found.");
        }

        // Create Update Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        ResponseEntity<EntityPatchResponse>  updateResponse = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_UPDATE_REQUESTED, updateResponse.getBody().getResponse());
        assertEquals(0, updateResponse.getBody().getCount());
    }

    // updateMetadata (negative Test: invalid ID or no record)
    @Order(30)
    @Test
    void negativeTestInvalidIdUpdateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestInvalidIdUpdateMetadata: Permission Not Found.");
        }

        // No Record
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.VALUE, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        // Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updateMetadata(String.format("%d", permId),"BLAH", requests);
        // HttpStatus
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        // Response Body
        assertEquals(HttpStatus.NO_CONTENT, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.NO_CONTENT, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
    }

    // updateMetadata (negative Test: unknown field and other errors)
    @Order(31)
    @Test
    void negativeTestInvalidRequestsUpdateMetadata() {
        // Get Permission ID
        if(ctdUtil == null)
            ctdUtil = new ControllerTestDataUtil();
        int permId = ctdUtil.getPermissionId(AdvancedAppDataTestConstant.PERM1_NAME);
        if(permId == -1) {
            fail("negativeTestInvalidRequestsUpdateMetadata: Permission Not Found.");
        }
        // Create Metadata Request
        MetadataCreateRequest metaRequest = new MetadataCreateRequest();
        metaRequest.setName(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        metaRequest.setValue(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        // Create
        ResponseEntity<PermissionSummaryResponse> createResponse = adminPermController.createMetadata(String.format("%d", permId), metaRequest);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // Test 1: Update Unknown Field
        // 1: Set Request
        List<EntityPatchRequest> requests = new ArrayList<>();
        requests.add(new EntityPatchRequest("unknown", MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        // 1: Call Update
        ResponseEntity<EntityPatchResponse> response = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 1: HttpStatus
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        // 1: Response Body
        assertEquals(HttpStatus.BAD_REQUEST, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.UPDATED_WARNING, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 1: Clear requests
        requests.clear();

        // Test 2: Update Archived to non-boolean
        // 2: Set Request
        requests.add(new EntityPatchRequest(iamDBUpdatableFields.ARCHIVED, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED));
        response = adminPermController.updateMetadata(String.format("%d", permId), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, requests);
        // 2: HttpStatus
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // 2: Response Body
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().getStatus());
        assertEquals(iamServiceResponseCode.SOMETHING_WENT_WRONG, response.getBody().getResponse());
        assertEquals(0, response.getBody().getCount());
        // 2: Clear requests
        requests.clear();
    }
}
