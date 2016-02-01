gcc -c -g utils.c -o ../libraries/src/utils.o
gcc -c -g encrypter.c -o ../libraries/src/encrypter.o
gcc -c -g decrypter.c -o ../libraries/src/decrypter.o
gcc -c -g hacker.c -o ../libraries/src/hacker.o
ar rcs ../libraries/libutils.a ../libraries/src/utils.o
ar rcs ../libraries/libencrypter.a ../libraries/src/encrypter.o
ar rcs ../libraries/libdecrypter.a ../libraries/src/decrypter.o
ar rcs ../libraries/libhacker.a ../libraries/src/hacker.o
gcc -L../libraries ../src/secretCoder.c -o ../bin/secretCoder -lencrypter -ldecrypter -lhacker -lutils
