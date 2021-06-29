// gcc solve.c

#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    // Read all bytes from ./data
    FILE *infile;
    char *buffer;
    long numbytes;
    infile = fopen("data", "r");
    fseek(infile, 0L, SEEK_END);
    numbytes = ftell(infile);
    fseek(infile, 0L, SEEK_SET);
    buffer = (char*)calloc(numbytes, sizeof(char));	
    fread(buffer, sizeof(char), numbytes, infile);
    fclose(infile);

    // Calculate the checksum
    int16_t checksum = 0x1d0f;
    for (int i = 0; i < numbytes; i++) {
        char c = buffer[i];
        checksum ^= c << 8;
        for (int y = 0; y <= 7; ++y) {
            if (checksum >=0) {
                checksum *= 2;
            } else {
                checksum = (2 * checksum) ^ 0xa02b;
            }
        }
    }

    // Print checksum as integer to stdout
    printf("%d", checksum);
}
