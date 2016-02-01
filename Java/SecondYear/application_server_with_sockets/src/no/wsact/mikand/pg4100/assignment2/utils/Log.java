package no.wsact.mikand.pg4100.assignment2.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * This is a class i devised for assignment 1, its been modified slightly.
 * <p>
 * Defines a log entry.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class Log {
    private final UUID logId;
    private final Date date;
    private final String logEntry;

    /**
     * Generates a UUID and date.
     *
     * @param logEntry java.lang.String
     */
    public Log(String logEntry) {
        logId = UUID.randomUUID();
        date = Calendar.getInstance().getTime();
        this.logEntry = logEntry;
    }

    /**
     * Returns table formatted log entry.
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return "-------------------------------------------------------------------------------\n" +
                "LOG: " + logId + "\n" +
                "DATE: " + date + "\n\n" +
                logEntry +
                "\n-------------------------------------------------------------------------------";
    }
}
