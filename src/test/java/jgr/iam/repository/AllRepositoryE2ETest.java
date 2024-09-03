package jgr.iam.repository;

// External Objects
import org.junit.jupiter.api.*; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
// Internal Objects
import jgr.iam.util.ExceptionHandlerTestUtil; // Exception Stack Trace Util
import jgr.iam.util.iamDBConnectorTestUtil; // iamDB Test Connect Util
import jgr.iam.util.iamDBConnectorUtil; // iamDB Connector Util
import jgr.iam.model.dto.ApplicationDTO; // Application
import jgr.iam.model.dto.FeatureDTO; // Feature
import jgr.iam.model.dto.RoleDTO; // Role
import jgr.iam.model.dto.PermissionDTO;
import jgr.iam.constant.data.AdvancedAppDataTestConstant;
import jgr.iam.constant.iamDBMetadataTable;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllRepositoryE2ETest {
    // Logger
    private final static Logger logger = LogManager.getLogger(AllRepositoryE2ETest.class.getCanonicalName());

    // iamDBConector
    private iamDBConnectorUtil connector;

    // Repositories
    private RolePermissionRepository rpRepository;
    private PermissionRepository pRepository;
    private RoleRepository roleRepository;
    private FeatureRepository featRepository;
    private ApplicationRepository appRepository;
    private MetadataRepository appMetaRepository;
    private MetadataRepository featMetaRepository;
    private MetadataRepository roleMetaRepository;
    private MetadataRepository permMetaRepository;

    @BeforeEach
    void setUp() {
        connector = new iamDBConnectorUtil();
        try {
            iamDBConnectorTestUtil.iamDBConnect(connector);
            rpRepository = new RolePermissionRepository(connector);
            pRepository = new PermissionRepository(connector);
            permMetaRepository = new MetadataRepository(connector, iamDBMetadataTable.PERM);
            roleRepository = new RoleRepository(connector);
            roleMetaRepository = new MetadataRepository(connector, iamDBMetadataTable.ROLE);
            featRepository = new FeatureRepository(connector);
            featMetaRepository = new MetadataRepository(connector, iamDBMetadataTable.FEAT);
            appRepository = new ApplicationRepository(connector);
            appMetaRepository = new MetadataRepository(connector, iamDBMetadataTable.APP);
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "setUp", e);
        }
    }

    @AfterEach
    void tearDown() {
        connector.close();
    }

    // Create Test Data
    @Order(1)
    @Test
    public void CreateTestData() {
        try {
            // Application 1: IAM
            appRepository.insert(AdvancedAppDataTestConstant.APP1_NAME, AdvancedAppDataTestConstant.APP1_DESCRIPTION);
            ApplicationDTO app1 = appRepository.getByName(AdvancedAppDataTestConstant.APP1_NAME);
            //      Metadata 1:
            appMetaRepository.insert(app1.getId(), AdvancedAppDataTestConstant.META1_NAME, AdvancedAppDataTestConstant.META1_VALUE);
            //      Metadata 2:
            appMetaRepository.insert(app1.getId(), AdvancedAppDataTestConstant.META2_NAME, AdvancedAppDataTestConstant.META2_VALUE);
            //      Metadata 3:
            appMetaRepository.insert(app1.getId(), AdvancedAppDataTestConstant.META3_NAME, AdvancedAppDataTestConstant.META3_VALUE);
            //      Metadata 4:
            appMetaRepository.insert(app1.getId(), AdvancedAppDataTestConstant.META4_NAME, AdvancedAppDataTestConstant.META4_VALUE);
            //      Feature 1: USER
            featRepository.insert(app1.getId(), AdvancedAppDataTestConstant.FEAT1_NAME,  AdvancedAppDataTestConstant.FEAT1_DESCRIPTION);
            FeatureDTO feat1 = featRepository.getByName(app1.getId(), AdvancedAppDataTestConstant.FEAT1_NAME);
            //          Role 1: STANDARD
            roleRepository.insert(feat1.getId(), AdvancedAppDataTestConstant.ROLE1_NAME, AdvancedAppDataTestConstant.ROLE1_DESCRIPTION);
            RoleDTO role1 = roleRepository.getByName(feat1.getId(), AdvancedAppDataTestConstant.ROLE1_NAME);
            //              Permission 1: CREATE
            pRepository.insert(AdvancedAppDataTestConstant.PERM1_NAME, AdvancedAppDataTestConstant.PERM1_DESCRIPTION);
            PermissionDTO perm1 = pRepository.getByName(AdvancedAppDataTestConstant.PERM1_NAME);
            rpRepository.insert(role1.getId(), perm1.getId());
            //                  Metadata 5:
            permMetaRepository.insert(perm1.getId(), AdvancedAppDataTestConstant.META5_NAME, AdvancedAppDataTestConstant.META5_VALUE);
            //              Permission 2: READ
            pRepository.insert(AdvancedAppDataTestConstant.PERM2_NAME, AdvancedAppDataTestConstant.PERM2_DESCRIPTION);
            PermissionDTO perm2 = pRepository.getByName(AdvancedAppDataTestConstant.PERM2_NAME);
            rpRepository.insert(role1.getId(), perm2.getId());
            //                  Metadata 6:
            permMetaRepository.insert(perm2.getId(), AdvancedAppDataTestConstant.META6_NAME, AdvancedAppDataTestConstant.META6_VALUE);
            //              Permission 3: UPDATE
            pRepository.insert(AdvancedAppDataTestConstant.PERM3_NAME, AdvancedAppDataTestConstant.PERM3_DESCRIPTION);
            PermissionDTO perm3 = pRepository.getByName(AdvancedAppDataTestConstant.PERM3_NAME);
            rpRepository.insert(role1.getId(), perm3.getId());
            //                  Metadata 7:
            permMetaRepository.insert(perm3.getId(), AdvancedAppDataTestConstant.META7_NAME, AdvancedAppDataTestConstant.META7_VALUE);
            //          Role 2: GUEST
            roleRepository.insert(feat1.getId(), AdvancedAppDataTestConstant.ROLE2_NAME, AdvancedAppDataTestConstant.ROLE2_DESCRIPTION);
            RoleDTO role2 = roleRepository.getByName(feat1.getId(), AdvancedAppDataTestConstant.ROLE2_NAME);
            //              Permission 1: READ
            rpRepository.insert(role2.getId(), perm2.getId());
            //      Feature 2: ADMIN
            featRepository.insert(app1.getId(), AdvancedAppDataTestConstant.FEAT2_NAME,  AdvancedAppDataTestConstant.FEAT2_DESCRIPTION);
            FeatureDTO feat2 = featRepository.getByName(app1.getId(), AdvancedAppDataTestConstant.FEAT2_NAME);
            //          Role 3: SYSTEM
            roleRepository.insert(feat2.getId(), AdvancedAppDataTestConstant.ROLE3_NAME, AdvancedAppDataTestConstant.ROLE3_DESCRIPTION);
            RoleDTO role3 = roleRepository.getByName(feat2.getId(), AdvancedAppDataTestConstant.ROLE3_NAME);
            //              Permission 1: CREATE
            rpRepository.insert(role3.getId(), perm1.getId());
            //              Permission 2: READ
            rpRepository.insert(role3.getId(), perm2.getId());
            //              Permission 3: UPDATE
            rpRepository.insert(role3.getId(), perm3.getId());
            //              Permission 4: ARCHIVE
            pRepository.insert(AdvancedAppDataTestConstant.PERM4_NAME, AdvancedAppDataTestConstant.PERM4_DESCRIPTION);
            PermissionDTO perm4 = pRepository.getByName(AdvancedAppDataTestConstant.PERM4_NAME);
            rpRepository.insert(role3.getId(), perm4.getId());
            //                  Metadata 8:
            permMetaRepository.insert(perm4.getId(), AdvancedAppDataTestConstant.META8_NAME, AdvancedAppDataTestConstant.META8_VALUE);
            //              Permission 5: DELETE
            pRepository.insert(AdvancedAppDataTestConstant.PERM5_NAME, AdvancedAppDataTestConstant.PERM5_DESCRIPTION);
            PermissionDTO perm5 = pRepository.getByName(AdvancedAppDataTestConstant.PERM5_NAME);
            rpRepository.insert(role3.getId(), perm5.getId());
            //                  Metadata 9:
            permMetaRepository.insert(perm5.getId(), AdvancedAppDataTestConstant.META9_NAME, AdvancedAppDataTestConstant.META9_VALUE);
            // Application 2: SAMPLE APP
            appRepository.insert(AdvancedAppDataTestConstant.APP2_NAME, AdvancedAppDataTestConstant.APP2_DESCRIPTION);
            ApplicationDTO app2 = appRepository.getByName(AdvancedAppDataTestConstant.APP2_NAME);
            //          Metadata 10:
            appMetaRepository.insert(app2.getId(), AdvancedAppDataTestConstant.META10_NAME, AdvancedAppDataTestConstant.META10_VALUE);
            //      Feature 3: SAMPLE FEATURE
            featRepository.insert(app2.getId(), AdvancedAppDataTestConstant.FEAT3_NAME,  AdvancedAppDataTestConstant.FEAT3_DESCRIPTION);
            FeatureDTO feat3 = featRepository.getByName(app2.getId(), AdvancedAppDataTestConstant.FEAT3_NAME);
            //          Metadata 11:
            featMetaRepository.insert(feat3.getId(), AdvancedAppDataTestConstant.META11_NAME, AdvancedAppDataTestConstant.META11_VALUE);
            //          Role 4: SAMPLE STANDARD ROLE
            roleRepository.insert(feat3.getId(), AdvancedAppDataTestConstant.ROLE4_NAME, AdvancedAppDataTestConstant.ROLE4_DESCRIPTION);
            RoleDTO role4 = roleRepository.getByName(feat3.getId(), AdvancedAppDataTestConstant.ROLE4_NAME);
            //              Metadata 12:
            roleMetaRepository.insert(role4.getId(),AdvancedAppDataTestConstant.META12_NAME, AdvancedAppDataTestConstant.META12_VALUE);
            //              Permission 1: CREATE
            rpRepository.insert(role4.getId(), perm1.getId());
            //              Permission 2: READ
            rpRepository.insert(role4.getId(), perm2.getId());
            //              Permission 3: UPDATE
            rpRepository.insert(role4.getId(), perm3.getId());
            //          Role 5: SAMPLE POWER USER ROLE
            roleRepository.insert(feat3.getId(), AdvancedAppDataTestConstant.ROLE5_NAME, AdvancedAppDataTestConstant.ROLE5_DESCRIPTION);
            RoleDTO role5 = roleRepository.getByName(feat3.getId(), AdvancedAppDataTestConstant.ROLE5_NAME);
            //              Metadata 13:
            roleMetaRepository.insert(role5.getId(),AdvancedAppDataTestConstant.META13_NAME, AdvancedAppDataTestConstant.META13_VALUE);
            //              Permission 1: CREATE
            rpRepository.insert(role5.getId(), perm1.getId());
            //              Permission 2: READ
            rpRepository.insert(role5.getId(), perm2.getId());
            //              Permission 3: UPDATE
            rpRepository.insert(role5.getId(), perm3.getId());
            //              Permission 4: ARCHIVE
            rpRepository.insert(role5.getId(), perm4.getId());
            //          Role 6: SAMPLE ADMIN ROLE
            roleRepository.insert(feat3.getId(), AdvancedAppDataTestConstant.ROLE6_NAME, AdvancedAppDataTestConstant.ROLE6_DESCRIPTION);
            RoleDTO role6 = roleRepository.getByName(feat3.getId(), AdvancedAppDataTestConstant.ROLE6_NAME);
            //              Metadata 14:
            roleMetaRepository.insert(role6.getId(),AdvancedAppDataTestConstant.META14_NAME, AdvancedAppDataTestConstant.META14_VALUE);
            //              Permission 1: CREATE
            rpRepository.insert(role6.getId(), perm1.getId());
            //              Permission 2: READ
            rpRepository.insert(role6.getId(), perm2.getId());
            //              Permission 3: UPDATE
            rpRepository.insert(role6.getId(), perm3.getId());
            //              Permission 4: ARCHIVE
            rpRepository.insert(role6.getId(), perm4.getId());
            //              Permission 4: DELETE
            rpRepository.insert(role6.getId(), perm5.getId());
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "CreateTestData", e);
        }
    }

    // Create Test Data
    @Order(2)
    @Test
    public void DeleteTestData() {
        try {
            // Application 1: IAM - On Delete Cascade take care of deleting dependencies
            ApplicationDTO app1 = appRepository.getByName(AdvancedAppDataTestConstant.APP1_NAME);
            appRepository.delete(app1.getId());
            //              Permission 1: CREATE - On Delete Cascade take care of deleting dependencies
            PermissionDTO perm1 = pRepository.getByName(AdvancedAppDataTestConstant.PERM1_NAME);
            pRepository.delete(perm1.getId());
            //              Permission 2: READ - On Delete Cascade take care of deleting dependencies
            PermissionDTO perm2 = pRepository.getByName(AdvancedAppDataTestConstant.PERM2_NAME);
            pRepository.delete(perm2.getId());
            //              Permission 3: UPDATE - On Delete Cascade take care of deleting dependencies
            PermissionDTO perm3 = pRepository.getByName(AdvancedAppDataTestConstant.PERM3_NAME);
            pRepository.delete(perm3.getId());
            //              Permission 4: ARCHIVE - On Delete Cascade take care of deleting dependencies
            PermissionDTO perm4 = pRepository.getByName(AdvancedAppDataTestConstant.PERM4_NAME);
            pRepository.delete(perm4.getId());
            //              Permission 5: DELETE - On Delete Cascade take care of deleting dependencies
            PermissionDTO perm5 = pRepository.getByName(AdvancedAppDataTestConstant.PERM5_NAME);
            pRepository.delete(perm5.getId());
            // Application 2: SAMPLE APP - On Delete Cascade take care of deleting dependencies
            ApplicationDTO app2 = appRepository.getByName(AdvancedAppDataTestConstant.APP2_NAME);
            appRepository.delete(app2.getId());
        }
        catch (SQLException e) {
            ExceptionHandlerTestUtil.Handle(logger, "DeleteTestData", e);
        }
    }
}
