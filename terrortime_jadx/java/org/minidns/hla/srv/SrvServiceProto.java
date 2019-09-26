package org.minidns.hla.srv;

import org.minidns.dnslabel.DnsLabel;

public class SrvServiceProto {
    public final DnsLabel proto;
    public final DnsLabel service;

    public SrvServiceProto(DnsLabel service2, DnsLabel proto2) {
        this.service = service2;
        this.proto = proto2;
    }
}
