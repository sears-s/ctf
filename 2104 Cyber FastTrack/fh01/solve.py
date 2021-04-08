from scapy.all import *
from struct import unpack

# Read the PCAP and initialization
packets = rdpcap("stream2.pcapng")
cur_file = None
cur_num = None
skip = False
frag = b""

# Iterate over packets
for packet in packets:

    # Save fragmented packets
    if UDP not in packet:
        frag += bytes(packet[IP].payload)
        continue
    data = bytes(packet[UDP].payload)

    # Handle client request
    if packet[IP].src.endswith(".128"):
        if skip:
            skip = False
            continue

        # Client requesting new file
        if b"get" in data:
            cur_file = data.replace(b"get ", b"").replace(b"\0", b"").decode()
            print(cur_file)
            skip = True
            cur_num = None

        # Check if client returned correct number
        else:
            if unpack("i", data[:4])[0] != cur_num:
                print("error")
                break

    # Handle server response
    else:
        if skip:
            continue

        # Check if number increased by one
        if cur_num is not None and unpack("i", data[:4])[0] != cur_num + 1:
            print("error")
            break
        cur_num = unpack("i", data[:4])[0]

        # Append the data to the file, with fragmented data prepended
        data = data[16:]
        if len(frag) > 0:
            data = frag + data
            frag = b""
        with open("out_" + cur_file, "ab") as f:
            f.write(data)
