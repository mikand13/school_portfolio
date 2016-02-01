package no.wsact.mikand.pg4100.assignment1.LeasingManager.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * Helper class with various helpermethods needed in the program.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class Util {
    /**
     * Builds a formatted table of information from any object.
     *
     * @param statusOwner java.lang.String
     * @param arl ArrayList.T
     * @param <T> T
     * @return java.lang.String
     */
    public static <T> String buildStringTableFromArrayList(String statusOwner,
                                                           ArrayList<T> arl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("******* Status for ")
                     .append(statusOwner)
                     .append(" ********\n");

        for (T t : arl) {
            stringBuilder.append("* ").append(t).append("\n");
        }

        stringBuilder.append("******* Status for ")
                     .append(statusOwner)
                     .append(" ********\n");

        return stringBuilder.toString();
    }

    /**
     * Outputs all logs to individual files based on companyname.
     *
     * @param company java.lang.String
     * @param logs ArrayList.Log
     */
    public static void storeLogsOnDisk(String company, ArrayList<Log> logs) {
        File fileOut = new File(company + ".txt");

        try (PrintStream out = new PrintStream(fileOut)) {
            for (Log l : logs) {
                out.print("\n" + l);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sleep a thread for a number of seconds.
     *
     * @param t Thread
     * @param seconds int
     * @throws InterruptedException InterruptedException
     */
    @SuppressWarnings("AccessStaticViaInstance")
    public static void sleepThreadForOneToSuppliedSecond(Thread t, int seconds)
            throws InterruptedException {
        t.sleep(((int) (Math.random() * seconds) + 1) * 1000);
    }
}
