package no.wsact.mikand.pg4100.assignment2.controllers;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class resembles a possible client class.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
class ApplicationClient {
    private static final String SERVER_ADRESS = "127.0.0.1";
    private static final int SERVER_PORT = 8000;

    private boolean active = true;
    private Thread writerThread;

    /**
     * This main method is a simple representation of how a client main method might look.
     *
     * @param args java.utils.Array
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            new ApplicationClient().connectToServer(SERVER_ADRESS, SERVER_PORT);
        } else {
            new ApplicationClient().connectToServer(args[0], Integer.parseInt(args[1]));
        }
    }

    /**
     * Starts the applicationclient.
     *
     * @param SERVER_ADRESS java.lang.String
     * @param SERVER_PORT Integer
     */
    private void connectToServer(String SERVER_ADRESS, int SERVER_PORT) {
        try (Socket serverConnection = new Socket(SERVER_ADRESS, SERVER_PORT)) {
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    serverConnection.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(
                    serverConnection.getInputStream()));
                 Scanner scanner = new Scanner(System.in)) {

                Thread interactionThread
                        = new Thread(() -> interactWithServer(writer, reader, scanner));
                interactionThread.start();
                interactionThread.join();

                System.out.println("\n\nServeren har lukket tilkoblingen.");

                if (writerThread.isAlive()) {
                    System.out.println("Tast inn en vilkårlig knapp for å avslutte...");
                }
            } catch (InterruptedException e) {
                System.out.println("Application aborted unexpectedly...");
            }
        } catch (ConnectException ce) {
            System.out.println("No connection could be made to '" + SERVER_ADRESS + ":" +
                    SERVER_PORT + "' Exiting...");
        } catch (IOException e) {
            System.out.println("Critical IO error with server. Exiting...");
        }
    }

    /**
     * Reads availible info from server. Initiates user response.
     *  @param writer BufferedWriter
     * @param reader BufferedReader
     * @param scanner java.util.Scanner
     */
    private void interactWithServer(BufferedWriter writer, BufferedReader reader, Scanner scanner) {
        try {
            while(active) {
                int counter = 0;

                while (reader.ready()) {
                    if (!(active = handleInformationFromServer(reader, counter))) {
                        break;
                    }

                    checkForUserInput(writer, reader, scanner);

                    counter++;
                }
            }
        } catch (IOException ioe) {
            System.out.println("Server socket closed.");
        }
    }

    /**
     * Verifies for serverexit and prints information from server
     *
     * @param reader BufferedReader
     * @param counter Integer
     * @return boolean
     * @throws IOException IOException
     */
    private boolean handleInformationFromServer(BufferedReader reader, int counter)
            throws IOException {
        if (counter > 0) {
            System.out.println();
        }

        String inputLine = reader.readLine();

        if (inputLine != null) {
            if (!inputLine.equals("Exit")) {
                System.out.print(inputLine);
            } else {
                return active = false;
            }
        }

        return true;
    }

    /**
     * Check if server requests user input.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @param scanner Scanner
     * @throws IOException IOException
     */
    private void checkForUserInput(BufferedWriter writer, BufferedReader reader, Scanner scanner)
            throws IOException {
        // if user is already prompted, ignore current message
        if (writerThread == null || !writerThread.isAlive() && !reader.ready()) {
            writerThread = new Thread(() -> handleUserResponseToServer(writer, scanner));
            writerThread.start();
        }
    }

    /**
     * Prompts the user for a reponse. Ignores specific keywords from server.
     *
     * @param writer BufferedWriter
     * @param scanner java.util.Scanner
     */
    private void handleUserResponseToServer(BufferedWriter writer, Scanner scanner) {
        try {
            String response = scanner.nextLine();

            writer.write(response + '\n', 0, response.length() + 1);
            writer.flush();
        } catch (IOException e) {
            active = false;
        } catch (NoSuchElementException nsee) {
            System.out.println("\n\nExiting...");
        }
    }
}
