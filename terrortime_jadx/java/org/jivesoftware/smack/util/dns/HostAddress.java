package org.jivesoftware.smack.util.dns;

import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.minidns.dnsname.DnsName;

public class HostAddress {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private final Map<InetAddress, Exception> exceptions;
    private final DnsName fqdn;
    private final List<InetAddress> inetAddresses;
    private final int port;

    public HostAddress(DnsName fqdn2, int port2, List<InetAddress> inetAddresses2) {
        this.exceptions = new LinkedHashMap();
        if (port2 < 0 || port2 > 65535) {
            StringBuilder sb = new StringBuilder();
            sb.append("Port must be a 16-bit unsigned integer (i.e. between 0-65535. Port was: ");
            sb.append(port2);
            throw new IllegalArgumentException(sb.toString());
        }
        this.fqdn = fqdn2;
        this.port = port2;
        if (!inetAddresses2.isEmpty()) {
            this.inetAddresses = inetAddresses2;
            return;
        }
        throw new IllegalArgumentException("Must provide at least one InetAddress");
    }

    public HostAddress(int port2, InetAddress hostAddress) {
        this(null, port2, Collections.singletonList(hostAddress));
    }

    public HostAddress(DnsName fqdn2, Exception e) {
        this.exceptions = new LinkedHashMap();
        this.fqdn = fqdn2;
        this.port = 5222;
        this.inetAddresses = Collections.emptyList();
        setException(e);
    }

    public String getHost() {
        DnsName dnsName = this.fqdn;
        if (dnsName != null) {
            return dnsName.toString();
        }
        return ((InetAddress) this.inetAddresses.get(0)).getHostAddress();
    }

    public DnsName getFQDN() {
        return this.fqdn;
    }

    public int getPort() {
        return this.port;
    }

    public void setException(Exception exception) {
        setException(null, exception);
    }

    public void setException(InetAddress inetAddress, Exception exception) {
        Exception exc = (Exception) this.exceptions.put(inetAddress, exception);
    }

    public Map<InetAddress, Exception> getExceptions() {
        return Collections.unmodifiableMap(this.exceptions);
    }

    public List<InetAddress> getInetAddresses() {
        return Collections.unmodifiableList(this.inetAddresses);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHost());
        sb.append(":");
        sb.append(this.port);
        return sb.toString();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (!(o instanceof HostAddress)) {
            return false;
        }
        HostAddress address = (HostAddress) o;
        if (!getHost().equals(address.getHost())) {
            return false;
        }
        if (this.port != address.port) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((1 * 37) + getHost().hashCode()) * 37) + this.port;
    }

    public String getErrorMessage() {
        if (this.exceptions.isEmpty()) {
            return "No error logged";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('\'');
        sb.append(toString());
        sb.append("' failed because: ");
        Iterator<Entry<InetAddress, Exception>> iterator = this.exceptions.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<InetAddress, Exception> entry = (Entry) iterator.next();
            if (((InetAddress) entry.getKey()) != null) {
                sb.append(entry.getKey());
                sb.append(" exception: ");
            }
            sb.append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
