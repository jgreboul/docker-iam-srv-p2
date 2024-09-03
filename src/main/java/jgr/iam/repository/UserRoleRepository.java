package jgr.iam.repository;

// External Objects
import java.sql.PreparedStatement; // https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html
import java.sql.ResultSet; // https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html

// Internal Objects
import jgr.iam.model.dto.UserRoleDTO; // UserRoleDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// UserRoleDTO DBO/Repository
public class UserRoleRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(UserRoleRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public UserRoleRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Insert UserRole
    public void insert(int userId, int roleId) throws SQLException {
        logger.debug("insert: [" + userId + "," + roleId + "]");
        String query = "INSERT INTO User_Role (userId, roleId, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        }
    }

    // Get
    public UserRoleDTO get(int userId, int roleId) throws SQLException {
        logger.debug("get: [" + userId + "," + roleId + "]");
        String query = "SELECT * FROM User_Role WHERE userId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // UserRoleDTO not found
            }
        }
    }

    // Delete
    public void delete(int userId, int roleId) throws SQLException {
        logger.debug("delete: [" + userId + "," + roleId + "]");
        String query = "DELETE FROM User_Role WHERE userId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        }
    }

    // Archive UserRoleDTO
    public void archive(int userId, int roleId) throws SQLException {
        logger.debug("archive: [" + userId + "," + roleId + "]");
        _archive(userId, roleId, true);
    }

    // Undo Archive UserRoleDTO
    public void undoArchive(int userId, int roleId) throws SQLException {
        logger.debug("undoArchive: [" + userId + "," + roleId + "]");
        _archive(userId, roleId, false);
    }

    // Update Archived
    private void _archive(int userId, int roleId, boolean archived) throws SQLException {
        String query = "UPDATE User_Role SET archived = ? WHERE userId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, userId);
            statement.setInt(3, roleId);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) UserRole for a specific User
    public List<UserRoleDTO> getAllForUser(int userId) throws SQLException {
        return _getAllByArchivedForUser(userId, false);
    }

    // Get All Archived UserRole for a specific User
    public List<UserRoleDTO> getAllArchivedForUser(int userId) throws SQLException {
        return _getAllByArchivedForUser(userId,true);
    }

    // Get All UserRole for a specific User by archived status
    private List<UserRoleDTO> _getAllByArchivedForUser(int userId, boolean archived) throws SQLException {
        List<UserRoleDTO> userRoles = new ArrayList<>();
        String query = "SELECT * FROM User_Role WHERE userId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userRoles.add(_mapResultSet(resultSet));
            }
        }
        return userRoles;
    }

    // Get All (non-archived) UserRoleDTO for a specific Role
    public List<UserRoleDTO> getAllForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId, false);
    }

    // Get All Archived UserRoleDTO for a specific Role
    public List<UserRoleDTO> getAllArchivedForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId,true);
    }

    // Get All UserRoleDTO for a specific Role by archived status
    private List<UserRoleDTO> _getAllByArchivedForRole(int roleId, boolean archived) throws SQLException {
        List<UserRoleDTO> userRoles = new ArrayList<>();
        String query = "SELECT * FROM User_Role WHERE roleId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userRoles.add(_mapResultSet(resultSet));
            }
        }
        return userRoles;
    }

    // Map ResultSet to UserRoleDTO object
    private UserRoleDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new UserRoleDTO(
                resultSet.getInt("userId"),
                resultSet.getInt("roleId"),
                resultSet.getBoolean("archived")
        );
    }
}
