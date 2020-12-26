#!/usr/bin/env python3

import socket
import string
import struct

# Constants
CONTROLLER_IP = "10.129.130.1"


class MySocket:
    def __init__(self, sock=None, host=None, port=None):
        if sock is None:
            self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            if host is not None:
                if port is None:
                    port = 9000
                self.connect(host, port)
        else:
            self.sock = sock

    def connect(self, host, port):
        self.sock.connect((host, port))

    def _send(self, msg):
        totalsent = 0
        while totalsent < len(msg):
            sent = self.sock.send(msg[totalsent:])
            if sent == 0:
                raise RuntimeError("socket connection broken")
            totalsent = totalsent + sent

    def send(self, msg):
        return self._send(msg)

    def receiven(self, n):
        chunks = []
        bytes_recd = 0
        while bytes_recd < n:
            chunk = self.sock.recv(min(n - bytes_recd, 2048))
            if chunk == b"":
                raise RuntimeError("socket connection broken")
            chunks.append(chunk)
            bytes_recd = bytes_recd + len(chunk)
        return b"".join(chunks)

    def receive(self):
        szbytes = self.receiven(2)
        sz = struct.unpack(">H", szbytes)[0]
        data = self.receiven(sz)
        return data


def parse_pkt(frame):
    pkt_hdr_fmt = ">BBH"
    flags, msg, zeros = struct.unpack_from(pkt_hdr_fmt, frame)
    return (flags, msg, zeros), frame[struct.calcsize(pkt_hdr_fmt) :]


def make_pkt(flags, msg, zeros, content):
    pkt_hdr_fmt = ">BBH"
    return struct.pack(pkt_hdr_fmt, flags, msg, zeros) + content


def make_hello(nodetype, name):
    name = name.encode("utf-8")
    name = name[:31]
    name = name + b"\x00" * (32 - len(name))
    flags = 0
    msg = 0
    zeros = 0
    bunknown = struct.pack(">H", 0)
    btype = struct.pack(">B", nodetype)
    content = bunknown + btype + name
    pkt = make_pkt(flags, msg, zeros, content)
    return pkt


def make_frame(pkt):
    # frames have a 16 bit big-endian length followed by packet content
    frame = struct.pack(">H", len(pkt)) + pkt
    return frame


def chunker(seq, size):
    return (seq[pos : pos + size] for pos in range(0, len(seq), size))


def connect(host, port):
    s = MySocket(host=host, port=port)

    my_name = "terminal"
    terminal_type = 1
    frame = make_frame(make_hello(terminal_type, my_name))

    print("sending HELLO:")
    print("\n".join(chunker(frame.hex(), 16)))
    s.send(frame)

    print("RECVing HELLO...")
    pkt = s.receive()
    print("\n".join(chunker(pkt.hex(), 16)))

    (flags, msg, unknown_zeros), rest = parse_pkt(pkt)
    assert flags == 1
    assert msg == 0
    assert len(rest) == 35
    # nodetype seems to correspond to the the first argument to "router"
    unknown_maybe_random, nodetype, name = struct.unpack(">HB32s", rest)
    name = name.rstrip(b"\x00")
    print("connected to:", unknown_maybe_random, nodetype, name)
    return s


def main():
    s = connect(CONTROLLER_IP, 9000)

    # Send the PEER frame
    frame = make_frame(make_pkt(0, 1, 0, b""))
    print("sending PEER:")
    print("\n".join(chunker(frame.hex(), 16)))
    s.send(frame)

    # Receive PEER response
    print("RECVing PEER...")
    pkt = s.receive()
    print("\n".join(chunker(pkt.hex(), 16)))
    (flags, msg, unknown_zeros), rest = parse_pkt(pkt)

    # Parse and print the hostnames
    hostname = ""
    for c in rest:
        c = chr(c)
        if c in string.printable:
            hostname += c
        elif len(hostname) > 1:
            print(hostname)
            hostname = ""


if __name__ == "__main__":
    main()
