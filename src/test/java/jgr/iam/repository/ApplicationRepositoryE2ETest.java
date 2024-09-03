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
import jgr.iam.constant.dto.ApplicationDTOTestConstant; // Application Test Constant
import jgr.iam.model.dto.ApplicationDTO; // Application

// Test ApplicationRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationRepositoryE2ETest {

    // Logger
    private final static Logger logger = LogManager.getLogger(ApplicationRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // ApplicationRepository
    private ApplicationRepository repository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            repository = new ApplicationRepository(connector);
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
            ApplicationDTO appTest = new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            repository.insert(appTest);
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
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetByName: No Application Found.");
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, appTest.getName(), "smokeTestGetByName: name doesn't match.");
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION, appTest.getDescription(), "smokeTestGetByName: description doesn't match.");
            assertSame(false, appTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
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
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_UPDATED);
            assertNull(appTest, "negativeTestGetByName: Application Found.");
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
            // Get Application by Name
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestUpdate: No Application Found.");

            // Test 1: Update Name
            appTest.setName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_UPDATED);
            repository.update(appTest);

            // Test 1: Get Application by Id and Check updated Name
            ApplicationDTO appTestNameUpdated = repository.getById(appTest.getId());
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_UPDATED, appTestNameUpdated.getName(), "smokeTestUpdate: name doesn't match.");

            // Test 2: Update Description
            appTest.setDescription(ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_UPDATED);
            repository.updateDescription(appTest.getId(), ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_UPDATED);

            // Test 2: Get Application by Id and Check updated Description
            ApplicationDTO appDescriptionUpdated = repository.getById(appTest.getId());
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_UPDATED, appDescriptionUpdated.getDescription(), "smokeTestUpdate: description doesn't match.");

            // Test 3: Update back to previous value
            appTest.setName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            appTest.setDescription(ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            repository.update(appTest);

            // Test 3: Get Application by Id and Check reverted name and description
            ApplicationDTO appTestReverted = repository.getById(appTest.getId());
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, appTestReverted.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION, appTestReverted.getDescription(), "smokeTestUpdate: description doesn't match.");
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
            // Get Application by Name
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");

            // Archive Application
            appTest.setArchived(true); // Not needed but let's stay consistent
            repository.archive(appTest.getId());

            // Test 1: Get Application by Id and Check archived
            ApplicationDTO appTestArchived = repository.getById(appTest.getId());
            assertNotSame(false, appTestArchived.isArchived(), "smokeTestArchive: Application is not archived.");

            // Test 2: Undo Archive
            appTest.setArchived(false); // Not needed but let's stay consistent
            repository.undoArchive(appTest.getId());

            // Test 2: Get Application by Id and Check updated archived
            ApplicationDTO appArchivedUpdated = repository.getById(appTest.getId());
            assertSame(false, appArchivedUpdated.isArchived(), "smokeTestArchive: Application is archived.");
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
            // Get Application by Name
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestDelete: No Application Found.");

            // Delete Application
            repository.delete(appTest.getId());

            // Test: Get Application by Id and Check
            ApplicationDTO appDeleted = repository.getById(appTest.getId());
            assertNull(appDeleted, "smokeTestDelete: Application Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test getAll and getAllArchived
    @Order(7)
    @Test
    public void smokeTestGetAll() {
        try {
            for(int i = 0; i < 6; i++) {
                repository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + i, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_X + i));
            }

            List<ApplicationDTO> appTests = new ArrayList<ApplicationDTO>();
            for(int i = 0; i < 6; i++) {
                appTests.add(repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + i));
            }

            // Create array of 6x MTEST application Ids for comparison
            List<Integer> appTestIds = new ArrayList<Integer>();
            for(int i = 0; i < 6; i++) {
                appTestIds.add(appTests.get(i).getId());
            }

            // Test1: getAll - call getAll
            List<ApplicationDTO> appAllTests = repository.getAll();
            assertFalse(appAllTests.isEmpty(), "smokeTestGetAll: List is Empty.");
            assertEquals(6, appAllTests.size(), "smokeTestGetAll: List is not 6 (" + appAllTests.size() + ").");

            // Test1: getAll - Check results
            for(ApplicationDTO appTest :  appAllTests) {
                appTestIds.removeIf(n -> n == appTest.getId());
            }
            assertTrue(appTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 2: getAllArchived - update appTest0 and appTest3 to archive
            appTests.get(0).setArchived(true); // Not needed but let's stay consistent
            repository.archive(appTests.get(0).getId());
            appTests.get(3).setArchived(true); // Not needed but let's stay consistent
            repository.archive(appTests.get(3).getId());

            // Create array of 2x ATEST application Ids for comparison
            appTestIds.add(appTests.get(0).getId());
            appTestIds.add(appTests.get(3).getId());

            // Test 2: getAllArchived - call function
            List<ApplicationDTO> applicationArchivedTests = repository.getAllArchived();

            // Test 2: getAllArchived - Check Result
            assertFalse(applicationArchivedTests.isEmpty(), "smokeTestGetAll: Archived List is Empty.");
            assertEquals(2, applicationArchivedTests.size(), "smokeTestGetAll: Archived List is not 2. (" + applicationArchivedTests.size() + ").");

            // Test1: getAll - Check results
            for(ApplicationDTO appTest :  applicationArchivedTests) {
                appTestIds.removeIf(n -> n == appTest.getId());
            }
            assertTrue(appTestIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Clean up
            for(int i = 0; i < 6; i++) {
                repository.delete(appTests.get(i).getId());
            }
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAll", e);
        }
    }

    // Test Insert by Name
    @Order(8)
    @Test
    public void smokeTestInsertByName() {
        try {
            //Insert
            repository.insert(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            ApplicationDTO appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsertByName: No Application Found.");
            //Delete
            repository.delete(appTest.getId());
            appTest = repository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNull(appTest, "smokeTestInsertByName:  Application Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsertByName", e);
        }
    }
}
