package no.wsact.mikand.pg4100.assignment2.utils.server;

import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import no.wsact.mikand.pg4100.assignment2.views.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

/**
 * This is a helper class for methods pertaining to reading from sockets.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class Reader {

    /**
     * Returns a read line from the supplied BufferedReader parameter, and outputs to log as well.
     *
     * @param application Application
     * @param reader BufferedReader
     * @return java.lang.String
     * @throws IOException IOException
     */
    public static String doClientRead(Application application, BufferedReader reader) throws
            IOException {
        try {
            String input = reader.readLine();
            Logger.getInstance().add(new Log(application.getName() + ":" + application.getId() +
                    " SERVER RECEIVED:\n" + input));

            return input;
        } catch (SocketException se) {
            Logger.getInstance().add(new Log(application.getName() + ":" + application.getId() +
                    " SOCKET EXCEPTION: " + se.getMessage() + "\n"));
        }

        return null;
    }

    /**
     * Returns a read line from the supplied BufferedReader parameter, and outputs to log as well.
     * This is an overloaded implementation for use by the server.
     *
     * @param reader BufferedReader
     * @return java.lang.String
     * @throws IOException IOException
     */
    public static String doClientRead(BufferedReader reader) throws
            IOException {
        try {
            String input = reader.readLine();
            Logger.getInstance().add(new Log(" SERVER RECEIVED:\n" + input));

            return input;
        } catch (SocketException se) {
            Logger.getInstance().add(new Log(" SOCKET EXCEPTION: " + se.getMessage() + "\n"));
        }

        return null;
    }
}
