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
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util
import jgr.iam.constant.dto.UserUserGroupDTOTestConstant; // UserUserGroupDTO Test Constant
import jgr.iam.model.dto.UserUserGroupDTO; // UserUserGroupDTO
import jgr.iam.model.dto.UserGroupDTO; // UserGroupDTO
import jgr.iam.model.dto.UserDTO; // UserDTO

// Test UserUserGroupRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserUserGroupRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserUserGroupRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Repositories
    private UserUserGroupRepository uugRepository;
    private UserGroupRepository ugRepository;
    private UserRepository usrRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            uugRepository = new UserUserGroupRepository(connector);
            ugRepository = new UserGroupRepository(connector);
            usrRepository = new UserRepository(connector);
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
            usrRepository.insert(new UserDTO(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME,
                    UserUserGroupDTOTestConstant.UUG_USR_TEST_EMAIL,
                    UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD.getBytes(StandardCharsets.UTF_8),
                    UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD.getBytes(StandardCharsets.UTF_8),
                    UserUserGroupDTOTestConstant.UUG_USR_TEST_FIRSTNAME,
                    UserUserGroupDTOTestConstant.UUG_USR_TEST_LASTNAME));
            UserDTO userTest = usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestInsert: No User Found.");
            //Insert User Group
            ugRepository.insert(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME, UserUserGroupDTOTestConstant.UUG_UG_TEST_DESCRIPTION);
            UserGroupDTO ugTest = ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestInsert: No UserGroup Found.");
            //Insert relationship
            uugRepository.insert(userTest.getId(), ugTest.getId());
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
            UserDTO userTest = usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestGet: No User Found.");
            UserGroupDTO ugTest = ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestGet: No UserGroup Found.");
            UserUserGroupDTO uugTest = uugRepository.get(userTest.getId(), ugTest.getId());
            assertNotNull(uugTest, "smokeTestGet: No UserUserGroup Found.");
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
            UserUserGroupDTO uugTest = uugRepository.get(UserUserGroupDTOTestConstant.UUG_USR_TEST_FAKEID, UserUserGroupDTOTestConstant.UUG_UG_TEST_FAKEID);
            assertNull(uugTest, "negativeTestGet: UserUserGroup Found.");
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
            UserDTO userTest = usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestArchive: No User Found.");
            UserGroupDTO ugTest = ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestArchive: No UserGroup Found.");
            UserUserGroupDTO uugTest = uugRepository.get(userTest.getId(), ugTest.getId());
            assertNotNull(uugTest, "smokeTestArchive: No UserUserGroup Found.");

            // Test 1: Archive
            uugTest.setArchived(true); // Not need but let's stay consistent
            uugRepository.archive(userTest.getId(), ugTest.getId());
            uugTest = uugRepository.get(userTest.getId(), ugTest.getId());
            assertTrue(uugTest.isArchived(), "smokeTestArchive: UserUserGroup is not archived.");

            // Test 2: Undo Archive
            uugRepository.undoArchive(userTest.getId(), ugTest.getId());
            uugTest = uugRepository.get(userTest.getId(), ugTest.getId());
            assertFalse(uugTest.isArchived(), "smokeTestArchive: UserUserGroup is archived.");
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
            UserDTO userTest = usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestDelete: No User Found.");
            UserGroupDTO ugTest = ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME);
            assertNotNull(ugTest, "smokeTestDelete: No UserGroup Found.");
            uugRepository.delete(userTest.getId(), ugTest.getId());
            UserUserGroupDTO uugTest = uugRepository.get(userTest.getId(), ugTest.getId());
            assertNull(uugTest, "smokeTestDelete: UserUserGroup Found.");
            usrRepository.delete(userTest.getId()); // Delete User
            ugRepository.delete(ugTest.getId()); // Delete User Group
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test GetAll(s) for a specific UserGroup
    @Order(6)
    @Test
    public void smokeTestGetAllUg() {
        try {
            // Insert 15 Users
            List<UserDTO> userTests = new ArrayList<UserDTO>();
            for(int i = 0; i < 15; i++) {
                usrRepository.insert(new UserDTO(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME + i,
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_EMAIL + i ,
                        (UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        (UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_FIRSTNAME + i,
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_LASTNAME + i));
                userTests.add(usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME + i));
                assertNotNull(userTests.get(i), "smokeTestGetAllUg: No User Found.");
            }

            // Insert 5 UserGroup
            List<UserGroupDTO> ugTests = new ArrayList<UserGroupDTO>();
            for(int i = 0; i < 5; i++) {
                ugRepository.insert(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME + i, UserUserGroupDTOTestConstant.UUG_UG_TEST_DESCRIPTION + i);
                ugTests.add(ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME + i));
                assertNotNull(ugTests.get(i), "smokeTestGetAllUg: No UserGroup Found.");
            }

            // Insert relationship
            // UserGroup 0: user 0, 1, 2, 3 (4)
            // UserGroup 1: user 4, 5 (2)
            // UserGroup 2: user 6 to 13 (8)
            // UserGroup 3: no user
            // UserGroup 4: user 14 (1)
            List<List<Integer>> ugLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                ugLists.add(new ArrayList<>());
            }
            for(int i = 0; i < 15; i++) {
                int iUserGroupIndx = i < 4 ? 0 : (i < 6 ? 1 : (i < 14 ? 2 : 4));
                int iUserGroupId = ugTests.get(iUserGroupIndx).getId();
                uugRepository.insert(userTests.get(i).getId(), iUserGroupId); // Insert into DB
                ugLists.get(iUserGroupIndx).add(userTests.get(i).getId()); // Add to CheckList
            }

            // Call getAllForUserGroup and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserUserGroupDTO> uugList = uugRepository.getAllForUserGroup(ugTests.get(i).getId());
                assertEquals(ugLists.get(i).size(), uugList.size(), "smokeTestGetAllUg: UUG[" + i + "] Size doesn't match reference (" + uugList.size() + " vs. " + ugLists.get(i).size() + ").");
                for(UserUserGroupDTO uugTest :  uugList) {
                    ugLists.get(i).removeIf(n -> n == uugTest.getUserId());
                }
                assertTrue(ugLists.get(i).isEmpty(), "smokeTestGetAllUg: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserUserGroup (User 6&8 for UG2)
            uugRepository.archive(userTests.get(6).getId(), ugTests.get(2).getId()); //Archive 6/2
            uugRepository.archive(userTests.get(8).getId(), ugTests.get(2).getId()); //Archive 8/2
            List<Integer> usrArchivedList = new ArrayList<>();
            usrArchivedList.add(userTests.get(6).getId());
            usrArchivedList.add(userTests.get(8).getId());
            List<UserUserGroupDTO> uugArchivedList = uugRepository.getAllArchivedForUserGroup(ugTests.get(2).getId());
            for(UserUserGroupDTO uugTest :  uugArchivedList) {
                usrArchivedList.removeIf(n -> n == uugTest.getUserId());
            }
            assertTrue(usrArchivedList.isEmpty(), "smokeTestGetAllUg: Archived Checklist is Not Empty.");

            // Clean Up - Delete Users
            for(int i = 0; i < 15; i++) {
                usrRepository.delete(userTests.get(i).getId());
            }
            // Clear Up - Delete User Groups
            for(int i = 0; i < 5; i++) {
                ugRepository.delete(ugTests.get(i).getId());
            }
            // Deleting User and User Group should have also removed the UserUserGroup entries
            UserUserGroupDTO uugDeleted = uugRepository.get(userTests.get(0).getId(), ugTests.get(0).getId());
            assertNull(uugDeleted, "smokeTestGetAllUg: UserUserGroup Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllUg", e);
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
                usrRepository.insert(new UserDTO(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME + i,
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_EMAIL + i ,
                        (UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        (UserUserGroupDTOTestConstant.UUG_USR_TEST_PWD + i).getBytes(StandardCharsets.UTF_8),
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_FIRSTNAME + i,
                        UserUserGroupDTOTestConstant.UUG_USR_TEST_LASTNAME + i));
                userTests.add(usrRepository.getByName(UserUserGroupDTOTestConstant.UUG_USR_TEST_USERNAME + i));
                assertNotNull(userTests.get(i), "smokeTestGetAllUser: No User Found.");
            }

            // Insert 3 UserGroup
            List<UserGroupDTO> ugTests = new ArrayList<UserGroupDTO>();
            for(int i = 0; i < 3; i++) {
                ugRepository.insert(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME + i, UserUserGroupDTOTestConstant.UUG_UG_TEST_DESCRIPTION + i);
                ugTests.add(ugRepository.getByName(UserUserGroupDTOTestConstant.UUG_UG_TEST_NAME + i));
                assertNotNull(ugTests.get(i), "smokeTestGetAllUser: No UserGroup Found.");
            }

            // Insert relationships
            List<List<Integer>> ugLists = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                ugLists.add(new ArrayList<>());
            }
            // User 0: ug 0, 1 (2)
            uugRepository.insert(userTests.get(0).getId(), ugTests.get(0).getId());
            uugRepository.insert(userTests.get(0).getId(), ugTests.get(1).getId());
            ugLists.get(0).add(ugTests.get(0).getId());
            ugLists.get(0).add(ugTests.get(1).getId());
            // User 1: ug 0 (1)
            uugRepository.insert(userTests.get(1).getId(), ugTests.get(0).getId());
            ugLists.get(1).add(ugTests.get(0).getId());
            // User 2: no user group (0)
            // User 3: ug 2 (1)
            uugRepository.insert(userTests.get(3).getId(), ugTests.get(2).getId());
            ugLists.get(3).add(ugTests.get(2).getId());
            // User 4: ug 0, 1, 2 (3)
            uugRepository.insert(userTests.get(4).getId(), ugTests.get(0).getId());
            uugRepository.insert(userTests.get(4).getId(), ugTests.get(1).getId());
            uugRepository.insert(userTests.get(4).getId(), ugTests.get(2).getId());
            ugLists.get(4).add(ugTests.get(0).getId());
            ugLists.get(4).add(ugTests.get(1).getId());
            ugLists.get(4).add(ugTests.get(2).getId());

            // Call getAllForUser and Check Results
            for(int i = 0; i < 5; i++) {
                List<UserUserGroupDTO> uugList = uugRepository.getAllForUser(userTests.get(i).getId()) ;
                assertEquals(ugLists.get(i).size(), uugList.size(), "smokeTestGetAllUser: UUG[" + i + "] Size doesn't match reference (" + uugList.size() + " vs. " + ugLists.get(i).size() + ").");
                for(UserUserGroupDTO uugTest :  uugList) {
                    ugLists.get(i).removeIf(n -> n == uugTest.getUserGroupId());
                }
                assertTrue(ugLists.get(i).isEmpty(), "smokeTestGetAllUser: Checklist [" + i + "] is Not Empty.");
            }

            // Archive UserUserGroup (user 1 / ug 0)
            uugRepository.archive(userTests.get(1).getId(), ugTests.get(0).getId()); //Archive 1/0
            List<Integer> ugArchivedList = new ArrayList<>();
            ugArchivedList.add(ugTests.get(0).getId());
            List<UserUserGroupDTO> uugArchivedList = uugRepository.getAllArchivedForUser(userTests.get(1).getId());
            assertEquals(ugArchivedList.size(), uugArchivedList.size(), "smokeTestGetAllUser: UUG Archived Size doesn't match reference (" + uugArchivedList.size() + " vs. " + ugArchivedList.size() + ").");
            for(UserUserGroupDTO uugTest :  uugArchivedList) {
                ugArchivedList.removeIf(n -> n == uugTest.getUserGroupId());
            }
            assertTrue(ugArchivedList.isEmpty(), "smokeTestGetAllUser: Archived Checklist is Not Empty.");

            // Clean Up - Delete Users
            for(int i = 0; i < 5; i++) {
                usrRepository.delete(userTests.get(i).getId());
            }
            // Clear Up - Delete User Groups
            for(int i = 0; i < 3; i++) {
                ugRepository.delete(ugTests.get(i).getId());
            }
            // Deleting User and User Group should have also removed the UserUserGroup entries
            UserUserGroupDTO uugDeleted = uugRepository.get(userTests.get(0).getId(), ugTests.get(0).getId());
            assertNull(uugDeleted, "smokeTestGetAllUser: UserUserGroup Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAllUser", e);
        }
    }
}
