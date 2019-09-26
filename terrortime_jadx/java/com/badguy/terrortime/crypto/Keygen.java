package com.badguy.terrortime.crypto;

import android.support.v4.util.Pair;

public class Keygen {
    private static native String generateRsaKeyPair(String str, int i);

    public static Pair<String, String> generatePublicPrivateKeys() {
        String[] keypair = generateRsaKeyPair("alg1", 2048).split("\n\\s+");
        return new Pair<>(keypair[0], keypair[1]);
    }
}
