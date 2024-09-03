package jgr.iam.repository;

import jgr.iam.model.dto.ApplicationDTO;
import jgr.iam.util.iamDBConnectorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApplicationRepositoryTest {

    @Mock
    private iamDBConnectorUtil mockConnector;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Spy
    @InjectMocks
    private ApplicationRepository applicationRepository;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        applicationRepository = new ApplicationRepository(mockConnector);
    }

    @Test
    void negativeTestGetByIdWithGetConnectionNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(null);
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByIdWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByIdWithSetIntSQLException() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLException("Test Exception")).when(mockPreparedStatement).setInt(anyInt(), anyInt());
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByIdWithExecuteQueryNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(null);
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByIdWithExecuteQuerySQLException() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Test Exception"));
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByIdWithMapResultException() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        doReturn(true).when(mockResultSet).next();
        doThrow(new RuntimeException("Test Exception")).when(mockResultSet).getInt(anyString());
        ApplicationDTO result = applicationRepository.getById(1);
        assertNull(result);
    }

    @Test
    void negativeTestGetByNameWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        ApplicationDTO result = applicationRepository.getByName("Test");
        assertNull(result);
    }

    @Test
    void negativeTestGetByNameWithExecuteQueryNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(null);
        ApplicationDTO result = applicationRepository.getByName("Test");
        assertNull(result);
    }

    @Test
    void negativeTestInsert1WithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        ApplicationDTO mockApplication = new ApplicationDTO("Test", "Test Description");
        assertThrows(Exception.class,  () -> applicationRepository.insert(mockApplication));
    }

    @Test
    void negativeTestInsert2WithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        assertThrows(Exception.class,  () -> applicationRepository.insert("Test", "Test Description"));
    }

    @Test
    void negativeTestUpdateWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        ApplicationDTO mockApplication = new ApplicationDTO("Test", "Test Description");
        assertThrows(Exception.class,  () -> applicationRepository.update(mockApplication));
    }

    @Test
    void negativeTestUpdateDescriptionPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        assertThrows(Exception.class,  () -> applicationRepository.updateDescription(1, "Test Description"));
    }

    @Test
    void negativeTestArchiveWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        assertThrows(Exception.class,  () -> applicationRepository.archive(1));
    }

    @Test
    void negativeTestDeleteWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        assertThrows(Exception.class,  () -> applicationRepository.delete(1));
    }

    @Test
    void negativeTestGetAllWithPrepareStatementNull() throws SQLException {
        when(mockConnector.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(null);
        List<ApplicationDTO> result = applicationRepository.getAll();
        assertTrue(result.isEmpty());
    }
}
