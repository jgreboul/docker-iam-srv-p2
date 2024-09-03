package jgr.iam.manager;

// External Objects
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLException;

// Internal Objects
import jgr.iam.util.iamDBConnectorUtil;

// Base Manager
@Getter
@Setter
public class BaseManager {
    protected Logger logger;
    protected iamDBConnectorUtil connector; // iamDB Connector

    public BaseManager() {
        logger = LogManager.getLogger(BaseManager.class.getCanonicalName());
        connector = new iamDBConnectorUtil();
    }

    // Connect to iamDB
    protected boolean connect() {
        boolean result = true;
        try {
            // Set Data source
            connector.setDataSource(connector.getConnectionURL(),
                                    connector.getConnectionUser(),
                                    connector.getConnectionPassword());
            // Connect
            connector.connect();
        }
        catch (SQLException e) {
            logger.error("connect: Failed to connect to the database.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            result = false;
        }
        return result;
    }

    // Disconnect to iamDB
    protected void disconnect() {
        connector.close();
    }
}
