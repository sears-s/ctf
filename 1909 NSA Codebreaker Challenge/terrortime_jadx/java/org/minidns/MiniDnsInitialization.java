package org.minidns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniDnsInitialization {
    private static final Logger LOGGER;
    static final String VERSION;

    static {
        String miniDnsVersion;
        Class<MiniDnsInitialization> cls = MiniDnsInitialization.class;
        String str = "IOException closing stream";
        LOGGER = Logger.getLogger(cls.getName());
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(cls.getClassLoader().getResourceAsStream("org.minidns/version")));
            miniDnsVersion = reader2.readLine();
            try {
                reader2.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, str, e);
            }
        } catch (Exception e2) {
            LOGGER.log(Level.SEVERE, "Could not determine MiniDNS version", e2);
            miniDnsVersion = "unkown";
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    LOGGER.log(Level.WARNING, str, e3);
                }
            }
            throw th;
        }
        VERSION = miniDnsVersion;
    }
}
