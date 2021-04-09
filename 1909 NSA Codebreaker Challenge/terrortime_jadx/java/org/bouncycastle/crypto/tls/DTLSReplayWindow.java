package org.bouncycastle.crypto.tls;

class DTLSReplayWindow {
    private static final long VALID_SEQ_MASK = 281474976710655L;
    private static final long WINDOW_SIZE = 64;
    private long bitmap = 0;
    private long latestConfirmedSeq = -1;

    DTLSReplayWindow() {
    }

    /* access modifiers changed from: 0000 */
    public void reportAuthenticated(long j) {
        if ((VALID_SEQ_MASK & j) == j) {
            long j2 = this.latestConfirmedSeq;
            if (j <= j2) {
                long j3 = j2 - j;
                if (j3 < WINDOW_SIZE) {
                    this.bitmap |= 1 << ((int) j3);
                    return;
                }
                return;
            }
            long j4 = j - j2;
            if (j4 >= WINDOW_SIZE) {
                this.bitmap = 1;
            } else {
                this.bitmap <<= (int) j4;
                this.bitmap |= 1;
            }
            this.latestConfirmedSeq = j;
            return;
        }
        throw new IllegalArgumentException("'seq' out of range");
    }

    /* access modifiers changed from: 0000 */
    public void reset() {
        this.latestConfirmedSeq = -1;
        this.bitmap = 0;
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldDiscard(long j) {
        if ((VALID_SEQ_MASK & j) != j) {
            return true;
        }
        long j2 = this.latestConfirmedSeq;
        if (j <= j2) {
            long j3 = j2 - j;
            if (j3 >= WINDOW_SIZE || (this.bitmap & (1 << ((int) j3))) != 0) {
                return true;
            }
        }
        return false;
    }
}
