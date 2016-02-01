package no.wsact.mikand.pg4100.assignment2.views;

import no.wsact.mikand.pg4100.assignment2.models.DatabaseHandler;
import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Row;
import no.wsact.mikand.pg4100.assignment2.models.mySqlConnectorModels.Table;
import no.wsact.mikand.pg4100.assignment2.utils.server.Reader;
import no.wsact.mikand.pg4100.assignment2.utils.server.Writer;
import no.wsact.mikand.pg4100.assignment2.views.utils.ApplicationUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class runs the Quizer application and communicates directly with the client.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
class Quizer extends Application {
    @SuppressWarnings("FieldCanBeLocal")
    private final String QUIZ_TABLE = "bokliste";
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private Table quizTable;

    private int correct = 0, wrong = 0;

    /**
     * This constructor sets the writer and reader. They are not closed in this class, that is
     * handled by the ClientGreeter class.
     *
     * @param writer BufferedWriter
     * @param reader BufferedReader
     */
    public Quizer(BufferedWriter writer, BufferedReader reader) {
        super("Quizer");

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

        try (DatabaseHandler dbHandler = new DatabaseHandler()) {
            quizTable = dbHandler.getTable(QUIZ_TABLE);

            if (quizTable == null) {
                try {
                    Writer.doClientWrite(this, writer,
                            "Kunne ikke hente informasjon fra databasen. Avslutter...");
                } catch (IOException ioe) {
                    ApplicationUtils.addApplicationLog(this, "CANNOT WRITE TO CLIENT");
                }
            } else {
                doQuiz();
            }
        } catch (SQLException se) {
            handleSQLException(writer, se);
        }

        ApplicationUtils.addApplicationLog(this, "EXITING");
    }

    /**
     * Handle actions on SQLException.
     *
     * @param writer BufferedWriter
     * @param se SQLException
     */
    private void handleSQLException(BufferedWriter writer, SQLException se) {
        if (se.getErrorCode() == 1045) {
            ApplicationUtils.addApplicationLog(this, "Authorization with db failed, exiting...");
        } else if (se.getErrorCode() == 0) {
            ApplicationUtils.addApplicationLog(this, "No db availible, exiting...");
        } else {
            ApplicationUtils.addApplicationLog(
                    this, "Table not accessible or doesnt exist, exiting...");
        }

        try {
            Writer.doClientWrite(this, writer, "\nDatabasen er utilgjengelig.");
        } catch (IOException e) {
            ApplicationUtils.addApplicationLog(this, "Critical IOException, user kicked...");
        }
    }

    /**
     * Shuffles the quizquestions and starts a quizround.
     */
    private void doQuiz() {
        List<Row> quizQuestions = quizTable.getRows();
        Collections.shuffle(quizQuestions, new Random(System.nanoTime()));

        try {
            handleQuizRound(quizQuestions);

            sendResultsToClient();
        } catch (IOException e) {
            ApplicationUtils.addApplicationLog(this, "Critical IOException, user kicked...");
        }
    }

    /**
     * Loops through all questions in the quiz and calls a helper for handling each question.
     * Client can break the quiz after every question.
     *
     * @param quizQuestions java.utils.List
     * @throws IOException IOException
     */
    private void handleQuizRound(List<Row> quizQuestions) throws IOException {
        for (int i = 0; i < quizQuestions.size(); i++) {
            Object[] current = quizQuestions.get(i).getRow();

            String response = handleQuestion(i, current, quizQuestions);

            if (i == quizQuestions.size() - 1) {
                break;
            } else {
                response += "Vil du fortsette? (j/n) ";

                Writer.doClientWrite(this, writer, response);

                ApplicationUtils.addApplicationLog(this, "WAITING FOR ANSWER");

                String answer = Reader.doClientRead(this, reader);

                if (answer == null || (!answer.toLowerCase().equals("ja") &&
                        !answer.toLowerCase().equals("j"))) {
                    break;
                }
            }
        }
    }

    /**
     * Handles every individual question.
     *
     * @param i         Integer
     * @param current   java.utils.Array
     * @param questions java.utils.List
     * @return java.lang.String
     * @throws IOException IOException
     */
    private String handleQuestion(int i, Object[] current, List<Row> questions) throws IOException {
        ApplicationUtils.addApplicationLog(this, "SENDING QUIZ QUESTION");

        Writer.doClientWrite(this, writer, "\n" + (i + 1) + "/" + questions.size() +
                ": Hvem har skrevet " + current[1] + " ");

        String answer = Reader.doClientRead(this, reader);
        String response = "";

        if (answer != null) {
            if (answer.equalsIgnoreCase(current[0].toString())) {
                response = "Riktig!\n";

                correct++;
            } else {
                response = "Feil!\n";

                wrong++;
            }
        }

        return response;
    }

    /**
     * Sends the result of a quizround to the client.
     *
     * @throws IOException IOException
     */
    private void sendResultsToClient() throws IOException {
        String response = Writer.doClientWrite(this, writer,
                "\nTakk for at du deltok!\n" +
                        "Riktige: " + correct + '\n' +
                        "Feil: " + wrong);

        ApplicationUtils.addApplicationLog(this, "SENT END MESSAGE: " + response);
    }
}
