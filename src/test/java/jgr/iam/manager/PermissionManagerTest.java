package jgr.iam.manager;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

// Internal Objects
import jgr.iam.constant.dto.MetadataDTOTestConstant;
import jgr.iam.constant.dto.PermissionDTOTestConstant;
import jgr.iam.enums.RequestType;
import jgr.iam.model.bo.Metadata;
import jgr.iam.model.bo.Permission;
import jgr.iam.model.dto.MetadataDTO;
import jgr.iam.model.dto.RolePermissionDTO;
import jgr.iam.model.dto.PermissionDTO;
import jgr.iam.repository.PermissionRepository;
import jgr.iam.repository.MetadataRepository;
import jgr.iam.repository.RolePermissionRepository;

// Permission Manager Test Class
public class PermissionManagerTest {

    @Mock
    private PermissionRepository permRepo;

    @Mock
    private RolePermissionRepository rpRepo;

    @Mock
    private MetadataRepository permMetadataRepo;

    @Spy
    @InjectMocks
    private PermissionManager permissionManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        permissionManager.setPermRepo(permRepo);
        permissionManager.setRpRepo(rpRepo);
        permissionManager.setPermMetadataRepo(permMetadataRepo);
    }

    @Test
    void smokeTestConstructor() {
        assertNotNull(permissionManager);
    }

    @Test
    void smokeTestGetters() {
        assertNotNull(permissionManager);
        PermissionRepository permRepo = permissionManager.getPermRepo();
        assertNotNull(permRepo);
        RolePermissionRepository rpRepo = permissionManager.getRpRepo();
        assertNotNull(rpRepo);
        MetadataRepository permMetadataRepo = permissionManager.getPermMetadataRepo();
        assertNotNull(permMetadataRepo);
    }

    @Test
    void smokeTestNonArchivedSummaryGetPermissions() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", false),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", false));
        when(permRepo.getAll()).thenReturn(mockPermissionDTOs);

        // Add Info - Summary View
        when(rpRepo.getAllCountForPermission(1)).thenReturn(2);
        when(permMetadataRepo.getAllCount(1)).thenReturn(1);
        when(rpRepo.getAllCountForPermission(2)).thenReturn(0);
        when(permMetadataRepo.getAllCount(2)).thenReturn(0);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(2, permissions.get(0).getRoleCount());
        assertEquals(1, permissions.get(0).getMetadataCount());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void negativeTestNonArchivedSummaryGetPermissionsNullResult() throws SQLException {
        when(permRepo.getAll()).thenReturn(null);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestNonArchivedSummaryGetPermissionsNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestNonArchivedSummaryGetPermissionsSQLException() throws SQLException {
        when(permRepo.getAll()).thenThrow(new SQLException("Test Exception"));
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void smokeTestArchivedSummaryGetPermissions() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", true),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", true));
        when(permRepo.getAllArchived()).thenReturn(mockPermissionDTOs);

        // Add Info - Summary View
        when(rpRepo.getAllCountForPermission(1)).thenReturn(2);
        when(permMetadataRepo.getAllCount(1)).thenReturn(1);
        when(rpRepo.getAllCountForPermission(2)).thenReturn(0);
        when(permMetadataRepo.getAllCount(2)).thenReturn(0);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, true);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(2, permissions.get(0).getRoleCount());
        assertEquals(1, permissions.get(0).getMetadataCount());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void negativeTestArchivedSummaryGetPermissionsNullResult() throws SQLException {
        when(permRepo.getAllArchived()).thenReturn(null);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, true);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestArchivedSummaryGetPermissionsNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, true);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestArchivedSummaryGetPermissionsSQLException() throws SQLException {
        when(permRepo.getAllArchived()).thenThrow(new SQLException("Test Exception"));
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.SUMMARY, true);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void smokeTestNonArchivedDetailGetPermissions() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", false),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", false));
        when(permRepo.getAll()).thenReturn(mockPermissionDTOs);

        // Add Info - Detail View
        List<RolePermissionDTO> mockRpList1  = Arrays.asList(
                new RolePermissionDTO(1, 1, false),
                new RolePermissionDTO(2, 1, false));
        when(rpRepo.getAllForPermission(1)).thenReturn(mockRpList1);
        List<String> mockRoleExtendedNames1  = Arrays.asList("role1", "role2");
        when(rpRepo.getAllRoleExtendedNameForPermission(1)).thenReturn(mockRoleExtendedNames1);
        List<MetadataDTO> mockMetas1  = Arrays.asList(
                new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false));
        when(permMetadataRepo.getAll(1)).thenReturn(mockMetas1);
        when(rpRepo.getAllForPermission(2)).thenReturn(null);
        when(rpRepo.getAllRoleExtendedNameForPermission(2)).thenReturn(null);
        when(permMetadataRepo.getAll(2)).thenReturn(null);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(2, permissions.get(0).getRoleCount());
        assertEquals(2, permissions.get(0).getRoleExtendedNameList().size());
        assertEquals("role1", permissions.get(0).getRoleExtendedNameList().get(0));
        assertEquals("role2", permissions.get(0).getRoleExtendedNameList().get(1));
        assertEquals(1, permissions.get(0).getMetadataCount());
        assertEquals(1, permissions.get(0).getMetadataList().size());
        assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, permissions.get(0).getMetadataList().get(0).getName());
        assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, permissions.get(0).getMetadataList().get(0).getValue());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsNullResult() throws SQLException {
        when(permRepo.getAll()).thenReturn(null);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsSQLException() throws SQLException {
        when(permRepo.getAll()).thenThrow(new SQLException("Test Execution"));
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(0, permissions.size());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsSQLExceptionFromGetAllPermission() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", false),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", false));
        when(permRepo.getAll()).thenReturn(mockPermissionDTOs);

        // Add Info - Detail View
        when(rpRepo.getAllForPermission(1)).thenThrow(new SQLException("Test Exception"));
        when(rpRepo.getAllForPermission(2)).thenReturn(null);
        when(rpRepo.getAllRoleExtendedNameForPermission(2)).thenReturn(null);
        when(permMetadataRepo.getAll(2)).thenReturn(null);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(0, permissions.get(0).getRoleCount());
        assertEquals(0, permissions.get(0).getMetadataCount());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsSQLExceptionFromGetAllRoleExtendedName() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", false),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", false));
        when(permRepo.getAll()).thenReturn(mockPermissionDTOs);

        // Add Info - Detail View
        List<RolePermissionDTO> mockRpList1  = Arrays.asList(
                new RolePermissionDTO(1, 1, false),
                new RolePermissionDTO(2, 1, false));
        when(rpRepo.getAllForPermission(1)).thenReturn(mockRpList1);
        when(rpRepo.getAllRoleExtendedNameForPermission(1)).thenThrow(new SQLException("Test Exception"));
        when(rpRepo.getAllForPermission(2)).thenReturn(null);
        when(rpRepo.getAllRoleExtendedNameForPermission(2)).thenReturn(null);
        when(permMetadataRepo.getAll(2)).thenReturn(null);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(2, permissions.get(0).getRoleCount());
        assertEquals(0, permissions.get(0).getRoleExtendedNameList().size());
        assertEquals(0, permissions.get(0).getMetadataCount());
        assertEquals(0, permissions.get(0).getMetadataList().size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void negativeTestNonArchivedDetailGetPermissionsSQLExceptionFromGetAllMetadata() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", false),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", false));
        when(permRepo.getAll()).thenReturn(mockPermissionDTOs);

        // Add Info - Detail View
        List<RolePermissionDTO> mockRpList1  = Arrays.asList(
                new RolePermissionDTO(1, 1, false),
                new RolePermissionDTO(2, 1, false));
        when(rpRepo.getAllForPermission(1)).thenReturn(mockRpList1);
        List<String> mockRoleExtendedNames1  = Arrays.asList("role1", "role2");
        when(rpRepo.getAllRoleExtendedNameForPermission(1)).thenReturn(mockRoleExtendedNames1);
        when(permMetadataRepo.getAll(1)).thenThrow(new SQLException("Test Exception"));
        when(rpRepo.getAllForPermission(2)).thenReturn(null);
        when(rpRepo.getAllRoleExtendedNameForPermission(2)).thenReturn(null);
        when(permMetadataRepo.getAll(2)).thenReturn(null);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.DETAIL, false);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(2, permissions.get(0).getRoleCount());
        assertEquals(2, permissions.get(0).getRoleExtendedNameList().size());
        assertEquals("role1", permissions.get(0).getRoleExtendedNameList().get(0));
        assertEquals("role2", permissions.get(0).getRoleExtendedNameList().get(1));
        assertEquals(0, permissions.get(0).getMetadataCount());
        assertEquals(0, permissions.get(0).getMetadataList().size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
        assertEquals(0, permissions.get(1).getRoleCount());
        assertEquals(0, permissions.get(1).getMetadataCount());
    }

    @Test
    void smokeTestArchivedInternalGetPermissions() throws SQLException {
        //Get All
        List<PermissionDTO> mockPermissionDTOs = Arrays.asList(
                new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"1", true),
                new PermissionDTO(2, PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X +"2", true));
        when(permRepo.getAllArchived()).thenReturn(mockPermissionDTOs);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        List<Permission> permissions = permissionManager.getPermissions(RequestType.INTERNAL, true);

        // Validate
        assertNotNull(permissions);
        assertEquals(2, permissions.size());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"1", permissions.get(0).getName());
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X +"2", permissions.get(1).getName());
    }

    @Test
    void negativeTestGetPermissionByIdNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission(1, RequestType.INTERNAL);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestGetPermissionByIdThrowException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getById(1)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission(1, RequestType.INTERNAL);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestGetPermissionByNameNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission("Test", RequestType.INTERNAL);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestGetPermissionByNameThrowException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getByName("Test")).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission("Test", RequestType.INTERNAL);

        // Validate
        assertNull(permission);
    }

    @Test
    void smokeTestGetDetailPermissionByName() throws SQLException {
        PermissionDTO  mockPermissionDTO = new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, false);
        when(permRepo.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME)).thenReturn(mockPermissionDTO);

        // Add Info - Detail View
        List<RolePermissionDTO> mockRpList  = Arrays.asList(
                new RolePermissionDTO(1, 1, false));
        when(rpRepo.getAllForPermission(1)).thenReturn(mockRpList);
        List<String> mockRoleExtendedNames  = Arrays.asList("role1");
        when(rpRepo.getAllRoleExtendedNameForPermission(1)).thenReturn(mockRoleExtendedNames);
        List<MetadataDTO> mockMetas  = Arrays.asList(
                new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false));
        when(permMetadataRepo.getAll(1)).thenReturn(mockMetas);

        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission(PermissionDTOTestConstant.PERMISSION_TEST_NAME, RequestType.DETAIL);

        // Validate
        assertNotNull(permission);
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME, permission.getName());
        assertEquals(1, permission.getRoleCount());
        assertEquals(1, permission.getRoleExtendedNameList().size());
        assertEquals("role1", permission.getRoleExtendedNameList().get(0));
        assertEquals(1, permission.getMetadataCount());
        assertEquals(1, permission.getMetadataList().size());
        assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, permission.getMetadataList().get(0).getName());
        assertEquals(MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, permission.getMetadataList().get(0).getValue());
    }

    @Test
    void smokeTestGetInternalPermissionByName() throws SQLException {
        PermissionDTO  mockPermissionDTO = new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, false);
        when(permRepo.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME)).thenReturn(mockPermissionDTO);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.getPermission(PermissionDTOTestConstant.PERMISSION_TEST_NAME, RequestType.INTERNAL);

        // Validate
        assertNotNull(permission);
        assertEquals(PermissionDTOTestConstant.PERMISSION_TEST_NAME, permission.getName());
        assertEquals(0, permission.getRoleCount());
        assertEquals(0, permission.getRoleExtendedNameList().size());
    }

    @Test
    void negativeTestCreatePermissionNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.createPermission("Test", "Test Permission");

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestCreatePermissionException() throws SQLException {
        doThrow(new SQLException("Test Exception")).when(permRepo).insert(PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.createPermission(PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestDeletePermissionNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.deletePermission(1);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestDeletePermissionFound() throws SQLException {
        doNothing().when(permRepo).delete(1);
        PermissionDTO mockPermissionDTO = new PermissionDTO(1, PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, false);
        when(permRepo.getById(1)).thenReturn(mockPermissionDTO);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.deletePermission(1);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestDeletePermissionException() throws SQLException {
        doThrow(new SQLException("Test Exception")).when(permRepo).delete(1);
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.deletePermission(1);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionDescriptionNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionDescription(1, "Test Description");

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionDescriptionNotFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getById(1)).thenReturn(null);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionDescription(1, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_UPDATED);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionDescriptionException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getById(1)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionDescription(1, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_UPDATED);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionArchivedNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionArchived(1, false);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionArchivedNotFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getById(1)).thenReturn(null);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionArchived(1, true);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdatePermissionArchivedException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permRepo.getById(1)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updatePermissionArchived(1, true);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestGetMetadataNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Metadata metadata = permissionManager.getMetadata(1, "NAME");

        // Validate
        assertEquals(null, metadata);
    }

    @Test
    void negativeTestGetMetadataException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Metadata metadata = permissionManager.getMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);

        // Validate
        assertNull(metadata);
    }

    @Test
    void negativeTestCreateMetadataNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.createMetadata(1, "NAME", "VALUE");

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestCreateMetadataException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        doThrow(new SQLException("Test Exception")).when(permMetadataRepo).insert(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.createMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestCreateMetadataNotFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permMetadataRepo).insert(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(null);
        doNothing().when(permissionManager).disconnect();

        // Call
        Permission permission = permissionManager.createMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);

        // Validate
        assertNull(permission);
    }

    @Test
    void negativeTestDeleteMetadataNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.deleteMetadata(1, "NAME");

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestDeleteMetadataException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        doThrow(new SQLException("Test Exception")).when(permMetadataRepo).delete(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result  = permissionManager.deleteMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestDeleteMetadataFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        doNothing().when(permMetadataRepo).delete(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
        MetadataDTO mockMetadata = new MetadataDTO(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE, false);
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(mockMetadata);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result  = permissionManager.deleteMetadata(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);

        // Validate
        assertEquals(false, result);
    }

    @Test
    void negativeTestUpdateMetadataValueNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataValue(1, "NAME", "VALUE");

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestUpdateMetadataValueNotFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(null);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataValue(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED);

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestUpdateMetadataValueException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataValue(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE_UPDATED);

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestUpdateMetadataArchivedNotConnect() throws SQLException {
        doReturn(false).when(permissionManager).connect();
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataArchived(1, "NAME", false);

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestUpdateMetadataArchivedNotFound() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenReturn(null);
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataArchived(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, true);

        // Validate
        assertEquals(false, result );
    }

    @Test
    void negativeTestUpdateMetadataArchivedException() throws SQLException {
        doReturn(true).when(permissionManager).connect();
        when(permMetadataRepo.get(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME)).thenThrow(new SQLException("Test Exception"));
        doNothing().when(permissionManager).disconnect();

        // Call
        Boolean result = permissionManager.updateMetadataArchived(1, MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, true);

        // Validate
        assertEquals(false, result );
    }
}


