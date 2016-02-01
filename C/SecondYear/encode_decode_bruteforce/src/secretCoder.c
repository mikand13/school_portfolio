#include <stdio.h>
#include <stdlib.h>

char statusMessage[200];

// main is a menu method
int main (int argc, char *argv[]) {
  int status = 0;
  if (argv[1] != NULL
          && (strcmp(argv[1], "-e")
          == 0 || strcmp(argv[1], "-d")
          == 0 || strcmp(argv[1], "-h")
          == 0)) {
    if (argv[2] != NULL && argv[3] != NULL && argv[4] != NULL) {
      if (strcmp(argv[1], "-e") == 0) {
        // check for distance, defaults to 0.
        if (argc == 6) {
          status = encryptFile("%t %k %o %d", argv[3], argv[2], argv[4],
                  atoi(argv[5]));
        } else {
          status = encryptFile("%t %k %o", argv[3], argv[2], argv[4]);
        }
      } else if (strcmp(argv[1], "-d") == 0) {
        status = decryptFile(argv[3], argv[2], argv[4]);
      } else if (strcmp(argv[1], "-h") == 0) {
        if (argv[5] != NULL) {
          status = hackFile(argv[2], argv[3], argv[4], argv[5]);
        } else {
          printf("\nCorrect format is \"assignment2 -h <(targetfile).txt>");
          printf(" <(outputFile).txt> <keyDir> <DictionaryDir>\"\n\n");
          status = 0;
        }
      }
    } else {
      printf("\nCorrect format is \"assignment2 -[edh] "
              "<(keyfile).txt)|(targetFile).txt>");
      printf(" <(targetfile).txt|(outputFile).txt> "
              "<(outputFile).txt|keyDir> [distance|<dictionaryDir>]\"\n\n");
      status = 0;
    }
  } else {
    printf("\nNot recognized control. \"-[edh]\"\n\n");
    status = 0;
  }

  if (statusMessage != NULL) {
    printf("%s", statusMessage);
  }

  return status;
}

