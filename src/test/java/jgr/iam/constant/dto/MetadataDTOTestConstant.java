package jgr.iam.constant.dto;

// <Table>MetadataDTO Test Values
public class MetadataDTOTestConstant {
    // UserMetadata Single Tests
    public static final String METADATA_TEST_USER_TABLE = "UserMetadata";
    public static final String METADATA_TEST_USER_NAME = "UDATA";
    public static final String METADATA_TEST_USER_VALUE = "{\"city\": \"Anywhere\", \"state\": \"WA\", \"postalCode\": \"98123\", \"streetAddress\": \"123 Main Street\"}";
    public static final String METADATA_TEST_USER_VALUE_UPDATED = "{\"city\": \"Somewhere\", \"state\": \"WA\", \"postalCode\": \"98456\", \"streetAddress\": \"456 Main Avenue\"}";
    public static final String METADATA_TEST_USER_VALUE_INVALID = "{\"streetAddress\": \"789";
    // UserGroupMetadata Single Tests
    public static final String METADATA_TEST_UG_TABLE = "UserGroupMetadata";
    public static final String METADATA_TEST_UG_NAME = "UGDATA";
    public static final String METADATA_TEST_UG_VALUE = "{\"team\": \"Team ABC\", \"lead\": \"John Doe\"}";
    // ApplicationMetadata Single Tests
    public static final String METADATA_TEST_APP_TABLE = "ApplicationMetadata";
    public static final String METADATA_TEST_APP_NAME = "ADATA";
    public static final String METADATA_TEST_APP_VALUE = "{\"acronym\": \"IAM\" }";
    // FeatureMetadata Single Tests
    public static final String METADATA_TEST_FEAT_TABLE = "FeatureMetadata";
    public static final String METADATA_TEST_FEAT_NAME = "FDATA";
    public static final String METADATA_TEST_FEAT_VALUE = "{\"name\": \"Administration\"}";
    // RoleMetadata Single Tests
    public static final String METADATA_TEST_ROLE_TABLE = "RoleMetadata";
    public static final String METADATA_TEST_ROLE_NAME = "RDATA";
    public static final String METADATA_TEST_ROLE_VALUE = "{\"explanation\": \"Provides an explanation\"}";
    // PermissionMetadata Single Tests
    public static final String METADATA_TEST_PERM_TABLE = "PermissionMetadata";
    public static final String METADATA_TEST_PERM_NAME = "PDATA";
    public static final String METADATA_TEST_PERM_VALUE = "{\"encryption\": \"SHA256\"}";
    public static final String METADATA_TEST_PERM_VALUE_UPDATED = "{\"encryption\": \"SHA512\"}";
}
