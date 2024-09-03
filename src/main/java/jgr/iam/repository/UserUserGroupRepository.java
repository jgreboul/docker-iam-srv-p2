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
import jgr.iam.model.dto.UserUserGroupDTO; // UserUserGroupDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// UserUserGroupDTO DBO/Repository
public class UserUserGroupRepository {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserUserGroupRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public UserUserGroupRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Insert UserUserGroup
    public void insert(int userId, int userGroupId) throws SQLException {
        logger.debug("insert: [" + userId + "," + userGroupId + "]");
        String query = "INSERT INTO User_UserGroup (userId, userGroupId, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userGroupId);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        }
    }

    // Get
    public UserUserGroupDTO get(int userId, int userGroupId) throws SQLException {
        logger.debug("get: [" + userId + "," + userGroupId + "]");
        String query = "SELECT * FROM User_UserGroup WHERE userId = ? AND userGroupId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userGroupId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // UserUserGroup not found
            }
        }
    }

    // Delete
    public void delete(int userId, int userGroupId) throws SQLException {
        logger.debug("delete: [" + userId + "," + userGroupId + "]");
        String query = "DELETE FROM User_UserGroup WHERE userId = ? AND userGroupId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userGroupId);
            statement.executeUpdate();
        }
    }

    // Archive UserUserGroup
    public void archive(int userId, int userGroupId) throws SQLException {
        logger.debug("archive: [" + userId + "," + userGroupId + "]");
        _archive(userId, userGroupId, true);
    }

    // Undo Archive UserUserGroup
    public void undoArchive(int userId, int userGroupId) throws SQLException {
        logger.debug("undoArchive: [" + userId + "," + userGroupId + "]");
        _archive(userId, userGroupId, false);
    }

    // Update Archived
    private void _archive(int userId, int userGroupId, boolean archived) throws SQLException {
        String query = "UPDATE User_UserGroup SET archived = ? WHERE userId = ? AND userGroupId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, userId);
            statement.setInt(3, userGroupId);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) UserUserGroupDTO for a specific UserGroup
    public List<UserUserGroupDTO> getAllForUserGroup(int userGroupId) throws SQLException {
        return _getAllByArchivedForUserGroup(userGroupId, false);
    }

    // Get All Archived UserUserGroupDTO for a specific UserGroup
    public List<UserUserGroupDTO> getAllArchivedForUserGroup(int userGroupId) throws SQLException {
        return _getAllByArchivedForUserGroup(userGroupId,true);
    }

    // Get All UserUserGroupDTO for a specific UserGroup by archived status
    private List<UserUserGroupDTO> _getAllByArchivedForUserGroup(int userGroupId, boolean archived) throws SQLException {
        List<UserUserGroupDTO> userUserGroups = new ArrayList<>();
        String query = "SELECT * FROM User_UserGroup WHERE userGroupId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userGroupId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userUserGroups.add(_mapResultSet(resultSet));
            }
        }
        return userUserGroups;
    }

    // Get All (non-archived) UserUserGroupDTO for a specific User
    public List<UserUserGroupDTO> getAllForUser(int userId) throws SQLException {
        return _getAllByArchivedForUser(userId, false);
    }

    // Get All Archived UserUserGroupDTO for a specific User
    public List<UserUserGroupDTO> getAllArchivedForUser(int userId) throws SQLException {
        return _getAllByArchivedForUser(userId,true);
    }

    // Get All  UserUserGroupDTO for a specific User by archived status
    private List<UserUserGroupDTO> _getAllByArchivedForUser(int userId, boolean archived) throws SQLException {
        List<UserUserGroupDTO> userUserGroups = new ArrayList<>();
        String query = "SELECT * FROM User_UserGroup WHERE userId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userUserGroups.add(_mapResultSet(resultSet));
            }
        }
        return userUserGroups;
    }

    // Map ResultSet to UserUserGroupDTO object
    private UserUserGroupDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new UserUserGroupDTO(
                resultSet.getInt("userId"),
                resultSet.getInt("userGroupId"),
                resultSet.getBoolean("archived")
        );
    }
}
