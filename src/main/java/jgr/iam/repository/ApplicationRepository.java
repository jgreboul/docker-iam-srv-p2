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
import jgr.iam.model.dto.ApplicationDTO; // ApplicationDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// ApplicationDTO DBO/Repository
public class ApplicationRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(ApplicationRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public ApplicationRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // get by ID
    public ApplicationDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM Application WHERE id = ?";
        ApplicationDTO result = null;
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    result = _mapResultSet(resultSet);
                }
            }
        }
        catch(Exception e) {
            logger.error("getById(" + id + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
        }
        return result;
    }

    // get by Name
    public ApplicationDTO getByName(String name) throws SQLException {
        logger.debug("getByName: [" + name + "]");
        String query = "SELECT * FROM Application WHERE name = ?";
        ApplicationDTO result = null;
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet != null) {
                if (resultSet.next()) {
                    result = _mapResultSet(resultSet);
                }
            }
        }
        catch(Exception e) {
            logger.error("getByName(" + name + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
        }
        return result;
    }

    // Insert Application
    public void insert(ApplicationDTO application) throws SQLException {
        logger.debug("insert: [" + application.toString() + "]");
        String query = "INSERT INTO Application (name, description, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, application.getName());
            statement.setString(2, application.getDescription());
            statement.setBoolean(3, application.isArchived());
            statement.executeUpdate();
        }
    }

    // Insert Application (with name and description only
    public void insert(String name, String description) throws SQLException {
        logger.debug("insert: [" + name + ", " + description + "]");
        String query = "INSERT INTO Application (name, description) VALUES (?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, description);
            statement.executeUpdate();
        }
    }

    // Update Application
    public void update(ApplicationDTO application) throws SQLException {
        logger.debug("update: [" + application.toString() + "]");
        String query = "UPDATE Application SET name = ?, description = ?, archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, application.getName());
            statement.setString(2, application.getDescription());
            statement.setBoolean(3, application.isArchived());
            statement.setInt(4, application.getId());
            statement.executeUpdate();
        }
    }

    // Update Application Description
    public void updateDescription(int id, String description) throws SQLException {
        logger.debug("updateDescription: [" + id + ", " + description + "]");
        String query = "UPDATE Application SET description = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive Application
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        _archive(id, true);
    }

    // Undo Archive
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        _archive(id, false);
    }

    // Update Archived
    private void _archive(int id, boolean archived) throws SQLException {
        String query = "UPDATE Application SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM Application WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) Applications
    public List<ApplicationDTO> getAll() throws SQLException {
        return _getAll(false);
    }

    // Get All Archived Applications
    public List<ApplicationDTO> getAllArchived() throws SQLException {
        return _getAll(true);
    }

    // Get All Applications
    private List<ApplicationDTO> _getAll(boolean archived) throws SQLException {
        List<ApplicationDTO> applications = new ArrayList<>();
        String query = "SELECT * FROM Application WHERE archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                applications.add(_mapResultSet(resultSet));
            }
        }
        catch(Exception e) {
            logger.error("_getAll(" + archived + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
        }
        return applications;
    }

    // Map ResultSet to Application object
    private ApplicationDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new ApplicationDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBoolean("archived")
        );
    }
}
