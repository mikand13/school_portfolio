package searchers;

import edu.princeton.cs.introcs.Stopwatch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
public class WebSearch {

    private static final String DEFAULT_SITE = "http://www.nith.no/";
    private static final String SEARCH_STRING = "skole";
    private static final int VISIT_MAX = 10;

    // Main checks for arguments, defaults to nith.no
    public static void main(String[] args) {
        if (args.length == 0) {
            start(DEFAULT_SITE, SEARCH_STRING, VISIT_MAX);
        } else if (args.length == 3) {
            start(args[0], args[1], Integer.parseInt(args[2]));
        } else {
            System.out.println("Invalid parameters");
        }
    }

    // Timer and driver for the searcher
    private static void start(String defaultSite, String searchString, int visitMax) {
        try {
            Stopwatch stopwatch = new Stopwatch();
            URL[] urls = search(new URL(defaultSite), searchString, visitMax);
            double duration = stopwatch.elapsedTime();
            System.out.println("Found " + urls.length + " webpages containing the search-string: \"" + searchString + "\". Searched " + visitMax + " pages in " + duration + " seconds.");
            for (URL url : urls) {
                System.out.println("\t" + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // accepts a string and checks it for URL conformity, if the string is a URL it returns an URL object.
    private static URL getURL(String input) throws MalformedURLException {
        int start = input.indexOf("href=\"");
        int end = input.indexOf("\"", start + 6);
        if (start < 0) {
            return null;
        } else {
            try {
                String url = input.substring(start + 6, end);
                if ((url.startsWith("http://www") || url.startsWith("https://www")) && url.endsWith("/")) {
                    return new URL(url);
                } else {
                    return null;
                }
            } catch (StringIndexOutOfBoundsException e) {
                System.err.println("Error occured when parsing string for URL.");
            }
            return null;
        }
    }

    private static URL[] search(URL url, String target, int max) throws IOException {
        LinkedHashSet<String> foundLinks = new LinkedHashSet<>();
        HashSet<String> visitedLinks = new HashSet<>();
        LinkedList<String> enqueuedLinks = new LinkedList<>();
        enqueuedLinks.add(url.toString());

        // searches all enqueued links until done or until upper limit is reached
        while (!enqueuedLinks.isEmpty() && visitedLinks.size() < max) {
            URL currentLink = new URL(enqueuedLinks.remove());
            visitedLinks.add(currentLink.toString());
            System.out.println(currentLink.toString()); // Added for progression verification
            URLConnection connection = currentLink.openConnection();

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                HashSet<String> tmpFound = new HashSet<>();
                boolean containsTarget;
                // searches the inputStream of connection once, with a scanner
                while (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    containsTarget = input.contains(target);
                    if (containsTarget && !foundLinks.contains(currentLink.toString())) {
                        foundLinks.add(currentLink.toString());
                    }
                    URL newUrl = getURL(input);
                    // Make sure newUrl is not already found, not already visited or enqueued
                    if (newUrl != null && !tmpFound.contains(newUrl.toString()) && !visitedLinks.contains(newUrl.toString()) && !enqueuedLinks.contains(newUrl.toString())) {
                        tmpFound.add(newUrl.toString());
                        enqueuedLinks.add(newUrl.toString());
                    } else if (containsTarget && enqueuedLinks.size() > max) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error occurred when trying to read " + currentLink);
            }
        }
        // needed due to constraints discovered in the URL class. Detailed in documentation.
        return toArray(foundLinks);
    }

    private static URL[] toArray(LinkedHashSet<String> links) throws MalformedURLException {
        URL[] array = new URL[links.size()];
        int counter = 0;
        for (String foundLink : links) {
            array[counter++] = new URL(foundLink);
        }
        return array;
    }
}