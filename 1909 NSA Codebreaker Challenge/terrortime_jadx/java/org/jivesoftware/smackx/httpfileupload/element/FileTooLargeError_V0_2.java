package org.jivesoftware.smackx.httpfileupload.element;

public class FileTooLargeError_V0_2 extends FileTooLargeError {
    public static final String NAMESPACE = "urn:xmpp:http:upload";

    public FileTooLargeError_V0_2(long maxFileSize) {
        super(maxFileSize, "urn:xmpp:http:upload");
    }
}
