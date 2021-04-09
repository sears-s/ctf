package org.jivesoftware.smack.util;

import org.bouncycastle.asn1.cmc.BodyPartID;

public class NumberUtil {
    public static void checkIfInUInt32Range(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("unsigned 32-bit integers can't be negative");
        } else if (value > BodyPartID.bodyIdMax) {
            throw new IllegalArgumentException("unsigned 32-bit integers can't be greater then 2^32 - 1");
        }
    }
}
