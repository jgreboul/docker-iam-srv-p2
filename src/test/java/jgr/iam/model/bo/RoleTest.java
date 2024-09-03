package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;

// Internal Objects
import jgr.iam.model.dto.RoleDTO;

public class RoleTest {

    private RoleDTO roleDTO;
    private Role role;

    @BeforeEach
    public void setUp() {
        roleDTO = new RoleDTO(1, "Admin", "Administrator Role", 1, false);
        role = new Role(roleDTO);
    }

    @Test
    public void smokeTestRoleConstructor() {
        assertNotNull(role);
        assertEquals(1, role.getId());
        assertEquals("Admin", role.getName());
        assertEquals("Administrator Role", role.getDescription());
        assertFalse(role.isArchived());
        assertEquals(0, role.getPermissonCount());
        assertNotNull(role.getPermissonList());
        assertEquals(0, role.getMetadataCount());
        assertNotNull(role.getMetadataList());
        assertEquals(0, role.getUserGroupCount());
        assertNotNull(role.getUserGroupList());
        assertEquals(0, role.getUserCount());
        assertNotNull(role.getUserList());
    }

    @Test
    public void negativeTestRoleConstructorWithNullDTO() {
        Role nullRole = new Role(null);
        assertNotNull(nullRole);
        assertEquals(0, nullRole.getId());
        assertNull(nullRole.getName());
        assertNull(nullRole.getDescription());
        assertFalse(nullRole.isArchived());
        assertEquals(0, nullRole.getPermissonCount());
        assertNull(nullRole.getPermissonList());
        assertEquals(0, nullRole.getMetadataCount());
        assertNull(nullRole.getMetadataList());
        assertEquals(0, nullRole.getUserGroupCount());
        assertNull(nullRole.getUserGroupList());
        assertEquals(0, nullRole.getUserCount());
        assertNull(nullRole.getUserList());
    }

    @Test
    void smokeTestSettersAndGetters() {
        role.setId(2);
        assertEquals(2, role.getId());

        role.setName("UpdatedRole");
        assertEquals("UpdatedRole", role.getName());

        role.setDescription("Updated Description");
        assertEquals("Updated Description", role.getDescription());

        role.setArchived(true);
        assertTrue(role.isArchived());

        role.setMetadataCount(3);
        assertEquals(3, role.getMetadataCount());

        role.setMetadataList(Arrays.asList(new Metadata("Metadata1"), new Metadata("Metadata2")));
        assertEquals(2, role.getMetadataList().size());

        role.setUserGroupCount(3);
        assertEquals(3, role.getUserGroupCount());

        role.setUserGroupList(Arrays.asList(new UserGroup(null), new UserGroup(null)));
        assertEquals(2, role.getUserGroupList().size());

        role.setUserCount(2);
        assertEquals(2, role.getUserCount());

        role.setUserList(Arrays.asList(new User(null), new User(null), new User(null)));
        assertEquals(3, role.getUserList().size());
    }

    @Test
    public void negativeTestRoleWithInvalidId() {
        roleDTO.setId(-1);
        Role invalidRole = new Role(roleDTO);
        assertEquals(-1, invalidRole.getId());
    }

    @Test
    public void negativeTestRoleWithEmptyName() {
        roleDTO.setName("");
        Role emptyNameRole = new Role(roleDTO);
        assertEquals("", emptyNameRole.getName());
    }

    @Test
    public void negativeTestRoleWithNullName() {
        roleDTO.setName(null);
        Role nullNameRole = new Role(roleDTO);
        assertNull(nullNameRole.getName());
    }

    @Test
    public void smokeTestRoleArchivedFlag() {
        roleDTO.setArchived(true);
        Role archivedRole = new Role(roleDTO);
        assertTrue(archivedRole.isArchived());
    }
}

