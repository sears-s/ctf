package org.jivesoftware.smack.tcp;

import java.util.concurrent.atomic.AtomicBoolean;

public class BundleAndDefer {
    private final AtomicBoolean isStopped;

    BundleAndDefer(AtomicBoolean isStopped2) {
        this.isStopped = isStopped2;
    }

    public void stopCurrentBundleAndDefer() {
        synchronized (this.isStopped) {
            if (!this.isStopped.get()) {
                this.isStopped.set(true);
                this.isStopped.notify();
            }
        }
    }
}
