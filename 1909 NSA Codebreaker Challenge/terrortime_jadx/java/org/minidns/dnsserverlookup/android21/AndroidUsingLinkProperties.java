package org.minidns.dnsserverlookup.android21;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.RouteInfo;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.List;
import org.minidns.DnsClient;
import org.minidns.dnsserverlookup.AbstractDnsServerLookupMechanism;

public class AndroidUsingLinkProperties extends AbstractDnsServerLookupMechanism {
    private final ConnectivityManager connectivityManager;

    public static AndroidUsingLinkProperties setup(Context context) {
        AndroidUsingLinkProperties androidUsingLinkProperties = new AndroidUsingLinkProperties(context);
        DnsClient.addDnsServerLookupMechanism(androidUsingLinkProperties);
        return androidUsingLinkProperties;
    }

    public AndroidUsingLinkProperties(Context context) {
        super(AndroidUsingLinkProperties.class.getSimpleName(), 998);
        this.connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
    }

    public boolean isAvailable() {
        return VERSION.SDK_INT >= 21;
    }

    public List<String> getDnsServerAddresses() {
        Network[] networks = this.connectivityManager.getAllNetworks();
        if (networks == null) {
            return null;
        }
        List<String> servers = new ArrayList<>(networks.length * 2);
        for (Network network : networks) {
            LinkProperties linkProperties = this.connectivityManager.getLinkProperties(network);
            if (linkProperties != null) {
                if (hasDefaultRoute(linkProperties)) {
                    servers.addAll(0, toListOfStrings(linkProperties.getDnsServers()));
                } else {
                    servers.addAll(toListOfStrings(linkProperties.getDnsServers()));
                }
            }
        }
        if (servers.isEmpty()) {
            return null;
        }
        return servers;
    }

    private static boolean hasDefaultRoute(LinkProperties linkProperties) {
        for (RouteInfo route : linkProperties.getRoutes()) {
            if (route.isDefaultRoute()) {
                return true;
            }
        }
        return false;
    }
}
