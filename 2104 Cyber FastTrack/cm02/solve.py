from string import ascii_uppercase

# Read the file
with open("cm02.txt", "r") as f:
    data = f.read()

# Convert emojis to uppercase letters
tbl = {}
res = ""
i = 0
for c in data:
    n = ord(c)
    if n < 300:
        res += c
        continue
    if n not in tbl:
        tbl[n] = ascii_uppercase[i]
        i += 1
    res += tbl[n]

# Print new ciphertext
print(res.replace("\n", " "))

# Key from https://www.boxentriq.com/code-breaking/cryptogram
key = "whatiselcryofumndgpbvkjqxz"

# Decrypt and print
plain = ""
for c in res:
    if c in ascii_uppercase:
        plain += key[ascii_uppercase.index(c)]
    else:
        plain += c
print(plain)