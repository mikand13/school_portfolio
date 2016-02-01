package no.wsact.mikand.pg4100.assignment2.views.utils;

import no.wsact.mikand.pg4100.assignment2.utils.Log;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import no.wsact.mikand.pg4100.assignment2.views.Application;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views.utils
 * <p>
 * This class encompases utilties for Applications on the Server.
 *
 * @author Anders Mikkelsen
 * @version 20.03.2015
 */
public class ApplicationUtils {

    /**
     * Facade for log entries.
     *
     * @param application Application
     * @param message java.lang.String
     */
    public static void addApplicationLog(Application application, String message) {
        Logger.getInstance().add(
                new Log(application.getName() + ": " + application.getId() + " " + message));
    }
}
