package org.jxmpp.xml.splitter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class XmlSplitter extends Writer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private String attributeName;
    private AttributeValueQuotes attributeValueQuotes;
    private final Map<String, String> attributes;
    protected final CompleteElementCallback completeElementCallback;
    private final DeclarationCallback declarationCallback;
    private int depth;
    private final ProcessingInstructionCallback processingInstructionCallback;
    private String qName;
    private final StringBuilder splittedPartBuffer;
    private State state;
    private final StringBuilder tokenBuffer;

    private enum AttributeValueQuotes {
        apos('\''),
        quot('\"');
        
        final char c;

        private AttributeValueQuotes(char c2) {
            this.c = c2;
        }
    }

    private enum State {
        START,
        AFTER_TAG_RIGHT_ANGLE_BRACKET,
        IN_TAG_NAME,
        IN_END_TAG,
        AFTER_START_NAME,
        IN_EMPTY_TAG,
        IN_ATTRIBUTE_NAME,
        AFTER_ATTRIBUTE_EQUALS,
        IN_ATTRIBUTE_VALUE,
        AFTER_COMMENT_BANG,
        AFTER_COMMENT_DASH1,
        AFTER_COMMENT_DASH2,
        AFTER_COMMENT,
        AFTER_COMMENT_CLOSING_DASH1,
        AFTER_COMMENT_CLOSING_DASH2,
        IN_PROCESSING_INSTRUCTION_OR_DECLARATION,
        IN_PROCESSING_INSTRUCTION_OR_DECLARATION_PSEUDO_ATTRIBUTE_VALUE,
        IN_PROCESSING_INSTRUCTION_OR_DECLARATION_QUESTION_MARK
    }

    public XmlSplitter(int bufferSize, CompleteElementCallback completeElementCallback2, DeclarationCallback declarationCallback2, ProcessingInstructionCallback processingInstructionCallback2) {
        this.tokenBuffer = new StringBuilder(256);
        this.attributes = new HashMap();
        this.state = State.START;
        this.splittedPartBuffer = new StringBuilder(bufferSize);
        if (completeElementCallback2 != null) {
            this.completeElementCallback = completeElementCallback2;
            this.declarationCallback = declarationCallback2;
            this.processingInstructionCallback = processingInstructionCallback2;
            return;
        }
        throw new IllegalArgumentException();
    }

    public XmlSplitter(int bufferSize, CompleteElementCallback completeElementCallback2) {
        this(bufferSize, completeElementCallback2, null, null);
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        for (int cur = off; cur < off + len; cur++) {
            processChar(cbuf[off + cur]);
        }
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }

    public final int getCurrentSplittedPartSize() {
        return this.splittedPartBuffer.length();
    }

    /* access modifiers changed from: protected */
    public void onNextChar() throws IOException {
    }

    /* access modifiers changed from: protected */
    public void onStartTag(String prefix, String localpart, Map<String, String> map) {
    }

    /* access modifiers changed from: protected */
    public void onEndTag(String qName2) {
    }

    /* access modifiers changed from: protected */
    public final void newSplittedPart() {
        this.depth = 0;
        this.splittedPartBuffer.setLength(0);
        this.state = State.START;
    }

    private void processChar(char c) throws IOException {
        onNextChar();
        this.splittedPartBuffer.append(c);
        switch (this.state) {
            case START:
                if (c == '<') {
                    this.state = State.AFTER_TAG_RIGHT_ANGLE_BRACKET;
                    return;
                }
                return;
            case AFTER_TAG_RIGHT_ANGLE_BRACKET:
                if (c == '!') {
                    this.state = State.AFTER_COMMENT_BANG;
                    return;
                } else if (c == '/') {
                    this.state = State.IN_END_TAG;
                    return;
                } else if (c != '?') {
                    this.tokenBuffer.append(c);
                    this.state = State.IN_TAG_NAME;
                    return;
                } else {
                    this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION;
                    return;
                }
            case IN_TAG_NAME:
                if (c == 9 || c == 10 || c == 13 || c == ' ') {
                    this.qName = getToken();
                    this.state = State.AFTER_START_NAME;
                    return;
                } else if (c == '/') {
                    this.qName = getToken();
                    onStartTagFinished();
                    this.state = State.IN_EMPTY_TAG;
                    return;
                } else if (c != '>') {
                    this.tokenBuffer.append(c);
                    return;
                } else {
                    this.qName = getToken();
                    onStartTagFinished();
                    this.state = State.START;
                    return;
                }
            case IN_END_TAG:
                if (c != '>') {
                    this.tokenBuffer.append(c);
                    return;
                } else {
                    onEndTagFinished();
                    return;
                }
            case AFTER_START_NAME:
                if (c != 9 && c != 10 && c != 13 && c != ' ') {
                    if (c == '/') {
                        onStartTagFinished();
                        this.state = State.IN_EMPTY_TAG;
                        return;
                    } else if (c != '>') {
                        this.tokenBuffer.append(c);
                        this.state = State.IN_ATTRIBUTE_NAME;
                        return;
                    } else {
                        onStartTagFinished();
                        this.state = State.START;
                        return;
                    }
                } else {
                    return;
                }
            case IN_ATTRIBUTE_NAME:
                if (c != '=') {
                    this.tokenBuffer.append(c);
                    return;
                }
                this.attributeName = getToken();
                this.state = State.AFTER_ATTRIBUTE_EQUALS;
                return;
            case AFTER_ATTRIBUTE_EQUALS:
                if (c == '\"') {
                    this.attributeValueQuotes = AttributeValueQuotes.quot;
                    this.state = State.IN_ATTRIBUTE_VALUE;
                    return;
                } else if (c == '\'') {
                    this.attributeValueQuotes = AttributeValueQuotes.apos;
                    this.state = State.IN_ATTRIBUTE_VALUE;
                    return;
                } else {
                    throw new IOException();
                }
            case IN_ATTRIBUTE_VALUE:
                if (c == this.attributeValueQuotes.c) {
                    this.attributes.put(this.attributeName, getToken());
                    this.state = State.AFTER_START_NAME;
                    return;
                }
                this.tokenBuffer.append(c);
                return;
            case IN_EMPTY_TAG:
                if (c == '>') {
                    onEndTagFinished();
                    return;
                }
                throw new IOException();
            case IN_PROCESSING_INSTRUCTION_OR_DECLARATION:
                if (c == '\"') {
                    this.attributeValueQuotes = AttributeValueQuotes.quot;
                    this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION_PSEUDO_ATTRIBUTE_VALUE;
                    return;
                } else if (c == '\'') {
                    this.attributeValueQuotes = AttributeValueQuotes.apos;
                    this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION_PSEUDO_ATTRIBUTE_VALUE;
                    return;
                } else if (c == '?') {
                    this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION_QUESTION_MARK;
                    return;
                } else {
                    return;
                }
            case IN_PROCESSING_INSTRUCTION_OR_DECLARATION_PSEUDO_ATTRIBUTE_VALUE:
                if (c == this.attributeValueQuotes.c) {
                    this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION;
                    return;
                }
                return;
            case IN_PROCESSING_INSTRUCTION_OR_DECLARATION_QUESTION_MARK:
                if (c == '>') {
                    onProcessingInstructionOrDeclaration(this.splittedPartBuffer.toString());
                    newSplittedPart();
                    return;
                }
                this.state = State.IN_PROCESSING_INSTRUCTION_OR_DECLARATION;
                return;
            case AFTER_COMMENT_BANG:
            case AFTER_COMMENT_DASH1:
            case AFTER_COMMENT_DASH2:
            case AFTER_COMMENT:
            case AFTER_COMMENT_CLOSING_DASH1:
            case AFTER_COMMENT_CLOSING_DASH2:
                throw new UnsupportedOperationException();
            default:
                return;
        }
    }

    private void onStartTagFinished() {
        this.depth++;
        onStartTag(extractPrefix(this.qName), extractLocalpart(this.qName), this.attributes);
        this.attributes.clear();
    }

    private void onEndTagFinished() {
        String endTagName = getToken();
        if (endTagName.length() == 0) {
            endTagName = this.qName;
        }
        this.depth--;
        if (this.depth == 0) {
            String completeElement = this.splittedPartBuffer.toString();
            this.splittedPartBuffer.setLength(0);
            this.completeElementCallback.onCompleteElement(completeElement);
        }
        onEndTag(endTagName);
        this.state = State.START;
    }

    private String getToken() {
        String token = this.tokenBuffer.toString();
        this.tokenBuffer.setLength(0);
        return token;
    }

    private void onProcessingInstructionOrDeclaration(String processingInstructionOrDeclaration) {
        if (processingInstructionOrDeclaration.startsWith("<?xml ")) {
            DeclarationCallback declarationCallback2 = this.declarationCallback;
            if (declarationCallback2 != null) {
                declarationCallback2.onDeclaration(processingInstructionOrDeclaration);
                return;
            }
            return;
        }
        ProcessingInstructionCallback processingInstructionCallback2 = this.processingInstructionCallback;
        if (processingInstructionCallback2 != null) {
            processingInstructionCallback2.onProcessingInstruction(processingInstructionOrDeclaration);
        }
    }

    private static String extractPrefix(String qName2) {
        int index = qName2.indexOf(58);
        return index > -1 ? qName2.substring(0, index) : qName2;
    }

    private static String extractLocalpart(String qName2) {
        int index = qName2.indexOf(58);
        return index > -1 ? qName2.substring(index + 1) : qName2;
    }
}
