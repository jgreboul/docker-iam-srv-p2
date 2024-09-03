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
import jgr.iam.model.dto.PermissionDTO; // PermissionDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// PermissionDTO DBO/Repository
public class PermissionRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public PermissionRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // getById
    public PermissionDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM Permission WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Permission not found
            }
        }
    }

    // getByName
    public PermissionDTO getByName(String name) throws SQLException {
        logger.debug("getByName: [" + name + "]");
        String query = "SELECT * FROM Permission WHERE name = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Permission not found
            }
        }
    }

    // Insert Permission by name and description
    public void insert(String name, String description) throws SQLException {
        logger.debug("insert: [" + name + "," + description + "]");
        String query = "INSERT INTO Permission (name, description, archived) VALUES (?, ?, FALSE)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    // Insert Permission
    public void insert(PermissionDTO permission) throws SQLException {
        logger.debug("insert: [" + permission.toString() + "]");
        String query = "INSERT INTO Permission (name, description, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, permission.getName());
            statement.setString(2, permission.getDescription());
            statement.setBoolean(3, permission.isArchived());
            statement.executeUpdate();
        }
    }

    // Update Permission
    public void update(PermissionDTO permission) throws SQLException {
        logger.debug("update: [" + permission.toString() + "]");
        String query = "UPDATE Permission SET name = ?, description = ?, archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, permission.getName());
            statement.setString(2, permission.getDescription());
            statement.setBoolean(3, permission.isArchived());
            statement.setInt(4, permission.getId());
            statement.executeUpdate();
        }
    }

    // Update Permission Description
    public void updateDescription(int id, String description) throws SQLException {
        logger.debug("updateDescription: [" + id + ", " + description + "]");
        String query = "UPDATE Permission SET description = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive Permission
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        updateArchive(id, true);
    }

    // Undo Archive Permission
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        updateArchive(id, false);
    }

    // Update Archived
    public void updateArchive(int id, boolean archived) throws SQLException {
        String query = "UPDATE Permission SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM Permission WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) Permissions
    public List<PermissionDTO> getAll() throws SQLException {
        return _getAllByArchived(false);
    }

    // Get All Archived Permissions
    public List<PermissionDTO> getAllArchived() throws SQLException {
        return _getAllByArchived(true);
    }

    // Get All Permissions by archived status
    private List<PermissionDTO> _getAllByArchived(boolean archived) throws SQLException {
        List<PermissionDTO> permissions = new ArrayList<>();
        String query = "SELECT * FROM Permission WHERE archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                permissions.add(_mapResultSet(resultSet));
            }
        }
        return permissions;
    }

    // Map ResultSet to Permission object
    private PermissionDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new PermissionDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBoolean("archived")
        );
    }
}
