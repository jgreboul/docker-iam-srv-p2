package jgr.iam.manager;

// External Objects
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import java.sql.SQLException;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

// Internal Objects
import jgr.iam.util.iamDBConnectorUtil;

public class BaseManagerTest {

    @Mock
    private iamDBConnectorUtil connector; // iamDB Connector

    @Spy
    @InjectMocks
    private BaseManager baseManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void smokeTestConstructor() {
        assertNotNull(baseManager);
    }

    @Test
    void smokeTestGetters() {
        assertNotNull(baseManager);
        Logger logger = baseManager.getLogger();
        assertNotNull(logger);
        iamDBConnectorUtil connector1 = baseManager.getConnector();
        assertNotNull(connector1);
    }

    @Test
    void negativeTestConnectException() throws SQLException {
        baseManager.setConnector(connector);
        doNothing().when(connector).setDataSource(anyString(), anyString(), anyString());
        doThrow(new SQLException("Test Exception")).when(connector).connect();
        // Call
        boolean result = baseManager.connect();
        assertEquals(false, result);
    }

}
