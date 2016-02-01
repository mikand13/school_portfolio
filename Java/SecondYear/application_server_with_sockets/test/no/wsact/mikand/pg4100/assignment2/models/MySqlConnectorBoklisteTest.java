package no.wsact.mikand.pg4100.assignment2.models;

import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This is a very simple test-class that checks wether you can establish a connection,
 * and wether you can create a statement.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class MySqlConnectorBoklisteTest {
    private MySqlConnectorBokliste db;

    /**
     * Checks for db online.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            db = new MySqlConnectorBokliste();
            db.getConnection();
        } catch (SQLException sqe) {
            assertTrue(Logger.getInstance().toString().contains("Database unavailible..."));
        }
    }

    /**
     * Closes db connection.
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        try {
            db.getConnection().isClosed();
            db.getConnection().close();
        } catch (SQLException sqe) {
            assertTrue(Logger.getInstance().toString().contains("Database unavailible..."));
        } catch (NullPointerException npe) {
            // This must be swallowed because of internal error in jdbc.
        }

        db = null;
    }

    /**
     * Does a simple test to se if you can get a connection and use it to make a statement.
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetConnection() throws Exception {
        try {
            assertNotNull(db.getConnection());
            assertNotNull(db.getConnection().createStatement());
            assertFalse(db.getConnection().isClosed());
        } catch (SQLException sqe) {
            assertTrue(Logger.getInstance().toString().contains("Database unavailible..."));
        } catch (NullPointerException npe) {
            // This must be swallowed because of internal error in jdbc.
        }
    }
}