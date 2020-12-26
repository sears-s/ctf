#!/usr/bin/env python3

import struct

from .hello import connect, make_frame, make_pkt, parse_pkt

# Constants
CONTROLLER_IP = "10.129.130.1"
CONTROLLER_PORT = 9000


def main():
    s = connect(CONTROLLER_IP, CONTROLLER_PORT)

    # Send the PEERS frame
    peers_frame = make_frame(make_pkt(0, 1, 0, b""))
    s.send(peers_frame)

    # Receive PEERS response
    peers_resp = s.receive()
    (_, _, _), content = parse_pkt(peers_resp)

    # Parse the PEERS response
    for addr, nodetype, name in struct.iter_unpack(">HB32s", content):
        name = name.rstrip(b"\x00").decode()
        print(f"Hostname = {name}, Type = {nodetype}, Address = {addr}")


if __name__ == "__main__":
    main()
