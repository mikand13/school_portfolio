#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <dirent.h>

FILE* openFile(FILE* file, char* name, char* type);
int buildKeyFile(char*, FILE*);

int decryptFile(char* decryptFile, char* key, char* outputFile);
char buildCharFromFile(FILE* decryptText, int setCap, char* keyContents);

char statusMessage[200];

int decryptFile(char* decryptFile, char* key, char* outputFile) {
  int exit = 1;
  FILE *keyFile = NULL;
  FILE *decryptText = NULL;

  decryptText = openFile(decryptText, decryptFile, "r");
  keyFile = openFile(keyFile, key, "r");

  if (decryptText == NULL || keyFile == NULL) {
    strcat(statusMessage, "\nDecryption failed!\n\n");
    exit = 0;
  }

  if (exit) {
    char *keyContents = NULL;

    keyContents = malloc(10 * sizeof(char *));
    buildKeyFile(keyContents, keyFile);

    int i = 0, c = 0, textSize = 10, textCounter = 0;
    char **text = calloc(textSize, sizeof(char *));

    while ((c = fgetc(decryptText)) != EOF) {
      if ((c >= 65 && c <= 90)
              || (c >= 97 && c <= 122)) {
        sprintf(statusMessage,
                "\nThis file does not seem to be encrypted."
                        " Aborting output... (%s)\n\n",
                outputFile);
        exit = 0;
        break;
      }

      if (textCounter > textSize) {
        int newSize = textSize + 10;
        text = realloc(text, newSize * sizeof(char *));
        textSize = newSize;
      }

      int addRandom = 0;
      if (!isspace(c)) {
        //check for word
        if (c == '[') {
          c = fgetc(decryptText);
          //add front [
          if (c == '[') {
            addRandom = 1;
          } else {
            char *word = malloc(200 * sizeof(char));
            memset(word, 0, 200 * sizeof(char));
            int wordCount = 0, setCap = 0;

            //check for cap
            if (c == '-') {
              setCap = 1;
            }
            ungetc(c, decryptText);
            // builds word
            do {
              if (wordCount > 200) {
                sprintf(statusMessage,
                        "\nToo large word encountered. Overflow prevented. (%s)"
                                "\n\n",
                        outputFile);
                exit = 0;
                break;
              }
              // start of letter and start of word
              if (c == '[' || wordCount == 0) {
                word[wordCount++] = buildCharFromFile(decryptText, setCap,
                        keyContents);
                setCap = 0;
                // end of letter
              } else if (c == ']') {
                c = fgetc(decryptText);
                // check for trailing ]
                if (c == ']') {
                  break;
                }
                ungetc(c, decryptText);
              } else if (c != '-' && !(c >= 48 && c <= 57)) {
                ungetc(c, decryptText);
                break;
              }
            } while ((c = fgetc(decryptText)) != EOF);

            text[textCounter] = malloc(wordCount * sizeof(char));
            text[textCounter++] = strdup(word);
            free(word);
            word = NULL;

            // add trailing ]
            if (c == ']') {
              addRandom = 1;
            }
          }
        } else {
          addRandom = 1;
        }
      } else {
        addRandom = 1;
      }

      if (addRandom) {
        text[textCounter] = malloc(sizeof(char *));
        memset(text[textCounter], 0, sizeof(char *));
        *text[textCounter++] = c;
        addRandom = 0;
      }
    }

    textSize = textCounter;
    text = realloc(text, textSize * sizeof(char *));

    if (exit) {
      // output decryption and free text
      FILE *output = NULL;
      output = openFile(output, outputFile, "w");

      if (output == NULL) {
        strcat(statusMessage, "\nDecryption failed!\n\n");
        exit = 0;
      }

      if (exit) {
        for (i = 0; i < textSize; i++) {
          fprintf(output, "%s", text[i]);
        }

        sprintf(statusMessage,
                "\nDecryption completed (%s)!\n\n",
                outputFile);

        fclose(output);
        output = NULL;
      }
    }

    for (i = 0; i < textSize; i++) {
      free(text[i]);
      text[i] = NULL;
    }

    free(text);
    text = NULL;
    free(keyContents);
    keyContents = NULL;
  }

  if (decryptText != NULL) {
    fclose(decryptText);
    decryptText = NULL;
  }

  if (keyFile != NULL) {
    fclose(keyFile);
    keyFile = NULL;
  }

  return exit;
}

char buildCharFromFile(FILE* decryptText, int setCap, char* keyContents) {
  int temp = 0;
  fscanf(decryptText, "%d", &temp);
  // set cap if necessary
  return (keyContents[abs(temp)] - ((setCap == 1) == 1) * 32);
}
