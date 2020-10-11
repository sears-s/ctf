#!/usr/bin/env python3

from Crypto.Cipher import AES

FILES = ["20200628_153027.log", "20200630_161219.log", "20200723_183021.log", "20200920_154004.log"]
MAX_LATITUDE = 9000
PADDING = len(str(MAX_LATITUDE))
BLOCK_SIZE = 16
ENCODING = "utf-8"

for filename in FILES:
	# Read the file
	with open("logs/" + filename, "rb") as f:
		data = f.read()

	# Seperate IV and ciphertext
	iv = data[:BLOCK_SIZE]
	ciphertext = data[BLOCK_SIZE:]
	
	# Make key for each latitude
	for i in range(MAX_LATITUDE + 1):
		key = (str(i).zfill(PADDING) * 4).encode(ENCODING)
		cipher = AES.new(key, AES.MODE_CBC, iv)
		
		# Attempt to decrypt
		try:
			plaintext = cipher.decrypt(ciphertext).decode(ENCODING)
		except:
			continue
		print(f"Found key {key} for file {filename}")

		# Write plaintext to file
		with open(filename + ".dec", "w") as f:
			f.write(plaintext)
		break
