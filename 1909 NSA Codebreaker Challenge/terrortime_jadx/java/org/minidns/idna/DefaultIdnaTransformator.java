package org.minidns.idna;

import java.net.IDN;
import org.minidns.dnsname.DnsName;

public class DefaultIdnaTransformator implements IdnaTransformator {
    public String toASCII(String input) {
        if (DnsName.ROOT.ace.equals(input)) {
            return DnsName.ROOT.ace;
        }
        return IDN.toASCII(input);
    }

    public String toUnicode(String input) {
        return IDN.toUnicode(input);
    }
}
