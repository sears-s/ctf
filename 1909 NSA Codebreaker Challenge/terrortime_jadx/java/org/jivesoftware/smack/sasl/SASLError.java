package org.jivesoftware.smack.sasl;

import java.util.logging.Level;
import java.util.logging.Logger;

public enum SASLError {
    aborted,
    account_disabled,
    credentials_expired,
    encryption_required,
    incorrect_encoding,
    invalid_authzid,
    invalid_mechanism,
    malformed_request,
    mechanism_too_weak,
    not_authorized,
    temporary_auth_failure;
    
    private static final Logger LOGGER = null;

    static {
        LOGGER = Logger.getLogger(SASLError.class.getName());
    }

    public String toString() {
        return name().replace('_', '-');
    }

    public static SASLError fromString(String string) {
        String string2 = string.replace('-', '_');
        try {
            return valueOf(string2);
        } catch (Exception e) {
            Logger logger = LOGGER;
            Level level = Level.WARNING;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not transform string '");
            sb.append(string2);
            sb.append("' to SASLError");
            logger.log(level, sb.toString(), e);
            return null;
        }
    }
}
