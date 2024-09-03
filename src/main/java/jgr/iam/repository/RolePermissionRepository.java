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
import jgr.iam.model.dto.RolePermissionDTO; // RolePermissionDTO
import jgr.iam.util.iamDBConnectorUtil; // iamDB Database Connector

// RolePermissionDTO DBO/Repository
public class RolePermissionRepository {

    // Logger
    private final static Logger logger = LogManager.getLogger(RolePermissionRepository.class.getCanonicalName());

    // iamDB Connector
    private iamDBConnectorUtil connector;

    // Constructor
    public RolePermissionRepository(iamDBConnectorUtil connector) {
        this.connector = connector;
    }

    // Insert RolePermission
    public void insert(int roleId, int permissionId) throws SQLException {
        logger.debug("insert: [" + roleId + "," + permissionId + "]");
        String query = "INSERT INTO Role_Permission (roleId, permissionId, archived) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setInt(2, permissionId);
            statement.setBoolean(3, false);
            statement.executeUpdate();
        }
    }

    // Get
    public RolePermissionDTO get(int roleId, int permissionId) throws SQLException {
        logger.debug("get: [" + roleId + "," + permissionId + "]");
        String query = "SELECT * FROM Role_Permission WHERE roleId = ? AND permissionId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setInt(2, permissionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return _mapResultSet(resultSet);
            } else {
                return null; // RolePermissionDTO not found
            }
        }
    }

    // Delete
    public void delete(int roleId, int permissionId) throws SQLException {
        logger.debug("delete: [" + roleId + "," + permissionId + "]");
        String query = "DELETE FROM Role_Permission WHERE roleId = ? AND permissionId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setInt(2, permissionId);
            statement.executeUpdate();
        }
    }

    // Archive UserUserGroup
    public void archive(int roleId, int permissionId) throws SQLException {
        logger.debug("archive: [" + roleId + "," + permissionId + "]");
        _archive(roleId, permissionId, true);
    }

    // Undo Archive UserUserGroup
    public void undoArchive(int roleId, int permissionId) throws SQLException {
        logger.debug("undoArchive: [" + roleId + "," + permissionId + "]");
        _archive(roleId, permissionId, false);
    }

    // Update Archived
    private void _archive(int roleId, int permissionId, boolean archived) throws SQLException {
        String query = "UPDATE Role_Permission SET archived = ? WHERE roleId = ? AND permissionId = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setBoolean(1, archived);
            statement.setInt(2, roleId);
            statement.setInt(3, permissionId);
            statement.executeUpdate();
        }
    }

    // Get All (non-archived) RolePermission for a specific Role
    public List<RolePermissionDTO> getAllForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId, false);
    }

    // Get All Archived RolePermission for a specific Role
    public List<RolePermissionDTO> getAllArchivedForRole(int roleId) throws SQLException {
        return _getAllByArchivedForRole(roleId,true);
    }

    // Get All RolePermission for a specific Role by archived status
    private List<RolePermissionDTO> _getAllByArchivedForRole(int roleId, boolean archived) throws SQLException {
        List<RolePermissionDTO> rolePermissions = new ArrayList<>();
        String query = "SELECT * FROM Role_Permission WHERE roleId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rolePermissions.add(_mapResultSet(resultSet));
            }
        }
        return rolePermissions;
    }

    // Get All (non-archived) RolePermission Count for a specific Role
    public int getAllCountForRole(int roleId) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(permissionId) as total FROM Role_Permission WHERE roleId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, roleId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("total");
            }
        }
        return count;
    }

    // Get All (non-archived) RolePermissionDTO for a specific Permission
    public List<RolePermissionDTO> getAllForPermission(int permissionId) throws SQLException {
        return _getAllByArchivedFoPermission(permissionId, false);
    }

    // Get All Archived RolePermissionDTO for a specific Permission
    public List<RolePermissionDTO> getAllArchivedForPermission(int permissionId) throws SQLException {
        return _getAllByArchivedFoPermission(permissionId,true);
    }

    // Get All RolePermissionDTO for a specific Permission by archived status
    private List<RolePermissionDTO> _getAllByArchivedFoPermission(int permissionId, boolean archived) throws SQLException {
        List<RolePermissionDTO> rolePermissions = new ArrayList<>();
        String query = "SELECT * FROM Role_Permission WHERE permissionId = ? AND archived = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, permissionId);
            statement.setBoolean(2, archived);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                rolePermissions.add(_mapResultSet(resultSet));
            }
        }
        return rolePermissions;
    }

    // Get All (non-archived) RolePermission Count for a specific Permission
    public int getAllCountForPermission(int permissionId) throws SQLException {
        int count = 0;
        String query = "SELECT COUNT(roleId) as total FROM Role_Permission WHERE permissionId = ? AND archived = FALSE";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, permissionId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt("total");
            }
        }
        return count;
    }

    // Get All (non-archived) Role Extended Names for a specific Permission
    public List<String> getAllRoleExtendedNameForPermission(int permissionId) throws SQLException {
        List<String> result = new ArrayList<>();
        String query = "SELECT concat(a.name, \".\" , f.name, \".\", r.name) as rolename FROM Application a, Feature f, Role r, Role_Permission rp WHERE rp.permissionId = ? AND rp.archived = FALSE AND rp.roleId = r.id AND r.featureId = f.id AND f.applicationId = a.id;";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, permissionId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString("rolename"));
            }
        }
        return result;
    }

    // Map ResultSet to RolePermissionDTO object
    private RolePermissionDTO _mapResultSet(ResultSet resultSet) throws SQLException {
        return new RolePermissionDTO(
                resultSet.getInt("roleId"),
                resultSet.getInt("permissionId"),
                resultSet.getBoolean("archived")
        );
    }
}
