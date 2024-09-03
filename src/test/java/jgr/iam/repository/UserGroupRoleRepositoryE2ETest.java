package jgr.iam.repository;

// External Objects
import org.junit.jupiter.api.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html
import static org.junit.jupiter.api.Assertions.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html

import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html
// Internal Objects
import jgr.iam.constant.dto.UserGroupRoleDTOTestConstant; // UserGroupRoleDTO Test Constant
import jgr.iam.model.dto.*; // Application, Feature, Role, UserGroup DTO
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util

// Test UserGroupRoleRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserGroupRoleRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserGroupRoleRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Repositories
    private UserGroupRoleRepository ugrRepository;
    private UserGroupRepository ugRepository;
    private RoleRepository roleRepository;
    private FeatureRepository featRepository;
    private ApplicationRepository appRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            ugrRepository = new UserGroupRoleRepository(connector);
            ugRepository = new UserGroupRepository(connector);
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
            //Insert UserGroup
            ugRepository.insert(new UserGroupDTO(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_UG_TEST_DESCRIPTION));
            UserGroupDTO ugTest = ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestInsert: No UserGroup Found.");
            // Insert App
            appRepository.insert(new ApplicationDTO(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsert: No Application Found.");
            // Insert Feature
            featRepository.insert(new FeatureDTO(UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestInsert: No Feature Found.");
            //Insert Role
            roleRepository.insert(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_DESCRIPTION);
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestInsert: No Role Found.");
            //Insert relationship
            ugrRepository.insert(ugTest.getId(), roleTest.getId());
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
            // Get UserGroup
            UserGroupDTO ugTest = ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestGet: No UserGroup Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGet: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGet: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestGet: No Role Found.");
            // Get UserGroupRole
            UserGroupRoleDTO ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertNotNull(ugrTest, "smokeTestGet: No UserGroupRole Found.");
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
            UserGroupRoleDTO ugrTest = ugrRepository.get(UserGroupRoleDTOTestConstant.UGR_UG_TEST_FAKEID, UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_FAKEID);
            assertNull(ugrTest, "negativeTestGet: UserGroupRole Found.");
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
            // Get UserGroup
            UserGroupDTO ugTest = ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestArchive: No UserGroup Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestArchive: No Role Found.");
            // Get UserGroupRole
            UserGroupRoleDTO ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertNotNull(ugrTest, "smokeTestArchive: No UserGroupRole Found.");

            // Test 1: Archive
            ugrTest.setArchived(true); // Not need but let's stay consistent
            ugrRepository.archive(ugTest.getId(), roleTest.getId());
            ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertTrue(ugrTest.isArchived(), "smokeTestArchive: UserGroupRole is not archived.");

            // Test 2: Undo Archive
            ugrRepository.undoArchive(ugTest.getId(), roleTest.getId());
            ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertFalse(ugrTest.isArchived(), "smokeTestArchive: UserGroupRole is archived.");
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
            // Get UserGroup
            UserGroupDTO ugTest = ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestDelete: No UserGroup Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestDelete: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestDelete: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestDelete: No Role Found.");
            // Get UserGroupRole
            UserGroupRoleDTO ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertNotNull(ugrTest, "smokeTestDelete: No UserGroupRole Found.");
            // Delete
            ugrRepository.delete(ugTest.getId(), roleTest.getId());
            ugrTest = ugrRepository.get(ugTest.getId(), roleTest.getId());
            assertNull(ugrTest, "smokeTestDelete: UserGroupRole Found.");
            ugRepository.delete(ugTest.getId()); // Delete UserGroup
            roleRepository.delete(roleTest.getId()); // Delete Role
            // Clean-up - Delete Feature And Application
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well
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
            // Insert 15 UserGroups
            List<UserGroupDTO> ugTests = new ArrayList<UserGroupDTO>();
            for(int i = 0; i < 15; i++) {
                ugRepository.insert(new UserGroupDTO(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME + i, UserGroupRoleDTOTestConstant.UGR_UG_TEST_DESCRIPTION + i));
                ugTests.add(ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME + i));
                assertNotNull(ugTests.get(i), "smokeTestGetAllRole: No UserGroup Found.");
            }

            // Insert Application and Feature
            appRepository.insert(new ApplicationDTO(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllRole: No Application Found.");
            featRepository.insert(new FeatureDTO(UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetAllRole: No Feature Found.");

            // Insert 5 Role
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 5; i++) {
                roleRepository.insert(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME + i, UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_DESCRIPTION + i);
                roleTests.add(roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllRole: No Role Found.");
            }

            // Insert relationship
            // Role 0: usergroup 0, 1, 2, 3 (4)
            // Role 1: usergroup 4, 5 (2)
            // Role 2: usergroup 6 to 13 (8)
            // Role 3: no usergroup
            // Role 4: usergroup 14 (1)
            List<List<Integer>> roleLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                roleLists.add(new ArrayList<>());
            }
            for(int i = 0; i < 15; i++) {
                int iRoleIndx = i < 4 ? 0 : (i < 6 ? 1 : (i < 14 ? 2 : 4));
                int iRoleId = roleTests.get(iRoleIndx).getId();
                ugrRepository.insert(ugTests.get(i).getId(), iRoleId); // Insert into DB
                roleLists.get(iRoleIndx).add(ugTests.get(i).getId()); // Add to CheckList
            }

            // Call getAllForRole and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserGroupRoleDTO> ugrList = ugrRepository.getAllForRole(roleTests.get(i).getId());
                assertEquals(roleLists.get(i).size(), ugrList.size(), "smokeTestGetAllRole: UGR[" + i + "] Size doesn't match reference (" + ugrList.size() + " vs. " + roleLists.get(i).size() + ").");
                for(UserGroupRoleDTO ugrTest :  ugrList) {
                    roleLists.get(i).removeIf(n -> n == ugrTest.getUserGroupId());
                }
                assertTrue(roleLists.get(i).isEmpty(), "smokeTestGetAllRole: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserGroupRole (UserGroup 6&8 for UR2)
            ugrRepository.archive(ugTests.get(6).getId(), roleTests.get(2).getId()); //Archive 6/2
            ugrRepository.archive(ugTests.get(8).getId(), roleTests.get(2).getId()); //Archive 8/2
            List<Integer> ugArchivedList = new ArrayList<>();
            ugArchivedList.add(ugTests.get(6).getId());
            ugArchivedList.add(ugTests.get(8).getId());
            List<UserGroupRoleDTO> ugrArchivedList = ugrRepository.getAllArchivedForRole(roleTests.get(2).getId());
            for(UserGroupRoleDTO ugrTest :  ugrArchivedList) {
                ugArchivedList.removeIf(n -> n == ugrTest.getUserGroupId());
            }
            assertTrue(ugArchivedList.isEmpty(), "smokeTestGetAllRole: Archived Checklist is Not Empty.");

            // Clean Up - Delete UserGroups
            for(int i = 0; i < 15; i++) {
                ugRepository.delete(ugTests.get(i).getId());
            }
            // Clear Up - Delete Roles
            for(int i = 0; i < 5; i++) {
                roleRepository.delete(roleTests.get(i).getId());
            }
            // Clean-up - Delete Feature And Application
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well
            // Deleting UserGroup and Role should have also removed the UserGroupRole entries
            UserGroupRoleDTO ugrDeleted = ugrRepository.get(ugTests.get(0).getId(), roleTests.get(0).getId());
            assertNull(ugrDeleted, "smokeTestGetAllRole: UserGroupRole Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllRole", e);
        }
    }

    // Test GetAll(s) for a specific UserGroup
    @Order(7)
    @Test
    public void smokeTestGetAllUserGroup() {
        try {
            // Insert 5 UserGroups
            List<UserGroupDTO> ugTests = new ArrayList<UserGroupDTO>();
            for(int i = 0; i < 5; i++) {
                ugRepository.insert(new UserGroupDTO(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME + i, UserGroupRoleDTOTestConstant.UGR_UG_TEST_DESCRIPTION + i));
                ugTests.add(ugRepository.getByName(UserGroupRoleDTOTestConstant.UGR_UG_TEST_NAME + i));
                assertNotNull(ugTests.get(i), "smokeTestGetAllUserGroup: No UserGroup Found.");
            }

            // Insert Application and Feature
            appRepository.insert(new ApplicationDTO(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserGroupRoleDTOTestConstant.UGR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllUserGroup: No Application Found.");
            featRepository.insert(new FeatureDTO(UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME, UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserGroupRoleDTOTestConstant.UGR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetAllUserGroup: No Feature Found.");

            // Insert 3 Role
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 3; i++) {
                roleRepository.insert(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME + i, UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_DESCRIPTION + i);
                roleTests.add(roleRepository.getByName(featTest.getId(), UserGroupRoleDTOTestConstant.UGR_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllUserGroup: No Role Found.");
            }

            // Insert relationships
            List<List<Integer>> roleLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                roleLists.add(new ArrayList<>());
            }
            // UserGroup 0: role 0, 1 (2)
            ugrRepository.insert(ugTests.get(0).getId(), roleTests.get(0).getId());
            ugrRepository.insert(ugTests.get(0).getId(), roleTests.get(1).getId());
            roleLists.get(0).add(roleTests.get(0).getId());
            roleLists.get(0).add(roleTests.get(1).getId());
            // UserGroup 1: role 0 (1)
            ugrRepository.insert(ugTests.get(1).getId(), roleTests.get(0).getId());
            roleLists.get(1).add(roleTests.get(0).getId());
            // UserGroup 2: no role (0)
            // UserGroup 3: role 2 (1)
            ugrRepository.insert(ugTests.get(3).getId(), roleTests.get(2).getId());
            roleLists.get(3).add(roleTests.get(2).getId());
            // UserGroup 4: ug 0, 1, 2 (3)
            ugrRepository.insert(ugTests.get(4).getId(), roleTests.get(0).getId());
            ugrRepository.insert(ugTests.get(4).getId(), roleTests.get(1).getId());
            ugrRepository.insert(ugTests.get(4).getId(), roleTests.get(2).getId());
            roleLists.get(4).add(roleTests.get(0).getId());
            roleLists.get(4).add(roleTests.get(1).getId());
            roleLists.get(4).add(roleTests.get(2).getId());

            // Call getAllForUserGroup and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserGroupRoleDTO> ugrList = ugrRepository.getAllForUserGroup(ugTests.get(i).getId()) ;
                assertEquals(roleLists.get(i).size(), ugrList.size(), "smokeTestGetAllUserGroup: UUG[" + i + "] Size doesn't match reference (" + ugrList.size() + " vs. " + roleLists.get(i).size() + ").");
                for(UserGroupRoleDTO ugrTest :  ugrList) {
                    roleLists.get(i).removeIf(n -> n == ugrTest.getRoleId());
                }
                assertTrue(roleLists.get(i).isEmpty(), "smokeTestGetAllUserGroup: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserGroupRole (ug 1 / role 0)
            ugrRepository.archive(ugTests.get(1).getId(), roleTests.get(0).getId()); //Archive 1/0
            List<Integer> ugArchivedList = new ArrayList<>();
            ugArchivedList.add(roleTests.get(0).getId());
            List<UserGroupRoleDTO> ugrArchivedList = ugrRepository.getAllArchivedForUserGroup(ugTests.get(1).getId());
            assertEquals(ugArchivedList.size(), ugrArchivedList.size(), "smokeTestGetAllUserGroup: UUG Archived Size doesn't match reference (" + ugrArchivedList.size() + " vs. " + ugArchivedList.size() + ").");
            for(UserGroupRoleDTO ugrTest :  ugrArchivedList) {
                ugArchivedList.removeIf(n -> n == ugrTest.getRoleId());
            }
            assertTrue(ugArchivedList.isEmpty(), "smokeTestGetAllUserGroup: Archived Checklist is Not Empty.");

            // Clean Up - Delete UserGroups
            for(int i = 0; i < 5; i++) {
                ugRepository.delete(ugTests.get(i).getId());
            }
            // Clear Up - Delete Roles
            for(int i = 0; i < 3; i++) {
                roleRepository.delete(roleTests.get(i).getId());
            }
            // Clean-up - Delete Feature And Application
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well
            // Deleting UserGroup and Role should have also removed the UserGroupRole entries
            UserGroupRoleDTO ugrDeleted = ugrRepository.get(ugTests.get(0).getId(), roleTests.get(0).getId());
            assertNull(ugrDeleted, "smokeTestGetAllUserGroup: UserGroupRole Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllUserGroup", e);
        }
    }
}