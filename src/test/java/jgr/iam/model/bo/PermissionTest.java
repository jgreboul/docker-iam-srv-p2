package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Internal Objects
import jgr.iam.model.dto.PermissionDTO;

class PermissionTest {

    @Mock
    private PermissionDTO mockPermissionDTO;

    private Permission permission;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockPermissionDTO.getId()).thenReturn(1);
        when(mockPermissionDTO.getName()).thenReturn("TestPermission");
        when(mockPermissionDTO.getDescription()).thenReturn("Test Description");
        when(mockPermissionDTO.isArchived()).thenReturn(false);
        permission = new Permission(mockPermissionDTO);
    }

    @Test
    void smokeTestNotNull() {
        assertNotNull(permission);
    }

    @Test
    void smokeTestConstructorWithValidDTO() {
        assertEquals(1, permission.getId());
        assertEquals("TestPermission", permission.getName());
        assertEquals("Test Description", permission.getDescription());
        assertFalse(permission.isArchived());
        assertEquals(0, permission.getMetadataCount());
        assertTrue(permission.getMetadataList().isEmpty());
        assertEquals(0, permission.getRoleCount());
        assertTrue(permission.getRoleExtendedNameList().isEmpty());
    }

    @Test
    void negativeTestConstructorWithNullDTO() {
        Permission nullPermission = new Permission(null);
        assertEquals(0, nullPermission.getId());
        assertNull(nullPermission.getName());
        assertNull(nullPermission.getDescription());
        assertFalse(nullPermission.isArchived());
        assertEquals(0, nullPermission.getMetadataCount());
        assertNull(nullPermission.getMetadataList());
        assertEquals(0, nullPermission.getRoleCount());
        assertNull(nullPermission.getRoleExtendedNameList());
    }

    @Test
    void smokeTestSettersAndGetters() {
        permission.setId(2);
        assertEquals(2, permission.getId());

        permission.setName("UpdatedPermission");
        assertEquals("UpdatedPermission", permission.getName());

        permission.setDescription("Updated Description");
        assertEquals("Updated Description", permission.getDescription());

        permission.setArchived(true);
        assertTrue(permission.isArchived());

        permission.setMetadataCount(3);
        assertEquals(3, permission.getMetadataCount());

        permission.setMetadataList(Arrays.asList(new Metadata("Metadata1"), new Metadata("Metadata2")));
        assertEquals(2, permission.getMetadataList().size());

        permission.setRoleCount(2);
        assertEquals(2, permission.getRoleCount());

        permission.setRoleExtendedNameList(Arrays.asList("Role1", "Role2"));
        assertEquals(2, permission.getRoleExtendedNameList().size());
    }

    @Test
    void negativeTestNegativeId() {
        permission.setId(-1);
        assertTrue(permission.getId() < 0);
    }

    @Test
    void negativeTestEmptyName() {
        permission.setName("");
        assertTrue(permission.getName().isEmpty());
    }

    @Test
    void negativeTestNullDescription() {
        permission.setDescription(null);
        assertNull(permission.getDescription());
    }

    @Test
    void negativeTestNegativeMetadataCount() {
        permission.setMetadataCount(-5);
        assertTrue(permission.getMetadataCount() < 0);
    }

    @Test
    void negativeTestNegativeRoleCount() {
        permission.setRoleCount(-3);
        assertTrue(permission.getRoleCount() < 0);
    }
}
