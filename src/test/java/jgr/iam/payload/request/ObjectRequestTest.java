package jgr.iam.payload.request;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// External Statuc Objects
import static org.junit.jupiter.api.Assertions.*;

// Object Request Test
public class ObjectRequestTest {
    private ObjectRequest objectRequest;

    @BeforeEach
    void setUp() {
        objectRequest = new ObjectRequest();
    }

    @Test
    void smokeTestConstructor() {
        assertNotNull(objectRequest);
    }

    @Test
    void smokeTestValidObjectRequestCreation() {
        objectRequest = new ObjectRequest("TestName", "TestRequest");
        assertEquals("TestName", objectRequest.getName());
        assertEquals("TestRequest", objectRequest.getRequest());
    }

    @Test
    void negativeTestObjectRequestCreationWithNullValues() {
        objectRequest = new ObjectRequest(null, null);
        assertNull(objectRequest.getName());
        assertNull(objectRequest.getRequest());
    }

    @Test
    void negativeTestSetNullName() {
        objectRequest.setName(null);
        assertNull(objectRequest.getName());
    }

    @Test
    void negativeTestSetEmptyName() {
        objectRequest.setName("");
        assertEquals("", objectRequest.getName());
    }

    @Test
    void negativeTestSetNullRequest() {
        objectRequest.setRequest(null);
        assertNull(objectRequest.getRequest());
    }

    @Test
    void negativeTestSetEmptyRequest() {
        objectRequest.setRequest("");
        assertEquals("", objectRequest.getRequest());
    }
}
