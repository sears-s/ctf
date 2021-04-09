package org.minidns.hla;

import java.util.Collections;
import java.util.Set;
import org.minidns.MiniDnsException.NullResultException;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.RESPONSE_CODE;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.dnssec.DnssecResultNotAuthenticException;
import org.minidns.dnssec.DnssecUnverifiedReason;
import org.minidns.record.Data;

public class ResolverResult<D extends Data> {
    protected final DnsMessage answer;
    private final Set<D> data;
    private DnssecResultNotAuthenticException dnssecResultNotAuthenticException;
    private final boolean isAuthenticData;
    protected final Question question;
    private ResolutionUnsuccessfulException resolutionUnsuccessfulException;
    private final RESPONSE_CODE responseCode;
    protected final DnsQueryResult result;
    protected final Set<DnssecUnverifiedReason> unverifiedReasons;

    ResolverResult(Question question2, DnsQueryResult result2, Set<DnssecUnverifiedReason> unverifiedReasons2) throws NullResultException {
        if (result2 != null) {
            this.result = result2;
            DnsMessage answer2 = result2.response;
            this.question = question2;
            this.responseCode = answer2.responseCode;
            this.answer = answer2;
            Set<D> r = answer2.getAnswersFor(question2);
            if (r == null) {
                this.data = Collections.emptySet();
            } else {
                this.data = Collections.unmodifiableSet(r);
            }
            if (unverifiedReasons2 == null) {
                this.unverifiedReasons = null;
                this.isAuthenticData = false;
                return;
            }
            this.unverifiedReasons = Collections.unmodifiableSet(unverifiedReasons2);
            this.isAuthenticData = this.unverifiedReasons.isEmpty();
            return;
        }
        throw new NullResultException(question2.asMessageBuilder().build());
    }

    public boolean wasSuccessful() {
        return this.responseCode == RESPONSE_CODE.NO_ERROR;
    }

    public Set<D> getAnswers() {
        throwIseIfErrorResponse();
        return this.data;
    }

    public Set<D> getAnswersOrEmptySet() {
        return this.data;
    }

    public RESPONSE_CODE getResponseCode() {
        return this.responseCode;
    }

    public boolean isAuthenticData() {
        throwIseIfErrorResponse();
        return this.isAuthenticData;
    }

    public Set<DnssecUnverifiedReason> getUnverifiedReasons() {
        throwIseIfErrorResponse();
        return this.unverifiedReasons;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void throwIfErrorResponse() throws ResolutionUnsuccessfulException {
        ResolutionUnsuccessfulException resolutionUnsuccessfulException2 = getResolutionUnsuccessfulException();
        if (resolutionUnsuccessfulException2 != null) {
            throw resolutionUnsuccessfulException2;
        }
    }

    public ResolutionUnsuccessfulException getResolutionUnsuccessfulException() {
        if (wasSuccessful()) {
            return null;
        }
        if (this.resolutionUnsuccessfulException == null) {
            this.resolutionUnsuccessfulException = new ResolutionUnsuccessfulException(this.question, this.responseCode);
        }
        return this.resolutionUnsuccessfulException;
    }

    public DnssecResultNotAuthenticException getDnssecResultNotAuthenticException() {
        if (!wasSuccessful() || this.isAuthenticData) {
            return null;
        }
        if (this.dnssecResultNotAuthenticException == null) {
            this.dnssecResultNotAuthenticException = DnssecResultNotAuthenticException.from(getUnverifiedReasons());
        }
        return this.dnssecResultNotAuthenticException;
    }

    public DnsMessage getRawAnswer() {
        return this.answer;
    }

    public DnsQueryResult getDnsQueryResult() {
        return this.result;
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(10);
        sb.append("Question: ");
        sb.append(this.question);
        sb.append(10);
        sb.append("Response Code: ");
        sb.append(this.responseCode);
        sb.append(10);
        if (this.responseCode == RESPONSE_CODE.NO_ERROR) {
            if (this.isAuthenticData) {
                sb.append("Results verified via DNSSEC\n");
            }
            if (hasUnverifiedReasons()) {
                sb.append(this.unverifiedReasons);
                sb.append(10);
            }
            sb.append(this.answer.answerSection);
        }
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public boolean hasUnverifiedReasons() {
        Set<DnssecUnverifiedReason> set = this.unverifiedReasons;
        return set != null && !set.isEmpty();
    }

    /* access modifiers changed from: protected */
    public void throwIseIfErrorResponse() {
        ResolutionUnsuccessfulException resolutionUnsuccessfulException2 = getResolutionUnsuccessfulException();
        if (resolutionUnsuccessfulException2 != null) {
            throw new IllegalStateException("Can not perform operation because the DNS resolution was unsuccessful", resolutionUnsuccessfulException2);
        }
    }
}
