/* Wrapper of legacyMergeSort from java.util.Arrays (java 7), rewritten for
 * pedagogical purposes. Copyright (c) 2014 Westerdals Høyskola.
 *  
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. 
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 * Denne klassen er tilrettelagt for utprøving
 * av ulike varianter av Merge sort.
 *
 * Merknad: Vanligvis vil sorterings-algoritmer
 * være representert ved statiske metoder. Her 
 * er det praktisk å knytte sorteringsmetoden
 * til objekter, i og med at vi ønsker å teste
 * sorteringsmetoden for ulike verdier av konstanten
 * insertionsortThreshold.
 *
 * @author Lars Sydnes (Har klippet dette sammen for pedagogiske formål:)
 * @author Anders Mikkelsen - mikand13@student.westerdals.no (Har gjort enkelte endringer)
 * @author Espen Rønning - ronesp13@student.westerdals.no (Har gjort enkelte endringer)
 *
 * Disse har skrevet selve søkealgoritmen, i klassen java.util.Arrays (java 7)
 * 
 * @author Josh Bloch
 * @author Neal Gafter
 * @author John Rose
 * @since  1.2
 */
import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class MergeSort<T extends Comparable<T>> {
    private int insertionsortThreshold;
    private int cmpCount;

    public MergeSort() {
        this(7); // Standard value
    }

    @SuppressWarnings("SameParameterValue")
    public MergeSort(int insertionsortThreshold) {
        this.insertionsortThreshold = insertionsortThreshold;
    }

    @SuppressWarnings("unchecked")
    public void sort(T[] array) {
        T[] aux = Arrays.copyOf(array, array.length);
        sort(aux, array, 0, array.length, 0);
    }

    public SortPack sortCountAndTime(T[] array) {
        cmpCount = 0;
        Stopwatch timer = new Stopwatch();
        sort(array);
        return new SortPack(cmpCount, timer.elapsedTime());
    }

    @SuppressWarnings("unchecked")
    private void sort(T[] src,
                      T[] dest,
                      int low,
                      int high,
                      int off) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (length < insertionsortThreshold) {
            for (int i = low; i < high; i++)
                for (int j = i; j > low &&
                        compareAndCount(dest[j - 1], dest[j]) > 0; j--)
                    swap(dest, j, j - 1);
            return;
        }

        // Recursively sort halves of dest into src
        int destLow = low;
        int destHigh = high;
        low += off;
        high += off;
        int mid = (low + high) >>> 1;
        sort(dest, src, low, mid, -off);
        sort(dest, src, mid, high, -off);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (compareAndCount(src[mid - 1], src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && compareAndCount(src[p], src[q]) <= 0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    public int getThreshold() {
        return insertionsortThreshold;
    }

    public void setInsertionsortThreshold(int insertionsortThreshold) {
        this.insertionsortThreshold = insertionsortThreshold;
    }

    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }


    @SuppressWarnings("TypeParameterHidesVisibleType")
    private <T extends Comparable<T>> int compareAndCount(T a, T b) {
        cmpCount++;
        return a.compareTo(b);
    }

    /**
     * This method returns an array containing the numbers 1..size in a
     * pseudo-random order.
     *
     * @return Integer[]-array containing the  numbers 1..size in a random order.
     */
    static Integer[] randomArray(int size) {

        Integer[] out = orderedArray(size);

        for (int i = size - 1; i > 0; i--)
            swap(out, i, StdRandom.uniform(i));

        return out;
    }

    static Integer[] orderedArray(int size) {

        Integer[] out = new Integer[size];

        for (int i = 0; i < size; i++)
            out[i] = i + 1;

        return out;
    }
}

