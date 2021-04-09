package org.minidns.dnsserverlookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.minidns.util.PlatformDetection;

public class AndroidUsingExec extends AbstractDnsServerLookupMechanism {
    public static final DnsServerLookupMechanism INSTANCE = new AndroidUsingExec();
    public static final int PRIORITY = 999;
    private static final String PROP_DELIM = "]: [";

    private AndroidUsingExec() {
        super(AndroidUsingExec.class.getSimpleName(), PRIORITY);
    }

    public List<String> getDnsServerAddresses() {
        try {
            Set<String> server = parseProps(new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("getprop").getInputStream())), true);
            if (server.size() > 0) {
                List<String> res = new ArrayList<>(server.size());
                res.addAll(server);
                return res;
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Exception in findDNSByExec", e);
        }
        return null;
    }

    public boolean isAvailable() {
        return PlatformDetection.isAndroid();
    }

    protected static Set<String> parseProps(BufferedReader lnr, boolean logWarning) throws UnknownHostException, IOException {
        Set<String> server = new HashSet<>(6);
        while (true) {
            String readLine = lnr.readLine();
            String line = readLine;
            if (readLine == null) {
                return server;
            }
            String str = PROP_DELIM;
            int split = line.indexOf(str);
            if (split != -1) {
                String property = line.substring(1, split);
                int valueStart = str.length() + split;
                int valueEnd = line.length() - 1;
                if (valueEnd >= valueStart) {
                    String value = line.substring(valueStart, valueEnd);
                    if (!value.isEmpty() && (property.endsWith(".dns") || property.endsWith(".dns1") || property.endsWith(".dns2") || property.endsWith(".dns3") || property.endsWith(".dns4"))) {
                        InetAddress ip = InetAddress.getByName(value);
                        if (ip != null) {
                            String value2 = ip.getHostAddress();
                            if (!(value2 == null || value2.length() == 0)) {
                                server.add(value2);
                            }
                        }
                    }
                } else if (logWarning) {
                    Logger logger = LOGGER;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Malformed property detected: \"");
                    sb.append(line);
                    sb.append('\"');
                    logger.warning(sb.toString());
                }
            }
        }
    }
}
