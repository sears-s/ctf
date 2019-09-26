package org.jivesoftware.smackx.jingle.transports;

import org.jivesoftware.smackx.bytestreams.BytestreamSession;

public interface JingleTransportInitiationCallback {
    void onException(Exception exc);

    void onSessionInitiated(BytestreamSession bytestreamSession);
}
