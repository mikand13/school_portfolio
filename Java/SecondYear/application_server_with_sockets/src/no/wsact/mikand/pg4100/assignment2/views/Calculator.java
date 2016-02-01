package no.wsact.mikand.pg4100.assignment2.views;

import no.wsact.mikand.pg4100.assignment2.utils.server.Reader;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import no.wsact.mikand.pg4100.assignment2.views.utils.ApplicationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This class
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
class Calculator extends Application {
    @SuppressWarnings("FieldCanBeLocal")
    private final BufferedReader reader;
    private final BufferedWriter writer;

    private double finalCalculation;
    private boolean success = true;

    /**
     * This constructor sets the writer and reader. They are not closed in this class, that is
     * handled by the ClientGreeter class.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     */
    public Calculator(BufferedWriter writer, BufferedReader reader) {
        super("Kalkulator");

        this.writer = writer;
        this.reader = reader;
    }

    /**
     * Fires up the Runnable and gets the necessary quiz table from the db. Returns to the
     * ClientGreeter on any SQLException.
     */
    @Override
    public void run() {
        ApplicationUtils.addApplicationLog(this, "STARTED");

        doCalc();

        ApplicationUtils.addApplicationLog(this, "EXITING");
    }

    /**
     * Runs a round of calculation.
     */
    private void doCalc() {
        try {
            handleCalculationRound();

            if (success) {
                sendResultsToClient();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does a single calculation.
     *
     * @throws IOException IOException
     */
    private void handleCalculationRound() throws IOException {
        while (true) {
            String choice = showMenu();
            char operator = 0;

            if (choice != null) {
                if (choice.equalsIgnoreCase("1")) {
                    operator = '+';
                } else if (choice.equalsIgnoreCase("2")) {
                    operator = '-';
                } else if (choice.equalsIgnoreCase("3")) {
                    operator = '/';
                } else if (choice.equalsIgnoreCase("4")) {
                    operator = '*';
                } else if (choice.equalsIgnoreCase("x")) {
                    ApplicationUtils.addApplicationLog(this, "EXITING BECAUSE OF USER ABORT");
                    success = false;

                    break;
                }

                if (operator != 0) {
                    finalCalculation = fetchAndCalculateNumbers(operator, writer);

                    break;
                } else {
                    Writer.doClientWrite(this, writer, "\nUlovlig valg!");
                }
            } else {
                break;
            }
        }
    }

    /**
     * This method calls fetchNumber to gather numbers and returns the calculated result based on
     * the operator.
     *
     * @param operator char
     * @param writer BufferedWriter
     * @return double
     * @throws IOException IOException
     */
    private double fetchAndCalculateNumbers(
            char operator, BufferedWriter writer) throws IOException {
        double first = Double.MAX_VALUE;
        double second = Double.MAX_VALUE;

        try {
            while (second == Double.MAX_VALUE) {
                if (first == Double.MAX_VALUE) {
                    first = fetchNumber(operator, 1);
                } else {
                    second = fetchNumber(operator, 2);
                }
            }

            return calculateNumbers(operator, first, second);
        } catch (NumberFormatException nfe) {
            Writer.doClientWrite(this, writer, "\nMå være på desimalformat!");

            success = false;
        }

        return 0.0;
    }

    /**
     * Calculates result based on operator.
     *
     * @param operator char
     * @param first double
     * @param second double
     * @return double
     *
     * @throws IOException IOException
     */
    private double calculateNumbers(char operator, double first, double second) throws IOException {
        switch (operator) {
            case '+':
                return first + second;
            case '-':
                return first - second;
            case '/':
                if (second == 0.0) {
                    Writer.doClientWrite(this, writer, "\nKan ikke dele på null!");
                    success = false;

                    return 0.0;
                }

                return first / second;
            case '*':
                return first * second;
            default:
                return 0.0;
        }
    }

    /**
     * Displays a menu of operators.
     *
     * @return java.lang.String
     * @throws IOException IOException
     */
    private String showMenu() throws IOException {
        String list = "\n\nHvilken operator vil du bruke?\n" +
                "1: +\n" +
                "2: -\n" +
                "3: /\n" +
                "4: *\n\n" +
                "x: Avbryt\n\n";

        Writer.doClientWrite(this, writer, list);
        ApplicationUtils.addApplicationLog(this, "WAITING FOR ANSWER");

        return Reader.doClientRead(this, reader);
    }

    /**
     * Handles every individual fetch.
     *
     * @param operator char
     * @param numberToFetch Integer
     * @return double
     * @throws IOException IOException
     */
    private double fetchNumber(char operator, int numberToFetch) throws IOException {
        ApplicationUtils.addApplicationLog(this, "SENDING FETCH REQUEST");

        switch (numberToFetch) {
            case 1:
                ApplicationUtils.addApplicationLog(this, "RECEIVED " + operator);
                Writer.doClientWrite(this, writer, "\nDu har valgt: " + operator + "\n" +
                        "\nSkriv inn første tall: ");
                break;
            case 2:
                Writer.doClientWrite(this, writer, "Skriv inn andre tall: ");
                break;
        }

        String answer = Reader.doClientRead(this, reader);

        if (answer != null) {
            return Double.parseDouble(answer);
        } else {
            return 0.0;
        }
    }

    /**
     * Sends the result of a round of calculation to the client.
     *
     * @throws IOException IOException
     */
    private void sendResultsToClient() throws IOException {
        String response = Writer.doClientWrite(this, writer,
                "\n" + String.format("%.2f", finalCalculation));

        ApplicationUtils.addApplicationLog(this, "SENT END MESSAGE: " + response);
    }
}
