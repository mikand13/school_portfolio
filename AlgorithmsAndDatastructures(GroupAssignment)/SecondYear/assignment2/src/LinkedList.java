
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Anders Mikkelsen - mikand13@student.westerdals.no
 * @author Espen Rønning - ronesp13@student.westerdals.no
 * @author Lars Sydnes
 */

@SuppressWarnings("WeakerAccess")
public class LinkedList<T extends Comparable<T>> implements Iterable<T> {
    private Node root;
    private int size;

    LinkedList() {
        root = null;
        size = 0;
    }

    /**
     * BASIC INTERFACE
     */
    public void add(T elem) {
        size++;
        root = new Node(elem, root);
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    /**
     * SELECTION SORT
     */
    public void selectionSort() {
        Node current = root;
        while (current != null) {
            exch(current, findMin(current));
            current = current.next;
        }
    }

    private Node findMin(Node current) {
        Node candidate = current;
        while (current != null) {
            if (less(current, candidate))
                candidate = current;

            current = current.next;
        }
        return candidate;
    }

    private void exch(Node a, Node b) {
        T tmp = a.item;
        a.item = b.item;
        b.item = tmp;
    }

    // edited this to <= to make the mergeSort stable
    private boolean less(Node a, Node b) {
        return a.item.compareTo(b.item) <= 0;
    }

    /**
     * MERGE SORT
     * <p/>
     * Both types are implemented below.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int insertionSortThreshold = 7;

    public void mergeSortTopDown() {
        root = mergeSortTopDown(root, size);
    }

    public void mergeSortBottomUp() {
        root = mergeSortBottomUp(root, size);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Node mergeSortTopDown(Node head, int size) {
        if (head == null || head.next == null) {
            return head;
        }

        if (size < insertionSortThreshold) {
            return insertionSort(head);
        }

        // chop list in two, recursively, until single lists
        Node rightList = mergeSortTopDown(findMiddle(head, size), size / 2 + 1);
        Node leftList = mergeSortTopDown(head, size / 2);

        /*
        // check already sorted
        if (checkIfSorted(leftList, rightList)) {
            return leftList;
        }
        */

        // iteratively merge sublists till one sublist is left,
        // which will be the sorted list, which is returned
        return mergeIteratively(leftList, rightList);
    }

    // find middle of list and chops it,
    // return middle or null if middle not found
    private Node findMiddle(Node head, int size) {
        int count = 0;
        while (head != null) {
            count++;
            Node next = head.next;

            if (count == size / 2) {
                head.next = null;
                return next;
            }
            head = next;
        }
        return null;
    }

    // removed
    private boolean checkIfSorted(Node leftList, Node rightList) {
        // find end of leftList. Small iteration here but should be
        // minimal performance impact compared to merging sorted lists
        Node temp = leftList;
        while (temp.next != null) {
            temp = temp.next;
        }

        // if already sorted, just combine, and dont do merges.
        if (rightList != null && less(temp, rightList)) {
            temp.next = rightList;
            return true;
        } else {
            return false;
        }
    }

    private Node mergeSortBottomUp(Node head, int size) {
        // we eventually ended on using a java.util.LinkedList as a queue and
        // kind of simulate a bottom-up sort.
        java.util.LinkedList<Node> queue = new java.util.LinkedList<>();
        if (head == null || head.next == null) {
            return head;
        }

        // here we divide the entire list into lists of 1 and push them
        // onto the queue.
        for (Node u; head != null; head = u) {
            u = head.next;
            head.next = null;
            queue.add(head);
        }
        head = queue.poll();

        // here we continually merge the first two lists in the queue
        // this is the same as 1-pass, 2-pass and so on. Eventually there will
        // only be two lists left. When they are merged the queue is empty and
        // the root is returned.
        int lengthCounter = 0, currentSubSize = 1;
        while (!queue.isEmpty()) {
            queue.add(head);

            Node leftList = queue.poll();
            Node rightList = queue.poll();

            if (lengthCounter < 7) {
                Node temp = leftList;

                while (temp.next != null) {
                    temp = temp.next;
                }
                temp.next = rightList;
                head = insertionSort(leftList);
                // iterative merge for unsorted sublists of over 7 in length
            } else {
                head = mergeIteratively(leftList, rightList);
            }

            // keeps track of sublist size
            if (lengthCounter < size) {
                lengthCounter += currentSubSize;
            } else {
                lengthCounter = 0;
                currentSubSize++;
            }
        }
        return head;
    }

    // recursive merge for top-down,
    // switched for iterative because of stack overflow
    private Node mergeRecursively(Node a, Node b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
            // under follows recursive merge of sublists
        } else if (less(a, b)) {
            a.next = mergeRecursively(a.next, b);
            return a;
        } else {
            b.next = mergeRecursively(a, b.next);
            return b;
        }
    }

    // merging two lists iteratively, to avoid stack overflow on big lists
    private Node mergeIteratively(Node leftList, Node rightList) {
        if (leftList == null) return rightList;
        if (rightList == null) return leftList;

        // check which side is less and start from that side
        // original is for return value
        Node head, original;
        if (less(leftList, rightList)) {
            head = leftList;
            original = leftList;
            leftList = leftList.next;
        } else {
            head = rightList;
            original = rightList;
            rightList = rightList.next;
        }

        while (rightList != null || leftList != null) {
            // if either side is null, dump other list into sorted list
            if (leftList == null) {
                head.next = rightList;
                rightList = rightList.next;
            } else if (rightList == null) {
                head.next = leftList;
                leftList = leftList.next;
            } else {
                // do compares and merge list iteratively as long as both lists != null
                if (less(leftList, rightList)) {
                    head.next = leftList;
                    leftList = leftList.next;
                } else {
                    head.next = rightList;
                    rightList = rightList.next;
                }
            }
            head = head.next;
        }

        // returns the original head
        return original;
    }

    // insertion sort for this LinkedList class
    private Node insertionSort(Node head) {
        // set head to head of sortedList, and then set next to null
        // then go to next element of head
        Node sorted = head;
        head = head.next;
        sorted.next = null;

        while (head != null) {
            Node current = head;
            head = head.next;

            if (less(current, sorted)) {
                // is current is less than head of sorted
                // set current to new head of sorted
                current.next = sorted;
                sorted = current;
            } else {
                // search through the list for position of current
                Node temp = sorted;

                while (temp.next != null && less(temp.next, current)) {
                    temp = temp.next;
                }

                // set pos of current
                current.next = temp.next;
                temp.next = current;
            }
        }
        return sorted;
    }

    /**
     * INNER CLASSES
     */
    private class Node {
        T item;
        Node next;

        Node(T i, Node n) {
            item = i;
            next = n;
        }
    }

    private class LinkedListIterator implements Iterator<T> {

        Node current;

        LinkedListIterator() {
            current = root;
        }

        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            T output = current.item;
            current = current.next;
            return output;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    /**
     * SIMPLE TEST CLIENT
     * <p/>
     * Creates two random lists according to testSize and mergeSorts with both methods.
     *
     * CLA for custom list size. Standard is 10000.
     * Verbose means printing out random and sorted lists.
     */
    public static void main(String[] args) {
        int testSize = 2000000;
        double time = 0.0;
        boolean verbose = false;

        if (args.length != 0) {
            testSize = Integer.parseInt(args[0]);
        }

        System.out.println("Do you want a verbose output? (y / n)");
        Scanner s = new Scanner(System.in);
        String choice = s.nextLine();

        switch (choice.charAt(0)) {
            case 'y':
                verbose = true;
                break;
            default:
                break;
        }

        System.out.printf("\nProducing testlist of size: %d\n", testSize);
        LinkedList<Integer> lst = new LinkedList<>();
        lst.createAndOutputTestList(lst, testSize, verbose);

        // Top-down
        System.out.printf("\nTop-Down MergeSort of size: %d\n", testSize);

        Stopwatch t = new Stopwatch();
        lst.mergeSortTopDown();
        time = t.elapsedTime();

        lst.outputResults(lst, time, verbose);

        System.out.printf("\nProducing testlist of size: %d\n", testSize);
        lst = new LinkedList<>();
        lst.createAndOutputTestList(lst, testSize, verbose);

        // Bottom-up
        System.out.printf("\nBottom-up MergeSort of size: %d\n", testSize);

        t = new Stopwatch();
        lst.mergeSortBottomUp();
        time = t.elapsedTime();

        lst.outputResults(lst, time, verbose);

        s.close();
    }

    @SuppressWarnings("unchecked")
    private void createAndOutputTestList(LinkedList lst, int size, boolean verbose) {
        Random rnd = new Random();

        for (int i = 0; i < size; i++) {
            lst.add(rnd.nextInt(size));
        }

        // outputs the randomized list
        if (verbose) { System.out.printf("\nRandomized List (Size: %d):\n\n", lst.size); }
        printList(lst, verbose);
    }

    private void outputResults(LinkedList lst, double time, boolean verbose) {
        // outputs sorted list
        if (verbose) { System.out.printf("\nSorted List (Size: %d):\n\n", lst.size); }
        boolean sorted = printList(lst, verbose);
        System.out.print("\nThis array returned this status for sort: " + sorted + "\n");
        System.out.printf("This array was sorted in %.3f seconds.\n", time);
    }

    private boolean printList(LinkedList lst, boolean verbose) {
        int counter = 0, previous = 0;
        boolean sorted = true;

        for (Object aLst : lst) {
            try {
                if (previous > Integer.parseInt("" + aLst)) {
                    sorted = false;
                }
                previous = Integer.parseInt("" + aLst);
            } catch (NumberFormatException nfe) {
                // this here because im only using this to show that the sorting is complete
                // as part of the school assignment
            }

            if (verbose) {
                System.out.printf("%-10s ", aLst);

                if (counter == 9) {
                    System.out.println();
                    counter = 0;
                } else {
                    counter++;
                }
            }
        }
        return sorted;
    }
}
