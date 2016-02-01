#include <stdio.h>
#include <stdlib.h>
	
struct pair {
	int value;
	int index;
};

void bubbleSort(struct pair*, int);
int binarySearch(struct pair*, int, int);

int main (int argc, char* argv[]) {
	int size = 100;
	struct pair *array = malloc(size * sizeof(struct pair *));

	printf("\nPointer array starts with a length of %d"
				   " (%lu bytes)", size, size * sizeof(struct pair *));

	FILE *f = fopen(argv[1], "r");
	int count = 0;
	int temp;

	printf("\nAdding integers from %s.\n", argv[1]);

	// reads the integers from file, truncating beyond 200k.
	while (fscanf(f, "%d", &temp) == 1 && count < 200001) {
		// dynamically resizes array if needed
		if (count == size) {
			int newSize = size * 2;
			array = realloc(array, newSize * sizeof(struct pair *));
			size = newSize;
		}
		array[count].value = temp;
		array[count].index = count;
		if (count == 200000) {
			printf("\nMaximum integer input is 200000,");
			printf(" ending input and closing file.\n");
		} else {
			count++;
		}
	}

	fclose(f);
	size = count;
	// trims the array to data size to preserve memory
	array = realloc(array, size * sizeof(int *));

	// checks size, and exits on count < 2
	int exitCode = 0;
	if (size < 2) {
		printf("\n\nThe minimum input is 2 integers,"
					   " program closing.\n\n");
		exitCode = 1;
	} else {
		// Sorts and searches array
		printf("\nThe count is %d. Array has been resized"
					   " and trimmed.\n\n", size);

		bubbleSort(array, size);
		printf("The array is now sorted.\n\n"
					   "Please input an integer for search:\n\n");

		int temp;
		scanf("%d", &temp);

		// Checks for exit condition
		if (temp != 0) {
			binarySearch(array, size, temp);
		} else {
			printf("\n%d accepted! Program exiting!\n\n", temp);
		}
	}
	free(array);
	exit(exitCode);
}

// enhanced but standard bubbleSort pattern
void bubbleSort(struct pair *array, int size) {
	int didSort;
	int i;
	int j = 0;
	do {
		didSort = 0;
		for (i = 0; i < size - 1 - j; i++) {
			// checks current and next, swaps if neccessary
			if (array[i].value > array[i + 1].value) {
				struct pair temp = array[i];
				array[i] = array[i + 1];
				array[i + 1] = temp;
				didSort = 1;
			}
		}
		j++; // counter for highest value per iteration.
		// shortens loop. ("Enhanced Bubblesort")
	} while (didSort != 0);
}

// standard binarysearch pattern
int binarySearch(struct pair * array, int size, int choice) {
	int low = 0, high = size;
	int mid = (low + high) / 2;
	while (low <= high) {
		if (array[mid].value < choice) {
			low = mid + 1;
			// outputs found info
		} else if (array[mid].value == choice) {
			printf("\nFound %d at position %d!\n",
				   array[mid].value,
				   mid);
			printf("\nPosition in file: %d!\n\n",
				   array[mid].index);
			return 1;
		} else {
			high = mid - 1;
		}
		mid = (low + high) / 2;
	}
	// outputs not found info
	printf("\nNot Found!\n\n");
	return 0;
}
