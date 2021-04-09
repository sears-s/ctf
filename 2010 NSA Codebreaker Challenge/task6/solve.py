from itertools import combinations
from struct import iter_unpack

import numpy as np

# Constants
INPUT_FILENAME = "signal.ham"
OUTPUT_FILENAME = "output.avi"
SKIP = 5000

## Read floats from file into bits ##
print("Reading floats from file into bits...")

# Read the file
with open(INPUT_FILENAME, "rb") as f:
    data = f.read()

# Get the bits from the floats
bits = []
for x in iter_unpack("<e", data):
    if x[0] > 0:
        bits.append(1)
    else:
        bits.append(0)

## Count proportion of ones for different codeword lengths ##
print("Counting proportion of ones for different codeword lengths...")

# Iterate over possible codeword lengths
for codeword_len in range(3, 30):

    # Iterate over each bit position in the codeword
    for bit_pos in range(codeword_len):
        num_ones = 0
        total = 0

        # Count proportion of ones
        for i in range(bit_pos, len(bits), codeword_len * SKIP):
            if bits[i] == 1:
                num_ones += 1
            total += 1
        prop_ones = num_ones / total

        # Print info when proportion of ones is much different than 0.5
        if abs(prop_ones - 0.5) > 0.2:
            print(
                f"codeword len = {codeword_len}, bit_pos = {bit_pos}, ones % = {prop_ones * 100}"
            )

# Can now assume codeword length is 17
codeword_len = 17

## Count proportion of ones at found codeword length (17) ##
print("Counting proportion of ones at found codeword length (17)...")

# Iterate over each bit position in found codeword length
for bit_pos in range(codeword_len):
    num_ones = 0
    total = 0

    # Count proportion of ones
    for i in range(bit_pos, len(bits), codeword_len):
        if bits[i] == 1:
            num_ones += 1
        total += 1
    prop_ones = num_ones / total

    # Print percentage of ones at every bit position
    print(f"bit pos = {bit_pos}, ones % = {prop_ones * 100}")

# Can now assume first 11 bits are data bits
num_data_bits = 11

## Bruteforce parity check bits configuration ##
print("Bruteforcing parity check bits configuration...")

# Iterate over possible parity check bits
data_bits = list(range(num_data_bits))
for check_bit in range(num_data_bits, codeword_len - 1):

    # Iterate over number of bits to take from data bits
    for num_take in range(2, num_data_bits):

        # Create combinations to take from data bits
        for comb in combinations(data_bits, num_take):
            correct = 0
            total = 0

            # Iterate over the codewords
            for i in range(0, len(bits), codeword_len * SKIP):

                # Get codeword and parity check bit
                codeword = bits[i : i + codeword_len]
                check_bit_value = codeword[check_bit]

                # Calculate the parity
                bits_sum = 0
                for j in comb:
                    bits_sum += codeword[j]

                # Check if check bit is correct
                if bits_sum % 2 == check_bit_value:
                    correct += 1
                total += 1

            # Print info when high proportion are correct
            prop_correct = correct / total
            if prop_correct > 0.9:
                print(
                    f"check bit pos {check_bit}, # data bits = {num_take}, data bits = {comb}, % correct {prop_correct * 100}"
                )

# Can now infer parity check matrix
parity_check_matrix = np.array(
    [
        [0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0],
        [1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0, 0, 0],
        [1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 0, 1, 0, 0],
        [0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0],
        [1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1],
    ]
)

## Decode bits using parity check matrix ##
print("Decoding bits using parity check matrix...")

# Iterate over codewords
decoded_bits = []
for i in range(0, len(bits), codeword_len):

    # Get codeword and calculate syndrome
    codeword = bits[i : i + codeword_len - 1]
    syndrome = np.matmul(parity_check_matrix, codeword) % 2

    # Correct error for non-zero syndrome
    if sum(syndrome) > 0:
        for j in range(codeword_len - 1):
            if np.array_equal(syndrome, parity_check_matrix.T[j]):
                codeword[j] = (codeword[j] + 1) % 2
                break

    # Add decoded bits
    decoded_bits.extend(codeword[:num_data_bits])

## Write decoded bits to a file ##
print("Writing decoded bits to a file...")
with open(OUTPUT_FILENAME, "wb") as f:
    for c in np.packbits(decoded_bits):
        f.write(bytes([c]))
