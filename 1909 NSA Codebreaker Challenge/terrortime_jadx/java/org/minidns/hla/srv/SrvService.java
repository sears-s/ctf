package org.minidns.hla.srv;

import org.minidns.dnslabel.DnsLabel;

public enum SrvService {
    xmpp_client,
    xmpp_server,
    xmpps_client,
    xmpps_server;
    
    public final DnsLabel dnsLabel;
}
