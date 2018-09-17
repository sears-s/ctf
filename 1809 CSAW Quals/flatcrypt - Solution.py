from pwn import *
import string

r = remote('crypto.chal.csaw.io', 8040)

buf = "ABCDEFGHIJKLMNOPQRST"
possible = string.lowercase + "_"
ans = ""

def randstr():
	return "".join(random.choice(string.ascii_uppercase + string.digits) for _ in range(10))

def mode(l):
	c = collections.Counter(l)
	return c.most_common(1)[0][0]

def try_value(val):
	print(val)
	#if len(val) > 3 and val[:2] in val[2:]:
	#	return
	min_l = 9999
	min_c = "A"
	samples = {}
	for c in possible:
		r.recvline()
		guess = c + val
		guess += guess*10 + buf
		r.sendline(guess)
		samples[c] = ord(r.recvline()[-2])
	print(samples)
	m = mode(samples.values())
	for k in samples:
		if samples[k] == m:
			continue
		try_value(k + val)

try_value("doesnt_have_a_logo")
