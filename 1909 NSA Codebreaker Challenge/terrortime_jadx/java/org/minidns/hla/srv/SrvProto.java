package org.minidns.hla.srv;

import org.minidns.dnslabel.DnsLabel;

public enum SrvProto {
    tcp,
    udp;
    
    public final DnsLabel dnsLabel;
}
