package org.minidns.dnsserverlookup;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.minidns.util.PlatformDetection;

public class AndroidUsingReflection extends AbstractDnsServerLookupMechanism {
    public static final DnsServerLookupMechanism INSTANCE = new AndroidUsingReflection();
    public static final int PRIORITY = 1000;

    protected AndroidUsingReflection() {
        super(AndroidUsingReflection.class.getSimpleName(), 1000);
    }

    public List<String> getDnsServerAddresses() {
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class});
            ArrayList<String> servers = new ArrayList<>(5);
            String[] strArr = {"net.dns1", "net.dns2", "net.dns3", "net.dns4"};
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                String value = (String) method.invoke(null, new Object[]{strArr[i]});
                if (value != null) {
                    if (value.length() != 0) {
                        if (!servers.contains(value)) {
                            InetAddress ip = InetAddress.getByName(value);
                            if (ip != null) {
                                String value2 = ip.getHostAddress();
                                if (value2 != null) {
                                    if (value2.length() != 0) {
                                        if (!servers.contains(value2)) {
                                            servers.add(value2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (servers.size() > 0) {
                return servers;
            }
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception in findDNSByReflection", e);
        }
    }

    public boolean isAvailable() {
        return PlatformDetection.isAndroid();
    }
}
