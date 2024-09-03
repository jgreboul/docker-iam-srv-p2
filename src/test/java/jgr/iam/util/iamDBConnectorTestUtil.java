package jgr.iam.util;

// External Objects
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html

// Internal Objects
import jgr.iam.constant.iamDBConnectionInfo;
import jgr.iam.constant.dto.iamDBConnectTestConstant; // Main Constant

// iamDB Database Connection Util
public class iamDBConnectorTestUtil {

    // Logger
    private final static Logger logger = LogManager.getLogger(iamDBConnectorTestUtil.class.getCanonicalName());

    // Set Connection Parameters: URL
    public static String setIamDBConnectURL() {
        // Get Hostname
        String sHostName = System.getenv(iamDBConnectionInfo.ENV_SERVER_HOSTNAME);
        logger.debug("setIamDBConnectURL: "+ iamDBConnectionInfo.ENV_SERVER_HOSTNAME + ": [" + sHostName + "].");

        // Get and/or Set DB URL
        String sURL = System.getenv(iamDBConnectionInfo.ENV_DB_URL);
        logger.debug("setIamDBConnectURL: ENV_DB_URL: [" + sURL + "].");
        if(sURL == null) {
            logger.debug("setIamDBConnectURL: Set Default DB URL.");
            sURL = iamDBConnectTestConstant.IAM_DB_CONFIG_TEST_URL;
        }
        logger.debug("setIamDBConnectURL: sURL: [" + sURL + "].");

        // Check if Unit Test is supposed to run locally or in docker
        if(sHostName != null && sHostName.contains("docker")) {
            sURL = sURL.replace("localhost", "docker-iam-db");
            logger.debug("setIamDBConnectURL: replace localhost: [" + sURL + "].");
        }

        logger.debug("setIamDBConnectURL: DB URL: [" + sURL + "].");
        return sURL;
    }

    // Set Connection Parameters: URL
    public static String setIamDBConnectUser() {
        // Get and/or Set DB User
        String sUser = System.getenv(iamDBConnectionInfo.ENV_DB_USR);
        if(sUser == null) {
            logger.debug("setIamDBConnectUser: Set Default DB USR.");
            sUser = iamDBConnectTestConstant.IAM_DB_CONFIG_TEST_USR;
        }
        logger.debug("setIamDBConnectUser: DB USR [" + sUser + "].");
        return sUser;
    }

    // Set Connection Parameters: URL
    public static String setIamDBConnectPassword() {
        // Get and/or Set DB Password
        String sPassword = System.getenv(iamDBConnectionInfo.ENV_DB_PWD);
        if(sPassword == null) {
            logger.debug("setIamDBConnectPassword: Set Default DB PWD.");
            sPassword = iamDBConnectTestConstant.IAM_DB_CONFIG_TEST_PWD;
        }
        logger.debug("setIamDBConnectPassword: DB PWD [" + sPassword + "].");
        return sPassword;
    }

    // Connect to the iamDB database
    public static void iamDBConnect(iamDBConnectorUtil connector) throws SQLException {
        // Get and/or Set DB URL
        String sURL = setIamDBConnectURL();
        logger.debug("iamDBConnect: DB URL: [" + sURL + "].");
        // Get and/or Set DB User
        String sUser = System.getenv(iamDBConnectionInfo.ENV_DB_USR);
        if(sUser == null) {
            logger.debug("iamDBConnect: Set Default DB USR.");
            sUser = iamDBConnectTestConstant.IAM_DB_CONFIG_TEST_USR;
        }
        logger.debug("iamDBConnect: DB USR [" + sUser + "].");
        // Get and/or Set DB Password
        String sPassword = System.getenv(iamDBConnectionInfo.ENV_DB_PWD);
        if(sPassword == null) {
            logger.debug("iamDBConnect: Set Default DB PWD.");
            sPassword = iamDBConnectTestConstant.IAM_DB_CONFIG_TEST_PWD;
        }
        logger.debug("iamDBConnect: DB PWD [" + sPassword + "].");
        // Connect
        logger.debug("iamDBConnect: Connect to iamDB");
        connector.setDataSource(sURL, sUser, sPassword);
        connector.connect();
    }

    // Close connection to iamDB database
    public static void iamDBClose(iamDBConnectorUtil connector) {
        logger.debug("iamDBClose: close connector.");
        connector.close();
    }
}
