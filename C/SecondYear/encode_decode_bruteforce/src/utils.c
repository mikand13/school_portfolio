#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

FILE* openFile(FILE* file, char* name, char* type);
int buildKeyFile(char*, FILE*);

char statusMessage[200];

// attempts to open file and returns it, makes status and returns NULL on fail
FILE* openFile(FILE* file, char* name, char* type) {
  file = fopen(name, type);
  if (file == NULL) {
    sprintf(statusMessage, "\nNot able to open %s. Exiting...\n", name);
    // returns NULL for check in calling method
    return NULL;
  }
  return file;
}

// parses keyfile and reduces it to only lower-case alpha with no whitespace
int buildKeyFile(char* key, FILE* keyFile) {
  int keySize = 10;
  int keyCounter = 0, c = 0;

  while ((c = fgetc(keyFile)) != EOF) {
    if (keyCounter > keySize) {
      int newSize = keySize + 10;
      key = realloc(key, newSize * sizeof(char *));
      keySize = newSize;
    } else if (!isspace(c) && c != '\'' && c != '.') {
      // if alpha convert
      if (isalpha(c)) {
        if (c <= 90) {
          c += 32;
        }
        key[keyCounter++] = (char) c;
      }
    }
  }

  keySize = keyCounter;
  key = realloc(key, keyCounter * sizeof(char *));
  return keySize;
}

// standard binarysearch
int binarySearch(char** array, int size, char* choice) {
  int low = 0, high = size - 1;
  int mid = (low + high) / 2;

  while (low <= high) {
    if (strcmp(array[mid], choice) < 0) {
      low = mid + 1;
    } else if (strcmp(array[mid], choice) == 0) {
      return 1;
    } else {
      high = mid - 1;
    }
    mid = (low + high) / 2;
  }

  return 0;
}