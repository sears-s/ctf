package org.minidns.dnsserverlookup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.minidns.util.PlatformDetection;

public class UnixUsingEtcResolvConf extends AbstractDnsServerLookupMechanism {
    public static final DnsServerLookupMechanism INSTANCE = new UnixUsingEtcResolvConf();
    private static final Logger LOGGER = Logger.getLogger(UnixUsingEtcResolvConf.class.getName());
    private static final Pattern NAMESERVER_PATTERN = Pattern.compile("^nameserver\\s+(.*)$");
    public static final int PRIORITY = 2000;
    private static final String RESOLV_CONF_FILE = "/etc/resolv.conf";
    private static List<String> cached;
    private static long lastModified;

    private UnixUsingEtcResolvConf() {
        super(UnixUsingEtcResolvConf.class.getSimpleName(), PRIORITY);
    }

    public List<String> getDnsServerAddresses() {
        String str = "Could not close reader";
        File file = new File(RESOLV_CONF_FILE);
        if (!file.exists()) {
            return null;
        }
        long currentLastModified = file.lastModified();
        if (currentLastModified == lastModified) {
            List<String> list = cached;
            if (list != null) {
                return list;
            }
        }
        List<String> servers = new ArrayList<>();
        String reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                reader = reader2.readLine();
                if (reader != null) {
                    Matcher matcher = NAMESERVER_PATTERN.matcher(reader);
                    if (matcher.matches()) {
                        servers.add(matcher.group(1).trim());
                    }
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                        LOGGER.log(Level.WARNING, str, e);
                    }
                }
            }
            reader2.close();
            if (servers.isEmpty()) {
                LOGGER.fine("Could not find any nameservers in /etc/resolv.conf");
                return null;
            }
            cached = servers;
            lastModified = currentLastModified;
            return cached;
        } catch (IOException e2) {
            LOGGER.log(Level.WARNING, "Could not read from /etc/resolv.conf", e2);
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    LOGGER.log(Level.WARNING, str, e3);
                }
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e4) {
                    LOGGER.log(Level.WARNING, str, e4);
                }
            }
        }
    }

    public boolean isAvailable() {
        if (!PlatformDetection.isAndroid() && new File(RESOLV_CONF_FILE).exists()) {
            return true;
        }
        return false;
    }
}
