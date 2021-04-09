package org.jivesoftware.smackx.jingle.transports;

public abstract class JingleTransportInitiationException extends Exception {
    private static final long serialVersionUID = 1;

    public static class CandidateError extends JingleTransportInitiationException {
        private static final long serialVersionUID = 1;
    }

    public static class ProxyError extends JingleTransportInitiationException {
        private static final long serialVersionUID = 1;
    }
}
