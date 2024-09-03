package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;

// Internal Objects
import jgr.iam.model.dto.FeatureDTO;

public class FeatureTest {

    private FeatureDTO featureDTO;
    private Feature feature;

    @BeforeEach
    public void setUp() {
        featureDTO = new FeatureDTO(1, "TestFeature", "Test Feature Description", 2, false);
        feature = new Feature(featureDTO);
    }

    @Test
    public void smokeTestFeatureConstructor() {
        assertNotNull(feature);
        assertEquals(1, feature.getId());
        assertEquals("TestFeature", feature.getName());
        assertEquals("Test Feature Description", feature.getDescription());
        assertFalse(feature.isArchived());
        assertEquals(0, feature.getRoleCount());
        assertNotNull(feature.getRoleList());
        assertEquals(0, feature.getMetadataCount());
        assertNotNull(feature.getMetadataList());
    }

    @Test
    public void negativeTestFeatureConstructorWithNullDTO() {
        Feature nullFeature = new Feature(null);
        assertNotNull(nullFeature);
        assertEquals(0, nullFeature.getId());
        assertNull(nullFeature.getName());
        assertNull(nullFeature.getDescription());
        assertFalse(nullFeature.isArchived());
        assertEquals(0, nullFeature.getRoleCount());
        assertNull(nullFeature.getRoleList());
        assertEquals(0, nullFeature.getMetadataCount());
        assertNull(nullFeature.getMetadataList());
    }

    @Test
    void smokeTestSettersAndGetters() {
        feature.setId(2);
        assertEquals(2, feature.getId());

        feature.setName("UpdatedFeature");
        assertEquals("UpdatedFeature", feature.getName());

        feature.setDescription("Updated Description");
        assertEquals("Updated Description", feature.getDescription());

        feature.setArchived(true);
        assertTrue(feature.isArchived());

        feature.setMetadataCount(3);
        assertEquals(3, feature.getMetadataCount());

        feature.setMetadataList(Arrays.asList(new Metadata("Metadata1"), new Metadata("Metadata2")));
        assertEquals(2, feature.getMetadataList().size());

        feature.setRoleCount(3);
        assertEquals(3, feature.getRoleCount());

        feature.setRoleList(Arrays.asList(new Role(null), new Role(null)));
        assertEquals(2, feature.getRoleList().size());
    }

    @Test
    public void negativeTestFeatureWithInvalidId() {
        featureDTO.setId(-1);
        Feature invalidFeature = new Feature(featureDTO);
        assertEquals(-1, invalidFeature.getId());
    }

    @Test
    public void negativeTestFeatureWithEmptyName() {
        featureDTO.setName("");
        Feature emptyNameFeature = new Feature(featureDTO);
        assertEquals("", emptyNameFeature.getName());
    }

    @Test
    public void negativeTestFeatureWithNullName() {
        featureDTO.setName(null);
        Feature nullNameFeature = new Feature(featureDTO);
        assertNull(nullNameFeature.getName());
    }

    @Test
    public void negativeTestFeatureArchivedFlag() {
        featureDTO.setArchived(true);
        Feature archivedFeature = new Feature(featureDTO);
        assertTrue(archivedFeature.isArchived());
    }

    @Test
    public void smokeTestRoleListInitialization() {
        assertEquals(0, feature.getRoleCount());
        assertInstanceOf(ArrayList.class, feature.getRoleList());
        assertEquals(0, feature.getRoleList().size());
    }

    @Test
    public void smokeTestMetadataListInitialization() {
        assertEquals(0, feature.getMetadataCount());
        assertInstanceOf(ArrayList.class, feature.getMetadataList());
        assertEquals(0, feature.getMetadataList().size());
    }
}
