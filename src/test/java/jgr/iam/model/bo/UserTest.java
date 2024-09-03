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
import jgr.iam.model.dto.UserDTO;

class UserTest {

    @Mock
    private UserDTO mockUserDTO;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockUserDTO.getId()).thenReturn(1);
        when(mockUserDTO.getUsername()).thenReturn("testuser");
        when(mockUserDTO.getEmail()).thenReturn("testuser@example.com");
        when(mockUserDTO.getPasswordHash()).thenReturn(new byte[]{1, 2, 3});
        when(mockUserDTO.getPasswordSalt()).thenReturn(new byte[]{4, 5, 6});
        when(mockUserDTO.getFirstName()).thenReturn("John");
        when(mockUserDTO.getLastName()).thenReturn("Doe");
        when(mockUserDTO.isActive()).thenReturn(true);
        when(mockUserDTO.isArchived()).thenReturn(false);
        user = new User(mockUserDTO);
    }

    @Test
    void smokeTestNotNull() {
        assertNotNull(user);
    }

    @Test
    void smokeTestValidUserCreation() {
        assertEquals(1, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("testuser@example.com", user.getEmail());
        assertArrayEquals(new byte[]{1, 2, 3}, user.getPasswordHash());
        assertArrayEquals(new byte[]{4, 5, 6}, user.getPasswordSalt());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertTrue(user.isActive());
        assertFalse(user.isArchived());
        assertEquals(0, user.getUserGroupCount());
        assertTrue(user.getUserGroupList().isEmpty());
        assertEquals(0, user.getRoleCount());
        assertTrue(user.getRoleList().isEmpty());
        assertEquals(0, user.getMetadataCount());
        assertTrue(user.getMetadataList().isEmpty());
    }

    @Test
    void negativeTestUserCreationWithNullDTO() {
        User nullUser = new User(null);
        assertEquals(0, nullUser.getId());
        assertNull(nullUser.getUsername());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getPasswordHash());
        assertNull(nullUser.getPasswordSalt());
        assertNull(nullUser.getFirstName());
        assertNull(nullUser.getLastName());
        assertTrue(nullUser.isActive());
        assertFalse(nullUser.isArchived());
        assertEquals(0, nullUser.getUserGroupCount());
        assertNull(nullUser.getUserGroupList());
        assertEquals(0, nullUser.getRoleCount());
        assertNull(nullUser.getRoleList());
        assertEquals(0, nullUser.getMetadataCount());
        assertNull(nullUser.getMetadataList());
    }

    @Test
    void negativeTestInvalidId() {
        user.setId(-1);
        assertTrue(user.getId() < 0);
    }

    @Test
    void negativeTestEmptyUsername() {
        user.setUsername("");
        assertTrue(user.getUsername().isEmpty());
    }

    @Test
    void negativeTestNullUsername() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    void negativeTestInvalidEmailFormat() {
        user.setEmail("invalid-email");
        assertFalse(user.getEmail().contains("@"));
    }

    @Test
    void negativeTestNullEmail() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void negativeTestNullPasswordHash() {
        user.setPasswordHash(null);
        assertNull(user.getPasswordHash());
    }

    @Test
    void negativeTestNullPasswordSalt() {
        user.setPasswordSalt(null);
        assertNull(user.getPasswordSalt());
    }
}
