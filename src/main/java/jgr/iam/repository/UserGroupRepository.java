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
import jgr.iam.model.dto.UserGroupDTO; // UserGroupDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// UserGroupDTO DBO/Repository
public class UserGroupRepository {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserGroupRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public UserGroupRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Get by ID
    public UserGroupDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM UserGroup WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // UserGroup not found
            }
        }
    }

    // getByName
    public UserGroupDTO getByName(String name) throws SQLException {
        logger.debug("getByName: [" + name + "]");
        String query = "SELECT * FROM UserGroup WHERE name = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // UserGroup not found
            }
        }
    }

    // Insert UserGroup (name, description)
    public void insert(String name, String description) throws SQLException {
        logger.debug("insert: [" + name + ", " + description + "]");
        String query = "INSERT INTO UserGroup (name, description) VALUES (?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    // Insert UserGroup
    public void insert(UserGroupDTO userGroup) throws SQLException {
        logger.debug("insert: [" + userGroup.toString() + "]");
        String query = "INSERT INTO UserGroup (name, description, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, userGroup.getName());
            statement.setString(2, userGroup.getDescription());
            statement.setBoolean(3, userGroup.isArchived());
            statement.executeUpdate();
        }
    }

    // Update UserGroup
    public void update(UserGroupDTO userGroup) throws SQLException {
        logger.debug("update: [" + userGroup.toString() + "]");
        String query = "UPDATE UserGroup SET name = ?, description = ?, archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, userGroup.getName());
            statement.setString(2, userGroup.getDescription());
            statement.setBoolean(3, userGroup.isArchived());
            statement.setInt(4, userGroup.getId());
            statement.executeUpdate();
        }
    }

    // Update UserGroup Description
    public void updateDescription(int id, String description) throws SQLException {
        logger.debug("updateDescription: [" + id + ", " + description + "]");
        String query = "UPDATE UserGroup SET description = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive UserGroup
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        _archive(id, true);
    }

    // Undo Archive UserGroup
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        _archive(id, false);
    }

    // Update Archived
    private void _archive(int id, boolean archived) throws SQLException {
        String query = "UPDATE UserGroup SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM UserGroup WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) UserGroups
    public List<UserGroupDTO> getAll() throws SQLException {
        return _getAllByArchived(false);
    }

    // Get All Archived UserGroups
    public List<UserGroupDTO> getAllArchived() throws SQLException {
        return _getAllByArchived(true);
    }

    // Get All UserGroups by archived status
    private List<UserGroupDTO> _getAllByArchived(boolean archived) throws SQLException {
        List<UserGroupDTO> userGroups = new ArrayList<>();
        String query = "SELECT * FROM UserGroup WHERE archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                userGroups.add(_mapResultSet(resultSet));
            }
        }
        return userGroups;
    }

    // Map ResultSet to UserGroup object
    private UserGroupDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new UserGroupDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBoolean("archived")
        );
    }
}
