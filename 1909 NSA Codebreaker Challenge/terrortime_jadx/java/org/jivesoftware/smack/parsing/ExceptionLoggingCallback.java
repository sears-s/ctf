package org.jivesoftware.smack.parsing;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.UnparseableStanza;

public class ExceptionLoggingCallback implements ParsingExceptionCallback {
    private static final Logger LOGGER = Logger.getLogger(ExceptionLoggingCallback.class.getName());

    public void handleUnparsableStanza(UnparseableStanza unparsed) throws Exception {
        Logger logger = LOGGER;
        Level level = Level.SEVERE;
        StringBuilder sb = new StringBuilder();
        sb.append("Smack message parsing exception. Content: '");
        sb.append(unparsed.getContent());
        sb.append("'");
        logger.log(level, sb.toString(), unparsed.getParsingException());
    }
}
