#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <dirent.h>

FILE* openFile(FILE* file, char* name, char* type);

int decryptFile(char* decryptFile, char* key, char* outputFile);

int hackFile(char* targetFile, char* outputFile, char* keyLib, char* dictName);
int buildWord(char* word, char* hackText, int hackTextCounter,
        char* keyContents, int* exit);
char buildChar(char* hackText, int* textCounter, int setCap, char* keyContents);
int binarySearch(char** array, int size, char* choice);

char statusMessage[200];

typedef struct KeyMatches {
  int matches;
  char *key;
  int active;
} KeyMatches ;

int hackFile(char* targetFile, char* outputFile, char* keyLib, char* dictName) {
  int e = 1;
  int *exit = &e;

  FILE *dict = NULL, *decryptText = NULL;
  DIR *songDir = NULL;

  struct dirent *dir = NULL;

  dict = openFile(dict, dictName, "r");
  decryptText = openFile(decryptText, targetFile, "r");
  songDir = opendir(keyLib);

  // ends execution if files not openable
  if (dict == NULL || !songDir || decryptText == NULL) {
    strcat(statusMessage, "\nHacking failed!\n\n");
    *exit = 0;
  }

  // checks for further running
  if (*exit) {
    int i = 0, d = 0, done = 0;
    char *c = malloc(100 * sizeof(char *));

    int dictSize = 100, dictCount = 0;
    char **dictionary = malloc(dictSize * sizeof(char *));

    int possibleKeysSize = 100, keyCount = 0;
    KeyMatches **possibleKeys = malloc(possibleKeysSize * sizeof(KeyMatches *));

    int hackTextSize = 10, hackCounter = 0;
    char *hackText = malloc(hackTextSize * sizeof(char *));

    // builds list of keyfiles and dictionary
    int dictDone = 0, dirDone = 0, hackTextDone = 0;

    while (!done) {
      if (fscanf(dict, "%s", c) != EOF) {
        if (dictCount > dictSize) {
          int newSize = (dictSize += 100);

          dictionary = realloc(dictionary, newSize * sizeof(char *));
          dictSize = newSize;
        }

        dictionary[dictCount] = malloc(strlen(c) * sizeof(char *));
        dictionary[dictCount++] = strdup(c);
      } else {
        if (dictDone == 0) {
          dictSize = dictCount;
          dictionary = realloc(dictionary, dictSize * sizeof(char *));

          dictDone = 1;
        }
      }

      if ((dir = readdir(songDir)) != NULL) {
        if (keyCount > possibleKeysSize) {
          int newSize = (possibleKeysSize += 100);

          possibleKeys = realloc(possibleKeys, newSize * sizeof(KeyMatches *));
          possibleKeysSize = newSize;
        }

        if (dir != NULL && strcmp(dir->d_name, ".") !=
                0 && strcmp(dir->d_name, "..") != 0) {
          char *temp = malloc(200 * sizeof(char *));
          memset(temp, 0, 200);
          strcat(temp, keyLib);
          strcat(temp, dir->d_name);

          possibleKeys[keyCount] = malloc(strlen(temp) * sizeof(KeyMatches *));
          possibleKeys[keyCount]->key = strdup(temp);
          possibleKeys[keyCount]->matches = 0;
          possibleKeys[keyCount++]->active = 1;

          free(temp);
          temp = NULL;
        }

      } else {
        if (dirDone == 0) {
          possibleKeysSize = keyCount;
          possibleKeys =
                  realloc(possibleKeys,
                          possibleKeysSize * sizeof(KeyMatches *));
          dirDone = 1;
        }
      }

      if ((d = fgetc(decryptText)) != EOF) {
        if (hackCounter > hackTextSize) {
          int newSize = hackTextSize + 10;

          hackText = realloc(hackText, newSize * sizeof(char *));
          hackTextSize = newSize;
        }

        hackText[hackCounter++] = (char) d;
      } else {
        if (hackTextDone == 0) {
          hackTextSize = hackCounter;
          hackText = realloc(hackText, hackTextSize * sizeof(char *));

          hackTextDone = 1;
        }
      }

      if (dirDone == 1 && dictDone == 1 && hackTextDone == 1) {
        done = 1;
      }
    }

    fclose(dict);
    dict = NULL;
    closedir(songDir);
    songDir = NULL;
    fclose(decryptText);
    decryptText = NULL;

    // fills key array
    int keyCounter = 0, e;
    char **allKeys = malloc(possibleKeysSize * sizeof(char *));

    do {
      FILE *tempKeyFile =
              openFile(tempKeyFile, possibleKeys[keyCounter]->key, "r");
      int keys = 10, counter = 0;
      allKeys[keyCounter] = malloc(keys * sizeof(char *));

      // adds values from key
      while ((e = fgetc(tempKeyFile)) != EOF) {
        if (counter > keys) {
          int newSize = keys + 10;

          allKeys[keyCounter] =
                  realloc(allKeys[keyCounter], newSize * sizeof(char *));
          keys = newSize;
        } else if (!isspace(e) && e != '\'' && e != '.') {
          // if alpha convert
          if (isalpha(e)) {
            if (e <= 90) {
              e += 32;
            }

            allKeys[keyCounter][counter++] = (char) e;
          }
        }
      }

      // triming
      allKeys[keyCounter] =
              realloc(allKeys[keyCounter], counter * sizeof(char *));
      keyCounter++;
      fclose(tempKeyFile);
    } while (keyCounter < possibleKeysSize);
    // dictionary, keylist and encrypted file loaded

    // compare decrypted word to dictionary. Keys are checked breadth first.
    int wordCount = 0, hackTextCounter = 0;
    int max = 0, wordChecked = 0;

    char *mostPossibleKey = malloc(200 * sizeof(char *));

    while (hackTextCounter < hackTextSize) {
      int addRandom = 0;
      if (!isspace(hackText[hackTextCounter])) {
        //check for word
        if (hackText[hackTextCounter] == '[') {
          hackTextCounter++;

          //check for [ in text
          if (hackText[hackTextCounter] == '[') {
            addRandom = 1;

          } else {
            // process encrypted word
            int newCounter = 0;
            int keysChecked = 0;

            for (i = 0; i < possibleKeysSize; i++) {
              // check against every key
              if (possibleKeys[i]->active == 1) {
                char *word = malloc(200 * sizeof(char *));
                newCounter = buildWord(word, hackText, hackTextCounter,
                        allKeys[i], exit);

                if (*exit == 0) {
                  free(word);
                  word = NULL;
                  break;
                }

                // binary search for words that match key
                if (binarySearch(dictionary, dictSize, word)) {
                  possibleKeys[i]->matches++;
                }

                if (possibleKeys[i]->matches > max) {
                  max = possibleKeys[i]->matches;
                  mostPossibleKey = possibleKeys[i]->key;
                }

                // discard key if too little matches
                if (wordCount % 10 == 0 && possibleKeys[i]->matches <
                        (wordCount / 2)) {
                  possibleKeys[i]->active = 0;
                }

                free(word);
                word = NULL;
                keysChecked++;
              }
            }

            // if only a single key is checked, break and decrypt with key.
            // if no key is checked, end program.
            if (keysChecked == 1) {
              break;
            } else if (keysChecked == 0) {
              sprintf(statusMessage, "%s", "\nNo suitable key found!\n\n");
              *exit = 0;
              break;
            }

            wordCount++;
            hackTextCounter = newCounter;

            if (hackText[hackTextCounter] == ']') {
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
        hackTextCounter++;
      }
    }

    int status = 0;

    // do decrypt if no errors, and output with message
    if (*exit) {
      status = decryptFile(targetFile, mostPossibleKey, outputFile);
    }

    if (status) {
      sprintf(statusMessage, "\nStats:\n\nWORDCOUNT: %d \nKEY: %s\n"
                      "\nHack completed (Most probable match: %s)!\n\n",
              max,
              mostPossibleKey,
              outputFile);
      *exit = 1;
    } else if (*exit == 1) {
      char* temp;
      sprintf(temp, "%s", "\nHack failed!\n\n");
      strcat(statusMessage, temp);
      *exit = 0;
    }

    // freeing below
    for (i = 0; i < dictSize; i++) {
      free(dictionary[i]);
      dictionary[i] = NULL;
    }
    free(dictionary);
    dictionary = NULL;

    for (i = 0; i < possibleKeysSize; i++) {
      if (possibleKeys[i] != NULL) {
        free(possibleKeys[i]->key);
        possibleKeys[i]->key = NULL;
      }
      free(possibleKeys[i]);
    }
    free(possibleKeys);
    possibleKeys = NULL;

    for (i = 0; i < possibleKeysSize; i++) {
      free(allKeys[i]);
      allKeys[i] = NULL;
    }
    free(allKeys);
    allKeys = NULL;

    free(hackText);
    hackText = NULL;

    free(c);
    c = NULL;
    free(mostPossibleKey);
    mostPossibleKey = NULL;
  }

  if (dict != NULL) {
    fclose(dict);
    dict = NULL;
  }

  if (songDir != NULL) {
    closedir(songDir);
    songDir = NULL;
  }

  if (decryptText != NULL) {
    fclose(decryptText);
    decryptText = NULL;
  }

  return *exit;
}

int buildWord(char* word, char* hackText, int hackTextCounter,
        char* keyContents, int* exit) {
  memset(word, 0, 200 * sizeof(char *));
  int wordCount = 0, setCap = 0;

  // check for cap
  if (hackText[hackTextCounter] == '-') {
    hackTextCounter++;
    setCap = 1;
  }
  // builds word

  do {
    if (wordCount > 200) {
      sprintf(statusMessage,
              "\nToo large word encountered. Overflow prevented.\n\n");
      *exit = 0;
      break;
    }

    word[wordCount++] = buildChar(hackText, &hackTextCounter,
            setCap, keyContents);

    if (wordCount == 1) {
      setCap = 0;
    }
    // end of letter

    if (hackText[hackTextCounter] == ']') {
      hackTextCounter++;
      // check for trailing ]

      if (hackText[hackTextCounter] == ']') {
        break;
      } else if (hackText[hackTextCounter] == '[') {
        hackTextCounter++;
      } else {
        break;
      }
    }
  } while (1);

  return hackTextCounter;
}

char buildChar(char* text, int* textSize, int setCap, char* keyContents) {
  int counter = 0, determinedLength = 0;

  while (determinedLength == 0) {
    if ((text[*textSize + counter] >= 48
            && text[*textSize + counter] <= 57)) {
      counter++;
    } else {
      determinedLength = 1;
    }
  }

  int i = 0, c = 0;
  if (counter > 1) {
    for (i = counter; i > 0; i--) {

      int j, pow = 1;
      for (j = 1; j < i; j++) {
        pow *= 10;
      }

      if (i == 1) {
        c += (text[*textSize + (counter - 1)] - 48);
      } else {
        c += ((text[*textSize + (counter - i)] - 48) * pow);
      }
    }
  } else {
    c = (text[*textSize] - 48);
  }

  *textSize += counter;

  int keyLength = strlen(keyContents);

  // set cap if necessary
  if (c < keyLength) {
    return (keyContents[c] - ((setCap == 1) * 32));
  } else {
    // insert random a if key too short
    return 97;
  }
}