package no.wsact.mikand.pg4100.assignment2.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This class is a simple connection tester for the client application in offline mode.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class ApplicationClientTestOffline {
    private static final String SERVER_ADRESS = "127.0.0.1";
    private static final int SERVER_PORT = 10000;

    private ByteArrayOutputStream mockOutStream;
    private PrintStream systemOut, mockOut;

    /**
     * Sets up sysout reroute.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        mockOutStream = new ByteArrayOutputStream();
        mockOut = new PrintStream(mockOutStream);
        systemOut = System.out;
        System.setOut(mockOut);
    }

    /**
     * Reset output.
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        System.out.flush();
        mockOut = null;

        System.setOut(systemOut);
    }

    /**
     * Simple test to see if server responds correctly and application exits.
     *
     * @throws Exception Exception
     */
    @Test
    public void testMain() throws Exception {
        ApplicationClient.main(new String[] {SERVER_ADRESS, "" + SERVER_PORT});

        assertTrue(mockOutStream.toString().contains(
                "No connection could be made to"));
    }
}