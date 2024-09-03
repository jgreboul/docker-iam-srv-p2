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
import jgr.iam.model.dto.MetadataDTO; // MetadataDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// MetadataDTO DBO/Repository
public class MetadataRepository {
    // Logger
    private final static Logger logger = LogManager.getLogger(MetadataRepository.class.getCanonicalName());

    // iamDB connector
    private iamDBConnectorUtil connector;

    // Table Metadata Name
    private String tableMetadata;

    // Constructor
    public MetadataRepository(iamDBConnectorUtil connector, String tableMetadata) {
        this.connector = connector;
        this.tableMetadata = tableMetadata;
    }

    // Insert Metadata
    public void insert(int refid, String name, String value) throws SQLException {
        logger.debug("insert: [" + refid + ", " + name+ ", " + value  + "]");
        String query = String.format("INSERT INTO %s (refid, name, value) VALUES (?, ?, ?)", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            statement.setString(2, name);
            statement.setString(3, value);
            logger.debug("insert: [" + statement.toString() + "].");
            statement.executeUpdate();
        }
    }

    // get Metadata
    public MetadataDTO get(int refid, String name) throws SQLException {
        logger.debug("get: [" + refid + ", " + name + "]");
        String query = String.format("SELECT * FROM %s WHERE refid = ? and name = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            statement.setString(2, name);
            logger.debug("get: [" + statement.toString() + "].");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // Metadata not found
            }
        }
    }

    // getValue (non-archived)
    public String getValue(int refid, String name) throws SQLException {
        logger.debug("getValue: [" + refid + ", " + name + "]");
        String query = String.format("SELECT * FROM %s WHERE refid = ? and name = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            statement.setString(2, name);
            logger.debug("getValue: [" + statement.toString() + "].");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet).getValue();
            } else {
                return null; // Metadata not found
            }
        }
    }

    // Update Value
    public void updateValue(int refid, String name, String value) throws SQLException {
        logger.debug("updateValue: [" + refid + ", " + name + ", " + value + "]");
        String query = String.format("UPDATE %s SET value = ? WHERE refid = ? AND name = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, value);
            statement.setInt(2, refid);
            statement.setString(3, name);
            logger.debug("update: [" + statement.toString() + "].");
            statement.executeUpdate();
        }
    }

    // Archive
    public void archive(int refid, String name) throws SQLException {
        logger.debug("archive: [" + refid + ", " + name + "]");
        updateArchive(refid, name, true);
    }

    // Undo-Archive
    public void undoArchive(int refid, String name) throws SQLException {
        logger.debug("undoArchive: [" + refid + ", " + name + "]");
        updateArchive(refid, name, false);
    }

    // Update Archived
    public void updateArchive(int refid, String name, boolean archived) throws SQLException {
        String query = String.format("UPDATE %s SET archived = ? WHERE refid = ? AND name = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, refid);
            statement.setString(3, name);
            logger.debug("_archive: [" + statement.toString() + "].");
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int refid, String name) throws SQLException {
        logger.debug("delete: [" + refid + ", " + name + "]");
        String query = String.format("DELETE FROM %s WHERE refid = ? AND name = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            statement.setString(2, name);
            logger.debug("delete: [" + statement.toString() + "].");
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) Metadata for this reference
    public List<MetadataDTO> getAll(int refid) throws SQLException {
        return _getAll( refid, false);
    }

    // Get All Archived Metadata for this reference
    public List<MetadataDTO> getAllArchived(int refid) throws SQLException {
        return _getAll( refid, true);
    }

    // Get All Metadata  for this reference
    private List<MetadataDTO> _getAll(int refid, Boolean archived) throws SQLException {
        List<MetadataDTO> metadataList = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE refid = ? AND archived = ?", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            statement.setBoolean(2, archived);
            logger.debug("_getAll: [" + statement.toString() + "].");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                metadataList.add(_mapResultSet(resultSet));
            }
        }
        return metadataList;
    }

    // Get All Metadata  for this reference
    public int getAllCount(int refid) throws SQLException {
        int result = 0;
        String query = String.format("SELECT COUNT(name) as total FROM %s WHERE refid = ? AND archived = FALSE", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, refid);
            logger.debug("getAllCount: [" + statement.toString() + "].");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result = resultSet.getInt("total");
            }
        }
        return result;
    }

    // Get All Archived Metadata
    public List<MetadataDTO> getAllArchived() throws SQLException {
        List<MetadataDTO> metadataList = new ArrayList<>();
        String query = String.format("SELECT * FROM %s WHERE archived = true", tableMetadata);
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            logger.debug("getAllArchived: [" + statement.toString() + "].");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                metadataList.add(_mapResultSet(resultSet));
            }
        }
        return metadataList;
    }

    // Map ResulSet to Metatada
    private MetadataDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new MetadataDTO(
                resultSet.getInt("refid"),
                resultSet.getString("name"),
                resultSet.getString("value"),
                resultSet.getBoolean("archived"));
    }
}
