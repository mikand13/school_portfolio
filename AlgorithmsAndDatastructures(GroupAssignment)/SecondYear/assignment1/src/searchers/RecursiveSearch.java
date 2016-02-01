package searchers;

import edu.princeton.cs.introcs.Stopwatch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
public class RecursiveSearch {

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

    // runs as long as it finds files.
    public static File[] search(File file, String target) {
        List<File> foundFiles = new LinkedList<>();
        search(file, target, foundFiles);
        return foundFiles.toArray(new File[foundFiles.size()]);
    }

    private static void search(File file, String target, List<File> list) {
        if (file.isFile()) {
            if (SearchHelper.contains(file, target)) {
                list.add(file);
            }
        } else if (file.isDirectory()) {
            System.out.println(file.getAbsolutePath()); // progress verification
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    search(f, target, list);
                }
            }
        }
    }
}