import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
class SearchHelper {
    public static boolean contains(File file, String target) {
        if (!file.canRead()) {
            return false;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                if (scanner.nextLine().contains(target)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not open " + file.getAbsolutePath());
        }
        return false;
    }
}