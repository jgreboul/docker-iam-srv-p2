package jgr.iam.util;

// External Objects
import java.sql.Connection; // https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html
import java.sql.SQLException; // https://docs.oracle.com/javase/8/docs/api/java/sql/SQLException.html
import lombok.Getter; // https://projectlombok.org/features/GetterSetter
import lombok.Setter; // https://projectlombok.org/features/GetterSetter
import org.apache.logging.log4j.LogManager; // https://logging.apache.org/log4j/2.x/manual/api.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

// Internal Objects
import jgr.iam.constant.iamDBConnectionInfo; // iamDB Connection Information

// iamDB Connector
public class iamDBConnectorUtil {

    // Logger
    private final static Logger logger = LogManager.getLogger(iamDBConnectorUtil.class.getCanonicalName());

    // DataSource
    @Setter
    private DataSource dataSource;

    // Connection
    @Getter
    @Setter
    private Connection connection;

    // Connect and open session with the database
    public void setDataSource(String url, String username, String password) throws SQLException {
        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setURL(url);
        mysqlDS.setUser(username);
        mysqlDS.setPassword(password);
        dataSource = mysqlDS;
    }

    // Connect and open session with the database
    public void connect() throws SQLException {
        if(dataSource != null) {
            try {
                // Establish the connection
                logger.debug("connect: About to dataSource.getConnection().");
                connection = dataSource.getConnection();
                logger.debug("connect: Connected to the database.");
            } catch (SQLException e) {
                logger.error("connect: Failed to connect to the database.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
                throw new SQLException("connect: Failed to connect to the database.");
            }
        }
        else {
            throw new SQLException("connect: Null Datasource.");
        }
    }

    // Close database session
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.debug("close: Connection closed.");
            }
        }
        catch (SQLException e) {
            logger.error("close: Something went wrong.");
        }
    }

    // Is Connection Opened
    public boolean isOpened() {
        if (connection != null) {
            try {
                return !connection.isClosed();
            } catch (Exception e) {
                logger.error("isOpened: Exception.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
        }
        return false;
    }

    // Get Connection URL
    public String getConnectionURL()
    {
        String result;
        try {
            result = getEnvironmentVariable(iamDBConnectionInfo.ENV_DB_URL);
            if (result == null) {
                result = iamDBConnectionInfo.DEFAULT_DB_URL;
            }
        }
        catch(Exception e)
        {
            logger.error("getConnectionURL: Failed get Environment Variable.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            result = iamDBConnectionInfo.DEFAULT_DB_URL;
        }
        logger.debug("getConnectionURL: (" + result + ").");
        return result;
    }

    // Get Connection User
    public String getConnectionUser()
    {
        String result;
        try {
            result = getEnvironmentVariable(iamDBConnectionInfo.ENV_DB_USR);
            if (result == null) {
                result = iamDBConnectionInfo.DEFAULT_DB_USR;
            }
        }
        catch(Exception e)
        {
            logger.error("getConnectionURL: Failed get Environment Variable.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            result = iamDBConnectionInfo.DEFAULT_DB_USR;
        }
        logger.debug("getConnectionUser: (" + result + ").");
        return result;
    }

    // Get Connection Password
    public String getConnectionPassword()
    {
        String result;
        try {
            result = getEnvironmentVariable(iamDBConnectionInfo.ENV_DB_PWD);
            if (result == null) {
                result = iamDBConnectionInfo.DEFAULT_DB_PWD;
            }
        }
        catch(Exception e)
        {
            logger.error("getConnectionPassword: Failed get Environment Variable.\n\t" + e.getClass().getCanonicalName() + ": " + e.getMessage());
            result = iamDBConnectionInfo.DEFAULT_DB_PWD;
        }
        logger.debug("getConnectionPassword: (" + result + ").");
        return result;
    }

    // Get Environment Variable
    public String getEnvironmentVariable(String envVariable) {
        return System.getenv(envVariable);
    }
}
