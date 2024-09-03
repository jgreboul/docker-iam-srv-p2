package jgr.iam.repository;

// External Objects
import org.junit.jupiter.api.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html
import static org.junit.jupiter.api.Assertions.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.nio.charset.StandardCharsets; // https://docs.oracle.com/javase/8/docs/api/java/nio/charset/StandardCharsets.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html
// Internal Objects
import jgr.iam.constant.dto.UserRoleDTOTestConstant; // UserRoleDTO Test Constant
import jgr.iam.model.dto.*; // Application, Feature, Role, User DTO
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util


// Test UserRoleRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRoleRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserRoleRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Repositories
    private UserRoleRepository urRepository;
    private UserRepository usrRepository;
    private RoleRepository roleRepository;
    private FeatureRepository featRepository;
    private ApplicationRepository appRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            urRepository = new UserRoleRepository(connector);
            usrRepository = new UserRepository(connector);
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
            //Insert User
            usrRepository.insert(new UserDTO(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME,
                    UserRoleDTOTestConstant.UR_USR_TEST_EMAIL,
                    UserRoleDTOTestConstant.UR_USR_TEST_PWD.getBytes(StandardCharsets.UTF_8),
                    UserRoleDTOTestConstant.UR_USR_TEST_PWD.getBytes(StandardCharsets.UTF_8),
                    UserRoleDTOTestConstant.UR_USR_TEST_FIRSTNAME,
                    UserRoleDTOTestConstant.UR_USR_TEST_LASTNAME));
            UserDTO userTest = usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestInsert: No User Found.");
            // Insert App
            appRepository.insert(new ApplicationDTO(UserRoleDTOTestConstant.UR_APP_TEST_NAME, UserRoleDTOTestConstant.UR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsert: No Application Found.");
            // Insert Feature
            featRepository.insert(new FeatureDTO(UserRoleDTOTestConstant.UR_FEAT_TEST_NAME, UserRoleDTOTestConstant.UR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestInsert: No Feature Found.");
            //Insert Role
            roleRepository.insert(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME, UserRoleDTOTestConstant.UR_ROLE_TEST_DESCRIPTION);
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestInsert: No Role Found.");
            //Insert relationship
            urRepository.insert(userTest.getId(), roleTest.getId());
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
            // Get User
            UserDTO userTest = usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestGet: No User Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGet: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGet: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestGet: No Role Found.");
            // Get UserRole
            UserRoleDTO urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertNotNull(urTest, "smokeTestGet: No UserRole Found.");
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
            UserRoleDTO urTest = urRepository.get(UserRoleDTOTestConstant.UR_USR_TEST_FAKEID, UserRoleDTOTestConstant.UR_ROLE_TEST_FAKEID);
            assertNull(urTest, "negativeTestGet: UserRole Found.");
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
            // Get User
            UserDTO userTest = usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestArchive: No User Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestArchive: No Role Found.");
            // Get UserRole
            UserRoleDTO urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertNotNull(urTest, "smokeTestArchive: No UserRole Found.");

            // Test 1: Archive
            urTest.setArchived(true); // Not need but let's stay consistent
            urRepository.archive(userTest.getId(), roleTest.getId());
            urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertTrue(urTest.isArchived(), "smokeTestArchive: UserRole is not archived.");

            // Test 2: Undo Archive
            urRepository.undoArchive(userTest.getId(), roleTest.getId());
            urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertFalse(urTest.isArchived(), "smokeTestArchive: UserRole is archived.");
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
            // Get User
            UserDTO userTest = usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestDelete: No User Found.");
            // Get Application
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestDelete: No Application Found.");
            // Get Feature
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestDelete: No Feature Found.");
            // Get Role
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestDelete: No Role Found.");
            // Get UserRole
            UserRoleDTO urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertNotNull(urTest, "smokeTestDelete: No UserRole Found.");
            // Delete
            urRepository.delete(userTest.getId(), roleTest.getId());
            urTest = urRepository.get(userTest.getId(), roleTest.getId());
            assertNull(urTest, "smokeTestDelete: UserRole Found.");
            usrRepository.delete(userTest.getId()); // Delete User
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
            // Insert 15 Users
            List<UserDTO> userTests = new ArrayList<UserDTO>();
            for(int i = 0; i < 15; i++) {
                usrRepository.insert(new UserDTO(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME + i,
                        UserRoleDTOTestConstant.UR_USR_TEST_EMAIL + i ,
                        (UserRoleDTOTestConstant.UR_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        (UserRoleDTOTestConstant.UR_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        UserRoleDTOTestConstant.UR_USR_TEST_FIRSTNAME + i,
                        UserRoleDTOTestConstant.UR_USR_TEST_LASTNAME + i));
                userTests.add(usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME + i));
                assertNotNull(userTests.get(i), "smokeTestGetAllRole: No User Found.");
            }

            // Insert Application and Feature
            appRepository.insert(new ApplicationDTO(UserRoleDTOTestConstant.UR_APP_TEST_NAME, UserRoleDTOTestConstant.UR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllRole: No Application Found.");
            featRepository.insert(new FeatureDTO(UserRoleDTOTestConstant.UR_FEAT_TEST_NAME, UserRoleDTOTestConstant.UR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetAllRole: No Feature Found.");

            // Insert 5 Role
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 5; i++) {
                roleRepository.insert(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME + i, UserRoleDTOTestConstant.UR_ROLE_TEST_DESCRIPTION + i);
                roleTests.add(roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllRole: No Role Found.");
            }

            // Insert relationship
            // Role 0: user 0, 1, 2, 3 (4)
            // Role 1: user 4, 5 (2)
            // Role 2: user 6 to 13 (8)
            // Role 3: no user
            // Role 4: user 14 (1)
            List<List<Integer>> roleLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                roleLists.add(new ArrayList<>());
            }
            for(int i = 0; i < 15; i++) {
                int iRoleIndx = i < 4 ? 0 : (i < 6 ? 1 : (i < 14 ? 2 : 4));
                int iRoleId = roleTests.get(iRoleIndx).getId();
                urRepository.insert(userTests.get(i).getId(), iRoleId); // Insert into DB
                roleLists.get(iRoleIndx).add(userTests.get(i).getId()); // Add to CheckList
            }

            // Call getAllForRole and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserRoleDTO> urList = urRepository.getAllForRole(roleTests.get(i).getId());
                assertEquals(roleLists.get(i).size(), urList.size(), "smokeTestGetAllRole: UR[" + i + "] Size doesn't match reference (" + urList.size() + " vs. " + roleLists.get(i).size() + ").");
                for(UserRoleDTO urTest :  urList) {
                    roleLists.get(i).removeIf(n -> n == urTest.getUserId());
                }
                assertTrue(roleLists.get(i).isEmpty(), "smokeTestGetAllRole: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserRole (User 6&8 for UR2)
            urRepository.archive(userTests.get(6).getId(), roleTests.get(2).getId()); //Archive 6/2
            urRepository.archive(userTests.get(8).getId(), roleTests.get(2).getId()); //Archive 8/2
            List<Integer> usrArchivedList = new ArrayList<>();
            usrArchivedList.add(userTests.get(6).getId());
            usrArchivedList.add(userTests.get(8).getId());
            List<UserRoleDTO> urArchivedList = urRepository.getAllArchivedForRole(roleTests.get(2).getId());
            for(UserRoleDTO urTest :  urArchivedList) {
                usrArchivedList.removeIf(n -> n == urTest.getUserId());
            }
            assertTrue(usrArchivedList.isEmpty(), "smokeTestGetAllRole: Archived Checklist is Not Empty.");

            // Clean Up - Delete Users
            for(int i = 0; i < 15; i++) {
                usrRepository.delete(userTests.get(i).getId());
            }
            // Clear Up - Delete Roles
            for(int i = 0; i < 5; i++) {
                roleRepository.delete(roleTests.get(i).getId());
            }
            // Clean-up - Delete Feature And Application
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well
            // Deleting User and Role should have also removed the UserRole entries
            UserRoleDTO urDeleted = urRepository.get(userTests.get(0).getId(), roleTests.get(0).getId());
            assertNull(urDeleted, "smokeTestGetAllRole: UserRole Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllRole", e);
        }
    }

    // Test GetAll(s) for a specific User
    @Order(7)
    @Test
    public void smokeTestGetAllUser() {
        try {
            // Insert 5 Users
            List<UserDTO> userTests = new ArrayList<UserDTO>();
            for(int i = 0; i < 5; i++) {
                usrRepository.insert(new UserDTO(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME + i,
                        UserRoleDTOTestConstant.UR_USR_TEST_EMAIL + i ,
                        (UserRoleDTOTestConstant.UR_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        (UserRoleDTOTestConstant.UR_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        UserRoleDTOTestConstant.UR_USR_TEST_FIRSTNAME + i,
                        UserRoleDTOTestConstant.UR_USR_TEST_LASTNAME + i));
                userTests.add(usrRepository.getByName(UserRoleDTOTestConstant.UR_USR_TEST_USERNAME + i));
                assertNotNull(userTests.get(i), "smokeTestGetAllUser: No User Found.");
            }

            // Insert Application and Feature
            appRepository.insert(new ApplicationDTO(UserRoleDTOTestConstant.UR_APP_TEST_NAME, UserRoleDTOTestConstant.UR_APP_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(UserRoleDTOTestConstant.UR_APP_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetAllUser: No Application Found.");
            featRepository.insert(new FeatureDTO(UserRoleDTOTestConstant.UR_FEAT_TEST_NAME, UserRoleDTOTestConstant.UR_FEAT_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), UserRoleDTOTestConstant.UR_FEAT_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetAllUser: No Feature Found.");

            // Insert 3 Role
            List<RoleDTO> roleTests = new ArrayList<RoleDTO>();
            for(int i = 0; i < 3; i++) {
                roleRepository.insert(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME + i, UserRoleDTOTestConstant.UR_ROLE_TEST_DESCRIPTION + i);
                roleTests.add(roleRepository.getByName(featTest.getId(), UserRoleDTOTestConstant.UR_ROLE_TEST_NAME + i));
                assertNotNull(roleTests.get(i), "smokeTestGetAllUser: No Role Found.");
            }

            // Insert relationships
            List<List<Integer>> roleLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                roleLists.add(new ArrayList<>());
            }
            // User 0: role 0, 1 (2)
            urRepository.insert(userTests.get(0).getId(), roleTests.get(0).getId());
            urRepository.insert(userTests.get(0).getId(), roleTests.get(1).getId());
            roleLists.get(0).add(roleTests.get(0).getId());
            roleLists.get(0).add(roleTests.get(1).getId());
            // User 1: role 0 (1)
            urRepository.insert(userTests.get(1).getId(), roleTests.get(0).getId());
            roleLists.get(1).add(roleTests.get(0).getId());
            // User 2: no role (0)
            // User 3: role 2 (1)
            urRepository.insert(userTests.get(3).getId(), roleTests.get(2).getId());
            roleLists.get(3).add(roleTests.get(2).getId());
            // User 4: ug 0, 1, 2 (3)
            urRepository.insert(userTests.get(4).getId(), roleTests.get(0).getId());
            urRepository.insert(userTests.get(4).getId(), roleTests.get(1).getId());
            urRepository.insert(userTests.get(4).getId(), roleTests.get(2).getId());
            roleLists.get(4).add(roleTests.get(0).getId());
            roleLists.get(4).add(roleTests.get(1).getId());
            roleLists.get(4).add(roleTests.get(2).getId());

            // Call getAllForUser and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserRoleDTO> urList = urRepository.getAllForUser(userTests.get(i).getId()) ;
                assertEquals(roleLists.get(i).size(), urList.size(), "smokeTestGetAllUser: UUG[" + i + "] Size doesn't match reference (" + urList.size() + " vs. " + roleLists.get(i).size() + ").");
                for(UserRoleDTO urTest :  urList) {
                    roleLists.get(i).removeIf(n -> n == urTest.getRoleId());
                }
                assertTrue(roleLists.get(i).isEmpty(), "smokeTestGetAllUser: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserRole (user 1 / ug 0)
            urRepository.archive(userTests.get(1).getId(), roleTests.get(0).getId()); //Archive 1/0
            List<Integer> ugArchivedList = new ArrayList<>();
            ugArchivedList.add(roleTests.get(0).getId());
            List<UserRoleDTO> urArchivedList = urRepository.getAllArchivedForUser(userTests.get(1).getId());
            assertEquals(ugArchivedList.size(), urArchivedList.size(), "smokeTestGetAllUser: UUG Archived Size doesn't match reference (" + urArchivedList.size() + " vs. " + ugArchivedList.size() + ").");
            for(UserRoleDTO urTest :  urArchivedList) {
                ugArchivedList.removeIf(n -> n == urTest.getRoleId());
            }
            assertTrue(ugArchivedList.isEmpty(), "smokeTestGetAllUser: Archived Checklist is Not Empty.");

            // Clean Up - Delete Users
            for(int i = 0; i < 5; i++) {
                usrRepository.delete(userTests.get(i).getId());
            }
            // Clear Up - Delete Roles
            for(int i = 0; i < 3; i++) {
                roleRepository.delete(roleTests.get(i).getId());
            }
            // Clean-up - Delete Feature And Application
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well
            // Deleting User and Role should have also removed the UserRole entries
            UserRoleDTO urDeleted = urRepository.get(userTests.get(0).getId(), roleTests.get(0).getId());
            assertNull(urDeleted, "smokeTestGetAllUser: UserRole Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllUser", e);
        }
    }
}