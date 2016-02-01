package searchers;

import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.Stopwatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anders Mikkelsen ( mikand13@student.westerdals.no )
 * @author Espen Rønning ( ronesp13@student.westerdals.no )
 */
public class SearchTest {
    private static boolean binaryWithSort = false;

	public static void fml() {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        Searcher<Integer> binary = new BinarySearch<>();
        Searcher<Integer> sequenceIndex = new SequentialIndexSearch<>();
        Searcher<Integer> sequenceForEach = new SequentialForEachSearch<>();
        Searcher<Integer> builtin = new BuiltinSearch<>();

        List<List<Integer>> lists = new ArrayList<>();

        lists.add(arrayList);
        lists.add(linkedList);

        List<Searcher<Integer>> searchers = new ArrayList<>();

        searchers.add(binary);
        searchers.add(sequenceIndex);
        searchers.add(sequenceForEach);
        searchers.add(builtin);

        // Added 4 arraylist to keep track of found / not found.
        ArrayList<String> arrayListFound = new ArrayList<>();
        ArrayList<String> arrayListNotFound = new ArrayList<>();
        ArrayList<String> linkedListFound = new ArrayList<>();
        ArrayList<String> linkedListNotFound = new ArrayList<>();

        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < searchers.size(); j++) {
                int range;
                if (searchers.get(j) instanceof SequentialIndexSearch) {
                    range = 400000;
                } else {
                    range = 2000000;
                }
                for (int k = 0; k <= range; k += 1000) { // Only starting at 0 to get a good representation on charts.
                    // These objects contain time to complete, and found / not found.
                    TestResult testResult = test(searchers.get(j) , lists.get(i), k);

                    String send= String.format("%d\t%.2f\t%s\t%s\n",
                            k,
                            testResult.getTime() * 1000,
                            lists.get(i).getClass().getSimpleName(),
                            searchers.get(j).getClass().getSimpleName()).replace(".", ",");
                    // This if adds the result to the approriate list.
                    if (testResult.isFound()) {
                        if (lists.get(i) instanceof ArrayList) {
                            arrayListFound.add(send);
                        } else {
                            linkedListFound.add(send);
                        }
                        System.out.println(k
                                + "\tFound in    \t"
                                + lists.get(i).getClass()
                                + "\t" + searchers.get(j).getClass()
                                + "\t" + testResult.getTime() * 1000); // Show result in seconds.
                    } else {
                        if (lists.get(i) instanceof LinkedList) {
                            linkedListNotFound.add(send);
                        } else {
                            arrayListNotFound.add(send);
                        }
                        System.out.println(k
                                + "\tNot found in\t"
                                + lists.get(i).getClass()
                                + "\t" + searchers.get(j).getClass()
                                + "\t" + testResult.getTime() * 1000); // Show result in seconds.
                    }
                }

                // Resets the searcher if its binary, to search again counting in sort time.
                if (searchers.get(j) instanceof BinarySearch && !binaryWithSort) {
                    binaryWithSort = true;
                    j--;
                }
            }
			binaryWithSort = false;
        }

        // Prints to approriate files with helpermethod outputList.
        try {
            File arrayListOutputFound = new File("arrayListStatisticsFound.txt");
            File arrayListOutputNotFound = new File("arrayListStatisticsNotFound.txt");
            File linkedListOutputFound = new File("linkedListStatisticsFound.txt");
            File linkedListOutputNotFound = new File("linkedListStatisticsNotFound.txt");

            outputList(arrayListOutputFound, arrayListFound);
            outputList(arrayListOutputNotFound, arrayListNotFound);
            outputList(linkedListOutputFound, linkedListFound);
            outputList(linkedListOutputNotFound, linkedListNotFound);

        } catch (FileNotFoundException fnfe) {
            System.out.println("\nFile not found, dumping output!\n");
            System.out.println("\nList of times from found elements:\n");
            for (String s : arrayListFound)
                StdOut.print(s);
            System.out.println("\nList of times from not found elements:\n");
            for (String s : arrayListNotFound)
                StdOut.print(s);
        }
    }

    private static void outputList (File f, ArrayList<String> l) throws FileNotFoundException {
        FileOutputStream fileOut = new FileOutputStream(f);
        Out outStream = new Out(fileOut);

        for (String s : l)
            outStream.print(s);
    }

	
	private static TestResult test(Searcher<Integer> searcher, List<Integer> list, int size){
        Stopwatch timer;

        // Defines timer start based on searcher.
        // Moved Collections.sort to this method.
        if (searcher instanceof BinarySearch && binaryWithSort) {
		    prepareForTest(searcher, list,size);
		    timer = new Stopwatch();
            Collections.sort(list);
        } else if (!(searcher instanceof BinarySearch)) {
            prepareForTest(searcher, list,size);
            timer = new Stopwatch();
        } else {
            prepareForTest(searcher, list,size);
            Collections.sort(list);
            timer = new Stopwatch();
        }

		int index = searcher.search(list,0);

        // Returns found or not found object.
        if (index == -1) {
            return new TestResult(timer.elapsedTime(), false);
        } else {
            return new TestResult(timer.elapsedTime(), true);
        }
	}

	private static void prepareForTest(Searcher<Integer> searcher, List<Integer> list,int size){
		list.clear();
		for(int i = 0; i < size; i++)
			list.add(StdRandom.uniform(-size, size));
	}
}



interface Searcher<T> {
	/*
	 * Returns index of target if the target is present
	 * in the list.'
	 *
	 * When the target is not in the list, this method
	 * return -1.
	 */
	int search(List<T> list, T target);
}

class TestResult { // Make a new class to save time and found / not found.
    double time;
    boolean found;

    public TestResult (double time, boolean found) {
        this.time = time;
        this.found = found;
    }

    public double getTime() { return time; }
    public boolean isFound() { return found; }
}


class SequentialIndexSearch<T> implements Searcher<T> {

	public int search(List<T> list, T target){
		for (int i = 0; i < list.size() ; i++){
			if (list.get(i).equals(target))
				return i;
		}
		return -1;
	}
}

class SequentialForEachSearch<T>  implements Searcher<T> {
	public int search(List<T> list, T target){
		int i = 0;
		for (T obj : list){
			if (obj.equals(target))
				return i;
			i += 1;
		}
		return -1;
	}
}

class BuiltinSearch<T> implements Searcher<T> {
	public int search(List<T> list, T target){
		return list.indexOf(target);
	}
}


class BinarySearch<T extends Comparable<T>> implements Searcher<T> {
	public int search(List<T> list, T target){
		int low = 0;
		int high = list.size() - 1;
		int middle;

		while (low <= high){
			middle = (low+high)/2;	
			int cmp = target.compareTo(list.get(middle));
			if (cmp < 0) {
				high = middle - 1;
			} else if (cmp > 0) {
				low = middle + 1;
			} else { // cmp == 0
				return middle;
			}
		}

		return -1;
	}	
}
