package jgr.iam.repository;

// External Objects
import org.junit.jupiter.api.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html
import static org.junit.jupiter.api.Assertions.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html
import java.util.Objects;
// Internal Objects
import jgr.iam.model.dto.*;
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util
import jgr.iam.constant.dto.RolePermissionDTOTestConstant; // RolePermissionDTO Test Constant

// Test RolePermissionRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RolePermissionRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(RolePermissionRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Repositories
    private RolePermissionRepository rpRepository;
    private PermissionRepository pRepository;
    private RoleRepository roleRepository;
    private FeatureRepository featRepository;
    private ApplicationRepository appRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            rpRepository = new RolePermissionRepository(connector);
            pRepository = new PermissionRepository(connector);
            roleRepository = new RoleRepository(connector);
            featRepository = new FeatureRepository(connector);
            appRepository = new ApplicationRepository(connector);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "setUp", e);
        }
    }

    @AfterEach
    void tearDown() {
        connector.close();
    }

    // Test insert
    @Order(1)
    @Test
    public void smokeTestInsert() {
        try {
            // Insert App
            appRepository.insert(new ApplicationDTO(RolePermissionDTOTestConstant.RP_APP_TEST_NAME, RolePermissionDTOTestConstant.RP_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsert: No Application Found.");
            // Insert Feature
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestInsert: No Feature Found.");
            //Insert Role
            roleRepository.insert(new RoleDTO(RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME, RolePermissionDTOTestConstant.RP_ROLE_TEST_DESCRIPTION, featTest.getId()));
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestInsert: No Role Found.");
            // Insert Permission
            pRepository.insert(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME, RolePermissionDTOTestConstant.RP_PERM_TEST_DESCRIPTION);
            PermissionDTO permTest = pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME);
            assertNotNull(permTest, "smokeTestInsert: No Permission Found.");
            // Insert relationship
            rpRepository.insert(roleTest.getId(), permTest.getId());
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    // Test Get
    @Order(2)
    @Test
    public void smokeTestGet() {
        try {
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGet: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGet: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestGet: No Role Found.");
            // Get Permission
            PermissionDTO permTest = pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME);
            assertNotNull(permTest, "smokeTestGet: No Permission Found.");
            RolePermissionDTO rpTest = rpRepository.get(roleTest.getId(), permTest.getId());
            assertNotNull(rpTest, "smokeTestGet: No RolePermission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGet", e);
        }
    }

    // Negative Test Get
    @Order(3)
    @Test
    public void negativeTestGet() {
        try {
            RolePermissionDTO rpTest = rpRepository.get(RolePermissionDTOTestConstant.RP_ROLE_TEST_FAKEID, RolePermissionDTOTestConstant.RP_PERM_TEST_FAKEID);
            assertNull(rpTest, "negativeTestGet: RolePermission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGet", e);
        }
    }

    // Test archive and undoArchive
    @Order(4)
    @Test
    public void smokeTestArchive() {
        try {
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestArchive: No Role Found.");
            // Get Permission
            PermissionDTO permTest = pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME);
            assertNotNull(permTest, "smokeTestArchive: No Permission Found.");
            RolePermissionDTO rpTest = rpRepository.get(roleTest.getId(), permTest.getId());
            assertNotNull(rpTest, "smokeTestArchive: No RolePermission Found.");

            // Test 1: Archive
            rpTest.setArchived(true); // Not need but let's stay consistent
            rpRepository.archive(roleTest.getId(), permTest.getId());
            rpTest = rpRepository.get(roleTest.getId(), permTest.getId());
            assertTrue(rpTest.isArchived(), "smokeTestArchive: RolePermission is not archived.");

            // Test 2: Undo Archive
            rpRepository.undoArchive(roleTest.getId(), permTest.getId());
            rpTest = rpRepository.get(roleTest.getId(), permTest.getId());
            assertFalse(rpTest.isArchived(), "smokeTestArchive: RolePermission is archived.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestArchive", e);
        }
    }

    // Test Delete
    @Order(5)
    @Test
    public void smokeTestDelete() {
        try {
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestDelete: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestDelete: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestDelete: No Role Found.");
            // Get Permission
            PermissionDTO permTest = pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME);
            assertNotNull(permTest, "smokeTestDelete: No Permission Found.");
            rpRepository.delete(roleTest.getId(), permTest.getId());
            RolePermissionDTO rpTest = rpRepository.get(roleTest.getId(), permTest.getId());
            assertNull(rpTest, "smokeTestDelete: RolePermission Found.");
            pRepository.delete(permTest.getId()); // Delete Permission
            roleRepository.delete(roleTest.getId()); // Delete Role
            featRepository.delete(featTest.getId()); // Delete Feature
            appRepository.delete(appTest.getId()); // Delete Application
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test GetAll(s) for a specific Role
    @Order(6)
    @Test
    public void smokeTestGetAllRole() {
        try {
            // Insert 1 application
            appRepository.insert(new ApplicationDTO(RolePermissionDTOTestConstant.RP_APP_TEST_NAME, RolePermissionDTOTestConstant.RP_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllRole: No Application Found.");

            // Insert 2 Features
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION + 0, appTest.getId()));
            FeatureDTO featTest0 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0);
            assertNotNull(featTest0, "smokeTestGetAllRole: No Feature Found.");
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION + 1, appTest.getId()));
            FeatureDTO featTest1 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1);
            assertNotNull(featTest1, "smokeTestGetAllRole: No Feature Found.");

            // Insert 5 Roles: 0-3 (feat0: 4), 4 (feat1: 1)
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 5; i++) {
                int iFeatureId = i < 4 ? featTest0.getId() : featTest1.getId();
                roleRepository.insert(new RoleDTO(RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i, RolePermissionDTOTestConstant.RP_ROLE_TEST_DESCRIPTION + i, iFeatureId));
                RoleDTO roleTest = roleRepository.getByName(iFeatureId, RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i);
                assertNotNull(roleTest, "smokeTestGetAllRole: No Role Found.");
                roleTests.add(roleRepository.getByName(iFeatureId, RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllRole: No Role Found.");
            }

            // Insert 4 Permission
            List<PermissionDTO> permTests = new ArrayList<PermissionDTO>();
            for(int i = 0; i < 4; i++) {
                pRepository.insert(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME + i, RolePermissionDTOTestConstant.RP_PERM_TEST_DESCRIPTION + i);
                permTests.add(pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME + i));
                assertNotNull(permTests.get(i), "smokeTestGetAllRole: No Permission Found.");
            }

            // Insert relationship
            List<List<Integer>> rpLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                rpLists.add(new ArrayList<>());
            }
            // Role 0: perm 0, 1, 2, 3 (4)
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(0).add(permTests.get(0).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(1).getId()); // Insert
            rpLists.get(0).add(permTests.get(1).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(2).getId()); // Insert
            rpLists.get(0).add(permTests.get(2).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(3).getId()); // Insert
            rpLists.get(0).add(permTests.get(3).getId()); // Add to CheckList
            // Role 1: None
            // Role 2: perm 0, 1 (2)
            rpRepository.insert(roleTests.get(2).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(2).add(permTests.get(0).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(2).getId(), permTests.get(1).getId()); // Insert
            rpLists.get(2).add(permTests.get(1).getId()); // Add to CheckList
            // Role 3: perm 2, 3 (2)
            rpRepository.insert(roleTests.get(3).getId(), permTests.get(2).getId()); // Insert
            rpLists.get(3).add(permTests.get(2).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(3).getId(), permTests.get(3).getId()); // Insert
            rpLists.get(3).add(permTests.get(3).getId()); // Add to CheckList
            // Role 4: perm 0 (1)
            rpRepository.insert(roleTests.get(4).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(4).add(permTests.get(0).getId()); // Add to CheckList

            // Call getAllForRole and Check Results
            for(int i = 0; i < 5; i++) {
                List<RolePermissionDTO> rpList = rpRepository.getAllForRole(roleTests.get(i).getId());
                assertEquals(rpLists.get(i).size(), rpList.size(), "smokeTestGetAllRole: RP[" + i + "] Size doesn't match reference (" + rpList.size() + " vs. " + rpLists.get(i).size() + ").");
                for(RolePermissionDTO rpTest :  rpList) {
                    rpLists.get(i).removeIf(n -> n == rpTest.getPermissionId());
                }
                assertTrue(rpLists.get(i).isEmpty(), "smokeTestGetAllRole: Checklist [" + i + "] is Not Empty.");
            }

            // Role 0: perm 0, 1, 2, 3 (4)
            // Archive RolePermission (0&1 and 0&2)
            rpRepository.archive(roleTests.get(0).getId(), permTests.get(1).getId()); //Archive 0/1
            rpRepository.archive(roleTests.get(0).getId(), permTests.get(2).getId()); //Archive 0/1
            List<Integer> rpiArchivedList = new ArrayList<>();
            rpiArchivedList.add(permTests.get(1).getId());
            rpiArchivedList.add(permTests.get(2).getId());
            List<RolePermissionDTO> rpArchivedList = rpRepository.getAllArchivedForRole(roleTests.get(0).getId());
            for(RolePermissionDTO rpTest :  rpArchivedList) {
                rpiArchivedList.removeIf(n -> n == rpTest.getPermissionId());
            }
            assertTrue(rpiArchivedList.isEmpty(), "smokeTestGetAllRole: Archived Checklist is Not Empty.");

            // Test 2: getAllCount for Application A - call getAllCount
            int permCount = rpRepository.getAllCountForRole(roleTests.get(0).getId());
            assertEquals(2, permCount, "smokeTestGetAll: Permission Count is not 2 (" + permCount + ").");

            // Clean Up - Delete Application
            appRepository.delete(appTest.getId()); // Should Delete Applications, Features, Roles, and RolePermission records
            RoleDTO rTest = roleRepository.getByName(featTest0.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + 0);
            assertNull(rTest, "smokeTestGetAllRole: Role Found.");
            featTest0 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0);
            assertNull(featTest0, "smokeTestGetAllRole: Feature Found.");
            featTest1 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1);
            assertNull(featTest1, "smokeTestGetAllRole: Feature Found.");
            // Clear Up - Delete Permissions
            for(int i = 0; i < 4; i++) {
                pRepository.delete(permTests.get(i).getId());
            }
            // Deleting Role and Permission should have also removed the RolePermission entries
            RolePermissionDTO rpDeleted = rpRepository.get(roleTests.get(0).getId(), permTests.get(0).getId());
            assertNull(rpDeleted, "smokeTestGetAllRole: RolePermission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllRole", e);
        }
    }

    // Test GetAll(s) for a specific Permission
    @Order(7)
    @Test
    public void smokeTestGetAllPermission() {
        try {
            // Insert 1 application
            appRepository.insert(new ApplicationDTO(RolePermissionDTOTestConstant.RP_APP_TEST_NAME, RolePermissionDTOTestConstant.RP_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllPermission: No Application Found.");

            // Insert 2 Features
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION + 0, appTest.getId()));
            FeatureDTO featTest0 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0);
            assertNotNull(featTest0, "smokeTestGetAllPermission: No Feature Found.");
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION + 1, appTest.getId()));
            FeatureDTO featTest1 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1);
            assertNotNull(featTest1, "smokeTestGetAllPermission: No Feature Found.");

            // Insert 5 Roles: 0-3 (feat0: 4), 4 (feat1: 1)
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 5; i++) {
                int iFeatureId = i < 4 ? featTest0.getId() : featTest1.getId();
                roleRepository.insert(new RoleDTO(RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i, RolePermissionDTOTestConstant.RP_ROLE_TEST_DESCRIPTION + i, iFeatureId));
                RoleDTO roleTest = roleRepository.getByName(iFeatureId, RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i);
                assertNotNull(roleTest, "smokeTestGetAllPermission: No Role Found.");
                roleTests.add(roleRepository.getByName(iFeatureId, RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllPermission: No Role Found.");
            }

            // Insert 4 Permission
            List<PermissionDTO> permTests = new ArrayList<PermissionDTO>();
            for(int i = 0; i < 4; i++) {
                pRepository.insert(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME + i, RolePermissionDTOTestConstant.RP_PERM_TEST_DESCRIPTION + i);
                permTests.add(pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME + i));
                assertNotNull(permTests.get(i), "smokeTestGetAllPermission: No Permission Found.");
            }

            // Insert relationship
            List<List<Integer>> rpLists = new ArrayList<>();
            for(int i = 0; i < 4; i++) {
                rpLists.add(new ArrayList<>());
            }
            // Role 0: perm 0, 1, 2, 3 (4)
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(0).add(roleTests.get(0).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(1).getId()); // Insert
            rpLists.get(1).add(roleTests.get(0).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(2).getId()); // Insert
            rpLists.get(2).add(roleTests.get(0).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(0).getId(), permTests.get(3).getId()); // Insert
            rpLists.get(3).add(roleTests.get(0).getId()); // Add to CheckList
            // Role 1: None
            // Role 2: perm 0, 1 (2)
            rpRepository.insert(roleTests.get(2).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(0).add(roleTests.get(2).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(2).getId(), permTests.get(1).getId()); // Insert
            rpLists.get(1).add(roleTests.get(2).getId()); // Add to CheckList
            // Role 3: perm 2, 3 (2)
            rpRepository.insert(roleTests.get(3).getId(), permTests.get(2).getId()); // Insert
            rpLists.get(2).add(roleTests.get(3).getId()); // Add to CheckList
            rpRepository.insert(roleTests.get(3).getId(), permTests.get(3).getId()); // Insert
            rpLists.get(3).add(roleTests.get(3).getId()); // Add to CheckList
            // Role 4: perm 0 (1)
            rpRepository.insert(roleTests.get(4).getId(), permTests.get(0).getId()); // Insert
            rpLists.get(0).add(roleTests.get(4).getId()); // Add to CheckList

            // Call getAllForPermission and Check Results
            for(int i = 0; i < 4; i++) {
                List<RolePermissionDTO> rpList = rpRepository.getAllForPermission(permTests.get(i).getId());
                assertEquals(rpLists.get(i).size(), rpList.size(), "smokeTestGetAllPermission: RP[" + i + "] Size doesn't match reference (" + rpList.size() + " vs. " + rpLists.get(i).size() + ").");
                int iRoleCount = rpRepository.getAllCountForPermission(permTests.get(i).getId());
                assertEquals(rpLists.get(i).size(), iRoleCount, "smokeTestGetAllPermission: RP[" + i + "] Count doesn't match reference (" + iRoleCount + " vs. " + rpLists.get(i).size() + ").");
                for(RolePermissionDTO rpTest :  rpList) {
                    rpLists.get(i).removeIf(n -> n == rpTest.getRoleId());
                }
                assertTrue(rpLists.get(i).isEmpty(), "smokeTestGetAllPermission: Checklist [" + i + "] is Not Empty.");
            }

            // Archive RolePermission (0&4) - Perm 0: role 0, 2, 4
            rpRepository.archive(roleTests.get(4).getId(), permTests.get(0).getId()); //Archive 4/0
            List<Integer> rpiArchivedList = new ArrayList<>();
            rpiArchivedList.add(roleTests.get(4).getId());
            List<RolePermissionDTO> rpArchivedList = rpRepository.getAllArchivedForPermission(permTests.get(0).getId());
            for(RolePermissionDTO rpTest :  rpArchivedList) {
                rpiArchivedList.removeIf(n -> n == rpTest.getRoleId());
            }
            assertTrue(rpiArchivedList.isEmpty(), "smokeTestGetAllPermission: Archived Checklist is Not Empty.");

            // Clean Up - Delete Application
            appRepository.delete(appTest.getId()); // Should Delete Applications, Features, Roles, and RolePermission records
            RoleDTO rTest = roleRepository.getByName(featTest0.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + 0);
            assertNull(rTest, "smokeTestGetAllPermission: Role Found.");
            featTest0 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0);
            assertNull(featTest0, "smokeTestGetAllPermission: Feature Found.");
            featTest1 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 1);
            assertNull(featTest1, "smokeTestGetAllPermission: Feature Found.");
            // Clear Up - Delete Permissions
            for(int i = 0; i < 4; i++) {
                pRepository.delete(permTests.get(i).getId());
            }
            // Deleting Role and Permission should have also removed the RolePermission entries
            RolePermissionDTO rpDeleted = rpRepository.get(roleTests.get(0).getId(), permTests.get(0).getId());
            assertNull(rpDeleted, "smokeTestGetAllPermission: RolePermission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllPermission", e);
        }
    }

    // Test getAllRoleExtendedNameForPermission
    @Order(8)
    @Test
    public void smokeTestGetAllRoleExtendedNameForPermission() {
        try {
            // Insert 1 application
            appRepository.insert(new ApplicationDTO(RolePermissionDTOTestConstant.RP_APP_TEST_NAME, RolePermissionDTOTestConstant.RP_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(RolePermissionDTOTestConstant.RP_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllRoleExtendedNameForPermission: No Application Found.");
            // Insert 1 Feature
            featRepository.insert(new FeatureDTO(RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0, RolePermissionDTOTestConstant.RP_FEAT_TEST_DESCRIPTION + 0, appTest.getId()));
            FeatureDTO featTest0 = featRepository.getByName(appTest.getId(), RolePermissionDTOTestConstant.RP_FEAT_TEST_NAME + 0);
            assertNotNull(featTest0, "smokeTestGetAllRoleExtendedNameForPermission: No Feature Found.");
            // Insert 5 Roles:
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 5; i++) {
                roleRepository.insert(new RoleDTO(RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i, RolePermissionDTOTestConstant.RP_ROLE_TEST_DESCRIPTION + i, featTest0.getId()));
                RoleDTO roleTest = roleRepository.getByName(featTest0.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i);
                assertNotNull(roleTest, "smokeTestGetAllRoleExtendedNameForPermission: No Role Found.");
                roleTests.add(roleRepository.getByName(featTest0.getId(), RolePermissionDTOTestConstant.RP_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllRoleExtendedNameForPermission: No Role Found.");
            }
            // Insert Permission
            pRepository.insert(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME, RolePermissionDTOTestConstant.RP_PERM_TEST_DESCRIPTION);
            PermissionDTO permTest = pRepository.getByName(RolePermissionDTOTestConstant.RP_PERM_TEST_NAME);
            assertNotNull(permTest, "smokeTestGetAllRoleExtendedNameForPermission: No Permission Found.");

            // Insert relationship
            List<String> roleExtendedNames = new ArrayList<>();
            rpRepository.insert(roleTests.get(0).getId(), permTest.getId()); // Insert
            roleExtendedNames.add(String.format("%s.%s.%s", appTest.getName(), featTest0.getName(), roleTests.get(0).getName())); // Add to CheckList
            rpRepository.insert(roleTests.get(2).getId(), permTest.getId()); // Insert
            roleExtendedNames.add(String.format("%s.%s.%s", appTest.getName(), featTest0.getName(), roleTests.get(2).getName())); // Add to CheckList
            rpRepository.insert(roleTests.get(3).getId(), permTest.getId()); // Insert
            roleExtendedNames.add(String.format("%s.%s.%s", appTest.getName(), featTest0.getName(), roleTests.get(3).getName())); // Add to CheckList

            // Call
            List<String> results = rpRepository.getAllRoleExtendedNameForPermission(permTest.getId());
            assertNotNull(results, "smokeTestGetAllRoleExtendedNameForPermission: No Result Found.");
            assertEquals(results.size(), roleExtendedNames.size(), "smokeTestGetAllRoleExtendedNameForPermission: Size doesn't match reference (" + results.size() + " vs. " + roleExtendedNames.size() + ").");

            // Check Result
            for(String name :  results) {
                roleExtendedNames.removeIf(n -> Objects.equals(n, name));
            }
            assertTrue(roleExtendedNames.isEmpty(), "smokeTestGetAllRoleExtendedNameForPermission: Checklist is Not Empty.");

            // Clean Up
            appRepository.delete(appTest.getId()); // Should Delete Applications, Features, Roles, and RolePermission records
            pRepository.delete(permTest.getId()); // Delete Permission
            // Check if Role and Permission have been deleted
            RolePermissionDTO rpDeleted = rpRepository.get(roleTests.get(3).getId(), permTest.getId());
            assertNull(rpDeleted, "smokeTestGetAllRoleExtendedNameForPermission: RolePermission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllRoleExtendedNameForPermission", e);
        }
    }
}
