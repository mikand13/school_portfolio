package no.wsact.mikand.pg4100.assignment2.views;

import no.wsact.mikand.pg4100.assignment2.utils.server.Reader;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import no.wsact.mikand.pg4100.assignment2.views.utils.ApplicationUtils;

import java.io.*;
import java.net.Socket;

/**
 * This class greets the client and asks if he wants to quiz. The reason i made an independent
 * greeter was to make the application as a whole more expandable. You could easily expand this
 * solution with some other application and route there instead of the quiz based on user
 * interaction.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class ClientGreeter extends Application implements Runnable {
    private final Socket serverConnection;

    /**
     * Defines the client connection.
     *
     * @param serverConnection java.net.Socket
     */
    public ClientGreeter(Socket serverConnection) {
        super("ClientGreeter");

        this.serverConnection = serverConnection;
    }

    /**
     * Fires up the Runnable and sends logging info to the Logger.
     */
    @Override
    public void run() {
        ApplicationUtils.addApplicationLog(this, "STARTED");

        doGreet();

        ApplicationUtils.addApplicationLog(this, "EXITING");
    }

    /**
     * Initializes in- and outputstreams and loops the interaction with the client.
     */
    private void doGreet() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                serverConnection.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                serverConnection.getInputStream()))) {

            ApplicationUtils.addApplicationLog(this, "ASKING FOR APPLICATION");

            boolean active = true;

            while (active) {
                String answer = showMenu(writer, reader);

                active = handleMenu(answer, writer, reader);
            }

            serverConnection.close();
        } catch (IOException e) {
            ApplicationUtils.addApplicationLog(this, "CLIENT EXITED UNEXPECTEDLY");
        }
    }

    /**
     * This handles the menuoptions.
     *
     * @param answer java.lang.String
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @return boolean
     * @throws IOException IOException
     */
    private boolean handleMenu(String answer, BufferedWriter writer, BufferedReader reader)
            throws IOException {
        if (answer != null) {
            if (answer.equalsIgnoreCase("1")) {
                doQuiz(writer, reader);
            } else if (answer.equalsIgnoreCase("2")) {
                doCalculator(writer, reader);
            } else if (answer.equalsIgnoreCase("x")) {
                Writer.doClientWrite(this, writer, "Exit");

                return false;
            } else {
                Writer.doClientWrite(this, writer, "\nUlovlig valg!");
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * This method shows a menu to a user and gives them the option of running any of the
     * applications on the server.
     *
     * @param writer BufferedWriter
     * @param reader BUfferedReader
     * @return java.lang.String
     * @throws IOException IOException
     */
    private String showMenu(BufferedWriter writer, BufferedReader reader) throws IOException {
        String list = "\nHvilken applikasjon vil du starte?\n" +
                "1: Quiz\n" +
                "2: Kalkulator\n\n" +
                "x: Avslutt\n\n";

        Writer.doClientWrite(this, writer, list);
        ApplicationUtils.addApplicationLog(this, "WAITING FOR ANSWER");

        return Reader.doClientRead(this, reader);
    }

    /**
     * This method controls the specific communication for starting the calculator.
     *
     * @param writer BufferedWriter
     * @param reader BUfferedReader
     * @throws IOException IOException
     */
    private void doCalculator(BufferedWriter writer, BufferedReader reader) throws IOException {
        boolean active = true;

        while (active) {
            startApplication(new Calculator(writer, reader));

            active = checkIfUserWantsToRunApplicationAgain("regne", writer, reader);
        }
    }

    /**
     * This method controls the specific communication for starting the quizer.
     *
     * @param writer BufferedWriter
     * @param reader BUfferedReader
     * @throws IOException IOException
     */
    private void doQuiz(BufferedWriter writer, BufferedReader reader) throws IOException {
        int timesquizing = 0;
        String response;

        ApplicationUtils.addApplicationLog(this, "ASKING FOR QUIZ PARTICIPATION");

        boolean active = true;

        while (active) {
            if (timesquizing == 0) {
                Writer.doClientWrite(this, writer, "\nVil du delta i forfatter-QUIZ? (ja/nei) ");
                ApplicationUtils.addApplicationLog(this, "WAITING FOR ANSWER");

                response = Reader.doClientRead(this, reader);
            } else {
                response = "j";
            }

            if (response != null) {
                if (response.toLowerCase().equals("ja") || response.toLowerCase().equals("j")) {
                    startApplication(new Quizer(writer, reader));

                    active = checkIfUserWantsToRunApplicationAgain("quize", writer, reader);
                } else {
                    ApplicationUtils.addApplicationLog(this, "EXITED BECAUSE OF NEGATIVE ANSWER");

                    return;
                }
            } else {
                active = false;
            }

            timesquizing++;
        }
    }

    /**
     * This method start a thread with an application. It will also interrupt the
     * application if itself gets interrupted, so there will be no loose threads.
     *
     * @param application Application
     */
    private void startApplication(Application application) {
        Thread applicationThread = new Thread(application);
        applicationThread.start();

        ApplicationUtils.addApplicationLog(this, "STARTED " + application.getName());

        try {
            applicationThread.join();
        } catch (InterruptedException e) {
            applicationThread.interrupt();
            ApplicationUtils.addApplicationLog(this, "EXITED BECAUSE OF INTERRUPTION");
        }
    }

    /**
     * Checks if user wants to run the same application again.
     *
     * @param applicationKey java.lang.String
     * @param writer BufferedWriter
     * @param reader BufferedReader
     * @return boolean
     * @throws IOException IOException
     */
    private boolean checkIfUserWantsToRunApplicationAgain(
            String applicationKey, BufferedWriter writer, BufferedReader reader)
            throws IOException {
        Writer.doClientWrite(this, writer, "\n\nVil du " + applicationKey + " igjen? (j/n) ");
        ApplicationUtils.addApplicationLog(this, "WAITING FOR ANSWER");

        String response = Reader.doClientRead(this, reader);

        return !(response == null || (!response.toLowerCase().equals("ja") &&
                !response.toLowerCase().equals("j")));
    }
}