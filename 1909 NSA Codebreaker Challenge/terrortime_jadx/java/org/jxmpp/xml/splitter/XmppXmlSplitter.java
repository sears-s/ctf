package org.jxmpp.xml.splitter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class XmppXmlSplitter extends XmlSplitter {
    private final int maxElementSize;
    private String streamPrefix;
    private final XmppElementCallback xmppElementCallback;

    public XmppXmlSplitter(XmppElementCallback xmppElementCallback2) {
        this(10000, xmppElementCallback2);
    }

    public XmppXmlSplitter(XmppElementCallback xmppElementCallback2, DeclarationCallback declarationCallback, ProcessingInstructionCallback processingInstructionCallback) {
        this(10000, xmppElementCallback2, declarationCallback, processingInstructionCallback);
    }

    public XmppXmlSplitter(int maxElementSize2, XmppElementCallback xmppElementCallback2) {
        this(maxElementSize2, xmppElementCallback2, null, null);
    }

    public XmppXmlSplitter(int maxElementSize2, XmppElementCallback xmppElementCallback2, DeclarationCallback declarationCallback, ProcessingInstructionCallback processingInstructionCallback) {
        super(maxElementSize2, xmppElementCallback2, declarationCallback, processingInstructionCallback);
        this.maxElementSize = maxElementSize2;
        this.xmppElementCallback = xmppElementCallback2;
    }

    /* access modifiers changed from: protected */
    public void onNextChar() throws IOException {
        if (getCurrentSplittedPartSize() >= this.maxElementSize) {
            throw new IOException("Max element size exceeded");
        }
    }

    /* access modifiers changed from: protected */
    public void onStartTag(String prefix, String localpart, Map<String, String> attributes) {
        if ("stream".equals(localpart)) {
            StringBuilder sb = new StringBuilder();
            sb.append("xmlns:");
            sb.append(prefix);
            if ("http://etherx.jabber.org/streams".equals(attributes.get(sb.toString()))) {
                this.streamPrefix = prefix;
                newSplittedPart();
                this.xmppElementCallback.streamOpened(prefix, Collections.unmodifiableMap(attributes));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onEndTag(String qName) {
        String str = this.streamPrefix;
        if (str != null && qName.startsWith(str)) {
            StringBuilder sb = new StringBuilder();
            sb.append(this.streamPrefix);
            sb.append(":stream");
            if (sb.toString().equals(qName)) {
                this.xmppElementCallback.streamClosed();
            }
        }
    }
}
