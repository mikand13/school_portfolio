package no.wsact.mikand.pg4100.assignment2.controllers;

import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import no.wsact.mikand.pg4100.assignment2.views.ClientGreeter;
import no.wsact.mikand.pg4100.assignment2.views.ServerOptionController;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is the basis for the server. It makes an optionscontroller so the server admin can
 * check the log and waits for clients.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class ApplicationServer {
    private static final int SERVER_PORT = 8000;
    private static final int QUEUE_LENGTH = 10;
    private static final int SERVER_AWAIT_CLIENTS_BEFORE_SHUTDOWN = 60;
    private static final int CLIENT_TIMEOUT = SERVER_AWAIT_CLIENTS_BEFORE_SHUTDOWN - 10;
    private static final String LOG_FILE = "ApplicationServerLog.txt";

    private ServerSocket serverSocket;

    /**
     * Starts an application server.
     *
     * @param args java.util.Array
     */
    public static void main(String[] args) {
        new ApplicationServer().runServer();
    }

    /**
     * Initializes the optionsmenu and awaits clients.
     */
    private void runServer() {
        try {

            serverSocket = new ServerSocket(SERVER_PORT, QUEUE_LENGTH);
            ArrayList<Socket> clients = new ArrayList<>();

            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(new ServerOptionController(this));

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> shutdownServer(executorService, clients)));

            Logger.getInstance().add(new Log("SERVER OPERATIONAL"));

            while (!executorService.isTerminated()) {
                try {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    ClientGreeter clientGreeter = new ClientGreeter(client);

                    executorService.execute(clientGreeter);
                    Logger.getInstance().add(new Log(
                            "SERVER CONNECTED TO CLIENT (" +
                            client.getInetAddress() + ":" +
                            client.getPort() + ")\n" +
                            "SERVER LAUNCED CLIENTGREETER (" +
                            clientGreeter.getId() + ")"));
                } catch (SocketException se) {
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed connection...");
        }
    }

    /**
     * Shutdown server by waiting a constant value for clients, then dumps them and exits.
     * @param executorService ExecutorService
     * @param clients Socket
     */
    private void shutdownServer(ExecutorService executorService, ArrayList<Socket> clients) {
        System.out.println("\nShutting down server...");

        executorService.shutdown();
        try {
            Thread.sleep(1000);

            if (!executorService.isTerminated()) {
                waitForAndFinallyDumpAllConnectedClients(executorService, clients);
            } else {
                System.out.println("No active clients...");
            }

            Logger.getInstance().add(new Log("SERVER SUCCESSFULLY SHUTDOWN"));

            storeLogToDisc();

            System.out.println("\nExiting...");
        } catch (InterruptedException e) {
            System.out.println("Server exited unexpectedly...");
        } catch (IOException e) {
            System.out.println("Unable to dump client...");
        }
    }

    /**
     * Waits a predetermined time for active clients and then dumps them forcibly.
     *
     * @param executorService ExecutorService
     * @param clients ArrayList of clients.
     * @throws java.lang.InterruptedException InterruptedException
     * @throws java.io.IOException IOException
     */
    private void waitForAndFinallyDumpAllConnectedClients(
            ExecutorService executorService, ArrayList<Socket> clients)
            throws InterruptedException, IOException {

        System.out.println("Waiting for active clients (" + CLIENT_TIMEOUT + " seconds)");

        executorService.awaitTermination(SERVER_AWAIT_CLIENTS_BEFORE_SHUTDOWN, TimeUnit.SECONDS);

        if (!executorService.isTerminated()) {
            informAllConnectedClientsOfServerShutdown(clients);
        }

        Thread.sleep(100);

        if (!executorService.isTerminated()) {
            System.out.println("Clients still not disconnected, dumping clients...");

            executorService.shutdownNow();
            executorService.awaitTermination(10000, TimeUnit.SECONDS);
        }

        System.out.println("Clients have been dumped...");
    }

    /**
     * Sends a closure message to all clients and closes the socket.
     *
     * @param clients ArrayList of Sockets
     * @throws IOException IOException
     */
    private void informAllConnectedClientsOfServerShutdown(ArrayList<Socket> clients)
            throws IOException {
        for (Socket c : clients) {
            if (!c.isClosed()) {
                try (BufferedWriter writer
                             = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()))) {
                    System.out.println("Sending notification of shutdown -> "
                            + c.getInetAddress() + ":" + c.getPort());

                    Writer.doClientWrite(writer, "Exit");

                    c.close();
                }
            }
        }
    }

    /**
     * Outputs log to file.
     */
    private void storeLogToDisc() {
        File f = new File(LOG_FILE);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(Logger.getInstance().toString());
            bw.flush();

            System.out.println("Log has been stored on disk... (" + LOG_FILE + ")");
        } catch (FileNotFoundException e) {
            System.out.println("Could not locate " + LOG_FILE + " for output on filesystem...");
        } catch (IOException e) {
            System.out.println("Could not write to file... (" + LOG_FILE + ")");
        }
    }

    /**
     * Interrupts and kills server.
     */
    public void killServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Unable to shutdown server...");
        }
    }
}