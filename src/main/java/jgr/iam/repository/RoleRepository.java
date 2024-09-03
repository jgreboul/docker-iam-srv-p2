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
import jgr.iam.model.dto.RoleDTO; // RoleDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// RoleDTO DBO/Repository
public class RoleRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(RoleRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public RoleRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    //getById
    public RoleDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM Role WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Role not found
            }
        }
    }

    // getByName
    public RoleDTO getByName(int featureId, String name) throws SQLException {
        logger.debug("getByName: [" + name + ", " + featureId + "]");
        String query = "SELECT * FROM Role WHERE name = ? AND featureId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt( 2, featureId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Role not found
            }
        }
    }

    // Insert Role
    public void insert(RoleDTO role) throws SQLException {
        logger.debug("insert: [" + role.toString() + "]");
        String query = "INSERT INTO Role (name, description, featureId, archived) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, role.getName());
            statement.setString(2, role.getDescription());
            statement.setInt(3, role.getFeatureId());
            statement.setBoolean(4, role.isArchived());
            statement.executeUpdate();
        }
    }

    // Insert Role by Name
    public void insert(int featId, String name, String description) throws SQLException {
        logger.debug("insert: [" + featId + ", " + name + ", " + description + "]");
        String query = "INSERT INTO Role (name, description, featureId) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, featId);
            statement.executeUpdate();
        }
    }

    // Update Role
    public void update(RoleDTO role) throws SQLException {
        logger.debug("update: [" + role.toString() + "]");
        String query = "UPDATE Role SET name = ?, description = ?, featureId = ?, archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, role.getName());
            statement.setString(2, role.getDescription());
            statement.setInt(3, role.getFeatureId());
            statement.setBoolean(4, role.isArchived());
            statement.setInt(5, role.getId());
            statement.executeUpdate();
        }
    }

    // Update Role Description
    public void updateDescription(int id, String description) throws SQLException {
        logger.debug("updateDescription: [" + id + ", " + description + "]");
        String query = "UPDATE Role SET description = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive Role
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        _archive(id, true);
    }

    // Undo Archive Role
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        _archive(id, false);
    }

    // Update Archived
    private void _archive(int id, boolean archived) throws SQLException {
        String query = "UPDATE Role SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM Role WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) Roles
    public List<RoleDTO> getAll() throws SQLException {
        return _getAllByArchived(false);
    }

    // Get All Archived Roles
    public List<RoleDTO> getAllArchived() throws SQLException {
        return _getAllByArchived(true);
    }

    // Get All Roles by archived status
    private List<RoleDTO> _getAllByArchived(boolean archived) throws SQLException {
        List<RoleDTO> roles = new ArrayList<>();
        String query = "SELECT * FROM Role WHERE archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                roles.add(_mapResultSet(resultSet));
            }
        }
        return roles;
    }

    // Get All (non-archived) Roles for a defined Feature
    public List<RoleDTO> getAll(int featureId) throws SQLException {
        List<RoleDTO> roles = new ArrayList<>();
        String query = "SELECT * FROM Role WHERE featureId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, featureId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                roles.add(_mapResultSet(resultSet));
            }
        }
        return roles;
    }

    // Get All (non-archived) Roles for a defined Feature
    public int getAllCount(int featureId) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(id) as total FROM Role WHERE featureId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, featureId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("total");
            }
        }
        return count;
    }

    // Map ResultSet to Role object
    private RoleDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new RoleDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("featureId"),
                resultSet.getBoolean("archived")
        );
    }
}
