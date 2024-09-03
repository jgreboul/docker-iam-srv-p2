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
import jgr.iam.constant.dto.PermissionDTOTestConstant; // Permission Test Constant
import jgr.iam.model.dto.PermissionDTO; // Permission

// Test PermissionRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PermissionRepositoryE2ETest {

    // Logger
    private final static Logger logger = LogManager.getLogger(PermissionRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // PermissionRepository
    private PermissionRepository repository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            repository = new PermissionRepository(connector);
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
            PermissionDTO permissionTest = new PermissionDTO(PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
            repository.insert(permissionTest);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    // test GetByName
    @Order(2)
    @Test
    public void smokeTestGetByName() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permissionTest, "smokeTestGetByName: No Permission Found.");
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_NAME, permissionTest.getName(), "smokeTestGetByName: name doesn't match.");
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, permissionTest.getDescription(), "smokeTestGetByName: description doesn't match.");
            assertSame(false, permissionTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetByName", e);
        }
    }

    // Negative test GetByName
    @Order(3)
    @Test
    public void negativeTestGetByName() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME_UPDATED);
            assertNull(permissionTest, "negativeTestGetByName: Permission Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGetByName", e);
        }
    }

    // Test Update Name and Description (and revert updates)
    @Order(4)
    @Test
    public void smokeTestUpdate() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            // Get Permission by Name
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permissionTest, "smokeTestUpdate: No Permission Found.");

            // Test 1: Update Name
            permissionTest.setName(PermissionDTOTestConstant.PERMISSION_TEST_NAME_UPDATED);
            repository.update(permissionTest);

            // Test 1: Get Permission by Id and Check updated Name
            PermissionDTO permissionTestNameUpdated = repository.getById(permissionTest.getId());
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_NAME_UPDATED, permissionTestNameUpdated.getName(), "smokeTestUpdate: name doesn't match.");

            // Test 2: Update Description
            permissionTest.setDescription(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_UPDATED);
            repository.updateDescription(permissionTest.getId(), PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_UPDATED);

            // Test 2: Get Permission by Id and Check updated Description
            PermissionDTO permissionDescriptionUpdated = repository.getById(permissionTest.getId());
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_UPDATED, permissionDescriptionUpdated.getDescription(), "smokeTestUpdate: description doesn't match.");

            // Test 3: Update back to previous value
            permissionTest.setName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            permissionTest.setDescription(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
            repository.update(permissionTest);

            // Test 3: Get Permission by Id and Check reverted name and description
            PermissionDTO permissionTestReverted = repository.getById(permissionTest.getId());
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_NAME, permissionTestReverted.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, permissionTestReverted.getDescription(), "smokeTestUpdate: description doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdate", e);
        }
    }

    // Test Archive
    @Order(5)
    @Test
    public void smokeTestArchive() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            // Get Permission by Name
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permissionTest, "smokeTestArchive: No Permission Found.");

            // Archive Permission
            permissionTest.setArchived(true); // Not needed but let's stay consistent
            repository.archive(permissionTest.getId());

            // Test 1: Get Permission by Id and Check archived
            PermissionDTO permissionTestArchived = repository.getById(permissionTest.getId());
            assertNotSame(false, permissionTestArchived.isArchived(), "smokeTestArchive: Permission is not archived.");

            // Test 2: Undo Archive
            permissionTest.setArchived(false); // Not needed but let's stay consistent
            repository.undoArchive(permissionTest.getId());

            // Test 2: Get Permission by Id and Check updated archived
            PermissionDTO permissionArchivedUpdated = repository.getById(permissionTest.getId());
            assertSame(false, permissionArchivedUpdated.isArchived(), "smokeTestArchive: Permission is archived.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestArchive", e);
        }
    }

    // Test Delete
    @Order(6)
    @Test
    public void smokeTestDelete() {
        try {
            // Get Permission by Name
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permissionTest, "smokeTestDelete: No Permission Found.");

            // Delete Permission
            repository.delete(permissionTest.getId());

            // Test: Get Permission by Id and Check
            PermissionDTO permissionDeleted = repository.getById(permissionTest.getId());
            assertNull(permissionDeleted, "smokeTestDelete: Permission Found.");
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
            repository.insert(PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
            PermissionDTO permissionTest = repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permissionTest, "smokeTestInsertByName: No Permission Found.");
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_NAME, permissionTest.getName(), "smokeTestInsertByName: name doesn't match.");
            assertNotSame(PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION, permissionTest.getDescription(), "smokeTestInsertByName: description doesn't match.");
            assertSame(false, permissionTest.isArchived(), "smokeTestInsertByName: archived doesn't match.");
            repository.delete(permissionTest.getId());
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
                repository.insert(new PermissionDTO(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X + i, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION_X + i));
            }

            List<PermissionDTO> permissionTests = new ArrayList<PermissionDTO>();
            for(int i = 0; i < 6; i++) {
                permissionTests.add(repository.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME_X + i));
            }

            // Create array of 6x PTEST Permission Ids for comparison
            List<Integer> permissionTestIds = new ArrayList<Integer>();
            for(int i = 0; i < 6; i++) {
                permissionTestIds.add(permissionTests.get(i).getId());
            }

            // Test1: getAll - call getAll
            List<PermissionDTO> permissionAllTests = repository.getAll();
            assertFalse(permissionAllTests.isEmpty(), "smokeTestGetAll: List is Empty.");
            assertEquals(6, permissionAllTests.size(), "smokeTestGetAll: List is not 6 (" + permissionAllTests.size() + ").");

            // Test1: getAll - Check results
            for(PermissionDTO permissionTest :  permissionAllTests) {
                permissionTestIds.removeIf(n -> n == permissionTest.getId());
            }
            assertTrue(permissionTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 2: getAllArchived - update permissionTest0 and permissionTest3 to archive
            permissionTests.get(0).setArchived(true); // Not needed but let's stay consistent
            repository.archive(permissionTests.get(0).getId());
            permissionTests.get(3).setArchived(true); // Not needed but let's stay consistent
            repository.archive(permissionTests.get(3).getId());

            // Create array of 2x PTEST Permission Ids for comparison
            permissionTestIds.add(permissionTests.get(0).getId());
            permissionTestIds.add(permissionTests.get(3).getId());

            // Test 2: getAllArchived - call function
            List<PermissionDTO> permissionArchivedTests = repository.getAllArchived();

            // Test 2: getAllArchived - Check Result
            assertFalse(permissionArchivedTests.isEmpty(), "smokeTestGetAll: Archived List is Empty.");
            assertEquals(2, permissionArchivedTests.size(), "smokeTestGetAll: Archived List is not 2. (" + permissionArchivedTests.size() + ").");

            // Test1: getAll - Check results
            for(PermissionDTO permissionTest :  permissionArchivedTests) {
                permissionTestIds.removeIf(n -> n == permissionTest.getId());
            }
            assertTrue(permissionTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Clean up
            for(int i = 0; i < 6; i++) {
                repository.delete(permissionTests.get(i).getId());
            }
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAll", e);
        }
    }
}