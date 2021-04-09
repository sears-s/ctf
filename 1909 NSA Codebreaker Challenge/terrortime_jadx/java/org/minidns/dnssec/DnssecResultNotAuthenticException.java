package org.minidns.dnssec;

import java.util.Collections;
import java.util.Set;
import org.minidns.MiniDnsException;

public class DnssecResultNotAuthenticException extends MiniDnsException {
    private static final long serialVersionUID = 1;
    private final Set<DnssecUnverifiedReason> unverifiedReasons;

    private DnssecResultNotAuthenticException(String message, Set<DnssecUnverifiedReason> unverifiedReasons2) {
        super(message);
        if (!unverifiedReasons2.isEmpty()) {
            this.unverifiedReasons = Collections.unmodifiableSet(unverifiedReasons2);
            return;
        }
        throw new IllegalArgumentException();
    }

    public static DnssecResultNotAuthenticException from(Set<DnssecUnverifiedReason> unverifiedReasons2) {
        StringBuilder sb = new StringBuilder();
        sb.append("DNSSEC result not authentic. Reasons: ");
        for (DnssecUnverifiedReason reason : unverifiedReasons2) {
            sb.append(reason);
            sb.append('.');
        }
        return new DnssecResultNotAuthenticException(sb.toString(), unverifiedReasons2);
    }

    public Set<DnssecUnverifiedReason> getUnverifiedReasons() {
        return this.unverifiedReasons;
    }
}
