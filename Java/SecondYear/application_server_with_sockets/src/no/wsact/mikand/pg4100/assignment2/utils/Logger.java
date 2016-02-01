package no.wsact.mikand.pg4100.assignment2.utils;

import java.util.ArrayList;

/**
 * This is a class i devised for assignment 1, its been modified slightly.
 * <p>
 * Defines a Logger, which i have made a singleton this time around.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class Logger {
    private static Logger instance;
    private ArrayList<Log> logList;

    /**
     * Sets a List for the Logger.
     */
    private Logger() {
        if (instance == null) {
            instance = this;

            logList = new ArrayList<>();
        } else {
            System.err.println("\n\nThere can only be one Logger\n\n");
        }
    }

    /**
     * Returns the singleton instance or generates it if it is null.
     *
     * @return utils.Logger
     */
    public static Logger getInstance() {
        if (instance != null) {
            return instance;
        }

        new Logger();
        return instance;
    }

    /**
     * Adds a Log object to the loglist.
     *
     * @param logEntry utils.Log
     */
    public void add(Log logEntry) {
        logList.add(logEntry);
    }

    /**
     * This builds a string based on all log entries.
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Log l : getList()) {
            stringBuilder.append("\n")
                    .append(l)
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    ArrayList<Log> getList() {
        return logList;
    }
}
