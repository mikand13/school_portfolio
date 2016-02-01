package no.wsact.mikand.pg4100.assignment2.controllers.mocks;

import no.wsact.mikand.pg4100.assignment2.views.ClientGreeter;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.controllers
 * <p>
 * This class was needed to set up a server without an options menu, so i could do the testing i
 * needed.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
public class MockApplicationServer implements Runnable {
    private static final int SERVER_PORT = 10000;
    private static final int QUEUE_LENGTH = 10;

    /**
     * Handles Clients.
     */
    void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT, QUEUE_LENGTH)) {
            new Thread(new ClientGreeter(serverSocket.accept())).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fires up the testserver.
     */
    @Override
    public void run() {
        runServer();
    }
}
