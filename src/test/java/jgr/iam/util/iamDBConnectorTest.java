package jgr.iam.util;

// External Objects
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

// External Static Objects
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

// Internal  Objects
import jgr.iam.constant.iamDBConnectionInfo; // iamDB Default Connection Info

// iamDBConnector Test Class
public class iamDBConnectorTest {

    @Mock
    private DataSource mockDataSource;

    @Mock
    private Connection mockConnection;

    @Spy
    @InjectMocks
    private iamDBConnectorUtil dbConnectorUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        try {
            if (dbConnectorUtil.isOpened()) {
                dbConnectorUtil.close();
            }
        }
        catch(Exception e) {
            //Do Nothing
        }
    }

    @Test
    void smokeTestConstructor() {
        assertNotNull(dbConnectorUtil);
    }

    @Test
    void smokeTestSuccessfulConnection() throws SQLException {
        String url = iamDBConnectorTestUtil.setIamDBConnectURL();
        String username = iamDBConnectorTestUtil.setIamDBConnectUser();
        String password = iamDBConnectorTestUtil.setIamDBConnectPassword();
        dbConnectorUtil.setDataSource(url, username, password);
        dbConnectorUtil.connect();
        assertNotNull(dbConnectorUtil.getConnection());
    }

    @Test
    void negativeTestConnectionFailure() throws SQLException {
        when(mockDataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        dbConnectorUtil.setDataSource("jdbc:mysql://localhost:3306/testdb", "testuser", "testpassword");
        SQLException exception = assertThrows(SQLException.class, () -> {
            dbConnectorUtil.connect();
        });
        assertEquals("connect: Failed to connect to the database.", exception.getMessage());
    }

    @Test
    void negativeTestNullDSConnectionFailure() throws SQLException {
        dbConnectorUtil.setDataSource(null);
        SQLException exception = assertThrows(SQLException.class, () -> {
            dbConnectorUtil.connect();
        });
        assertEquals("connect: Null Datasource.", exception.getMessage());
    }

    @Test
    void smokeTestCloseConnection() throws SQLException {
        when(mockConnection.isClosed()).thenReturn(false);
        dbConnectorUtil.setConnection(mockConnection);
        dbConnectorUtil.close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    void negativeTestCloseConnectionFailure() throws SQLException {
        doThrow(new SQLException("Close failed")).when(mockConnection).close();
        dbConnectorUtil.setConnection(mockConnection);
        dbConnectorUtil.close();
        verify(mockConnection, times(1)).close();
    }

    @Test
    void negativeTestNullConnectionCloseConnectionFailure() throws SQLException {
        // Test 1: Exception
        try {
            when(mockConnection.isClosed()).thenThrow(new RuntimeException("Test Exception"));
            dbConnectorUtil.setConnection(mockConnection);
            boolean result = dbConnectorUtil.isOpened();
            assertEquals(false, result);
        }
        catch(Exception e) {
            // Do nothing
        }
        // Test 2: Close
        try {
            dbConnectorUtil.close();
        }
        catch(Exception e) {
            //Do Nothing
        }
        // Test 2: Null
        dbConnectorUtil.setConnection(null);
        boolean result = dbConnectorUtil.isOpened();
        assertEquals(false, result);
        dbConnectorUtil.close();
        // No Exception should be thrown
    }

    @Test
    void negativeTestIsClosedCloseConnectionFailure() throws SQLException {
        // Test 1: is Closed false
        try {
            when(mockConnection.isClosed()).thenReturn(false);
            dbConnectorUtil.setConnection(mockConnection);
            dbConnectorUtil.close();
        }
        catch(Exception e) {
            // Do nothing
        }
        // Test 2: Close
        try {
            when(mockConnection.isClosed()).thenReturn(true);
            dbConnectorUtil.close();
        }
        catch(Exception e) {
            //Do Nothing
        }
    }

    @Test
    void smokeTestGetConnectionURLWithValidEnv() {
        String expectedURL = iamDBConnectorTestUtil.setIamDBConnectURL();
        System.setProperty("ENV_DB_URL", expectedURL);
        String actualURL = dbConnectorUtil.getConnectionURL();
        assertEquals(expectedURL, actualURL);
    }

    @Test
    void smokeTestGetConnectionURLWithValidEnvV2() {
        doReturn("expectedURL").when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualURL = dbConnectorUtil.getConnectionURL();
        assertEquals("expectedURL", actualURL);
    }

    @Test
    void negativeTestGetConnectionURLWithInvalidEnv() {
        System.clearProperty("ENV_DB_URL");
        String actualURL = dbConnectorUtil.getConnectionURL();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_URL, actualURL);
    }

    @Test
    public void negativeTestGetConnectionURLWithNullEnv() {
        doReturn(null).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionURL();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_URL, actualPassword);
    }

    @Test
    public void negativeTestGetConnectionURLWithException() {
        doThrow(new SecurityException("Access denied")).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionURL();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_URL, actualPassword);
    }

    @Test
    void smokeTestGetConnectionUserWithValidEnv() {
        String expectedUser = iamDBConnectorTestUtil.setIamDBConnectUser();
        System.setProperty("ENV_DB_USR", expectedUser);
        String actualUser = dbConnectorUtil.getConnectionUser();
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void smokeTestGetConnectionUserWithValidEnvV2() {
        doReturn("expectedUser").when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String result = dbConnectorUtil.getConnectionUser();
        assertEquals("expectedUser", result);
    }

    @Test
    void negativeTestGetConnectionUserWithInvalidEnv() {
        System.clearProperty("ENV_DB_USR");
        String actualUser = dbConnectorUtil.getConnectionUser();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_USR, actualUser);
    }

    @Test
    public void negativeTestGetConnectionUserWithNullEnv() {
        doReturn(null).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionUser();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_USR, actualPassword);
    }

    @Test
    public void negativeTestGetConnectionUserWithException() {
        doThrow(new SecurityException("Access denied")).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionUser();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_USR, actualPassword);
    }

    @Test
    void smokeTestGetConnectionPasswordWithValidEnv() {
        String expectedPassword = iamDBConnectorTestUtil.setIamDBConnectPassword();
        System.setProperty("ENV_DB_PWD", expectedPassword);
        String actualPassword = dbConnectorUtil.getConnectionPassword();
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    void smokeTestGetConnectionPasswordWithValidEnvV2() {
        doReturn("expectedPassword").when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String result = dbConnectorUtil.getConnectionPassword();
        assertEquals("expectedPassword", result);
    }

    @Test
    void negativeTestGetConnectionPasswordWithInvalidEnv() {
        System.clearProperty("ENV_DB_PWD");
        String actualPassword = dbConnectorUtil.getConnectionPassword();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_PWD, actualPassword);
    }

    @Test
    public void negativeTestGetConnectionPasswordWithNullEnv() {
        doReturn(null).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionPassword();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_PWD, actualPassword);
    }

    @Test
    public void negativeTestGetConnectionPasswordWithException() {
        doThrow(new SecurityException("Access denied")).when(dbConnectorUtil).getEnvironmentVariable(anyString());
        String actualPassword = dbConnectorUtil.getConnectionPassword();
        assertEquals(iamDBConnectionInfo.DEFAULT_DB_PWD, actualPassword);
    }
}
