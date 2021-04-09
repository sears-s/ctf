package org.jivesoftware.smack.sm;

import java.math.BigInteger;

public class SMUtils {
    private static long MASK_32_BIT = BigInteger.ONE.shiftLeft(32).subtract(BigInteger.ONE).longValue();

    public static long incrementHeight(long height) {
        long j = 1 + height;
        long height2 = j;
        return j & MASK_32_BIT;
    }

    public static long calculateDelta(long reportedHandledCount, long lastKnownHandledCount) {
        if (lastKnownHandledCount <= reportedHandledCount) {
            return (reportedHandledCount - lastKnownHandledCount) & MASK_32_BIT;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Illegal Stream Management State: Last known handled count (");
        sb.append(lastKnownHandledCount);
        sb.append(") is greater than reported handled count (");
        sb.append(reportedHandledCount);
        sb.append(')');
        throw new IllegalStateException(sb.toString());
    }
}
