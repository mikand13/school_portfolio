package no.wsact.mikand.pg4100.assignment2.views;

import no.wsact.mikand.pg4100.assignment2.controllers.mocks.MockApplicationServer;
import no.wsact.mikand.pg4100.assignment2.utils.server.Reader;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.controllers
 * <p>
 * This class does simple testing on the clientgreeter to check if it handles input and shuts
 * down the connection correctly when called for.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class ClientGreeterTest {
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
     * This tests wether the ClientGreeter actually shows you a menu and properly shuts down on a
     * negative reply and repeats on an illegal choice.
     *
     * @throws Exception Exception
     */
    @Test
    public void testDoGreet() throws Exception {
        Socket server = null;

        try (Socket serverConnection = new Socket(SERVER_ADRESS, SERVER_PORT)) {
            server = serverConnection;
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

                Writer.doClientWrite(writer, "someValueThatIsNotLegit");

                assertEquals("", Reader.doClientRead(reader));
                assertEquals("Ulovlig valg!", Reader.doClientRead(reader));

                while (reader.ready()) {
                    reader.readLine();
                }

                Writer.doClientWrite(writer, "x");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (server != null) {
            assertTrue(server.isClosed());
        } else {
            fail("Server not initialized");
        }
    }
}