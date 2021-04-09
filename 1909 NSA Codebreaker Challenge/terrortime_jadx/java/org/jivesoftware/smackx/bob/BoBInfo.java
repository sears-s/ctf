package org.jivesoftware.smackx.bob;

import java.util.Set;

public class BoBInfo {
    private final BoBData data;
    private final Set<BoBHash> hashes;

    BoBInfo(Set<BoBHash> hashes2, BoBData data2) {
        this.hashes = hashes2;
        this.data = data2;
    }

    public Set<BoBHash> getHashes() {
        return this.hashes;
    }

    public BoBData getData() {
        return this.data;
    }
}
