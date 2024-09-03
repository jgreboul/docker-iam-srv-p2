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
import jgr.iam.constant.dto.RoleDTOTestConstant; // Role Test Constant
import jgr.iam.model.dto.RoleDTO; // Role

// Test RoleRepository Class
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(RoleRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Role Repository
    private RoleRepository roleRepository;

    // Feature  Repository
    private FeatureRepository featRepository;

    // Application  Repository
    private ApplicationRepository appRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
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

    @Test
    @Order(1)
    void smokeTestInsert() {
        try {
            appRepository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION));
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestInsert: No Application Found.");
            featRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION, appTest.getId()));
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestInsert: No Feature Found.");
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION, featTest.getId()));
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    @Test
    @Order(2)
    void smokeTestGetByName()  {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestGetByName: No Application Found.");
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestGetByName: No Feature Found.");
            // Get Role by Name
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestGetByName: No Role Found.");
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_NAME, roleTest.getName(), "smokeTestGetByName: name doesn't match.");
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_DESCRIPTION, roleTest.getDescription(), "smokeTestGetByName: description doesn't match.");
            assertSame(false, roleTest.isArchived(), "smokeTestGetByName: archived doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetByName", e);
        }
    }

    @Test
    @Order(3)
    void negativeTestGetByName() {
        iamDBConnectorUtil connector = new iamDBConnectorUtil();
        try {
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "negativeTestGetByName: No Application Found.");
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "negativeTestGetByName: No Feature Found.");
            // Get Role by Name
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_UPDATED);
            assertNull(roleTest, "negativeTestGetByName: Role Found.");
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
            // Get Role by Name
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestUpdate: No Application Found.");
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestUpdate: No Feature Found.");
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNotNull(featTest, "smokeTestUpdate: No Role Found.");

            // Test 1: Update Name and Description
            roleTest.setName(RoleDTOTestConstant.ROLE_TEST_NAME_UPDATED);
            roleTest.setDescription(RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_UPDATED);
            roleRepository.update(roleTest);

            // Test 1: Get Role by Id and Check updated Name and Description
            RoleDTO roleTestUpdated = roleRepository.getById(roleTest.getId());
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_NAME_UPDATED, roleTestUpdated.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_UPDATED, roleTestUpdated.getDescription(), "smokeTestUpdate: description doesn't match.");

            // Test 2: Update back to previous value
            roleTest.setName(RoleDTOTestConstant.ROLE_TEST_NAME);
            roleRepository.update(roleTest);
            roleTest.setDescription(RoleDTOTestConstant.ROLE_TEST_DESCRIPTION);
            roleRepository.updateDescription(roleTest.getId(), RoleDTOTestConstant.ROLE_TEST_DESCRIPTION);

            // Test 2: Get Role by Id and Check reverted name and description
            RoleDTO roleTestReverted = roleRepository.getById(roleTest.getId());
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_NAME, roleTestReverted.getName(), "smokeTestUpdate: name doesn't match.");
            assertNotSame(RoleDTOTestConstant.ROLE_TEST_DESCRIPTION, roleTestReverted.getDescription(), "smokeTestUpdate: description doesn't match.");
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
            // Get Role by Name
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestArchive: No Application Found.");
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Feature Found.");
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNotNull(featTest, "smokeTestArchive: No Role Found.");

            // Archive Feature
            roleTest.setArchived(true); // Not needed but let's stay consistent
            roleRepository.archive(roleTest.getId());

            // Test 1: Get Role by Id and Check archived
            RoleDTO roleTestArchived = roleRepository.getById(roleTest.getId());
            assertSame(true, roleTestArchived.isArchived(), "smokeTestArchive: Role is not archived.");

            // Test 2: Undo Archive
            roleTest.setArchived(false); // Not needed but let's stay consistent
            roleRepository.undoArchive(roleTest.getId());

            // Test 2: Get Feature by Id and Check updated archived
            RoleDTO roleTestNotArchived = roleRepository.getById(roleTest.getId());
            assertSame(false, roleTestNotArchived.isArchived(), "smokeTestArchive: Role is archived.");
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
            FeatureDTO featTest = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestDelete: No Feature Found.");

            // Get Role by Name
            RoleDTO roleTest = roleRepository.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestDelete: No Role Found.");

            // Delete Role
            roleRepository.delete(featTest.getId());
            featRepository.delete(featTest.getId()); //Delete Feature as well
            appRepository.delete(appTest.getId()); //Delete Application as well

            // Test: Get Role by Id and Check
            RoleDTO RoleDeleted = roleRepository.getById(roleTest.getId());
            assertNull(RoleDeleted, "smokeTestDelete: Role Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test getAll, getAllArchived, and getAll for a specific Feature
    @Order(7)
    @Test
    public void smokeTestGetAll() {
        try {
            // Setup Data
            appRepository.insert(new ApplicationDTO(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION_X));
            ApplicationDTO appTest = appRepository.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME_X);
            assertNotNull(appTest, "smokeTestGetAll: No Application Found.");

            featRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 0, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 0, appTest.getId()));
            featRepository.insert(new FeatureDTO(FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 1, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION_AX + 1, appTest.getId()));
            FeatureDTO featTest0 = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 0);
            FeatureDTO featTest1 = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 1);
            assertNotNull(featTest0, "smokeTestGetAll: featTest0 Found.");
            assertNotNull(featTest1, "smokeTestGetAll: featTest1 Found.");

            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_AX + 0, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_AX + 0, featTest0.getId()));
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_AX + 1, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_AX + 1, featTest0.getId()));
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_AX + 2, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_AX + 2, featTest0.getId()));
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_AX + 3, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_AX + 3, featTest0.getId()));
            RoleDTO roleTestA0 = roleRepository.getByName(featTest0.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_AX + 0);
            RoleDTO roleTestA1 = roleRepository.getByName(featTest0.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_AX + 1);
            RoleDTO roleTestA2 = roleRepository.getByName(featTest0.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_AX + 2);
            RoleDTO roleTestA3 = roleRepository.getByName(featTest0.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_AX + 3);
            assertNotNull(roleTestA0, "smokeTestGetAll: roleTestA0 Found.");
            assertNotNull(roleTestA1, "smokeTestGetAll: roleTestA1 Found.");
            assertNotNull(roleTestA2, "smokeTestGetAll: roleTestA2 Found.");
            assertNotNull(roleTestA3, "smokeTestGetAll: roleTestA3 Found.");

            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_BX + 0, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_BX + 0, featTest1.getId()));
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_BX + 1, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_BX + 1, featTest1.getId()));
            roleRepository.insert(new RoleDTO(RoleDTOTestConstant.ROLE_TEST_NAME_BX + 2, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION_BX + 2, featTest1.getId()));
            RoleDTO roleTestB0 = roleRepository.getByName(featTest1.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_BX + 0);
            RoleDTO roleTestB1 = roleRepository.getByName(featTest1.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_BX + 1);
            RoleDTO roleTestB2 = roleRepository.getByName(featTest1.getId(), RoleDTOTestConstant.ROLE_TEST_NAME_BX + 2);
            assertNotNull(roleTestB0, "smokeTestGetAll: roleTestB0 Found.");
            assertNotNull(roleTestB1, "smokeTestGetAll: roleTestB1 Found.");
            assertNotNull(roleTestB2, "smokeTestGetAll: roleTestB2 Found.");

            // Test 1: getAll - Create array of all RTEST Role Ids for comparison
            List<Integer> roleTestAllIds = new ArrayList<Integer>();
            roleTestAllIds.add(roleTestA0.getId());
            roleTestAllIds.add(roleTestA1.getId());
            roleTestAllIds.add(roleTestA2.getId());
            roleTestAllIds.add(roleTestA3.getId());
            roleTestAllIds.add(roleTestB0.getId());
            roleTestAllIds.add(roleTestB1.getId());
            roleTestAllIds.add(roleTestB2.getId());

            // Test 1: getAll - call getAll
            List<RoleDTO> roleAlls = roleRepository.getAll();
            assertFalse(roleAlls.isEmpty(), "smokeTestGetAll: roleAlls List is Empty.");
            assertEquals(7, roleAlls.size(), "smokeTestGetAll: roleAlls List is not 7 (" + roleAlls.size() + ").");

            // Test 1: getAll - Check results
            for (RoleDTO roleTest : roleAlls) {
                roleTestAllIds.removeIf(n -> n == roleTest.getId());
            }
            assertTrue(roleTestAllIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 2: getAll for Role A -  Create array of the 4X RTEST-A  Role Ids for comparison
            List<Integer> roleTestAIds = new ArrayList<Integer>();
            roleTestAIds.add(roleTestA0.getId());
            roleTestAIds.add(roleTestA1.getId());
            roleTestAIds.add(roleTestA2.getId());
            roleTestAIds.add(roleTestA3.getId());

            // Test 2: getAll for Feature A -  call getAll
            List<RoleDTO> roleAs = roleRepository.getAll(featTest0.getId());
            assertFalse(roleAs.isEmpty(), "smokeTestGetAll: roleAs List is Empty.");
            assertEquals(4, roleAs.size(), "smokeTestGetAll: roleAs List is not 4 (" + roleAs.size() + ").");

            // Test 2: getAllCount for Application A - call getAllCount
            int roleCount = roleRepository.getAllCount(featTest0.getId());
            assertEquals(4, roleCount, "smokeTestGetAll: Role Count is not 4 (" + roleCount + ").");

            // Test 2: getAll for Feature A -  Check results
            for (RoleDTO roleTest : roleAs) {
                roleTestAIds.removeIf(n -> n == roleTest.getId());
            }
            assertTrue(roleTestAIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 3: getAllArchived - update roleTestA2, roleTestB1 and roleTestB2  to archive
            roleTestA2.setArchived(true); // Not needed but let's stay consistent
            roleRepository.archive(roleTestA2.getId());
            roleTestB1.setArchived(true); // Not needed but let's stay consistent
            roleRepository.archive(roleTestB1.getId());
            roleTestB2.setArchived(true); // Not needed but let's stay consistent
            roleRepository.archive(roleTestB2.getId());

            // Test 3: gCreate array of all archived RTEST Ids for comparison
            List<Integer> roleTestArchivedIds = new ArrayList<Integer>();
            roleTestArchivedIds.add(roleTestA2.getId());
            roleTestArchivedIds.add(roleTestB1.getId());
            roleTestArchivedIds.add(roleTestB2.getId());

            // Test 3: getAllArchived - call function
            List<RoleDTO> roleArchivedTests = roleRepository.getAllArchived();

            // Test 3: getAllArchived - Check Result
            assertFalse(roleArchivedTests.isEmpty(), "smokeTestGetAll: Archived List is Empty.");
            assertEquals(3, roleArchivedTests.size(), "smokeTestGetAll: Archived List is not 3. (" + roleArchivedTests.size() + ").");

            // Test 3: getAll - Check results
            for (RoleDTO roleTest : roleArchivedTests) {
                roleTestArchivedIds.removeIf(n -> n == roleTest.getId());
            }
            assertTrue(roleTestArchivedIds.isEmpty(), "smokeTestGetAll: Checklist is Not Empty.");

            // Test 4: getAll for Feature B -  call getAll
            List<RoleDTO> roleBs = roleRepository.getAll(featTest1.getId());
            assertFalse(roleBs.isEmpty(), "smokeTestGetAll: roleBs List is Empty.");
            assertEquals(1, roleBs.size(), "smokeTestGetAll: roleBs List is not 1 (" + roleAs.size() + ").");

            // Test 4: getAll for Feature B -  Check results
            assertEquals(roleBs.get(0).getId(), roleTestB0.getId(), "smokeTestGetAll: roleBs doesn't match.");

            // Clean up
            appRepository.delete(appTest.getId());

            //Check if Features and Roles have been deleted: they should have been due to ON DELETE CASCADE clause
            roleAs = roleRepository.getAll(featTest0.getId());
            roleBs = roleRepository.getAll(featTest1.getId());
            featTest0 = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 0);
            assertNull(featTest0, "smokeTestGetAll: featTest0 is not null.");
            featTest1 = featRepository.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME_AX + 1);
            assertNull(featTest0, "smokeTestGetAll: featTest1 is not null.");
        } catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAll", e);
        }
    }
}