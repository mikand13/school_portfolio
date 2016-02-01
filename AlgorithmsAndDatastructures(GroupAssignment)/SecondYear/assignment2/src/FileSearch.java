import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Anders Mikkelsen - mikand13@student.westerdals.no
 * @author Espen Rønning - ronesp13@student.westerdals.no
 */
class FileSearch {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Katalogsti: ");
        String path = scanner.nextLine();
        System.out.print("Søkestreng: ");
        String word = scanner.nextLine();

        File[] files = search(new File(path), word);
        criteria();
        System.out.print("Velg kriterium: ");
        String criteria = scanner.nextLine();
        sort(criteria, files);
        printFiles(path, files);
    }

    private static void sort(String criteria, File[] files) {
        switch (criteria) {
            case "1":
                Arrays.sort(files, new FileNameComparator());
                break;
            case "2":
                Arrays.sort(files, new FileModificationComparator().reversed());
                break;
            case "3":
                Arrays.sort(files, new FileSizeComparator().reversed());
                break;
            case "4":
                Arrays.sort(files, new FileExecutableComparator().reversed());
                break;
            default:
                System.out.println("Valget finnes ikke.");
                break;
        }
    }

    private static void criteria() {
        System.out.println("Sorteringskriterier\n");
        System.out.println("1: Filnavn");
        System.out.println("2: Modifiseringstidspunkt");
        System.out.println("3: Størrelse");
        System.out.println("4: Kjørbarhet\n");
    }

    private static void printFiles(String path, File[] files) {
        System.out.println("Filer: ");
        for (File file : files) {
            System.out.println("\t" + path + "\\" + file.getName());
        }
    }

    private static File[] search(File file, String target) {
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
                File[] files = dequeuedFile.listFiles();
                if (files != null) {
                    Collections.addAll(fileQueue, files);
                }
            }
        }
        return foundFiles.toArray(new File[foundFiles.size()]);
    }

    private static class FileNameComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private static class FileModificationComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return Long.compare(o1.lastModified(), o2.lastModified());
        }
    }

    private static class FileSizeComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return Long.compare(o1.length(), o2.length());
        }
    }

    private static class FileExecutableComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            return Boolean.compare(o1.canExecute(), o2.canExecute());
        }
    }
}
