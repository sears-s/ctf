package org.minidns.hla;

import org.minidns.MiniDnsException;
import org.minidns.dnsmessage.DnsMessage.RESPONSE_CODE;
import org.minidns.dnsmessage.Question;

public class ResolutionUnsuccessfulException extends MiniDnsException {
    private static final long serialVersionUID = 1;
    public final Question question;
    public final RESPONSE_CODE responseCode;

    public ResolutionUnsuccessfulException(Question question2, RESPONSE_CODE responseCode2) {
        StringBuilder sb = new StringBuilder();
        sb.append("Asking for ");
        sb.append(question2);
        sb.append(" yielded an error response ");
        sb.append(responseCode2);
        super(sb.toString());
        this.question = question2;
        this.responseCode = responseCode2;
    }
}
