package no.wsact.mikand.pg4100.assignment1.LeasingManager.utils;

import java.util.ArrayList;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * This class defines Logger objects for stories log entries as Log objects
 * for a company. It is generic enough to hold any logs in String form.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class Logger {
    private final ArrayList<Log> logList;

    /**
     * Sets a List for the Logger.
     */
    public Logger() {
        logList = new ArrayList<>();
    }

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

        for (Log l : logList) {
            stringBuilder.append("\n")
                         .append(l)
                         .append("\n");
        }

        return stringBuilder.toString();
    }

    public ArrayList<Log> getList() {
        return logList;
    }
}
