package searchers;

import edu.princeton.cs.introcs.Stopwatch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
public class IterativeSearchWithStack {

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
        Stack<File> fileStack = new Stack<>();
        fileStack.push(file);

        // runs as long as the stack contains files.
        while (!fileStack.isEmpty()) {
            File poppedFile = fileStack.pop();
            // Checks for file or directory, handles appropriately.
            if (poppedFile.isFile()) {
                if (SearchHelper.contains(poppedFile, target)) {
                    foundFiles.add(poppedFile);
                }
            } else if (poppedFile.isDirectory()) {
                System.out.println(poppedFile.getAbsolutePath()); // progress verification
                File[] files = poppedFile.listFiles();
                if (files != null) {
                    for (File f : files) {
                        fileStack.push(f);
                    }
                }
            }
        }
        return foundFiles.toArray(new File[foundFiles.size()]);
    }
}