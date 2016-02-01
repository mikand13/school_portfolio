import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Anders Mikkelsen - mikand13@student.westerdals.no
 * @author Espen Rønning - ronesp13@student.westerdals.no
 */
class WebSearch {
    private static final int MAX_SEARCHED_URLS = 50;

    private static int shortestName = Integer.MAX_VALUE;
    private static int longestWordCountInName = Integer.MIN_VALUE;

    public static void main(String[] args) throws IOException {
        HashMap<String, List<String>> map = parseFile();
        Stopwatch t = new Stopwatch();
        System.out.print("Indekserer omtale av stortingsrepresentanter under domenet klassekampen.no. Vennligst vent...");
        search(new URL("http://www.klassekampen.no"), map, MAX_SEARCHED_URLS);
        System.out.println(" Indeksering fullført på " + t.elapsedTime() + " sekunder.");

        do {
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nNavn på stortingsrepresentant: ");
            String person = scanner.nextLine();

            if (map.containsKey(person)) {
                System.out.println("\nOppslag om " + person + ":\n");
                List<String> list = map.get(person);

                for (String item : list) {
                    System.out.println("\t" + item);
                }
            } else {
                System.out.println("\nDenne personen finnes ikke.\n");
            }
            System.out.print("\nVil du avslutte? (j/n): ");

            if (scanner.nextLine().equals("j")) {
                break;
            }
        } while (true);
    }

    private static HashMap<String, List<String>> parseFile() {
        HashMap<String, List<String>> map = new HashMap<>();

        try (Scanner scanner = new Scanner(
                new File("storting/stortinget2014.txt"))) {
            while (scanner.hasNext()) {
                String representative = scanner.nextLine().replaceFirst("([a-zA-ZæøåÆØÅ]+\\s){2}", "");
                map.put(representative, new ArrayList<String>());
                String[] wordChecker = representative.split(" ");

                if (representative.length() < shortestName) {
                    shortestName = representative.length();
                }

                if (wordChecker.length > longestWordCountInName) {
                    longestWordCountInName = wordChecker.length;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    @SuppressWarnings("SameParameterValue")
    private static void search(URL url, HashMap<String, List<String>> map, int max) throws IOException {
        String link = url.getProtocol() + "://" + url.getHost();
        HashSet<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        queue.addLast(link);

        // iterates the queue and does search
        while (!queue.isEmpty() && visited.size() < max) {
            String current = queue.pollFirst();
            visited.add(current);
            URL currentURL = new URL(current);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(currentURL.openStream(), "Windows-1252"));

            try (Scanner scanner = new Scanner(in)) {
                // iterate through page line by line, compare
                while (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    String newUrl = getURL(input);

                    // check if line is long enough for any of the keys
                    input = input.trim();
                    
                    if (input.length() >= shortestName) {
                        // remove html tags
                        input = input.replaceAll("<.*?>", " ");
                        String[] checkInput = input.split(" ");

                        for (int i = 0; i < checkInput.length - longestWordCountInName; i++) {
                            String person = checkInput[i];

                            // check if word in string has capital first letter
                            if (person.length() > 0 && Character.isUpperCase(person.charAt(0))) {
                                // check consecutive words for match in map
                                for (int j = 1; j < longestWordCountInName; j++) {
                                    person += " " + checkInput[i + j];

                                    if ((map.keySet().contains(person))) {
                                        List<String> temp = map.get(person);

                                        if (!temp.contains(current)) {
                                            temp.add(current);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // prevents duplicates
                    if (newUrl != null &&
                            !visited.contains(newUrl) &&
                            !queue.contains(newUrl) &&
                            visited.size() < max) {
                        queue.addLast(newUrl);
                    }
                }
            }
        }
    }

    // we decided that only articles are the urls that contain the info we want in this assignment
    private static String getURL(String input) {
        if (!input.contains("href")) {
            return null;
        }
        String tmp = input.replaceFirst(".*href=\"(.*?)\".*", "$1");

        if (tmp.startsWith("/article")) {
            return "http://www.klassekampen.no" + tmp;
        } else {
            return null;
        }
    }
}
