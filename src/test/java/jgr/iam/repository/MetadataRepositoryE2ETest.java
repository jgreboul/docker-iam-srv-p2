package jgr.iam.repository;

// External Objects
import org.junit.jupiter.api.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html
import static org.junit.jupiter.api.Assertions.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.nio.charset.StandardCharsets; // https://docs.oracle.com/javase/8/docs/api/java/nio/charset/StandardCharsets.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import java.util.ArrayList; // https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
import java.util.List; // https://docs.oracle.com/javase/8/docs/api/java/util/List.html
// Internal Objects
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util
import jgr.iam.constant.dto.MetadataDTOTestConstant; // Metadata Test Constant
import jgr.iam.model.dto.MetadataDTO; // Metadata
import jgr.iam.constant.dto.UserDTOTestConstant; // User Test Constant
import jgr.iam.model.dto.UserDTO; // User
import jgr.iam.constant.dto.UserGroupDTOTestConstant; // UserGroup Test Constant
import jgr.iam.model.dto.UserGroupDTO; // UserGroup
import jgr.iam.constant.dto.ApplicationDTOTestConstant; // Application Test Constant
import jgr.iam.model.dto.ApplicationDTO; // Application
import jgr.iam.constant.dto.FeatureDTOTestConstant; // Feature Test Constant
import jgr.iam.model.dto.FeatureDTO; // Feature
import jgr.iam.constant.dto.RoleDTOTestConstant; // Role Test Constant
import jgr.iam.model.dto.RoleDTO; // Role
import jgr.iam.constant.dto.PermissionDTOTestConstant; // Permission Test Constant
import jgr.iam.model.dto.PermissionDTO; // Permission

//MetadataRepository Test
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MetadataRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(MetadataRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Metadata Repositories
    private MetadataRepository userMetaRepo; // UserMetadata Repository
    private UserRepository userRepo; // User Repository
    private MetadataRepository ugMetaRepo; // UserGroupMetadata Repository
    private UserGroupRepository ugRepo; // UserGroup Repository
    private MetadataRepository appMetaRepo; // ApplicationMetadata Repository
    private ApplicationRepository appRepo; // Application Repository
    private MetadataRepository featMetaRepo; // FeatureMetadata Repository
    private FeatureRepository featRepo; // Feature Repository
    private MetadataRepository roleMetaRepo; // RoleMetadata Repository
    private RoleRepository roleRepo; // Role Repository
    private MetadataRepository permMetaRepo; // PermissionMetadata Repository
    private PermissionRepository permRepo; // Permission Repository

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            userMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_USER_TABLE);
            userRepo =  new UserRepository(connector);
            ugMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_UG_TABLE);
            ugRepo =  new UserGroupRepository(connector);
            appMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_APP_TABLE);
            appRepo =  new ApplicationRepository(connector);
            featMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_FEAT_TABLE);
            featRepo =  new FeatureRepository(connector);
            roleMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_ROLE_TABLE);
            roleRepo =  new RoleRepository(connector);
            permMetaRepo = new MetadataRepository(connector, MetadataDTOTestConstant.METADATA_TEST_PERM_TABLE);
            permRepo =  new PermissionRepository(connector);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "setUp", e);
        }
    }

    @AfterEach
    void tearDown() {
        connector.close();
    }

    // Test Insert
    @Order(1)
    @Test
    public void smokeTestInsert() {
        try {
            userRepo.insert(new UserDTO(UserDTOTestConstant.USER_TEST_USERNAME,
                    UserDTOTestConstant.USER_TEST_EMAIL,
                    UserDTOTestConstant.USER_TEST_PASSWORDHASH.getBytes(StandardCharsets.UTF_8),
                    UserDTOTestConstant.USER_TEST_PASSWORDSALT.getBytes(StandardCharsets.UTF_8),
                    UserDTOTestConstant.USER_TEST_FIRSTNAME,
                    UserDTOTestConstant.USER_TEST_LASTNAME));
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestInsert: No User Found.");
            userMetaRepo.insert(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME, MetadataDTOTestConstant.METADATA_TEST_USER_VALUE);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestInsert", e);
        }
    }

    // Test get
    @Order(2)
    @Test
    public void smokeTestGet() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestGet: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(userMetaTest, "smokeTestGet: No Metadata Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGet", e);
        }
    }

    // Negative Test get
    @Order(3)
    @Test
    public void negativeTestGet() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "negativeTestGet: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME + 0);
            assertNull(userMetaTest, "negativeTestGet: Metadata Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGet", e);
        }
    }

    // Test getValue from (non-archived) Metadata
    @Order(4)
    @Test
    public void smokeTestGetValue() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestGetValue: No User Found.");
            String value = userMetaRepo.getValue(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(value, "smokeTestGetValue: No Metadata Value Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetValue", e);
        }
    }

    // Negative Test getValue from (non-archived) Metadata
    @Order(5)
    @Test
    public void negativeTestGetValue() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            String value = userMetaRepo.getValue(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME + 0);
            assertNull(value, "negativeTestGetValue: Metadata Value Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestGetValue", e);
        }
    }

    // Test Update
    @Order(6)
    @Test
    public void smokeTestUpdateValue() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestUpdate: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(userMetaTest, "smokeTestUpdate: No UserMetadata Found.");
            userMetaRepo.updateValue(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME, MetadataDTOTestConstant.METADATA_TEST_USER_VALUE_UPDATED);
            userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertTrue(userMetaTest.getValue().equals(MetadataDTOTestConstant.METADATA_TEST_USER_VALUE_UPDATED), "smokeTestUpdate: value doesn't match.");
            userMetaRepo.updateValue(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME, MetadataDTOTestConstant.METADATA_TEST_USER_VALUE);
            userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertTrue(userMetaTest.getValue().equals(MetadataDTOTestConstant.METADATA_TEST_USER_VALUE), "smokeTestUpdate: value doesn't match.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUpdate", e);
        }
    }

    // Negative Test Update
    @Order(7)
    @Test
    public void negativeTestUpdateValue() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "negativeTestUpdate: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(userMetaTest, "negativeTestUpdate: No Metadata Found.");
            userMetaRepo.updateValue(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME, MetadataDTOTestConstant.METADATA_TEST_USER_VALUE_INVALID);
            // Expect a MysqlDataTruncation Exception
            fail("negativeTestUpdate: No Exception Thrown");
        }
        catch (MysqlDataTruncation e) {
            // Do Nothing
        }
        catch (Exception e) {
            ExceptionHandlerTestUtil.Handle(logger, "negativeTestUpdate", e);
        }
    }

    // Test Archive and Undo-Archive
    @Order(8)
    @Test
    public void smokeTestArchive() {
        try {
            // Get data
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestArchive: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(userMetaTest, "smokeTestArchive: No Metadata Found.");
            // Archive
            userMetaRepo.archive(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            // Check Result
            userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertTrue(userMetaTest.isArchived(), "smokeTestArchive: Metadata is not archived.");
            // Undo Archive
            userMetaRepo.undoArchive(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            // Check Result
            userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertFalse(userMetaTest.isArchived(), "smokeTestArchive: Metadata is active.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestArchive", e);
        }
    }

    // Test Delete
    @Order(9)
    @Test
    public void smokeTestDelete() {
        try {
            UserDTO userTest = userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME);
            assertNotNull(userTest, "smokeTestDelete: No User Found.");
            MetadataDTO userMetaTest = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNotNull(userMetaTest, "smokeTestDelete: No Metadata Found.");
            userMetaRepo.delete(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            MetadataDTO userMetaTestDeleted = userMetaRepo.get(userTest.getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME);
            assertNull(userMetaTestDeleted, "smokeTestDelete: Metadata Found.");
            userRepo.delete(userTest.getId());
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestDelete", e);
        }
    }

    // Test getAll(int refid), getAllArchived(int refid), and getAllArchived()
    @Order(10)
    @Test
    public void smokeTestGetAlls() {
        try {
            // Insert 3 Users, 10 UserMetadata: 6, 2, 2
            for(int i = 0; i < 3; i++) {
                userRepo.insert(new UserDTO(UserDTOTestConstant.USER_TEST_USERNAME_X + i,
                        UserDTOTestConstant.USER_TEST_EMAIL_X + i,
                        (UserDTOTestConstant.USER_TEST_PASSWORD_X + i).getBytes(StandardCharsets.UTF_8),
                        (UserDTOTestConstant.USER_TEST_PASSWORD_X + i).getBytes(StandardCharsets.UTF_8),
                        UserDTOTestConstant.USER_TEST_FIRSTNAME_X + i,
                        UserDTOTestConstant.USER_TEST_LASTNAME_X + i));
            }
            List<UserDTO> UserTests = new ArrayList<UserDTO>();
            for(int i = 0; i < 3; i++) {
                UserTests.add(userRepo.getByName(UserDTOTestConstant.USER_TEST_USERNAME_X + i));
            }
            for(int i = 0; i < 10; i++) { // - 3 Users, 10 UserMetadata: 6, 2, 2
                int iUserId = i < 6 ? UserTests.get(0).getId() : (i < 8 ? UserTests.get(1).getId() : UserTests.get(2).getId());
                userMetaRepo.insert(iUserId, MetadataDTOTestConstant.METADATA_TEST_USER_NAME + i, MetadataDTOTestConstant.METADATA_TEST_USER_VALUE);
            }
            List<MetadataDTO> UserMetadataTests = new ArrayList<MetadataDTO>();
            for(int i = 0; i < 10; i++) { // - 3 Users, 10 UserMetadata: 6, 2, 2
                int iUserId = i < 6 ? UserTests.get(0).getId() : (i < 8  ? UserTests.get(1).getId() : UserTests.get(2).getId());
                UserMetadataTests.add(userMetaRepo.get(iUserId, MetadataDTOTestConstant.METADATA_TEST_USER_NAME + i));
            }
            List<String> userMetadataTestNames = new ArrayList<String>();
            for(int i = 0; i < 10; i++) {
                userMetadataTestNames.add(UserMetadataTests.get(i).getName());
            }

            // getAll(int refid) - Call
            List<MetadataDTO> UserMetadataTestAs = userMetaRepo.getAll(UserTests.get(0).getId()); // should be 6
            assertFalse(UserMetadataTestAs.isEmpty(), "smokeTestGetAlls: Active List(A) is Empty.");
            assertEquals(6, UserMetadataTestAs.size(), "smokeTestGetAlls: Active List(A) is not 6 (" + UserMetadataTestAs.size() + ").");
            List<MetadataDTO> UserMetadataTestBs = userMetaRepo.getAll(UserTests.get(1).getId()); // should be 2
            assertFalse(UserMetadataTestBs.isEmpty(), "smokeTestGetAlls: Active List(B) is Empty.");
            assertEquals(2, UserMetadataTestBs.size(), "smokeTestGetAlls: Active List(B) is not 2 (" + UserMetadataTestBs.size() + ").");
            List<MetadataDTO> UserMetadataTestCs = userMetaRepo.getAll(UserTests.get(2).getId()); // should be 2
            assertFalse(UserMetadataTestCs.isEmpty(), "smokeTestGetAlls: Active List(C) is Empty.");
            assertEquals(2, UserMetadataTestCs.size(), "smokeTestGetAlls: Active List(C) is not 2 (" + UserMetadataTestCs.size() + ").");

            // getAll(int refid) - Check Results: 3 Users, 10 UserMetadata: 6, 2, 2
            for(MetadataDTO userMetaTest :  UserMetadataTestAs) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            for(MetadataDTO userMetaTest :  UserMetadataTestBs) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            for(MetadataDTO userMetaTest :  UserMetadataTestCs) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            assertTrue(userMetadataTestNames.isEmpty(), "smokeTestGetAlls: Checklist Active is Not Empty.");

            // getAllArchived(int refid) - Archive UserMetadata (0)1, (0)2, (2)8
            userMetaRepo.archive(UserTests.get(0).getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME + 1);
            userMetaRepo.archive(UserTests.get(0).getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME + 2);
            userMetaRepo.archive(UserTests.get(2).getId(), MetadataDTOTestConstant.METADATA_TEST_USER_NAME + 8);
            userMetadataTestNames.add(UserMetadataTests.get(1).getName());
            userMetadataTestNames.add(UserMetadataTests.get(2).getName());
            userMetadataTestNames.add(UserMetadataTests.get(8).getName());

            // getAllArchived(int refid) - Call
            List<MetadataDTO> UserMetadataTestArchivedAs = userMetaRepo.getAllArchived(UserTests.get(0).getId()); // should be 2
            assertFalse(UserMetadataTestArchivedAs.isEmpty(), "smokeTestGetAlls: Archived List(A) is Empty.");
            assertEquals(2, UserMetadataTestArchivedAs.size(), "smokeTestGetAlls: Archived List(A) is not 2 (" + UserMetadataTestArchivedAs.size() + ").");
            List<MetadataDTO> UserMetadataTestArchivedBs = userMetaRepo.getAllArchived(UserTests.get(1).getId()); // should be 0
            assertTrue(UserMetadataTestArchivedBs.isEmpty(), "smokeTestGetAlls: Archived List(B) is Not Empty.");
            List<MetadataDTO> UserMetadataTestArchivedCs = userMetaRepo.getAllArchived(UserTests.get(2).getId()); // should be 1
            assertFalse(UserMetadataTestArchivedCs.isEmpty(), "smokeTestGetAlls: Archived List(C) is Empty.");
            assertEquals(1, UserMetadataTestArchivedCs.size(), "smokeTestGetAlls: Archived List(C) is not 1 (" + UserMetadataTestArchivedCs.size() + ").");

            // getAllArchived(int refid) - Check Results
            for(MetadataDTO userMetaTest :  UserMetadataTestArchivedAs) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            for(MetadataDTO userMetaTest :  UserMetadataTestArchivedCs) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            assertTrue(userMetadataTestNames.isEmpty(), "smokeTestGetAlls: Checklist Archived is Not Empty.");

            // getAllArchived() - Call
            List<MetadataDTO> UserMetadataTestArchivedAlls = userMetaRepo.getAllArchived(); // should be 3
            assertFalse(UserMetadataTestArchivedAlls.isEmpty(), "smokeTestGetAlls: Archived List(All) is Empty.");
            assertEquals(3, UserMetadataTestArchivedAlls.size(), "smokeTestGetAlls: Archived List(All is not 3 (" + UserMetadataTestArchivedAlls.size() + ").");

            // getAllArchived() - Check Results
            userMetadataTestNames.add(UserMetadataTests.get(1).getName());
            userMetadataTestNames.add(UserMetadataTests.get(2).getName());
            userMetadataTestNames.add(UserMetadataTests.get(8).getName());
            for(MetadataDTO userMetaTest :  UserMetadataTestArchivedAlls) {
                userMetadataTestNames.removeIf(n -> userMetaTest.getName().equals(n));
            }
            assertTrue(userMetadataTestNames.isEmpty(), "smokeTestGetAlls: Checklist Archived is Not Empty.");

            // Clean up
            for(int i = 0; i < 10; i++) { // - 3 Users, 10 UserMetadata: 6, 2, 2
                int iUserId = i < 6 ? UserTests.get(0).getId() : (i < 7  ? UserTests.get(1).getId() : UserTests.get(2).getId());
                userMetaRepo.delete(iUserId, MetadataDTOTestConstant.METADATA_TEST_USER_NAME + i);
            }
            for(int i = 0; i < 3; i++) {
                userRepo.delete(UserTests.get(i).getId());
            }
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestGetAlls", e);
        }
    }

    // Test UserGroupMetadata table
    @Order(11)
    @Test
    public void smokeTestUgMetadata() {
        try {
            //Insert
            ugRepo.insert(UserGroupDTOTestConstant.USERGROUP_TEST_NAME, UserGroupDTOTestConstant.USERGROUP_TEST_DESCRIPTION);
            UserGroupDTO ugTest = ugRepo.getByName(UserGroupDTOTestConstant.USERGROUP_TEST_NAME);
            assertNotNull(ugTest, "smokeTestUgMetadata: No UserGroup Found.");
            ugMetaRepo.insert(ugTest.getId(), MetadataDTOTestConstant.METADATA_TEST_UG_NAME, MetadataDTOTestConstant.METADATA_TEST_UG_VALUE);
            //Get
            MetadataDTO ugMetaTest = ugMetaRepo.get(ugTest.getId(), MetadataDTOTestConstant.METADATA_TEST_UG_NAME);
            assertNotNull(ugMetaTest, "smokeTestUgMetadata: No Metadata Found.");
            //Test Setter functions
            ugMetaTest.setRefid(ugTest.getId());
            ugMetaTest.setName(MetadataDTOTestConstant.METADATA_TEST_UG_NAME);
            ugMetaTest.setValue(MetadataDTOTestConstant.METADATA_TEST_UG_VALUE );
            ugMetaTest.setArchived(true);
            //Delete
            ugMetaRepo.delete(ugTest.getId(), MetadataDTOTestConstant.METADATA_TEST_UG_NAME);
            ugRepo.delete(ugTest.getId()); // Delete User Group as well
            ugMetaTest = ugMetaRepo.get(ugTest.getId(), MetadataDTOTestConstant.METADATA_TEST_UG_NAME);
            assertNull(ugMetaTest, "smokeTestUgMetadata:  Metadata Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestUgMetadata", e);
        }
    }

    // Test ApplicationMetadata table
    @Order(12)
    @Test
    public void smokeTestAppMetadata() {
        try {
            //Insert
            appRepo.insert(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            ApplicationDTO appTest = appRepo.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestAppMetadata: No Application Found.");
            appMetaRepo.insert(appTest.getId(), MetadataDTOTestConstant.METADATA_TEST_APP_NAME, MetadataDTOTestConstant.METADATA_TEST_APP_VALUE);
            //Get
            MetadataDTO appMetaTest = appMetaRepo.get(appTest.getId(), MetadataDTOTestConstant.METADATA_TEST_APP_NAME);
            assertNotNull(appMetaTest, "smokeTestAppMetadata: No Metadata Found.");
            //Delete
            appRepo.delete(appTest.getId()); // Delete Application first (test ON DELETE CASCADE)
            appMetaTest = appMetaRepo.get(appTest.getId(), MetadataDTOTestConstant.METADATA_TEST_APP_NAME);
            assertNull(appMetaTest, "smokeTestAppMetadata:  Metadata Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestAppMetadata", e);
        }
    }

    // Test FeatureMetadata table
    @Order(13)
    @Test
    public void smokeTestFeatMetadata() {
        try {
            //Insert
            appRepo.insert(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            ApplicationDTO appTest = appRepo.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestFeatMetadata: No Application Found.");
            featRepo.insert(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION);
            FeatureDTO featTest = featRepo.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestFeatMetadata: No Feature Found.");
            featMetaRepo.insert(featTest.getId(), MetadataDTOTestConstant.METADATA_TEST_FEAT_NAME, MetadataDTOTestConstant.METADATA_TEST_FEAT_VALUE);
            //Get
            MetadataDTO featMetaTest = featMetaRepo.get(featTest.getId(), MetadataDTOTestConstant.METADATA_TEST_FEAT_NAME);
            assertNotNull(featMetaTest, "smokeTestFeatMetadata: No Metadata Found.");
            //Delete
            appRepo.delete(appTest.getId()); // Delete Application should delete Feature and Metadata
            featMetaTest = featMetaRepo.get(featTest.getId(), MetadataDTOTestConstant.METADATA_TEST_FEAT_NAME);
            assertNull(featMetaTest, "smokeTestFeatMetadata:  Metadata Found.");
            featTest = featRepo.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNull(featTest, "smokeTestFeatMetadata: Feature Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestFeatMetadata", e);
        }
    }

    // Test RoleMetadata table
    @Order(14)
    @Test
    public void smokeTestRoleMetadata() {
        try {
            //Insert
            appRepo.insert(ApplicationDTOTestConstant.APPLICATION_TEST_NAME, ApplicationDTOTestConstant.APPLICATION_TEST_DESCRIPTION);
            ApplicationDTO appTest = appRepo.getByName(ApplicationDTOTestConstant.APPLICATION_TEST_NAME);
            assertNotNull(appTest, "smokeTestRoleMetadata: No Application Found.");
            featRepo.insert(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME, FeatureDTOTestConstant.FEATURE_TEST_DESCRIPTION);
            FeatureDTO featTest = featRepo.getByName(appTest.getId(), FeatureDTOTestConstant.FEATURE_TEST_NAME);
            assertNotNull(featTest, "smokeTestRoleMetadata: No Feature Found.");
            roleRepo.insert(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME, RoleDTOTestConstant.ROLE_TEST_DESCRIPTION);
            RoleDTO roleTest = roleRepo.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNotNull(roleTest, "smokeTestRoleMetadata: No Role Found.");
            roleMetaRepo.insert(roleTest.getId(), MetadataDTOTestConstant.METADATA_TEST_ROLE_NAME, MetadataDTOTestConstant.METADATA_TEST_ROLE_VALUE);
            //Get
            MetadataDTO roleMetaTest = roleMetaRepo.get(roleTest.getId(), MetadataDTOTestConstant.METADATA_TEST_ROLE_NAME);
            assertNotNull(roleMetaTest, "smokeTestRoleMetadata: No Metadata Found.");
            //Delete
            appRepo.delete(appTest.getId()); // Delete Application should delete all dependent records
            roleMetaTest = roleMetaRepo.get(roleTest.getId(), MetadataDTOTestConstant.METADATA_TEST_ROLE_NAME);
            assertNull(roleMetaTest, "smokeTestRoleMetadata: Metadata Found.");
            roleTest = roleRepo.getByName(featTest.getId(), RoleDTOTestConstant.ROLE_TEST_NAME);
            assertNull(roleTest, "smokeTestRoleMetadata: Role Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestRoleMetadata", e);
        }
    }

    // Test PermissionMetadata table
    @Order(15)
    @Test
    public void smokeTestPermMetadata() {
        try {
            //Insert
            permRepo.insert(PermissionDTOTestConstant.PERMISSION_TEST_NAME, PermissionDTOTestConstant.PERMISSION_TEST_DESCRIPTION);
            PermissionDTO permTest = permRepo.getByName(PermissionDTOTestConstant.PERMISSION_TEST_NAME);
            assertNotNull(permTest, "smokeTestPermMetadata: No Permission Found.");
            permMetaRepo.insert(permTest.getId(), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME, MetadataDTOTestConstant.METADATA_TEST_PERM_VALUE);
            //Get
            MetadataDTO permMetaTest = permMetaRepo.get(permTest.getId(), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
            assertNotNull(permMetaTest, "smokeTestPermMetadata: No Metadata Found.");
            //Delete
            permRepo.delete(permTest.getId()); // Delete Permission first (test ON DELETE CASCADE)
            permMetaTest = appMetaRepo.get(permTest.getId(), MetadataDTOTestConstant.METADATA_TEST_PERM_NAME);
            assertNull(permMetaTest, "smokeTestPermMetadata: Metadata Found.");
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "smokeTestPermMetadata", e);
        }
    }
}