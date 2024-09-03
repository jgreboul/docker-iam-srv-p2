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
import jgr.iam.constant.dto.UserDTOTestConstant; // User Test Constant
import jgr.iam.model.dto.UserDTO; // User

// Test UserRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryE2ETest {

    // Logger
    private final static Logger logger = LogManager.getLogger(UserRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // User Repository
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            repository = new UserRepository(connector);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "setUp", e);
        }
    }

    @AfterEach
    void tearDown() {
        connector.close();
    }

    // Test Create User
    @Order(1)
    @Test
    public void smokeTestInsert() {
        try {
            UserDTO userTest = new UserDTO(UserDTOTestConstant.USER_TEST_USERNAME,
                    UserDTOTestConstant.USER_TEST_EMAIL,
                    UserDTOTestConstant.USER_TEST_PASSWORDHASH.getBytes(StandardCharsets.UTF_8),
                    UserDTOTestConstant.USER_TEST_PASSWORDSALT.getBytes(StandardCharsets.UTF_8),
                    UserDTOTestConstant.USER_TEST_FIRSTNAME,
                    UserDTOTestConstant.USER_TEST_LASTNAME);
            repository.insert(userTest);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    // Test getByName
    @Order(2)
    @Test
    public void smokeTestGetByName() {
        try {
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestGetByName: No User Found.");
            assertNotSame(UserDTOTestConstant.USER_TEST_USERNAME, userTest.getUsername(), "smokeTestGetByName: username doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_EMAIL, userTest.getEmail(), "smokeTestGetByName: email doesn't match.");
            assertNotSame(userTest.getPasswordHash(), UserDTOTestConstant.USER_TEST_PASSWORDHASH.getBytes(StandardCharsets.UTF_8), "smokeTestGetByName: passwordHash doesn't match.");
            assertNotSame(userTest.getPasswordSalt(), UserDTOTestConstant.USER_TEST_PASSWORDSALT.getBytes(StandardCharsets.UTF_8), "smokeTestGetByName: passwordSalt doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_FIRSTNAME, userTest.getFirstName(), "smokeTestGetByName: firstName doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_LASTNAME, userTest.getLastName(), "smokeTestGetByName: lastName doesn't match.");
            assertTrue(userTest.isActive(), "smokeTestGetByName: active doesn't match.");
            assertFalse(userTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetByName", e);
        }
    }

    // Negative Test getByName
    @Order(3)
    @Test
    public void negativeTestGetByName() {
        try {
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME_X);
            assertNull(userTest, "negativeTestGetByName: User Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGetByName", e);
        }
    }

    // Test update (update fields and revert updates)
    @Order(4)
    @Test
    public void smokeTestUpdate() {
        try {
            // Get User by Name
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestUpdate: No User Found.");

            // Test 1: Update Fields
            userTest.setEmail(UserDTOTestConstant.USER_TEST_EMAIL_UPDATED);
            userTest.setPasswordHash(UserDTOTestConstant.USER_TEST_PASSWORDHASH_UPDATED.getBytes(StandardCharsets.UTF_8));
            userTest.setPasswordSalt(UserDTOTestConstant.USER_TEST_PASSWORDSALT_UPDATED.getBytes(StandardCharsets.UTF_8));
            userTest.setFirstName(UserDTOTestConstant.USER_TEST_FIRSTNAME_UPDATED);
            userTest.setLastName(UserDTOTestConstant.USER_TEST_LASTNAME_UPDATED);
            repository.update(userTest);

            // Test 1: Get User by Id and Check updated fields
            UserDTO userTestUpdated = repository.getById(userTest.getId());
            assertNotSame(UserDTOTestConstant.USER_TEST_EMAIL_UPDATED, userTestUpdated.getEmail(), "smokeTestUpdate: email doesn't match.");
            assertNotSame(userTestUpdated.getPasswordHash(), UserDTOTestConstant.USER_TEST_PASSWORDHASH_UPDATED.getBytes(StandardCharsets.UTF_8), "smokeTestUpdate: passwordHash doesn't match.");
            assertNotSame(userTestUpdated.getPasswordSalt(), UserDTOTestConstant.USER_TEST_PASSWORDSALT_UPDATED.getBytes(StandardCharsets.UTF_8), "smokeTestUpdate: passwordSalt doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_FIRSTNAME_UPDATED, userTestUpdated.getFirstName(), "smokeTestUpdate: firstName doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_LASTNAME_UPDATED, userTestUpdated.getLastName(), "smokeTestUpdate: lastName doesn't match.");

            // Test 2: Update back to previous value
            userTest.setEmail(UserDTOTestConstant.USER_TEST_EMAIL);
            userTest.setPasswordHash(UserDTOTestConstant.USER_TEST_PASSWORDHASH.getBytes(StandardCharsets.UTF_8));
            userTest.setPasswordSalt(UserDTOTestConstant.USER_TEST_PASSWORDSALT.getBytes(StandardCharsets.UTF_8));
            userTest.setFirstName(UserDTOTestConstant.USER_TEST_FIRSTNAME);
            userTest.setLastName(UserDTOTestConstant.USER_TEST_LASTNAME);
            repository.update(userTest);

            // Test 2: Get User by Id and Check updated fields
            UserDTO userTestReverted = repository.getById(userTest.getId());
            assertNotSame(UserDTOTestConstant.USER_TEST_EMAIL, userTestReverted.getEmail(), "smokeTestUpdate: email doesn't match.");
            assertNotSame(userTestReverted.getPasswordHash(), UserDTOTestConstant.USER_TEST_PASSWORDHASH.getBytes(StandardCharsets.UTF_8), "smokeTestUpdate: passwordHash doesn't match.");
            assertNotSame(userTestReverted.getPasswordSalt(), UserDTOTestConstant.USER_TEST_PASSWORDSALT.getBytes(StandardCharsets.UTF_8), "smokeTestUpdate: passwordSalt doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_FIRSTNAME, userTestReverted.getFirstName(), "smokeTestUpdate: firstName doesn't match.");
            assertNotSame(UserDTOTestConstant.USER_TEST_LASTNAME, userTestReverted.getLastName(), "smokeTestUpdate: lastName doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdate", e);
        }
    }

    // Test De-Activate and Re-Activate
    @Order(5)
    @Test
    public void smokeTestActivate() {
        try {
            // Get User by Name
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestActivate: No User Found.");

            // Test 1: DeActive
            userTest.setActive(false); // Not needed but let's stay consistent
            repository.deActivate(userTest.getId());

            // Test 1: Get User by Id and Check Activate status
            UserDTO userTestDeactivated = repository.getById(userTest.getId());
            assertFalse(userTestDeactivated.isActive(), "smokeTestActivate: user is active.");

            // Test 2: ReActive
            userTest.setActive(true); // Not needed but let's stay consistent
            repository.reActivate(userTest.getId());

            // Test 2: Get User by Id and Check Activate status
            UserDTO userTestActivated = repository.getById(userTest.getId());
            assertTrue(userTestActivated.isActive(), "smokeTestActivate: user is not active.");

        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestActivate", e);
        }
    }

    // Test Archive and Undo-Archive
    @Order(6)
    @Test
    public void smokeTestArchive() {
        try {
            // Get User by Name
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestArchive: No User Found.");

            // Test 1: Archive
            userTest.setArchived(true); // Not needed but let's stay consistent
            repository.archive(userTest.getId());

            // Test 1: Get User by Id and Check Activate status
            UserDTO userTestArchived = repository.getById(userTest.getId());
            assertTrue(userTestArchived.isArchived(), "smokeTestArchive: user is not archived.");

            // Test 2: Undo Archive
            userTest.setArchived(false); // Not needed but let's stay consistent
            repository.undoArchive(userTest.getId());

            // Test 2: Get User by Id and Check Activate status
            UserDTO userTestNotArchived = repository.getById(userTest.getId());
            assertFalse(userTestNotArchived.isArchived(), "smokeTestArchive: user is archived.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestArchive", e);
        }
    }

    // Test Delete
    @Order(7)
    @Test
    public void smokeTestDelete() {
        try {
            // Get User by Name
            UserDTO userTest = repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestDelete: No User Found.");

            // Delete User
            repository.delete(userTest.getId());

            // Test: Get User by Id and Check
            UserDTO userDeleted = repository.getById(userTest.getId());
            assertNull(userDeleted, "smokeTestDelete: User Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test getAllActive, getAllInactive and getAllArchived
    @Order(8)
    @Test
    public void smokeTestGetAlls() {
        try {
            for(int i = 0; i < 10; i++) {
            repository.insert(new UserDTO(UserDTOTestConstant.USER_TEST_USERNAME_X + i,
                    UserDTOTestConstant.USER_TEST_EMAIL_X + i,
                    (UserDTOTestConstant.USER_TEST_PASSWORD_X + i).getBytes(StandardCharsets.UTF_8),
                    (UserDTOTestConstant.USER_TEST_PASSWORD_X + i).getBytes(StandardCharsets.UTF_8),
                    UserDTOTestConstant.USER_TEST_FIRSTNAME_X + i,
                    UserDTOTestConstant.USER_TEST_LASTNAME_X + i));
            }

            List<UserDTO> UserTests = new ArrayList<UserDTO>();
            for(int i = 0; i < 10; i++) {
                UserTests.add(repository.getByName(UserDTOTestConstant.USER_TEST_USERNAME_X + i));
            }

            // Create array of 10x MTEST User Ids for comparison
            List<Integer> userTestIds = new ArrayList<Integer>();
            for(int i = 0; i < 10; i++) {
                userTestIds.add(UserTests.get(i).getId());
            }

             // Test 1: getAll - call getAll
            List<UserDTO> userAllTests = repository.getAllActive();
            assertFalse(userAllTests.isEmpty(), "smokeTestGetAlls: Active List is Empty.");
            assertEquals(10, userAllTests.size(), "smokeTestGetAlls: Active List is not 10 (" + userAllTests.size() + ").");

            // Test 1: getAll - Check results
            for(UserDTO userTest :  userAllTests) {
                userTestIds.removeIf(n -> n == userTest.getId());
            }
            assertTrue(userTestIds.isEmpty(), "smokeTestGetAlls: Checklist Active is Not Empty.");

            // Test 2: getAllInactive - de-active 2 users: 2 and 4
            UserTests.get(2).setActive(false); // Not needed but let's stay consistent
            repository.deActivate(UserTests.get(2).getId());
            UserTests.get(4).setActive(false); // Not needed but let's stay consistent
            repository.deActivate(UserTests.get(4).getId());
            userTestIds.add(UserTests.get(2).getId());
            userTestIds.add(UserTests.get(4).getId());

            // Test 2: getAllInactive - call getAllInactive
            List<UserDTO> mUserInactiveTests = repository.getAllInactive();
            assertFalse(mUserInactiveTests.isEmpty(), "smokeTestGetAlls: Inactive List is Empty.");
            assertEquals(2, mUserInactiveTests.size(), "smokeTestGetAlls: Inactive List is not 2 (" + mUserInactiveTests.size() + ").");

            // Test 2: getAllInactive - Check results
            for(UserDTO mUserTest :  mUserInactiveTests) {
                userTestIds.removeIf(n -> n == mUserTest.getId());
            }
            assertTrue(userTestIds.isEmpty(), "smokeTestGetAlls: Checklist Inactive is Not Empty.");

            // Test 3: getAllArchived - archive 3 users: 2, 5 and 8
            UserTests.get(2).setArchived(true); // Not needed but let's stay consistent
            repository.archive(UserTests.get(2).getId());
            UserTests.get(5).setArchived(true); // Not needed but let's stay consistent
            repository.archive(UserTests.get(5).getId());
            UserTests.get(8).setArchived(true); // Not needed but let's stay consistent
            repository.archive(UserTests.get(8).getId());
            userTestIds.add(UserTests.get(2).getId());
            userTestIds.add(UserTests.get(5).getId());
            userTestIds.add(UserTests.get(8).getId());

            // Test 3: getAllArchived - call getAllArchived
            List<UserDTO> mUserArchivedTests = repository.getAllArchived();
            assertFalse(mUserArchivedTests.isEmpty(), "smokeTestGetAlls: Archived List is Empty.");
            assertEquals(3, mUserArchivedTests.size(), "smokeTestGetAlls: Archived List is not 3 (" + mUserArchivedTests.size() + ").");

            // Test 3: getAllArchived - Check results
            for(UserDTO mUserTest :  mUserArchivedTests) {
                userTestIds.removeIf(n -> n == mUserTest.getId());
            }
            assertTrue(userTestIds.isEmpty(), "smokeTestGetAlls: Checklist Archived is Not Empty.");

            // Clean up
            for(int i = 0; i < 10; i++) {
                repository.delete(UserTests.get(i).getId());
            }
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAlls", e);
        }
    }
}
