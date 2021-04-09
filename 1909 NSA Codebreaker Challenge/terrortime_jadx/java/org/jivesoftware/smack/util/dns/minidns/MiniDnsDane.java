package org.jivesoftware.smack.util.dns.minidns;

import org.jivesoftware.smack.util.DNSUtil;
import org.jivesoftware.smack.util.dns.SmackDaneProvider;
import org.jivesoftware.smack.util.dns.SmackDaneVerifier;

public class MiniDnsDane implements SmackDaneProvider {
    public static final MiniDnsDane INSTANCE = new MiniDnsDane();

    public SmackDaneVerifier newInstance() {
        return new MiniDnsDaneVerifier();
    }

    public static void setup() {
        DNSUtil.setDaneProvider(INSTANCE);
    }
}
