from pwn import *

r = remote('rev.chal.csaw.io', 9003)
answers = ['0x00', '0x00', '0x0000', '0x0e74', '0x0e61']

data = r.recvrepeat(1)
print(data)

for answer in answers:
	r.sendline(answer)
	data = r.recvline()
	print(data)

r.interactive()
