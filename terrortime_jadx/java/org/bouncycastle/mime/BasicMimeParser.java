package org.bouncycastle.mime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.encoding.Base64InputStream;
import org.bouncycastle.mime.encoding.QuotedPrintableInputStream;
import org.jivesoftware.smackx.hoxt.packet.AbstractHttpOverXmpp.Base64;

public class BasicMimeParser implements MimeParser {
    private final String boundary;
    private final String defaultContentTransferEncoding;
    private Headers headers;
    private boolean isMultipart;
    private final MimeParserContext parserContext;
    private final InputStream src;

    public BasicMimeParser(InputStream inputStream) throws IOException {
        this(null, new Headers(inputStream, "7bit"), inputStream);
    }

    public BasicMimeParser(Headers headers2, InputStream inputStream) {
        this(null, headers2, inputStream);
    }

    public BasicMimeParser(MimeParserContext mimeParserContext, InputStream inputStream) throws IOException {
        this(mimeParserContext, new Headers(inputStream, mimeParserContext.getDefaultContentTransferEncoding()), inputStream);
    }

    public BasicMimeParser(MimeParserContext mimeParserContext, Headers headers2, InputStream inputStream) {
        String str;
        this.isMultipart = false;
        if (headers2.isMultipart()) {
            this.isMultipart = true;
            str = headers2.getBoundary();
        } else {
            str = null;
        }
        this.boundary = str;
        this.headers = headers2;
        this.parserContext = mimeParserContext;
        this.src = inputStream;
        this.defaultContentTransferEncoding = mimeParserContext != null ? mimeParserContext.getDefaultContentTransferEncoding() : "7bit";
    }

    private InputStream processStream(Headers headers2, InputStream inputStream) {
        return headers2.getContentTransferEncoding().equals(Base64.ELEMENT) ? new Base64InputStream(inputStream) : headers2.getContentTransferEncoding().equals("quoted-printable") ? new QuotedPrintableInputStream(inputStream) : inputStream;
    }

    public boolean isMultipart() {
        return this.isMultipart;
    }

    public void parse(MimeParserListener mimeParserListener) throws IOException {
        MimeContext createContext = mimeParserListener.createContext(this.parserContext, this.headers);
        if (this.isMultipart) {
            MimeMultipartContext mimeMultipartContext = (MimeMultipartContext) createContext;
            StringBuilder sb = new StringBuilder();
            String str = "--";
            sb.append(str);
            sb.append(this.boundary);
            String sb2 = sb.toString();
            LineReader lineReader = new LineReader(this.src);
            boolean z = false;
            int i = 0;
            while (true) {
                String readLine = lineReader.readLine();
                if (readLine != null && !str.equals(readLine)) {
                    String str2 = "MIME object not fully processed";
                    if (z) {
                        BoundaryLimitedInputStream boundaryLimitedInputStream = new BoundaryLimitedInputStream(this.src, this.boundary);
                        Headers headers2 = new Headers((InputStream) boundaryLimitedInputStream, this.defaultContentTransferEncoding);
                        int i2 = i + 1;
                        InputStream applyContext = mimeMultipartContext.createContext(i).applyContext(headers2, boundaryLimitedInputStream);
                        mimeParserListener.object(this.parserContext, headers2, processStream(headers2, applyContext));
                        if (applyContext.read() < 0) {
                            i = i2;
                        } else {
                            throw new IOException(str2);
                        }
                    } else if (sb2.equals(readLine)) {
                        BoundaryLimitedInputStream boundaryLimitedInputStream2 = new BoundaryLimitedInputStream(this.src, this.boundary);
                        Headers headers3 = new Headers((InputStream) boundaryLimitedInputStream2, this.defaultContentTransferEncoding);
                        int i3 = i + 1;
                        InputStream applyContext2 = mimeMultipartContext.createContext(i).applyContext(headers3, boundaryLimitedInputStream2);
                        mimeParserListener.object(this.parserContext, headers3, processStream(headers3, applyContext2));
                        if (applyContext2.read() < 0) {
                            z = true;
                            i = i3;
                        } else {
                            throw new IOException(str2);
                        }
                    } else {
                        continue;
                    }
                } else {
                    return;
                }
            }
        } else {
            InputStream applyContext3 = createContext.applyContext(this.headers, this.src);
            MimeParserContext mimeParserContext = this.parserContext;
            Headers headers4 = this.headers;
            mimeParserListener.object(mimeParserContext, headers4, processStream(headers4, applyContext3));
        }
    }
}
