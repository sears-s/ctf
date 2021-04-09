#!/usr/bin/env python3

import socket
import struct

# Constants
VERBOSE = True
CONTROLLER_IP = "10.129.130.1"
CONTROLLER_PORT = 9000
MY_ADDR = 10
MY_NODE_TYPE = 1
MY_HOSTNAME = "terminal"
POWEROFF_PKT_FILENAME = "poweroff_pkt"


def main():
    # Connect to the controller via HELLO
    s, nodes = connect(
        CONTROLLER_IP, CONTROLLER_PORT, MY_ADDR, MY_NODE_TYPE, MY_HOSTNAME
    )
    controller_addr, _, _ = nodes[0]

    ## Task 7 ##
    print("Starting Task 7...")

    # Send PEERS packet
    peers_pkt = make_peers_pkt()
    if VERBOSE:
        print("Sending PEERS...")
    s.send(peers_pkt)

    # Receive PEERS response
    if VERBOSE:
        print("Receiving PEERS...")
    peers_resp = s.receive()
    drones = parse_pkt(peers_resp)

    ## Task 8 ##
    print("Starting Task 8...")

    # Send routed PEERS packet to each drone router
    routing_addrs = [MY_ADDR, controller_addr]
    for drone_addr, _, _ in drones:

        # Send a routed PEERS to get the module addresses
        routing_addrs.append(drone_addr)
        routed_peers_pkt = make_peers_pkt(
            (1, int("11", 2), [MY_ADDR, controller_addr, drone_addr])
        )
        if VERBOSE:
            print("Sending routed PEERS...")
        s.send(routed_peers_pkt)

        # Receive routed PEERS response
        if VERBOSE:
            print("Receiving routed PEERS...")
        routed_peers_resp = s.receive()
        modules = parse_pkt(routed_peers_resp)

        # Save power module addresses in routing list
        for module_addr, _, module_name in modules:
            if module_name == "power":
                routing_addrs.append(module_addr)

    # Send the poweroff command to all power modules
    path_code = int("0011" * len(drones) + "1", 2)
    poweroff_pkt = make_routed_pkt(4, b"poweroff", 1, path_code, routing_addrs)
    if VERBOSE:
        print("Sending poweroff command...")
    s.send(poweroff_pkt)

    # Receive poweroff command responses
    for _ in range(len(drones)):
        if VERBOSE:
            print("Receiving poweroff command response..")
        poweroff_resp = s.receive()
        parse_pkt(poweroff_resp)

    # Write the poweroff packet to a file
    if VERBOSE:
        print("Writing poweroff packet to a file...")
    with open(POWEROFF_PKT_FILENAME, "wb") as f:
        f.write(poweroff_pkt)


def make_routed_header(path_index, path_code, addrs):
    encoded_addrs = b""
    for addr in addrs:
        encoded_addrs += struct.pack(">H", addr)
    return struct.pack(">BLB", path_index, path_code, len(addrs)) + encoded_addrs


def connect(host, port, addr, node_type, hostname):
    # Start the socket
    s = ControllerSocket(host, port)

    # Send HELLO packet
    hello_pkt = make_hello_pkt(addr, node_type, hostname)
    if VERBOSE:
        print("Sending HELLO...")
    s.send(hello_pkt)

    # Receive HELLO response
    if VERBOSE:
        print("Receiving HELLO...")
    hello_resp = s.receive()
    return s, parse_pkt(hello_resp)


def make_hello_pkt(addr, node_type, hostname):
    # Encode and enforce length of hostname
    hostname = hostname.encode()[31:]
    hostname = hostname + b"\x00" * (32 - len(hostname))

    # Create the content
    content = struct.pack(">HB", addr, node_type) + hostname
    return make_pkt(0, 0, 0, content)


def make_peers_pkt(routing=None):
    # Handle routed packets
    if routing is not None:
        path_index, path_code, addrs = routing
        return make_routed_pkt(1, b"\x00" * 10, path_index, path_code, addrs)
    return make_pkt(0, 1, 0, b"")


def make_routed_pkt(msg_type, content, path_index, path_code, addrs):
    # Create routed header
    encoded_addrs = b""
    for addr in addrs:
        encoded_addrs += struct.pack(">H", addr)
    routed_hdr = struct.pack(">BLB", path_index, path_code, len(addrs)) + encoded_addrs

    # Create packet
    return make_pkt(128, msg_type, len(routed_hdr), routed_hdr + content)


def make_pkt(flags, msg_type, offset, content):
    return struct.pack(">BBH", flags, msg_type, offset) + content


def parse_pkt(pkt):
    # Parse the packet
    format_str = ">BBH"
    flags, msg_type, offset = struct.unpack_from(">BBH", pkt)
    content = pkt[struct.calcsize(format_str) + offset :]

    # Print packet info
    if VERBOSE:
        print(f"Flags = {flags}, Type = {msg_type}")

    # Handle HELLO or PEERS packet
    if msg_type == 0 or msg_type == 1:
        nodes = []
        for addr, node_type, name in struct.iter_unpack(">HB32s", content):
            name = name.rstrip(b"\x00").decode()
            nodes.append((addr, node_type, name))
            if VERBOSE:
                print(f"Hostname = {name}, Type = {node_type}, Address = {hex(addr)}")
        return nodes

    # Handle other message types
    content = content.replace(b"\x00", b"")
    if VERBOSE:
        print(f"Content = {content}")
    return content


# Modified from hello.py
class ControllerSocket:
    def __init__(self, host, port):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.connect((host, port))

    def send(self, pkt):
        # Create the frame from the packet
        frame = struct.pack(">H", len(pkt)) + pkt

        # Send the frame
        total_sent = 0
        while total_sent < len(frame):
            sent = self.sock.send(frame[total_sent:])
            if sent == 0:
                raise RuntimeError("socket connection broken")
            total_sent = total_sent + sent

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


if __name__ == "__main__":
    main()
