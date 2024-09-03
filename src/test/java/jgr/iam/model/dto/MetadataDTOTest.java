package jgr.iam.model.dto;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;

public class MetadataDTOTest {

    private MetadataDTO metadataDTO;

    @BeforeEach
    void setUp() {
        metadataDTO = new MetadataDTO();
    }

    @Test
    void smokeTestConstructor() {
        assertNotNull(metadataDTO);
    }

    @Test
    void smokeTestValidMetadataDTOCreation() {
        metadataDTO.setRefid(1);
        metadataDTO.setName("TestMetadata");
        metadataDTO.setValue("TestValue");
        assertEquals(1, metadataDTO.getRefid());
        assertEquals("TestMetadata", metadataDTO.getName());
        assertEquals("TestValue", metadataDTO.getValue());
    }

    @Test
    void negativeTestMetadataDTOCreationWithNullValues() {
        assertNull(metadataDTO.getName());
        assertNull(metadataDTO.getValue());
    }

    @Test
    void negativeTestSetNegativeId() {
        metadataDTO.setRefid(-1);
        assertEquals(-1, metadataDTO.getRefid());
    }

    @Test
    void negativeTestSetNullName() {
        metadataDTO.setName(null);
        assertNull(metadataDTO.getName());
    }

    @Test
    void negativeTestSetEmptyName() {
        metadataDTO.setName("");
        assertEquals("", metadataDTO.getName());
    }

    @Test
    void negativeTestSetNullValue() {
        metadataDTO.setValue(null);
        assertNull(metadataDTO.getValue());
    }

    @Test
    void negativeTestSetEmptyValue() {
        metadataDTO.setValue("");
        assertEquals("", metadataDTO.getValue());
    }
}
