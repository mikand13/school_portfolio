package no.wsact.mikand.pg4100.assignment2.views;

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
 * Package: no.wsact.mikand.pg4100.assignment2.controllers
 * <p>
 * This class does simple testing to see if the quizer is accessible and operational.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class QuizerTest {
    private static final String SERVER_ADRESS = "127.0.0.1";
    private static final int SERVER_PORT = 10000;

    private Thread serverThread;
    private Socket serverConnection;

    private boolean dbNotAvailible = false;

    /**
     * Initializes a Testingserver.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        MockApplicationServer mockApplicationServer = new MockApplicationServer();
        serverThread = new Thread(mockApplicationServer);
        serverThread.start();

        serverConnection = new Socket(SERVER_ADRESS, SERVER_PORT);
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

        serverConnection.close();
        serverConnection = null;
    }

    /**
     * This is an ugly test that slowly and steadly works its way through the server to test the
     * quizer. It attempts one question and then quits.
     *
     * @throws Exception Exception
     */
    @Test
    public void testRun() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                serverConnection.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                     serverConnection.getOutputStream()))) {
            assertFalse(serverConnection.isClosed());

            checkPassingThroughMainMenu(writer, reader);
            checkQuizLoop(writer, reader);
            checkForCorrectApplicationExit(writer, reader);
        }
    }

    /**
     * Helper method for runtest.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @throws IOException IOException
     */
    private void checkPassingThroughMainMenu(BufferedWriter writer, BufferedReader reader)
            throws IOException {
        assertEquals("", Reader.doClientRead(reader));
        assertEquals("Hvilken applikasjon vil du starte?", Reader.doClientRead(reader));

        do {
            Reader.doClientRead(reader);
        } while (reader.ready());

        Writer.doClientWrite(writer, "1");

        assertEquals("", Reader.doClientRead(reader));
        assertEquals("Vil du delta i forfatter-QUIZ? (ja/nei) ", Reader.doClientRead(reader));

        Writer.doClientWrite(writer, "j");
    }

    /**
     * Helper method for runtest.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @throws IOException IOException
     */
    private void checkQuizLoop(BufferedWriter writer, BufferedReader reader) throws IOException {
        assertEquals("", Reader.doClientRead(reader));
        String quizInitialized = Reader.doClientRead(reader);

        if (!quizInitialized.equals("") && !quizInitialized.contains("Database")) {
            assertTrue(quizInitialized.contains("Hvem har skrevet"));

            Writer.doClientWrite(writer, "someValueThatDoesntMakeSense");

            assertEquals("Feil!", Reader.doClientRead(reader));

            do {
                Reader.doClientRead(reader);
            } while (reader.ready());

            Writer.doClientWrite(writer, "nei");

            assertEquals("", Reader.doClientRead(reader));
            assertEquals("Takk for at du deltok!", Reader.doClientRead(reader));
        } else {
            assertEquals("", Reader.doClientRead(reader));
            assertEquals("", Reader.doClientRead(reader));
            assertEquals("Vil du quize igjen? (j/n) ", Reader.doClientRead(reader));
            Writer.doClientWrite(writer, "nei");

            dbNotAvailible = true;
        }
    }

    /**
     * Helper method for runtest.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @throws IOException IOException
     */
    private void checkForCorrectApplicationExit(BufferedWriter writer, BufferedReader reader)
            throws IOException {
        if (!dbNotAvailible) {
            do {
                Reader.doClientRead(reader);
            } while (reader.ready());

            assertEquals("", Reader.doClientRead(reader));
            assertEquals("", Reader.doClientRead(reader));
            assertEquals("Vil du quize igjen? (j/n) ", Reader.doClientRead(reader));

            Writer.doClientWrite(writer, "nei");
        }

        assertEquals("", Reader.doClientRead(reader));
        assertEquals("Hvilken applikasjon vil du starte?", Reader.doClientRead(reader));

        do {
            Reader.doClientRead(reader);
        } while (reader.ready());

        Writer.doClientWrite(writer, "x");
    }
}