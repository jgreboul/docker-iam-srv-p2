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
import jgr.iam.model.dto.UserGroupRoleDTO; // UserGroupRoleDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// UserGroupRoleDTO DBO/Repository
public class UserGroupRoleRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(UserGroupRoleRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public UserGroupRoleRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Insert UserGroupRole
    public void insert(int userGroupId, int roleId) throws SQLException {
        logger.debug("insert: [" + userGroupId + "," + roleId + "]");
        String query = "INSERT INTO UserGroup_Role (userGroupId, roleId, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userGroupId);
            statement.setInt(2, roleId);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        }
    }

    // Get
    public UserGroupRoleDTO get(int userGroupId, int roleId) throws SQLException {
        logger.debug("get: [" + userGroupId + "," + roleId + "]");
        String query = "SELECT * FROM UserGroup_Role WHERE userGroupId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userGroupId);
            statement.setInt(2, roleId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // UserGroupRoleDTO not found
            }
        }
    }

    // Delete
    public void delete(int userGroupId, int roleId) throws SQLException {
        logger.debug("delete: [" + userGroupId + "," + roleId + "]");
        String query = "DELETE FROM UserGroup_Role WHERE userGroupId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userGroupId);
            statement.setInt(2, roleId);
            statement.executeUpdate();
        }
    }

    // Archive UserGroupRoleDTO
    public void archive(int userGroupId, int roleId) throws SQLException {
        logger.debug("archive: [" + userGroupId + "," + roleId + "]");
        _archive(userGroupId, roleId, true);
    }

    // Undo Archive UserGroupRoleDTO
    public void undoArchive(int userGroupId, int roleId) throws SQLException {
        logger.debug("undoArchive: [" + userGroupId + "," + roleId + "]");
        _archive(userGroupId, roleId, false);
    }

    // Update Archived
    private void _archive(int userGroupId, int roleId, boolean archived) throws SQLException {
        String query = "UPDATE UserGroup_Role SET archived = ? WHERE userGroupId = ? AND roleId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, userGroupId);
            statement.setInt(3, roleId);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) UserGroupRole for a specific UserGroup
    public List<UserGroupRoleDTO> getAllForUserGroup(int userGroupId) throws SQLException {
        return _getAllByArchivedForUserGroup(userGroupId, false);
    }

    // Get All Archived UserGroupRole for a specific User
    public List<UserGroupRoleDTO> getAllArchivedForUserGroup(int userGroupId) throws SQLException {
        return _getAllByArchivedForUserGroup(userGroupId,true);
    }

    // Get All UserGroupRole for a specific UserGroup by archived status
    private List<UserGroupRoleDTO> _getAllByArchivedForUserGroup(int userGroupId, boolean archived) throws SQLException {
        List<UserGroupRoleDTO> userGroupRoles = new ArrayList<>();
        String query = "SELECT * FROM UserGroup_Role WHERE userGroupId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userGroupId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userGroupRoles.add(_mapResultSet(resultSet));
            }
        }
        return userGroupRoles;
    }

    // Get All (non-archived) UserGroupRoleDTO for a specific Role
    public List<UserGroupRoleDTO> getAllForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId, false);
    }

    // Get All Archived UserGroupRoleDTO for a specific Role
    public List<UserGroupRoleDTO> getAllArchivedForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId,true);
    }

    // Get All UserGroupRoleDTO for a specific Role by archived status
    private List<UserGroupRoleDTO> _getAllByArchivedForRole(int roleId, boolean archived) throws SQLException {
        List<UserGroupRoleDTO> userGroupRoles = new ArrayList<>();
        String query = "SELECT * FROM UserGroup_Role WHERE roleId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userGroupRoles.add(_mapResultSet(resultSet));
            }
        }
        return userGroupRoles;
    }

    // Map ResultSet to UserGroupRoleDTO object
    private UserGroupRoleDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new UserGroupRoleDTO(
                resultSet.getInt("userGroupId"),
                resultSet.getInt("roleId"),
                resultSet.getBoolean("archived")
        );
    }
}
