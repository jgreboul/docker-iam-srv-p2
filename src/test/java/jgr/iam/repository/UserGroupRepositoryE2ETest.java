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
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util
import jgr.iam.constant.dto.UserGroupDTOTestConstant; // UserGroup Test Constant
import jgr.iam.model.dto.UserGroupDTO; // UserGroupDTO

// Test UserGroupRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserGroupRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(UserGroupRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // UserGroupRepository
    private UserGroupRepository repository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            repository = new UserGroupRepository(connector);
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
            UserGroupDTO userGroupTest = new UserGroupDTO(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION);
            repository.insert(userGroupTest);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    // Test getByName
    @Order(2)
    @Test
    public void smokeTestGetByName() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(userGroupTest, "smokeTestGetByName: No UserGroup Found.");
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, userGroupTest.getName(), "smokeTestGetByName: name doesn't match.");
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION, userGroupTest.getDescription(), "smokeTestGetByName: description doesn't match.");
            assertSame(false, userGroupTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetByName", e);
        }
    }

    // Negative test getByName
    @Order(3)
    @Test
    public void negativeTestGetByName() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME_UPDATED);
            assertNull(userGroupTest, "negativeTestGetByName: UserGroup Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGetByName", e);
        }
    }

    // Test update Name and Description (and revert updates)
    @Order(4)
    @Test
    public void smokeTestUpdate() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            // Get UserGroup by Name
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(userGroupTest, "smokeTestUpdate: No UserGroup Found.");

            // Test 1: Update Name
            userGroupTest.setName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME_UPDATED);
            repository.update(userGroupTest);

            // Test 1: Get UserGroup by Id and Check updated Name
            UserGroupDTO ugTestNameUpdated = repository.getById(userGroupTest.getId());
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_NAME_UPDATED, ugTestNameUpdated.getName(), "smokeTestUpdate: name doesn't match.");

            // Test 2: Update Description
            userGroupTest.setDescription(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION_UPDATED);
            repository.updateDescription(userGroupTest.getId(), UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION_UPDATED);

            // Test 2: Get UserGroup by Id and Check updated Description
            UserGroupDTO ugDescriptionUpdated = repository.getById(userGroupTest.getId());
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION_UPDATED, ugDescriptionUpdated.getDescription(), "smokeTestUpdate: description doesn't match.");

            // Test 3: Update back to previous value
            userGroupTest.setName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            userGroupTest.setDescription(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION);
            repository.update(userGroupTest);

            // Test 3: Get UserGroup by Id and Check reverted name and description
            UserGroupDTO ugTestReverted = repository.getById(userGroupTest.getId());
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, ugTestReverted.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION, ugTestReverted.getDescription(), "smokeTestUpdate: description doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdate", e);
        }
    }

    // Test archive
    @Order(5)
    @Test
    public void smokeTestArchive() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            // Get UserGroup by Name
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(userGroupTest, "smokeTestArchive: No UserGroup Found.");

            // Archive UserGroup
            userGroupTest.setArchived(true); // Not needed but let's stay consistent
            repository.archive(userGroupTest.getId());

            // Test 1: Get UserGroup by Id and Check archived
            UserGroupDTO userGroupTestArchived = repository.getById(userGroupTest.getId());
            assertNotSame(false, userGroupTestArchived.isArchived(), "smokeTestArchive: UserGroup is not archived.");

            // Test 2: Undo Archive
            userGroupTest.setArchived(false); // Not needed but let's stay consistent
            repository.undoArchive(userGroupTest.getId());

            // Test 2: Get UserGroup by Id and Check updated archived
            UserGroupDTO ugArchivedUpdated = repository.getById(userGroupTest.getId());
            assertSame(false, ugArchivedUpdated.isArchived(), "smokeTestArchive: UserGroup is archived.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestArchive", e);
        }
    }

    // Test delete
    @Order(6)
    @Test
    public void smokeTestDelete() {
        try {
            // Get UserGroup by Name
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(userGroupTest, "smokeTestDelete: No UserGroup Found.");

            // Delete UserGroup
            repository.delete(userGroupTest.getId());

            // Test: Get UserGroup by Id and Check
            UserGroupDTO ugDeleted = repository.getById(userGroupTest.getId());
            assertNull(ugDeleted, "smokeTestDelete: UserGroup Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test insert with name/description
    @Order(7)
    @Test
    public void smokeTestInsertByName() {
        try {
            repository.insert(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION);
            UserGroupDTO userGroupTest = repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(userGroupTest, "smokeTestInsertByName: No UserGroup Found.");
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, userGroupTest.getName(), "smokeTestInsertByName: name doesn't match.");
            assertNotSame(UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION, userGroupTest.getDescription(), "smokeTestInsertByName: description doesn't match.");
            assertSame(false, userGroupTest.isArchived(), "smokeTestInsertByName: archived doesn't match.");
            repository.delete(userGroupTest.getId());
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsertByName", e);
        }
    }

    // Test getAll and getAllArchived
    @Order(8)
    @Test
    public void smokeTestGetAll() {
        try {
            for(int i = 0; i < 6; i++) {
                repository.insert(new UserGroupDTO(UserGroupDTOTestConstant.USERGROUP_TEST_NAME_X + i, UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION_X + i));
            }
            List<UserGroupDTO> userGroupTests = new ArrayList<UserGroupDTO>();
            for(int i = 0; i < 6; i++) {
                userGroupTests.add(repository.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME_X + i));
            }

            // Create array of 6x UGTEST UserGroup Ids for comparison
            List<Integer> userGroupTestIds = new ArrayList<Integer>();
            for(int i = 0; i < 6; i++) {
                userGroupTestIds.add(userGroupTests.get(i).getId());
            }

            // Test1: getAll - call getAll
            List<UserGroupDTO> ugAllTests = repository.getAll();
            assertFalse(ugAllTests.isEmpty(), "smokeTestGetAll: List is Empty.");
            assertEquals(6, ugAllTests.size(), "smokeTestGetAll: List is not 6 (" + ugAllTests.size() + ").");

            // Test1: getAll - Check results
            for(UserGroupDTO userGroupTest :  ugAllTests) {
                userGroupTestIds.removeIf(n -> n == userGroupTest.getId());
            }
            assertTrue(userGroupTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 2: getAllArchived - update userGroupTest0 and userGroupTest3 to archive
            userGroupTests.get(0).setArchived(true); // Not needed but let's stay consistent
            repository.archive(userGroupTests.get(0).getId());
            userGroupTests.get(3).setArchived(true); // Not needed but let's stay consistent
            repository.archive(userGroupTests.get(3).getId());

            // Create array of 2x UGTEST UserGroup Ids for comparison
            userGroupTestIds.add(userGroupTests.get(0).getId());
            userGroupTestIds.add(userGroupTests.get(3).getId());

            // Test 2: getAllArchived - call function
            List<UserGroupDTO> ugArchivedTests = repository.getAllArchived();

            // Test 2: getAllArchived - Check Result
            assertFalse(ugArchivedTests.isEmpty(), "smokeTestGetAll: Archived List is Empty.");
            assertEquals(2, ugArchivedTests.size(), "smokeTestGetAll: Archived List is not 2. (" + ugArchivedTests.size() + ").");

            // Test1: getAll - Check results
            for(UserGroupDTO userGroupTest :  ugArchivedTests) {
                userGroupTestIds.removeIf(n -> n == userGroupTest.getId());
            }
            assertTrue(userGroupTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Clean up
            for(int i = 0; i < 6; i++) {
                repository.delete(userGroupTests.get(i).getId());
            }
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAll", e);
        }
    }
}
