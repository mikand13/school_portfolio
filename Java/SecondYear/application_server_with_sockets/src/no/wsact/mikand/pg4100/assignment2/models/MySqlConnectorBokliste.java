package no.wsact.mikand.pg4100.assignment2.models;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This is a class i devised for an assignment in PG3100, its been modified slightly.
 * <p>
 * Basic connectionclass for MySQL.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
class MySqlConnectorBokliste {
    private static final String hostname = "localhost";
    private static final String schema = "pg4100innlevering2";
    private static final String username = "root";
    private static final String password = ""; // Set to your dev environment
    private final MysqlDataSource ds = new MysqlDataSource();
    private Connection conn;

    /**
     * Connects to the db.
     *
     * @throws SQLException java.sql.SQLException
     */
    public MySqlConnectorBokliste() throws SQLException {
        ds.setDatabaseName(schema);
        ds.setServerName(hostname);
        ds.setUser(username);
        ds.setPassword(password);

        try {
            getConnection().createStatement().execute("USE " + schema + ";");
        } catch (SQLException se) {
            Logger.getInstance().add(new Log("Database unavailible..."));

            throw se;
        }
    }

    /**
     * Gives connection and creates one if closed.
     *
     * @return Connection
     * @throws SQLException Exception
     */
    public Connection getConnection() throws SQLException {
        try {
            if (conn == null) {
                conn = ds.getConnection();
            }
        } catch (NullPointerException npe) {
            Logger.getInstance().add(new Log("Connection not possible..."));

            return null;
        } catch (SQLException sqe) {
            Logger.getInstance().add(new Log("Communication link failure..."));

            throw sqe;
        }

        return conn;
    }
}
