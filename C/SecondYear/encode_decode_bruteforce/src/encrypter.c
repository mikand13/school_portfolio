#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <ctype.h>
#include <string.h>

FILE* openFile(FILE* file, char* name, char* type);
int buildKeyFile(char*, FILE*);

int encryptFile(char*, ...);
int* encryptChar(char, int*, int, char*, int, int*);
int encrypt(char*, int, int*, int);

char statusMessage[200];

int encryptFile(char* argv, ...) {
  int exit = 1;

  // checks validity of arguments
  if (!argv ||
          !((strcmp(argv, "%t %k %o %d") == 0) ||
                  (strcmp(argv, "%t %k %o") == 0))) {
    sprintf(statusMessage,
            "\nIncorrect arguments, please supply in this format: (%s)!\n\n",
            "\"%t %k %o %d\" or \"%t %k %o\"");
    exit = 0;
  }

  // check further running
  if (exit) {
    FILE *keyFile = NULL, *encryptText = NULL;
    int d = 0;

    // set files based on varargs
    va_list vaList;
    va_start(vaList, argv);

    int args = (strlen(argv) / 3) + 1;

    char *outputFile = NULL;
    encryptText = openFile(encryptText, va_arg(vaList, char*), "r");
    keyFile = openFile(keyFile, va_arg(vaList, char*), "r");
    outputFile = va_arg(vaList, char*);

    if (args == 4)
      d = va_arg(vaList, int);

    va_end(vaList);

    // ends execution if files not openable
    if (encryptText == NULL || keyFile == NULL) {
      strcat(statusMessage, "\nEncryption failed!\n\n");
      exit = 0;
    }

    if (exit) {
      char *keyContents = NULL;
      int keySize = 0;

      keyContents = malloc(10 * sizeof(char *));
      keySize = buildKeyFile(keyContents, keyFile);

      int textSize = 10, previous = 0;
      int length = 0, textCounter = 0, c = 0;
      int *intLength = &length;
      int *tempIntArray = NULL;
      int *previousChar = &previous;
      char *text = malloc(textSize * sizeof(char *));

      // build encrypted file, end encryption if unable to satisfy d
      int charsEncoded = 0;
      while ((c = fgetc(encryptText)) != EOF) {
        if (textCounter > textSize) {
          int newSize = textSize + 10;
          text = realloc(text, newSize * sizeof(char *));
          textSize = newSize;
        }

        if (isalpha(c)) { // ignore non english alpha
          text[textCounter++] = '[';
          if (c <= 90) {
            text[textCounter++] = '-';
            c += 32;
          }

          // passes char for encryption
          tempIntArray = encryptChar((char) c, previousChar, d, keyContents,
                  keySize, intLength);
          charsEncoded++;

          // d check
          if (tempIntArray == NULL) {
            exit = 0;
            break;
          }

          textCounter = encrypt(text, textCounter, tempIntArray, *intLength);
          text[textCounter++] = ']';
        } else {
          if (c == '[') {
            text[textCounter++] = '[';
          }
          text[textCounter++] = (char) c;
        }
      }

      textSize = textCounter;
      text = realloc(text, textSize * sizeof(char *));

      // check if file already encrypted
      if (charsEncoded == 0) {
        sprintf(statusMessage,
                "\nThis file seems like its already encrypted."
                        " Aborting output... (%s)\n\n",
                outputFile);
        exit = 0;
      }

      if (exit) {
        // write to file when encryption sucessfully done
        FILE *output = NULL;
        output = openFile(output, outputFile, "w");

        if (output == NULL) {
          strcat(statusMessage, "\nEncryption failed!\n\n");
          exit = 0;
        }

        if (exit) {
          int i;
          for (i = 0; i < textCounter; i++) {
            fprintf(output, "%c", text[i]);
          }

          sprintf(statusMessage,
                  "\n** WARNING - ALL NON-ENGLISH ALPHA WONT BE ENCRYPTED **\n\n"
                          "\tEncryption completed (%s)!\n\n",
                  outputFile);
          fclose(output);
          output = NULL;
        }
      }

      //might be null
      if (!tempIntArray) {
        free(tempIntArray);
        tempIntArray = NULL;
      }

      free(keyContents);
      keyContents = NULL;
      free(text);
      text = NULL;
    }

    if (keyFile != NULL) {
      fclose(keyFile);
      keyFile = NULL;
    }

    if (encryptText != NULL) {
      fclose(encryptText);
      encryptText = NULL;
    }
  }

  return exit;
}

// inserts encrypted number pos in text array.
int encrypt(char *text, int counter, int *pInt, int intLength) {
  int i = 0;

  for (i = intLength - 1; i >= 0; i--) {
    text[counter++] = (char) (pInt[i] + 48);
  }

  return counter;
}

// encrypts a single english alpha char. Also verifies key validity, and d condition.
int* encryptChar(char c, int* previousChar, int d, char* key, int keySize,
        int* intLength) {
  int *intArray = malloc(10 * sizeof(int *));
  int i = 0, intCounter = 0, keyExists = 0;

  for (i = 0; i < keySize; i++) {
    // check d condition
    if ((key[i] == c &&
            d == 0) ||
            (key[i] == c &&
                    (((i - d) > *previousChar) ||
                            (i + d) < *previousChar))) {
      *previousChar = i;

      do {
        // builds number from singlenums
        if (i /= 10 != 0) {
          intArray[intCounter] = i % 10;
        } else {
          intArray[intCounter] = i;
        }
        intCounter++;
        i /= 10;
      } while (i != 0);

      intArray = realloc(intArray, intCounter * sizeof(int *));
      *intLength = intCounter;

      return intArray;
    } else if (key[i] == c) {
      keyExists = 1;
    }
  }

  if (keyExists) {
    sprintf(statusMessage,
            "%s",
            "\nNot able to satisify d criteria. Check file length. Exiting..."
                    "\n\n");
  } else {
    sprintf(statusMessage,
            "\nThis key is unsuitable for encryption. Verify validity.\n\n");
  }

  *intLength = intCounter;
  free(intArray);
  intArray = NULL;

  return NULL;
}