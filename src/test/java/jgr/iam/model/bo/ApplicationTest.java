package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;

// Internal Objects
import jgr.iam.model.dto.ApplicationDTO;

public class ApplicationTest {

    private ApplicationDTO applicationDTO;
    private Application application;

    @BeforeEach
    public void setUp() {
        applicationDTO = new ApplicationDTO(1, "TestApplication", "Test Application Description", false);
        application = new Application(applicationDTO);
    }

    @Test
    public void smokeTestApplicationConstructor() {
        assertNotNull(application);
        assertEquals(1, application.getId());
        assertEquals("TestApplication", application.getName());
        assertEquals("Test Application Description", application.getDescription());
        assertFalse(application.isArchived());
        assertEquals(0, application.getFeatureCount());
        assertNotNull(application.getFeatureList());
        assertEquals(0, application.getMetadataCount());
        assertNotNull(application.getMetadataList());
    }

    @Test
    public void negativeTestApplicationConstructorWithNullDTO() {
        Application nullApplication = new Application(null);
        assertNotNull(nullApplication);
        assertEquals(0, nullApplication.getId());
        assertNull(nullApplication.getName());
        assertNull(nullApplication.getDescription());
        assertFalse(nullApplication.isArchived());
        assertEquals(0, nullApplication.getFeatureCount());
        assertNull(nullApplication.getFeatureList());
        assertEquals(0, nullApplication.getMetadataCount());
        assertNull(nullApplication.getMetadataList());
    }

    @Test
    void smokeTestSettersAndGetters() {
        application.setId(2);
        assertEquals(2, application.getId());

        application.setName("UpdatedApplication");
        assertEquals("UpdatedApplication", application.getName());

        application.setDescription("Updated Description");
        assertEquals("Updated Description", application.getDescription());

        application.setArchived(true);
        assertTrue(application.isArchived());

        application.setMetadataCount(3);
        assertEquals(3, application.getMetadataCount());

        application.setMetadataList(Arrays.asList(new Metadata("Metadata1"), new Metadata("Metadata2")));
        assertEquals(2, application.getMetadataList().size());

        application.setFeatureCount(3);
        assertEquals(3, application.getFeatureCount());

        application.setFeatureList(Arrays.asList(new Feature(null), new Feature(null), new Feature(null)));
        assertEquals(3, application.getFeatureList().size());
    }

    @Test
    public void negativeTestApplicationWithInvalidId() {
        applicationDTO.setId(-1);
        Application invalidApplication = new Application(applicationDTO);
        assertEquals(-1, invalidApplication.getId());
    }

    @Test
    public void negativeTestApplicationWithEmptyName() {
        applicationDTO.setName("");
        Application emptyNameApplication = new Application(applicationDTO);
        assertEquals("", emptyNameApplication.getName());
    }

    @Test
    public void negativeTestApplicationWithNullName() {
        applicationDTO.setName(null);
        Application nullNameApplication = new Application(applicationDTO);
        assertNull(nullNameApplication.getName());
    }

    @Test
    public void smokeTestApplicationArchivedFlag() {
        applicationDTO.setArchived(true);
        Application archivedApplication = new Application(applicationDTO);
        assertTrue(archivedApplication.isArchived());
    }

    @Test
    public void smokeTestFeatureListInitialization() {
        assertEquals(0, application.getFeatureCount());
        assertInstanceOf(ArrayList.class, application.getFeatureList());
        assertEquals(0, application.getFeatureList().size());
    }

    @Test
    public void smokeTestMetadataListInitialization() {
        assertEquals(0, application.getMetadataCount());
        assertInstanceOf(ArrayList.class, application.getMetadataList());
        assertEquals(0, application.getMetadataList().size());
    }
}
