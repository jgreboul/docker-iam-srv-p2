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
import jgr.iam.constant.dto.FeatureDTOTestConstant; // Feature Test Constant
import jgr.iam.model.dto.FeatureDTO; // Feature

// Test FeatureRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FeatureRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(FeatureRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Feature Repository
    private FeatureRepository featureRepository;

    // Application  Repository
    private ApplicationRepository appRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            featureRepository = new FeatureRepository(connector);
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
    @Test
    @Order(1)
    void smokeTestInsert() throws SQLException {
        try {
            appRepository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsert: No Application Found.");
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION, appTest.getId()));
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    @Test
    @Order(2)
    void smokeTestGetByName() throws SQLException {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetByName: No Application Found.");

            // Get Feature by Name
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetByName: No Feature Found.");
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_NAME, featTest.getName(), "smokeTestGetByName: name doesn't match.");
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION, featTest.getDescription(), "smokeTestGetByName: description doesn't match.");
            assertSame(false, featTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
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
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "negativeTestGetByName: No Application Found.");

            // Get Feature by Name
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_UPDATED);
            assertNull(featTest, "negativeTestGetByName: Feature Found.");
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
            // Get Feature by Name
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestUpdate: No Application Found.");
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestUpdate: No Feature Found.");

            // Test 1: Update Name and Description
            featTest.setName(FeatureDTOTestConstant.FEATURE_TEST_NAME_UPDATED);
            featTest.setDescription(FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_UPDATED);
            featureRepository.update(featTest);

            // Test 1: Get Feature by Id and Check updated Name and Description
            FeatureDTO featTestUpdated = featureRepository.getById(featTest.getId());
            logger.info("\t" + featTestUpdated.toString());
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_NAME_UPDATED, featTestUpdated.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_UPDATED, featTestUpdated.getDescription(), "smokeTestUpdate: description doesn't match.");

            // Test 2: Update name back to previous value
            featTest.setName(FeatureDTOTestConstant.FEATURE_TEST_NAME);
            featureRepository.update(featTest);

            // Test 2: Update description back to previous value
            featTest.setDescription(FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION);
            featureRepository.updateDescription(featTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION);

            // Test 2: Get Feature by Id and Check reverted name and description
            FeatureDTO featTestReverted = featureRepository.getById(featTest.getId());
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_NAME, featTestReverted.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION, featTestReverted.getDescription(), "smokeTestUpdate: description doesn't match.");
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
            // Get Feature by Name
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Feature Found.");

            // Archive Application
            featTest.setArchived(true); // Not needed but let's stay consistent
            featureRepository.archive(featTest.getId());

            // Test 1: Get feature by Id and Check archived
            FeatureDTO featureTestArchived = featureRepository.getById(featTest.getId());
            assertSame(true, featureTestArchived.isArchived(), "smokeTestArchive: Feature is not archived.");

            // Test 2: Undo Archive
            featTest.setArchived(false); // Not needed but let's stay consistent
            featureRepository.undoArchive(featTest.getId());

            // Test 2: Get Application by Id and Check updated archived
            FeatureDTO featureTestNotArchived = featureRepository.getById(featTest.getId());
            assertSame(false, featureTestNotArchived.isArchived(), "smokeTestArchive: Feature is archived.");
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
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestDelete: No Application Found.");

            // Get Feature by Name
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestDelete: No Feature Found.");

            // Delete Application
            featureRepository.delete(featTest.getId());
            appRepository.delete(appTest.getId()); //Delete Application as well

            // Test: Get Feature by Id and Check
            FeatureDTO featureDeleted = featureRepository.getById(featTest.getId());
            assertNull(featureDeleted, "smokeTestDelete: Feature Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test getAll, getAllArchived, and getAll for a specific Application
    @Order(7)
    @Test
    public void smokeTestGetAll() {
        try {
            // Setup Data
            appRepository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + 0, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_X + 0));
            appRepository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + 1, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_X + 1));
            ApplicationDTO appTest0 = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + 0);
            ApplicationDTO appTest1 = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X + 1);
            assertNotNull(appTest0, "smokeTestGetAll: appTest0 Found.");
            assertNotNull(appTest1, "smokeTestGetAll: appTest1 Found.");

            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 0, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 0, appTest0.getId()));
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 1, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 1, appTest0.getId()));
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 2, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 2, appTest0.getId()));
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 3, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 3, appTest0.getId()));
            FeatureDTO featureTestA0 = featureRepository.getByName(appTest0.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 0);
            FeatureDTO featureTestA1 = featureRepository.getByName(appTest0.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 1);
            FeatureDTO featureTestA2 = featureRepository.getByName(appTest0.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 2);
            FeatureDTO featureTestA3 = featureRepository.getByName(appTest0.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 3);
            assertNotNull(featureTestA0, "smokeTestGetAll: featureTestA0 Found.");
            assertNotNull(featureTestA1, "smokeTestGetAll: featureTestA1 Found.");
            assertNotNull(featureTestA2, "smokeTestGetAll: featureTestA2 Found.");
            assertNotNull(featureTestA3, "smokeTestGetAll: featureTestA3 Found.");

            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 0, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_BX + 0, appTest1.getId()));
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 1, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_BX + 1, appTest1.getId()));
            featureRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 2, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_BX + 2, appTest1.getId()));
            FeatureDTO featureTestB0 = featureRepository.getByName(appTest1.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 0);
            FeatureDTO featureTestB1 = featureRepository.getByName(appTest1.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 1);
            FeatureDTO featureTestB2 = featureRepository.getByName(appTest1.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_BX + 2);
            assertNotNull(featureTestB0, "smokeTestGetAll: featureTestB0 Found.");
            assertNotNull(featureTestB1, "smokeTestGetAll: featureTestB1 Found.");
            assertNotNull(featureTestB2, "smokeTestGetAll: featureTestB2 Found.");

            // Test 1: getAll - Create array of all FTEST  feature Ids for comparison
            List<Integer> featureTestAllIds = new ArrayList<Integer>();
            featureTestAllIds.add(featureTestA0.getId());
            featureTestAllIds.add(featureTestA1.getId());
            featureTestAllIds.add(featureTestA2.getId());
            featureTestAllIds.add(featureTestA3.getId());
            featureTestAllIds.add(featureTestB0.getId());
            featureTestAllIds.add(featureTestB1.getId());
            featureTestAllIds.add(featureTestB2.getId());

            // Test 1: getAll - call getAll
            List<FeatureDTO> FeatureAlls = featureRepository.getAll();
            assertFalse(FeatureAlls.isEmpty(), "smokeTestGetAll: FeatureAlls List is Empty.");
            assertEquals(7, FeatureAlls.size(), "smokeTestGetAll: FeatureAlls List is not 7 (" + FeatureAlls.size() + ").");

            // Test 1: getAll - Check results
            for (FeatureDTO featureTest : FeatureAlls) {
                featureTestAllIds.removeIf(n -> n == featureTest.getId());
            }
            assertTrue(featureTestAllIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 2: getAll for Application A -  Create array of the 4X FTEST-A  feature Ids for comparison
            List<Integer> featureTestAIds = new ArrayList<Integer>();
            featureTestAIds.add(featureTestA0.getId());
            featureTestAIds.add(featureTestA1.getId());
            featureTestAIds.add(featureTestA2.getId());
            featureTestAIds.add(featureTestA3.getId());

            // Test 2: getAll for Application A -  call getAll
            List<FeatureDTO> FeatureAs = featureRepository.getAll(appTest0.getId());
            assertFalse(FeatureAs.isEmpty(), "smokeTestGetAll: FeatureAs List is Empty.");
            assertEquals(4, FeatureAs.size(), "smokeTestGetAll: FeatureAs List is not 4 (" + FeatureAs.size() + ").");

            // Test 2: getAllCount for Application A - call getAllCount
            int featureCount = featureRepository.getAllCount(appTest0.getId());
            assertEquals(4, featureCount, "smokeTestGetAll: Feature Count is not 4 (" + featureCount + ").");

            // Test 2: getAll for Application A -  Check results
            for (FeatureDTO featureTest : FeatureAs) {
                featureTestAIds.removeIf(n -> n == featureTest.getId());
            }
            assertTrue(featureTestAIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 3: getAllArchived - update featureTestA2, featureTestB1 and featureTestB2  to archive
            featureTestA2.setArchived(true); // Not needed but let's stay consistent
            featureRepository.archive(featureTestA2.getId());
            featureTestB1.setArchived(true); // Not needed but let's stay consistent
            featureRepository.archive(featureTestB1.getId());
            featureTestB2.setArchived(true); // Not needed but let's stay consistent
            featureRepository.archive(featureTestB2.getId());

            // Test 3: gCreate array of all archived FTEST Ids for comparison
            List<Integer> featureTestArchivedIds = new ArrayList<Integer>();
            featureTestArchivedIds.add(featureTestA2.getId());
            featureTestArchivedIds.add(featureTestB1.getId());
            featureTestArchivedIds.add(featureTestB2.getId());

            // Test 3: getAllArchived - call function
            List<FeatureDTO> FeatureArchivedTests = featureRepository.getAllArchived();

            // Test 3: getAllArchived - Check Result
            assertFalse(FeatureArchivedTests.isEmpty(), "smokeTestGetAll: Archived List is Empty.");
            assertEquals(3, FeatureArchivedTests.size(), "smokeTestGetAll: Archived List is not 3. (" + FeatureArchivedTests.size() + ").");

            // Test 3: getAll - Check results
            for (FeatureDTO featureTest : FeatureArchivedTests) {
                featureTestArchivedIds.removeIf(n -> n == featureTest.getId());
            }
            assertTrue(featureTestArchivedIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 4: getAll for Application B -  call getAll
            List<FeatureDTO> FeatureBs = featureRepository.getAll(appTest1.getId());
            assertFalse(FeatureBs.isEmpty(), "smokeTestGetAll: FeatureBs List is Empty.");
            assertEquals(1, FeatureBs.size(), "smokeTestGetAll: FeatureBs List is not 1 (" + FeatureAs.size() + ").");

            // Test 4: getAll for Application B -  Check results
            assertEquals(FeatureBs.get(0).getId(), featureTestB0.getId(), "smokeTestGetAll: FeatureBs doesn't match.");

            // Clean up
            appRepository.delete(appTest0.getId());
            appRepository.delete(appTest1.getId());

            //Check if Features have been deleted: they should have been due to ON DELETE CASCADE clause
            FeatureAs = featureRepository.getAll(appTest0.getId());
            FeatureBs = featureRepository.getAll(appTest1.getId());
        } catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAll", e);
        }
    }

    // Test Insert By Name
    @Order(8)
    @Test
    public void smokeTestInsertByName() {
        try {
            //Insert
            appRepository.insert(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsertByName: No Application Found.");
            featureRepository.insert(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION);
            FeatureDTO featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestInsertByName: No Feature Found.");
            //Delete
            appRepository.delete(appTest.getId()); // Delete Application should delete Feature
            featTest = featureRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNull(featTest, "smokeTestInsertByName: Feature Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsertByName", e);
        }
    }
}
