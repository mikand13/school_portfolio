/*
    This lib wil encode and decode messages for you based on a .txt file as key.
    Only demand on the .txt file is it must include at least one of every
    english alphabetical character.

    Distance is optional.

    You can also attempt to hack the file by suppling a key folder, and a
    dictionary folder.
	
    Compiled like so:
    gcc <source>.c -o <applicationName> -lencrypter -ldecrypter -lhacker -lutils

    @version 1.0
    @author Anders Mikkelsen
 */

char statusMessage[200];

/*
    This global stores any additional message produced by the program for status
    of method execution.

    This will have to be declared in your application if you want to extract
    this information.
 */

int encryptFile(char*, ...);

/*
    This form needs as a minimum target file, key file and output file.
    In that order.
    returns status. 1 is ok, 0 is problem. Outputs result to output file.

    example: int status = encryptFile("%t %k %o %d",
                                      "inputText.txt",
                                      "./songLibrary/allThatSheWants.txt",
                                      "encrypted.txt",
                                      1);
    (Distance set to 1)
    example: int status = encryptFile("%t %k %o",
                                      "inputText.txt",
                                      "./songLibrary/allThatSheWants.txt",
                                      "encrypted.txt");
    (No Distance)
*/

int decryptFile(char* decryptFile, char* key, char* outputFile);

/*
    This form needs as a minimum target file, key file and output file.
    In that order.
    returns status. 1 is ok, 0 is problem. Outputs result to output file.

    example: int status = decryptFile("encrypted.txt",
                                      "./songLibrary/allThatSheWants.txt",
                                      "decrypted.txt");
*/

int hackFile(char* targetFile, char* outputFile, char* keyLib, char* dictName);

/*
    This form does a brute force attack on the file with keys and compares the
    output to the dictionary.
    it then prints out the most probable result to the output file.
    returns status. 1 is ok, 0 is problem. Outputs result to output file.

	  Third argument is the directory for your key files.
	  Fourth argument is your dictionary file.
	
    example: int status = hackFile("encrypted.txt",
                                   "hacked.txt",
                                   "./songLibrary/",
                                   "/usr/share/dict/words");
 */
