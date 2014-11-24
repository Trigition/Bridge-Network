#Makefile for compiling C++ Code

CC=g++
CFLAGS=-std=c++11

all: connection main cleanup

main: main.cpp connection.o
	$(CC) $(CFLAGS) main.cpp -o bridge

connection: connection.h connection.cpp
	$(CC) $(CFLAGS) -c connection.cpp -o connection.o

cleanup:
	rm -r *.dSYM
