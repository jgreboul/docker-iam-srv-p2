package jgr.iam.manager;

// External Objects
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

// Internal Objects
import jgr.iam.constant.iamDBMetadataTable;
import jgr.iam.util.iamDBConnectorUtil;
import jgr.iam.enums.RequestType;
import jgr.iam.repository.PermissionRepository;
import jgr.iam.repository.MetadataRepository;
import jgr.iam.repository.RolePermissionRepository;
import jgr.iam.model.bo.Permission;
import jgr.iam.model.dto.PermissionDTO;
import jgr.iam.model.dto.RolePermissionDTO;
import jgr.iam.model.bo.Metadata;
import jgr.iam.model.dto.MetadataDTO;

// Permission Manager
@Getter
@Setter
public class PermissionManager extends BaseManager {

    // Repository objects
    private PermissionRepository permRepo; // Permission Repository
    private RolePermissionRepository rpRepo; // Role-Permission Repository
    private MetadataRepository permMetadataRepo; // Permission Metadata Repository

    // Constructor
    public PermissionManager()
    {
        super.setLogger(LogManager.getLogger(PermissionManager.class.getCanonicalName()));
        super.setConnector(new iamDBConnectorUtil());
        permRepo = new PermissionRepository(connector);
        rpRepo = new RolePermissionRepository(connector);
        permMetadataRepo = new MetadataRepository(connector, iamDBMetadataTable.PERM);
    }

    // Get All Permissions
    public List<Permission> getPermissions(RequestType requestType, boolean archived) {
        logger.debug("getPermissions(" + requestType.toString() +  ", " + archived + ").");
        List<Permission> result = new ArrayList<>();
        // Connect
        if(super.connect())
        {
            try {
                // Get basic (archived or non-archived)
                List<PermissionDTO> permsList = archived ? permRepo.getAllArchived() : permRepo.getAll();
                if(permsList != null) {
                    for (PermissionDTO permDTO : permsList) {
                        result.add(new Permission(permDTO));
                    }
                    // Get added info
                    if (requestType == RequestType.SUMMARY) {
                        for (Permission perm : result) {
                            _addSummaryInfo(perm);
                        }
                    } else if (requestType == RequestType.DETAIL) {
                        for (Permission perm : result) {
                            _addDetailInfo(perm);
                        }
                    }
                }
            }
            catch(SQLException e) {
                logger.error("getPermissions(" + requestType.toString() + ", " + archived + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Add Summary Information
    private void _addSummaryInfo(Permission perm) throws SQLException {
        logger.debug("_addSummaryInfo(" + perm.getName() + "].");
        perm.setRoleCount(rpRepo.getAllCountForPermission(perm.getId()));
        perm.setMetadataCount(permMetadataRepo.getAllCount(perm.getId()));
    }

    // Add Detail Information
    private void _addDetailInfo(Permission perm) throws SQLException {
        logger.debug("_addDetailInfo(" + perm.getName() + "].");

        // Roles (extended name)
        List<RolePermissionDTO> rpList = rpRepo.getAllForPermission(perm.getId());
        perm.setRoleCount(rpList == null ? 0 : rpList.size());
        List<String> roleExtendedNames = rpRepo.getAllRoleExtendedNameForPermission(perm.getId());
        if(roleExtendedNames != null) {
            for (String role : roleExtendedNames) {
                perm.getRoleExtendedNameList().add(role);
            }
        }

        // Metadata
        List<MetadataDTO> metas = permMetadataRepo.getAll(perm.getId());
        perm.setMetadataCount(metas == null ? 0 : metas.size());
        if (metas != null ) {
            for (MetadataDTO meta : metas) {
                perm.getMetadataList().add(new Metadata(meta));
            }
        }
    }

    // Get Permission
    public Permission getPermission(int id, RequestType requestType) {
        logger.debug("getPermission(" + id + ", " + requestType.toString() + ").");
        Permission result = null;
        // Connect
        if(super.connect())
        {
            try {
                // Get PermissionDTO
                PermissionDTO permDTO = permRepo.getById(id);
                if(permDTO == null)
                    return null;

                result = new Permission(permDTO);
                // Get added info
                if(requestType == RequestType.SUMMARY) {
                    _addSummaryInfo(result);
                }
                else if(requestType == RequestType.DETAIL) {
                    _addDetailInfo(result);
                }
            }
            catch(SQLException e) {
                logger.error("getPermission(" + id + ", " + requestType.toString() + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Get Permission by Name
    public Permission getPermission(String name, RequestType requestType) {
        logger.debug("getPermission(" + name + ", " + requestType + ").");
        Permission result = null;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                PermissionDTO permDTO = permRepo.getByName(name);
                if (permDTO != null) {
                    result = new Permission(permDTO);
                    // Get added info
                    if(requestType == RequestType.SUMMARY) {
                        _addSummaryInfo(result);
                    }
                    else if(requestType == RequestType.DETAIL) {
                        _addDetailInfo(result);
                    }
                }
            }
            catch(SQLException e) {
                logger.error("getPermission(" + name + ", " + requestType + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Create Permission
    public Permission createPermission(String name, String description) {
        logger.info("createPermission(" + name + ", " + description + ").");
        Permission result = null;
        // Connect
        if(super.connect())
        {
            try {
                // Insert
                permRepo.insert(name, description);
                // Retrieve
                PermissionDTO permDTO = permRepo.getByName(name);
                result = new Permission(permDTO);
                _addSummaryInfo(result);
            }
            catch(SQLException e) {
                logger.error("createPermission(" + name + ", " + description + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Delete Permission
    public boolean deletePermission(int id) {
        logger.info("deletePermission(" + id + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Delete
                permRepo.delete(id);
                // Retrieve
                PermissionDTO permDTO = permRepo.getById(id);
                logger.info("deletePermission(" + (permDTO == null) + ").");
                result = (permDTO == null);
            }
            catch(SQLException e) {
                logger.error("deletePermission(" + id + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Update Permission Description
    public boolean updatePermissionDescription(int id, String description) {
        logger.debug("updatePermissionDescription(" + id + ", " + description + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                PermissionDTO permDTO = permRepo.getById(id);
                if (permDTO != null) {
                    // Update
                    permRepo.updateDescription(id, description);
                    // Check
                    permDTO = permRepo.getById(id);
                    result = Objects.equals(description, permDTO.getDescription());
                }
            }
            catch(SQLException e) {
                logger.error("updatePermissionDescription(" + id + ", " + description + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Update Permission Archived status
    public boolean updatePermissionArchived(int id, boolean archived) {
        logger.debug("updatePermissionArchived(" + id + ", " + archived + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                PermissionDTO permDTO = permRepo.getById(id);
                if (permDTO != null) {
                    permRepo.updateArchive(id, archived);
                    // Check
                    permDTO = permRepo.getById(id);
                    result = Objects.equals(archived, permDTO.isArchived());
                }
            }
            catch(SQLException e) {
                logger.error("updatePermissionArchived(" + id + ", " + archived + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Get Metadata
    public Metadata getMetadata(int permId, String name) {
        logger.debug("getMetadata(" + permId + ", " + name + ").");
        Metadata result = null;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                MetadataDTO metaDTO = permMetadataRepo.get(permId, name);
                if (metaDTO != null) {
                    result = new Metadata(metaDTO);
                }
            }
            catch(SQLException e) {
                logger.error("getMetadata(" + + permId + ", " + name + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Create Metadata
    public Permission createMetadata(int permId, String name, String value) {
        logger.info("createMetadata(" + permId + "," + name + ", " + value + ").");
        Permission result = null;
        // Connect
        if(super.connect())
        {
            try {
                // Insert
                permMetadataRepo.insert(permId, name, value);
                // Check
                MetadataDTO metaDTO = permMetadataRepo.get(permId, name);
                if(metaDTO == null) {
                    logger.error("createMetadata(" + permId + "," + name + ", " + value + "): Inserted Metadata Not Found.");
                }
                else { // Return result
                    PermissionDTO permDTO = permRepo.getById(permId);
                    result = new Permission(permDTO);
                    _addSummaryInfo(result);
                }
            }
            catch(SQLException e) {
                logger.error("createMetadata(" + permId + "," + name + ", " + value + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Delete Metadata
    public boolean deleteMetadata(int id, String name) {
        logger.info("deleteMetadata(" + id + ", " + name + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Delete
                permMetadataRepo.delete(id, name);
                // Retrieve
                MetadataDTO metdataDTO = permMetadataRepo.get(id, name);
                logger.info("deleteMetadata(" + id + ", " + name + "): " + (metdataDTO == null) + ".");
                result = (metdataDTO == null);
            }
            catch(SQLException e) {
                logger.error("deleteMetadata(" + id + ", " + name + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Update Metadata Value
    public boolean updateMetadataValue(int id, String name, String value) {
        logger.debug("updateMetadataValue(" + id + ", " + name + ", " + value + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                MetadataDTO metaDTO = permMetadataRepo.get(id, name);
                if (metaDTO != null) {
                    // Update
                    permMetadataRepo.updateValue(id, name, value);
                    // Check
                    metaDTO = permMetadataRepo.get(id, name);
                    result = Objects.equals(value, metaDTO.getValue());
                }
            }
            catch(SQLException e) {
                logger.error("updateMetadataValue(" + id + ", " + name + ", " + value + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }

    // Update Metadata Archived status
    public boolean updateMetadataArchived(int id, String name, boolean archived) {
        logger.debug("updateMetadataArchived(" + id + ", " + name + ", " + archived + ").");
        boolean result = false;
        // Connect
        if(super.connect())
        {
            try {
                // Check if there's an existing record first
                MetadataDTO metaDTO = permMetadataRepo.get(id, name);
                if (metaDTO != null) {
                    permMetadataRepo.updateArchive(id, name, archived);
                    // Check
                    metaDTO = permMetadataRepo.get(id, name);
                    result = Objects.equals(archived, metaDTO.isArchived());
                }
            }
            catch(SQLException e) {
                logger.error("updateMetadataArchived(" + id + ", " + name + ", " + archived + "): Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
            finally {
                super.disconnect();
            }
        }
        return result;
    }
}


