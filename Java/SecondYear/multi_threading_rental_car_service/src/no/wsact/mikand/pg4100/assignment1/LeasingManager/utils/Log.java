package no.wsact.mikand.pg4100.assignment1.LeasingManager.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * VO for log entries.
 *
 * @author mikand@westerdals.no
 * @version 1.0
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
        return "LOG: " + logId + "\n\n" +
                "DATE: " + date + "\n\n" +
                logEntry;
    }
}
