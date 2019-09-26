package org.minidns.hla;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import org.minidns.AbstractDnsClient;
import org.minidns.dnslabel.DnsLabel;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.hla.srv.SrvProto;
import org.minidns.hla.srv.SrvService;
import org.minidns.hla.srv.SrvServiceProto;
import org.minidns.hla.srv.SrvType;
import org.minidns.iterative.ReliableDnsClient;
import org.minidns.record.Data;
import org.minidns.record.PTR;
import org.minidns.record.Record.TYPE;
import org.minidns.record.SRV;
import org.minidns.util.InetAddressUtil;

public class ResolverApi {
    public static final ResolverApi INSTANCE = new ResolverApi(new ReliableDnsClient());
    private final AbstractDnsClient dnsClient;

    public ResolverApi(AbstractDnsClient dnsClient2) {
        this.dnsClient = dnsClient2;
    }

    public final <D extends Data> ResolverResult<D> resolve(String name, Class<D> type) throws IOException {
        return resolve(DnsName.from(name), type);
    }

    public final <D extends Data> ResolverResult<D> resolve(DnsName name, Class<D> type) throws IOException {
        return resolve(new Question(name, TYPE.getType(type)));
    }

    public <D extends Data> ResolverResult<D> resolve(Question question) throws IOException {
        return new ResolverResult<>(question, this.dnsClient.query(question), null);
    }

    public SrvResolverResult resolveSrv(SrvType type, String serviceName) throws IOException {
        return resolveSrv(type.service, type.proto, DnsName.from(serviceName));
    }

    public SrvResolverResult resolveSrv(SrvType type, DnsName serviceName) throws IOException {
        return resolveSrv(type.service, type.proto, serviceName);
    }

    public SrvResolverResult resolveSrv(SrvService service, SrvProto proto, String name) throws IOException {
        return resolveSrv(service.dnsLabel, proto.dnsLabel, DnsName.from(name));
    }

    public SrvResolverResult resolveSrv(SrvService service, SrvProto proto, DnsName name) throws IOException {
        return resolveSrv(service.dnsLabel, proto.dnsLabel, name);
    }

    public SrvResolverResult resolveSrv(DnsLabel service, DnsLabel proto, DnsName name) throws IOException {
        return resolveSrv(name, new SrvServiceProto(service, proto));
    }

    public SrvResolverResult resolveSrv(String name) throws IOException {
        return resolveSrv(DnsName.from(name));
    }

    public ResolverResult<PTR> reverseLookup(CharSequence inetAddressCs) throws IOException {
        return reverseLookup(InetAddress.getByName(inetAddressCs.toString()));
    }

    public ResolverResult<PTR> reverseLookup(InetAddress inetAddress) throws IOException {
        if (inetAddress instanceof Inet4Address) {
            return reverseLookup((Inet4Address) inetAddress);
        }
        if (inetAddress instanceof Inet6Address) {
            return reverseLookup((Inet6Address) inetAddress);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("The given InetAddress '");
        sb.append(inetAddress);
        sb.append("' is neither of type Inet4Address or Inet6Address");
        throw new IllegalArgumentException(sb.toString());
    }

    public ResolverResult<PTR> reverseLookup(Inet4Address inet4Address) throws IOException {
        return resolve(DnsName.from(InetAddressUtil.reverseIpAddressOf(inet4Address), DnsName.IN_ADDR_ARPA), PTR.class);
    }

    public ResolverResult<PTR> reverseLookup(Inet6Address inet6Address) throws IOException {
        return resolve(DnsName.from(InetAddressUtil.reverseIpAddressOf(inet6Address), DnsName.IP6_ARPA), PTR.class);
    }

    public SrvResolverResult resolveSrv(DnsName srvDnsName) throws IOException {
        int labelCount = srvDnsName.getLabelCount();
        if (labelCount >= 3) {
            return resolveSrv(srvDnsName.stripToLabels(labelCount - 2), new SrvServiceProto(srvDnsName.getLabel(labelCount - 1), srvDnsName.getLabel(labelCount - 2)));
        }
        throw new IllegalArgumentException();
    }

    public SrvResolverResult resolveSrv(DnsName name, SrvServiceProto srvServiceProto) throws IOException {
        return new SrvResolverResult(resolve(DnsName.from(srvServiceProto.service, srvServiceProto.proto, name), SRV.class), srvServiceProto, this);
    }

    public final AbstractDnsClient getClient() {
        return this.dnsClient;
    }
}
