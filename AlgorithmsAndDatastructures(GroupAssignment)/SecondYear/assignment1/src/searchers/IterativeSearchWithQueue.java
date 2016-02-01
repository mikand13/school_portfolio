package searchers;

import edu.princeton.cs.introcs.Stopwatch;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
public class IterativeSearchWithQueue {

    private static final String BASE_FILE = "/";
    private static final String SEARCH_STRING = "public static void main";

    // Main checks for arguments, defaults to root and java files with main method.
    public static void main(String[] args) {
        if (args.length == 0) {
            start(BASE_FILE, SEARCH_STRING);
        } else if (args.length == 2) {
            start(args[0], args[1]);
        } else {
            System.out.println("Invalid parameters");
        }
    }

    // Timer and driver for the searcher
    private static void start(String baseFile, String searchString) {
        File file = new File(baseFile);
        Stopwatch stopwatch = new Stopwatch();
        File[] files = search(file, searchString);
        double duration = stopwatch.elapsedTime();
        System.out.println("Found " + files.length + " files in " + duration + " seconds.");
        System.out.println("Files containing the searchstring \"" + searchString + "\":");
        for (File f : files) {
            System.out.println("\t" + f.getAbsolutePath());
        }
    }

    public static File[] search(File file, String target) {
        List<File> foundFiles = new LinkedList<>();
        LinkedList<File> fileQueue = new LinkedList<>(); // used as queue
        fileQueue.add(file);

        // runs as long as the queue contains files.
        while (!fileQueue.isEmpty()) {
            File dequeuedFile = fileQueue.remove();
            // Checks for file or directory, handles appropriately.
            if (dequeuedFile.isFile()) {
                if (SearchHelper.contains(dequeuedFile, target)) {
                    foundFiles.add(dequeuedFile);
                }
            } else if (dequeuedFile.isDirectory()) {
                System.out.println(dequeuedFile.getAbsolutePath()); // progress verification
                File[] files = dequeuedFile.listFiles();
                if (files != null) {
                    Collections.addAll(fileQueue, files);
                }
            }
        }
        return foundFiles.toArray(new File[foundFiles.size()]);
    }
}