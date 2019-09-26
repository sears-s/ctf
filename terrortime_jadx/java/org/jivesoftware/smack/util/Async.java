package org.jivesoftware.smack.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Async {

    public static abstract class ThrowingRunnable implements Runnable {
        public static final Logger LOGGER = Logger.getLogger(ThrowingRunnable.class.getName());

        public abstract void runOrThrow() throws Exception;

        public final void run() {
            try {
                runOrThrow();
            } catch (Exception e) {
                if (!(e instanceof RuntimeException)) {
                    LOGGER.log(Level.WARNING, "Caught Exception", e);
                    return;
                }
                throw ((RuntimeException) e);
            }
        }
    }

    public static Thread go(Runnable runnable) {
        Thread thread = daemonThreadFrom(runnable);
        thread.start();
        return thread;
    }

    public static Thread go(Runnable runnable, String threadName) {
        Thread thread = daemonThreadFrom(runnable);
        thread.setName(threadName);
        thread.start();
        return thread;
    }

    public static Thread daemonThreadFrom(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    }
}
