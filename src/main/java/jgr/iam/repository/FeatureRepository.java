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
import jgr.iam.model.dto.FeatureDTO; // FeatureDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// FeatureDTO DBO/Repository
public class FeatureRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(FeatureRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public FeatureRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // getById
    public FeatureDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM Feature WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Feature not found
            }
        }
    }

    // getByName
    public FeatureDTO getByName(int applicationId, String name) throws SQLException {
        logger.debug("getByName: [" + name + ", " + applicationId + "]");
        String query = "SELECT * FROM Feature WHERE name = ? AND applicationId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, applicationId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Feature not found
            }
        }
    }

    // Insert Feature
    public void insert(FeatureDTO feature) throws SQLException {
        logger.debug("insert: [" + feature.toString() + "]");
        String query = "INSERT INTO Feature (name, description, applicationId, archived) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, feature.getName());
            statement.setString(2, feature.getDescription());
            statement.setInt(3, feature.getApplicationId());
            statement.setBoolean(4, feature.isArchived());
            statement.executeUpdate();
        }
    }

    // Insert Feature
    public void insert(int appId, String name, String description) throws SQLException {
        logger.debug("insert: [" + appId + ", " + name + ", " + description + "]");
        String query = "INSERT INTO Feature (name, description, applicationId) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setInt(3, appId);
            statement.executeUpdate();
        }
    }

    // Update Feature
    public void update(FeatureDTO feature) throws SQLException {
        logger.debug("update: [" + feature.toString() + "]");
        String query = "UPDATE Feature SET name = ?, description = ?, applicationId = ?, archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, feature.getName());
            statement.setString(2, feature.getDescription());
            statement.setInt(3, feature.getApplicationId());
            statement.setBoolean(4, feature.isArchived());
            statement.setInt(5, feature.getId());
            statement.executeUpdate();
        }
    }

    // Update Feature Description
    public void updateDescription(int id, String description) throws SQLException {
        logger.debug("updateDescription: [" + id + ", " + description + "]");
        String query = "UPDATE Feature SET description = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive Feature
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        _archive(id, true);
    }

    // Undo Archive Feature
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        _archive(id, false);
    }

    // Update Archived
    private void _archive(int id, boolean archived) throws SQLException {
        String query = "UPDATE Feature SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM Feature WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) Features
    public List<FeatureDTO> getAll() throws SQLException {
        return _getAllByArchived(false);
    }

    // Get All Archived Features
    public List<FeatureDTO> getAllArchived() throws SQLException {
        return _getAllByArchived(true);
    }

    // Get All Features by archived status
    private List<FeatureDTO> _getAllByArchived(boolean archived) throws SQLException {
        List<FeatureDTO> features = new ArrayList<>();
        String query = "SELECT * FROM Feature WHERE archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                features.add(_mapResultSet(resultSet));
            }
        }
        return features;
    }

    // Get All (non-archived) Features for a defined Application
    public List<FeatureDTO> getAll(int applicationId) throws SQLException {
        List<FeatureDTO> features = new ArrayList<>();
        String query = "SELECT * FROM Feature WHERE applicationId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, applicationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                features.add(_mapResultSet(resultSet));
            }
        }
        return features;
    }

    // Get All (non-archived) Features for a defined Application
    public int getAllCount(int applicationId) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(id) as total FROM Feature WHERE applicationId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, applicationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("total");
            }
        }
        return count;
    }

    // Map ResultSet to Feature object
    private FeatureDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new FeatureDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getInt("applicationId"),
                resultSet.getBoolean("archived")
        );
    }
}
