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
import jgr.iam.model.dto.UserDTO; // UserDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// UserDTO DBO/Repository
public class UserRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(UserRepository.class.getCanonicalName());

    // iamDB connector
    private iamDBConnectorUtil connector;

    // Constructor
    public UserRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Get by Id
    public UserDTO getById(int id) throws SQLException {
        logger.debug("getById: [" + id + "]");
        String query = "SELECT * FROM User WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // User not found
            }
        }
    }

    // Get by username
    public UserDTO getByName(String username) throws SQLException {
        logger.debug("getByName: [" + username + "]");
        String query = "SELECT * FROM User WHERE username = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // User not found
            }
        }
    }

    // Insert
    public void insert(UserDTO user) throws SQLException {
        logger.debug("insert: [" + user.toString() + "]");
        String query = "INSERT INTO User (username, email, passwordHash, passwordSalt, firstName, lastName, active, archived) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setBytes(3, user.getPasswordHash());
            statement.setBytes(4, user.getPasswordSalt());
            statement.setString(5, user.getFirstName());
            statement.setString(6, user.getLastName());
            statement.setBoolean(7, user.isActive());
            statement.setBoolean(8, user.isArchived());
            statement.executeUpdate();
        }
    }

    // Update
    public void update(UserDTO user) throws SQLException {
        logger.debug("update: [" + user.toString() + "]");
        String query = "UPDATE User SET username = ?, email = ?, passwordHash = ?, passwordSalt = ?, " +
                "firstName = ?, lastName = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setBytes(3, user.getPasswordHash());
            statement.setBytes(4, user.getPasswordSalt());
            statement.setString(5, user.getFirstName());
            statement.setString(6, user.getLastName());
            statement.setInt(7, user.getId());
            statement.executeUpdate();
        }
    }

    // De-Activate
    public void deActivate(int id) throws SQLException {
        logger.debug("deActivate: [" + id + "]");
        _activate(id, false);
    }

    // Re-Activate
    public void reActivate(int id) throws SQLException {
        logger.debug("reActivate: [" + id + "]");
        _activate(id, true);
    }

    // Update Active
    private void _activate(int id, boolean active) throws SQLException {
        String query = "UPDATE User SET active = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, active);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Archive
    public void archive(int id) throws SQLException {
        logger.debug("archive: [" + id + "]");
        _archive(id, true);
    }

    // Undo-Archive
    public void undoArchive(int id) throws SQLException {
        logger.debug("undoArchive: [" + id + "]");
        _archive(id, false);
    }

    // Update Archived
    private void _archive(int id, boolean archived) throws SQLException {
        String query = "UPDATE User SET archived = ? WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

    // Delete
    public void delete(int id) throws SQLException {
        logger.debug("delete: [" + id + "]");
        String query = "DELETE FROM User WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get All Active (and non-archived) User
    public List<UserDTO> getAllActive() throws SQLException {
        return _getAll(true);
    }

    // Get All Inactive (and non-archived) User
    public List<UserDTO> getAllInactive() throws SQLException {
        return _getAll(false);
    }

    // Get All non-archived Users
    private List<UserDTO> _getAll(boolean active) throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT * FROM User WHERE active = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, active);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(_mapResultSet(resultSet));
            }
        }
        return users;
    }

    // Get All Archived User
    public List<UserDTO> getAllArchived() throws SQLException {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT * FROM User WHERE archived = true";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(_mapResultSet(resultSet));
            }
        }
        return users;
    }

    // Map ResulSet to User
    private UserDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        UserDTO user = new UserDTO();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getBytes("passwordHash"));
        user.setPasswordSalt(resultSet.getBytes("passwordSalt"));
        user.setFirstName(resultSet.getString("firstName"));
        user.setLastName(resultSet.getString("lastName"));
        user.setActive(resultSet.getBoolean("active"));
        user.setArchived(resultSet.getBoolean("archived"));
        return user;
    }
}
