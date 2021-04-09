package org.jivesoftware.smack.parsing;

import java.util.logging.Logger;
import org.jivesoftware.smack.UnparseableStanza;

public class ExceptionThrowingCallbackWithHint extends ExceptionThrowingCallback {
    private static final Logger LOGGER = Logger.getLogger(ExceptionThrowingCallbackWithHint.class.getName());

    public void handleUnparsableStanza(UnparseableStanza packetData) throws Exception {
        LOGGER.warning("Parsing exception encountered. This exception will be re-thrown, leading to a disconnect. You can change this behavior by setting a different ParsingExceptionCallback using setParsingExceptionCallback(). More information an be found in AbstractXMPPConnection's javadoc.");
        super.handleUnparsableStanza(packetData);
    }
}
