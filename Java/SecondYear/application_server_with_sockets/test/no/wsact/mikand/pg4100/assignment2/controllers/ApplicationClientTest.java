package no.wsact.mikand.pg4100.assignment2.controllers;

import no.wsact.mikand.pg4100.assignment2.controllers.mocks.MockApplicationServer;
import no.wsact.mikand.pg4100.assignment2.utils.server.Reader;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This class is a simple connection tester for the client application.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class ApplicationClientTest {
    private static final String SERVER_ADRESS = "127.0.0.1";
    private static final int SERVER_PORT = 10000;

    private Thread serverThread;

    /**
     * Initializes a Testingserver.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        serverThread = new Thread(new MockApplicationServer());
        serverThread.start();
    }

    /**
     * Kills Testingserver.
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        if (serverThread.isAlive()) {
            serverThread.interrupt();
        }

        serverThread = null;
    }

    /**
     * Simple test to see if connection can be made, welcome greet is received and if closure is
     * possible.
     *
     * @throws Exception Exception
     */
    @Test
    public void testMain() throws Exception {
        try (Socket serverConnection = new Socket(SERVER_ADRESS, SERVER_PORT)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    serverConnection.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    serverConnection.getOutputStream()))) {
                assertFalse(serverConnection.isClosed());

                assertEquals("", Reader.doClientRead(reader));
                assertEquals("Hvilken applikasjon vil du starte?", Reader.doClientRead(reader));

                while (reader.ready()) {
                    reader.readLine();
                }

                Writer.doClientWrite(writer, "x");

                while (reader.ready()) {
                    reader.readLine();
                }

                serverConnection.close();
                assertTrue(serverConnection.isClosed());
            }
        }
    }
}