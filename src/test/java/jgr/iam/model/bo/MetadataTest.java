package jgr.iam.model.bo;

// External Objects
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Internal Objects
import jgr.iam.model.dto.MetadataDTO;

@ExtendWith(MockitoExtension.class)
class MetadataTest {

    @Mock
    private MetadataDTO mockMetadataDTO;

    private Metadata metadata;

    @BeforeEach
    void setUp() {
        when(mockMetadataDTO.getName()).thenReturn("TestName");
        when(mockMetadataDTO.getValue()).thenReturn("TestValue");
        when(mockMetadataDTO.isArchived()).thenReturn(false);

        metadata = new Metadata(mockMetadataDTO);
    }

    @Test
    void smokeTestNotNull() {
        assertNotNull(metadata);
        assertEquals("TestName", metadata.getName());
        assertEquals("TestValue", metadata.getValue());
        assertFalse(metadata.isArchived());
    }

    @Test
    void negativeTestConstructorWithNullDTO() {
        Metadata nullMetadata = new Metadata((MetadataDTO) null);
        assertNull(nullMetadata.getName());
        assertNull(nullMetadata.getValue());
        assertFalse(nullMetadata.isArchived());
    }

    @Test
    void smokeTestSettersAndGetters() {
        metadata.setName("NewName");
        assertEquals("NewName", metadata.getName());

        metadata.setValue("NewValue");
        assertEquals("NewValue", metadata.getValue());

        metadata.setArchived(true);
        assertTrue(metadata.isArchived());
    }

    @Test
    void negativeTestEmptyName() {
        metadata.setName("");
        assertTrue(metadata.getName().isEmpty());
    }

    @Test
    void negativeTestNullName() {
        metadata.setName(null);
        assertNull(metadata.getName());
    }

    @Test
    void negativeTestEmptyValue() {
        metadata.setValue("");
        assertTrue(metadata.getValue().isEmpty());
    }

    @Test
    void negativeTestNullValue() {
        metadata.setValue(null);
        assertNull(metadata.getValue());
    }

    @Test
    void smokeTestString() {
        Metadata metadata2 = new Metadata("TestName");
        assertNotNull(metadata2);
        assertEquals("TestName", metadata2.getName());
        assertEquals(null, metadata2.getValue());
        assertFalse(metadata2.isArchived());
    }
}
