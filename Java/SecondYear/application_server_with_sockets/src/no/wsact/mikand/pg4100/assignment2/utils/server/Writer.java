package no.wsact.mikand.pg4100.assignment2.utils.server;

import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import no.wsact.mikand.pg4100.assignment2.views.Application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;

/**
 * This is a helper class for methods pertaining to writing to sockets.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class Writer {

    /**
     * Outputs a line from the supplied Application parameter to a client, and outputs to log as
     * well.
     *
     * @param application Application
     * @param writer BufferedWriter
     * @param output java.lang.String
     * @return java.lang.String
     * @throws IOException IOException
     */
    public static String doClientWrite(Application application, BufferedWriter writer, String
            output) throws
            IOException {
        try {
            writer.write(output + '\n', 0, output.length() + 1);
            writer.flush();

            Logger.getInstance().add(new Log(application.getName() + ":" + application.getId() +
                    " WROTE TO CLIENT:\n" + output));
        } catch (SocketException se) {
            Logger.getInstance().add(new Log(application.getName() + ":" + application.getId() +
                    " SOCKET EXCEPTION: " + se.getMessage() + "\n" + output));
        }

        return output;
    }

    /**
     * Outputs a line from the supplied Application parameter to a client, and outputs to log as
     * well.
     *
     * Overloaded implementation for user by the server.
     *
     * @param writer BufferedWriter
     * @param output java.lang.String
     * @throws IOException IOException
     */
    public static void doClientWrite(BufferedWriter writer, String output) throws IOException {
        try {
            writer.write(output + '\n', 0, output.length() + 1);
            writer.flush();

            Logger.getInstance().add(
                    new Log(" WROTE TO CLIENT:\n" + output));
        } catch (SocketException se) {
            Logger.getInstance().add(
                    new Log(" SOCKET EXCEPTION: " + se.getMessage() + "\n" + output));
        }
    }
}
