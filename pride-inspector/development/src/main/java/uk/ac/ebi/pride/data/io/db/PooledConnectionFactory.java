package uk.ac.ebi.pride.data.io.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;

import java.beans.PropertyVetoException;
import java.sql.*;


/**
 * PooledConnectionFactory is a singleton which manages a database connection pool
 */
public class PooledConnectionFactory {

    private static final Logger logger = LoggerFactory.getLogger(PooledConnectionFactory.class);

    private static PooledConnectionFactory instance = new PooledConnectionFactory();

    private ComboPooledDataSource connectionPool = null;

    private DesktopContext context;

    private PooledConnectionFactory() {

        //will throw exception if not properly configured
        context = Desktop.getInstance().getDesktopContext();

        try {
            // retrieve the active schema from the master schema
            String schema = getActiveSchema();
            logger.info("Using PRIDE public active schema: " + schema);

            if (schema != null) {
                // create a new connection pool
                setupConnectionPool(schema);
            } else {
                String msg = "Failed to get a valid active DB schema.";
                logger.error(msg);
                throw new IllegalStateException(msg);
            }

        } catch (PropertyVetoException e) {
            String msg = "Error while creating database pool";
            logger.error(msg, e);
            throw new IllegalStateException(msg + ": " + e.getMessage());
        }
    }

    private void setupConnectionPool(String schema) throws PropertyVetoException {
        if (connectionPool == null) {
            connectionPool = new ComboPooledDataSource();
        }
        // setting up the database connection to the master database
        connectionPool.setDriverClass(context.getProperty("pride.database.driver"));
        String databaseURL = context.getProperty("pride.database.protocol") + ':'
                + context.getProperty("pride.database.subprotocol") +
                ':' + context.getProperty("pride.database.alias") + "/" + schema;

        connectionPool.setJdbcUrl(databaseURL);
        connectionPool.setUser(context.getProperty("pride.database.user"));
        connectionPool.setPassword(context.getProperty("pride.database.password"));
    }

    private String getActiveSchema(){
        String schema = null;

        // get connection to the master database
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            Class.forName(context.getProperty("pride.database.driver"));
            String databaseURL = context.getProperty("pride.database.protocol") + ':'
                    + context.getProperty("pride.database.subprotocol") + ':'
                    + context.getProperty("pride.database.alias") + "/"
                    + context.getProperty("pride.database.master.schema");
            connection = DriverManager.getConnection(
                    databaseURL ,
                    context.getProperty("pride.database.user"),
                    context.getProperty("pride.database.password"));
            stmt = connection.prepareStatement("select schema_name from active_schema");
            resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                schema = resultSet.getString("schema_name");
            }
        } catch (SQLException e) {
            logger.error("Failed to get active DB schema.", e);
        } catch (ClassNotFoundException e) {
            logger.error("Fail to load database driver class: " + context.getProperty("pride.database.driver"));
        } finally {
            DBUtilities.releaseResources(connection, stmt, resultSet);
        }

        return schema;
    }

    /**
     * Get a pooled connection
     *
     * @return Connection   database connection
     * @throws SQLException SQL connection exception
     */
    public static synchronized Connection getConnection() throws SQLException {

        if (getInstance().getConnectionPool() != null) {
            return getInstance().getConnectionPool().getConnection();
        } else {
            String msg = "PooledConnectionFactory DataSource not initialized";
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Shut down the connection pool
     */
    public static void shutdownPool() {
        try {
            DataSources.destroy(getInstance().getConnectionPool());
        } catch (SQLException e) {
            logger.error("Error while shutting down the connection pool", e);
            throw new IllegalStateException("Could not shut down database pool: " + e.getMessage());
        }
    }

    private static PooledConnectionFactory getInstance() {
        return instance;
    }

    private ComboPooledDataSource getConnectionPool() {
        return connectionPool;
    }

}