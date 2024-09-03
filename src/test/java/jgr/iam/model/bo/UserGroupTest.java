package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Internal Objects
import jgr.iam.model.dto.UserGroupDTO;

class UserGroupTest {

    @Mock
    private UserGroupDTO mockUserGroupDTO;

    private UserGroup userGroup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUserGroupDTO.getId()).thenReturn(1);
        when(mockUserGroupDTO.getName()).thenReturn("Admins");
        when(mockUserGroupDTO.getDescription()).thenReturn("Administrator Group");
        when(mockUserGroupDTO.isArchived()).thenReturn(false);
        userGroup = new UserGroup(mockUserGroupDTO);
    }

    @Test
    void smokeTestNotNull() {
        assertNotNull(userGroup);
    }

    @Test
    void smokeTestValidUserGroupCreation() {
        assertEquals(1, userGroup.getId());
        assertEquals("Admins", userGroup.getName());
        assertEquals("Administrator Group", userGroup.getDescription());
        assertFalse(userGroup.isArchived());
        assertEquals(0, userGroup.getUserCount());
        assertTrue(userGroup.getUserList().isEmpty());
        assertEquals(0, userGroup.getRoleCount());
        assertTrue(userGroup.getRoleList().isEmpty());
        assertEquals(0, userGroup.getMetadataCount());
        assertTrue(userGroup.getMetadataList().isEmpty());
    }

    @Test
    void negativeTestUserGroupCreationWithNullDTO() {
        UserGroup nullUserGroup = new UserGroup(null);
        assertEquals(0, nullUserGroup.getId());
        assertNull(nullUserGroup.getName());
        assertNull(nullUserGroup.getDescription());
        assertFalse(nullUserGroup.isArchived());
        assertEquals(0, nullUserGroup.getUserCount());
        assertNull(nullUserGroup.getUserList());
        assertEquals(0, nullUserGroup.getRoleCount());
        assertNull(nullUserGroup.getRoleList());
        assertEquals(0, nullUserGroup.getMetadataCount());
        assertNull(nullUserGroup.getMetadataList());
    }

    @Test
    void negativeTestInvalidId() {
        userGroup.setId(-1);
        assertTrue(userGroup.getId() < 0);
    }

    @Test
    void negativeTestEmptyName() {
        userGroup.setName("");
        assertTrue(userGroup.getName().isEmpty());
    }

    @Test
    void negativeTestNullName() {
        userGroup.setName(null);
        assertNull(userGroup.getName());
    }

    @Test
    void negativeTestNullDescription() {
        userGroup.setDescription(null);
        assertNull(userGroup.getDescription());
    }

    @Test
    void negativeTestNullUserList() {
        userGroup.setUserList(null);
        assertNull(userGroup.getUserList());
    }

    @Test
    void negativeTestNullRoleList() {
        userGroup.setRoleList(null);
        assertNull(userGroup.getRoleList());
    }

    @Test
    void negativeTestNullMetadataList() {
        userGroup.setMetadataList(null);
        assertNull(userGroup.getMetadataList());
    }
}
