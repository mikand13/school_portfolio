import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author Anders Mikkelsen - mikand13@student.westerdals.no
 * @author Espen Rønning - ronesp13@student.westerdals.no
 */
class MergeSortStatistics {
    private static final int TEST_STANDARD = 10000;

    public static void main(String[] args) {
        SortPack[] sortedResults = new SortPack[TEST_STANDARD];
        SortPack[] unSortedResults = new SortPack[TEST_STANDARD];

        int counter = 0;

        ArrayList<String> results = new ArrayList<>();
        MergeSort<Integer> s = new MergeSort<>();

        results.add(String.format("%-15s" +
                        "%-15s" +
                        "%-15s" +
                        "%-15s" +
                        "%-15s" +
                        "%-15s",
                "THRESHOLD:\t",
                "SIZE:\t",
                "SortedTime:\t",
                "SortedCompares:\t",
                "UnsortedTime:\t",
                "UnsortedCompares\n"));

        int INSERTION_MAX_THRESHOLD = 50;
        int INSERTION_MIN_THRESHOLD = 2;
        for (int j = INSERTION_MIN_THRESHOLD; j <= INSERTION_MAX_THRESHOLD; j++) {
            s.setInsertionsortThreshold(j);
            System.out.println("Now testing threshold " + j);
            for (int i = 0; i <= TEST_STANDARD; i += 100) {
                Integer[] sortedArray = MergeSort.orderedArray(i * 1000);
                Integer[] unSortedArray = MergeSort.randomArray(i * 1000);
                sortedResults[counter] = s.sortCountAndTime(sortedArray);
                unSortedResults[counter] = s.sortCountAndTime(unSortedArray);

                results.add(String.format("%-15d\t" +
                                "%-15d\t" +
                                "%-15.3f\t" +
                                "%-15d\t" +
                                "%-15.3f\t" +
                                "%-15d\n",
                        j,
                        sortedArray.length,
                        sortedResults[counter].getTime(),
                        sortedResults[counter].getCompares(),
                        unSortedResults[counter].getTime(),
                        unSortedResults[counter].getCompares()));
                counter++;
            }
            counter = 0;
        }

        try {
            File f = new File("TestResults.txt");
            FileOutputStream fileOut = new FileOutputStream(f);
            Out outStream = new Out(fileOut);

            for (String stemp : results)
                outStream.print(stemp);
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found.\n");

            for (String stemp : results)
                System.out.println(stemp);
        }
    }
}
