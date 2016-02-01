package no.wsact.mikand.pg4100.assignment2.models;

import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Table;

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
 * This class is a slimmed down version of my testclass for PG3100 assignment 2.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class DatabaseHandlerTest {
    private DatabaseHandler dbHandler;

    /**
     * Initializes a DatabaseHandler for my tests.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            dbHandler = new DatabaseHandler();
        } catch (SQLException sqe) {
            assertTrue(Logger.getInstance().toString().contains("Communication link failure..."));
        }
    }

    /**
     * Kills the DatabaseHandler
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        try {
            if (!dbHandler.isClosed()) {
                dbHandler.close();
            }
        } catch (NullPointerException npe) {
            assertTrue(Logger.getInstance().toString().contains("Communication link failure..."));
        }

        dbHandler = null;
    }

    /**
     * Checks to see if a table is returned on correct table name.
     * <p>
     * This is collected from the production database because the handler is not meant to be able
     * to make its own tables or do any commands directly to the db other than this call.
     * <p>
     * In other words, the prod db must be set for this test to pass.
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetTable() throws Exception {
        try {
            Table t = dbHandler.getTable("bokliste");

            assertNotNull(t);
        } catch (SQLException sqe) {
            assertTrue(Logger.getInstance().toString().contains(
                    "Table doesnt exist."));
        } catch (NullPointerException npe) {
            assertTrue(Logger.getInstance().toString().contains("Communication link failure..."));
        }
    }

    /**
     * Closes and reestablishes DatabaseHandler.
     *
     * @throws Exception Exception
     */
    @Test
    public void testClose() throws Exception {
        try {
            dbHandler.close();

            assertTrue(dbHandler.isClosed());

            // re-establish connection so test can shutdown properly
            dbHandler = new DatabaseHandler();
        } catch (NullPointerException npe) {
            assertTrue(Logger.getInstance().toString().contains("Communication link failure..."));
        }
    }
}