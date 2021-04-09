package org.minidns.dnssec;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.minidns.DnsCache;
import org.minidns.dnsmessage.DnsMessage;
import org.minidns.dnsmessage.DnsMessage.Builder;
import org.minidns.dnsmessage.Question;
import org.minidns.dnsname.DnsName;
import org.minidns.dnsqueryresult.DnsQueryResult;
import org.minidns.dnssec.DnssecUnverifiedReason.NoActiveSignaturesReason;
import org.minidns.dnssec.DnssecUnverifiedReason.NoSecureEntryPointReason;
import org.minidns.dnssec.DnssecUnverifiedReason.NoSignaturesReason;
import org.minidns.dnssec.DnssecUnverifiedReason.NoTrustAnchorReason;
import org.minidns.dnssec.DnssecValidationFailedException.AuthorityDoesNotContainSoa;
import org.minidns.iterative.ReliableDnsClient;
import org.minidns.record.DNSKEY;
import org.minidns.record.Data;
import org.minidns.record.NSEC;
import org.minidns.record.NSEC3;
import org.minidns.record.RRSIG;
import org.minidns.record.Record;
import org.minidns.record.Record.CLASS;
import org.minidns.record.Record.TYPE;

public class DnssecClient extends ReliableDnsClient {
    private static final DnsName DEFAULT_DLV = DnsName.from("dlv.isc.org");
    private static final BigInteger rootEntryKey = new BigInteger("1628686155461064465348252249725010996177649738666492500572664444461532807739744536029771810659241049343994038053541290419968870563183856865780916376571550372513476957870843322273120879361960335192976656756972171258658400305760429696147778001233984421619267530978084631948434496468785021389956803104620471232008587410372348519229650742022804219634190734272506220018657920136902014393834092648785514548876370028925405557661759399901378816916683122474038734912535425670533237815676134840739565610963796427401855723026687073600445461090736240030247906095053875491225879656640052743394090544036297390104110989318819106653199917493");
    private DnsName dlv;
    private final Map<DnsName, byte[]> knownSeps;
    private boolean stripSignatureRecords;

    /* renamed from: org.minidns.dnssec.DnssecClient$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$minidns$record$Record$TYPE = new int[TYPE.values().length];

        static {
            try {
                $SwitchMap$org$minidns$record$Record$TYPE[TYPE.NSEC.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$minidns$record$Record$TYPE[TYPE.NSEC3.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private class VerifySignaturesResult {
        Set<DnssecUnverifiedReason> reasons;
        boolean sepSignaturePresent;
        boolean sepSignatureRequired;

        private VerifySignaturesResult() {
            this.sepSignatureRequired = false;
            this.sepSignaturePresent = false;
            this.reasons = new HashSet();
        }

        /* synthetic */ VerifySignaturesResult(DnssecClient x0, AnonymousClass1 x1) {
            this();
        }
    }

    public DnssecClient() {
        this(DEFAULT_CACHE);
    }

    public DnssecClient(DnsCache cache) {
        super(cache);
        this.knownSeps = new ConcurrentHashMap();
        this.stripSignatureRecords = true;
        addSecureEntryPoint(DnsName.ROOT, rootEntryKey.toByteArray());
    }

    public DnsQueryResult query(Question q) throws IOException {
        DnssecQueryResult dnssecQueryResult = queryDnssec(q);
        if (dnssecQueryResult.isAuthenticData()) {
            return dnssecQueryResult.dnsQueryResult;
        }
        throw new IOException();
    }

    public DnssecQueryResult queryDnssec(CharSequence name, TYPE type) throws IOException {
        return queryDnssec(new Question(name, type, CLASS.IN));
    }

    public DnssecQueryResult queryDnssec(Question q) throws IOException {
        return performVerification(q, super.query(q));
    }

    private DnssecQueryResult performVerification(Question q, DnsQueryResult dnsQueryResult) throws IOException {
        if (dnsQueryResult == null) {
            return null;
        }
        DnsMessage dnsMessage = dnsQueryResult.response;
        Builder messageBuilder = dnsMessage.asBuilder();
        Set<DnssecUnverifiedReason> unverifiedReasons = verify(dnsMessage);
        messageBuilder.setAuthenticData(unverifiedReasons.isEmpty());
        List<Record<? extends Data>> answers = dnsMessage.answerSection;
        List<Record<? extends Data>> nameserverRecords = dnsMessage.authoritySection;
        List<Record<? extends Data>> additionalResourceRecords = dnsMessage.additionalSection;
        Set<Record<RRSIG>> signatures = new HashSet<>();
        Record.filter(signatures, RRSIG.class, answers);
        Record.filter(signatures, RRSIG.class, nameserverRecords);
        Record.filter(signatures, RRSIG.class, additionalResourceRecords);
        if (this.stripSignatureRecords) {
            messageBuilder.setAnswers(stripSignatureRecords(answers));
            messageBuilder.setNameserverRecords(stripSignatureRecords(nameserverRecords));
            messageBuilder.setAdditionalResourceRecords(stripSignatureRecords(additionalResourceRecords));
        }
        return new DnssecQueryResult(messageBuilder.build(), dnsQueryResult, signatures, unverifiedReasons);
    }

    private static List<Record<? extends Data>> stripSignatureRecords(List<Record<? extends Data>> records) {
        if (records.isEmpty()) {
            return records;
        }
        List<Record<? extends Data>> recordList = new ArrayList<>(records.size());
        for (Record<? extends Data> record : records) {
            if (record.type != TYPE.RRSIG) {
                recordList.add(record);
            }
        }
        return recordList;
    }

    private Set<DnssecUnverifiedReason> verify(DnsMessage dnsMessage) throws IOException {
        if (!dnsMessage.answerSection.isEmpty()) {
            return verifyAnswer(dnsMessage);
        }
        return verifyNsec(dnsMessage);
    }

    private Set<DnssecUnverifiedReason> verifyAnswer(DnsMessage dnsMessage) throws IOException {
        Question q = (Question) dnsMessage.questions.get(0);
        List<Record<? extends Data>> answers = dnsMessage.answerSection;
        List<Record<? extends Data>> toBeVerified = dnsMessage.copyAnswers();
        VerifySignaturesResult verifiedSignatures = verifySignatures(q, answers, toBeVerified);
        Set<DnssecUnverifiedReason> result = verifiedSignatures.reasons;
        if (!result.isEmpty()) {
            return result;
        }
        boolean sepSignatureValid = false;
        Set<DnssecUnverifiedReason> sepReasons = new HashSet<>();
        Iterator<Record<? extends Data>> iterator = toBeVerified.iterator();
        while (iterator.hasNext()) {
            Record<DNSKEY> record = ((Record) iterator.next()).ifPossibleAs(DNSKEY.class);
            if (record != null) {
                Set<DnssecUnverifiedReason> reasons = verifySecureEntryPoint(q, record);
                if (reasons.isEmpty()) {
                    sepSignatureValid = true;
                } else {
                    sepReasons.addAll(reasons);
                }
                if (!verifiedSignatures.sepSignaturePresent) {
                    LOGGER.finer("SEP key is not self-signed.");
                }
                iterator.remove();
            }
        }
        if (verifiedSignatures.sepSignaturePresent && !sepSignatureValid) {
            result.addAll(sepReasons);
        }
        if (verifiedSignatures.sepSignatureRequired && !verifiedSignatures.sepSignaturePresent) {
            result.add(new NoSecureEntryPointReason(q.name));
        }
        if (!toBeVerified.isEmpty()) {
            if (toBeVerified.size() == answers.size()) {
                result.add(new NoSignaturesReason(q));
            } else {
                throw new DnssecValidationFailedException(q, "Only some records are signed!");
            }
        }
        return result;
    }

    private Set<DnssecUnverifiedReason> verifyNsec(DnsMessage dnsMessage) throws IOException {
        DnssecUnverifiedReason reason;
        Set<DnssecUnverifiedReason> result = new HashSet<>();
        Question q = (Question) dnsMessage.questions.get(0);
        boolean validNsec = false;
        boolean nsecPresent = false;
        DnsName zone = null;
        List<Record<? extends Data>> authoritySection = dnsMessage.authoritySection;
        Iterator it = authoritySection.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Record<? extends Data> authorityRecord = (Record) it.next();
            if (authorityRecord.type == TYPE.SOA) {
                zone = authorityRecord.name;
                break;
            }
        }
        if (zone != null) {
            for (Record<? extends Data> record : authoritySection) {
                int i = AnonymousClass1.$SwitchMap$org$minidns$record$Record$TYPE[record.type.ordinal()];
                if (i == 1) {
                    nsecPresent = true;
                    reason = Verifier.verifyNsec(record.as(NSEC.class), q);
                } else if (i == 2) {
                    nsecPresent = true;
                    reason = Verifier.verifyNsec3(zone, record.as(NSEC3.class), q);
                }
                if (reason != null) {
                    result.add(reason);
                } else {
                    validNsec = true;
                }
            }
            if (!nsecPresent || validNsec) {
                List<Record<? extends Data>> toBeVerified = dnsMessage.copyAuthority();
                VerifySignaturesResult verifiedSignatures = verifySignatures(q, authoritySection, toBeVerified);
                if (!validNsec || !verifiedSignatures.reasons.isEmpty()) {
                    result.addAll(verifiedSignatures.reasons);
                } else {
                    result.clear();
                }
                if (toBeVerified.isEmpty() || toBeVerified.size() == authoritySection.size()) {
                    return result;
                }
                throw new DnssecValidationFailedException(q, "Only some resource records from the authority section are signed!");
            }
            throw new DnssecValidationFailedException(q, "Invalid NSEC!");
        }
        throw new AuthorityDoesNotContainSoa(dnsMessage);
    }

    private VerifySignaturesResult verifySignatures(Question q, Collection<Record<? extends Data>> reference, List<Record<? extends Data>> toBeVerified) throws IOException {
        DnssecClient dnssecClient = this;
        Question question = q;
        List<Record<? extends Data>> list = toBeVerified;
        Date now = new Date();
        List<RRSIG> outdatedRrSigs = new LinkedList<>();
        VerifySignaturesResult result = new VerifySignaturesResult(dnssecClient, null);
        List<Record<RRSIG>> rrsigs = new ArrayList<>(toBeVerified.size());
        for (Record<? extends Data> recordToBeVerified : toBeVerified) {
            Record<RRSIG> record = recordToBeVerified.ifPossibleAs(RRSIG.class);
            if (record != null) {
                RRSIG rrsig = (RRSIG) record.payloadData;
                if (rrsig.signatureExpiration.compareTo(now) < 0 || rrsig.signatureInception.compareTo(now) > 0) {
                    outdatedRrSigs.add(rrsig);
                } else {
                    rrsigs.add(record);
                }
            }
        }
        if (rrsigs.isEmpty()) {
            if (!outdatedRrSigs.isEmpty()) {
                result.reasons.add(new NoActiveSignaturesReason(question, outdatedRrSigs));
            } else {
                result.reasons.add(new NoSignaturesReason(question));
            }
            return result;
        }
        for (Record<RRSIG> sigRecord : rrsigs) {
            RRSIG rrsig2 = (RRSIG) sigRecord.payloadData;
            List<Record<? extends Data>> records = new ArrayList<>(reference.size());
            for (Record<? extends Data> record2 : reference) {
                if (record2.type == rrsig2.typeCovered && record2.name.equals(sigRecord.name)) {
                    records.add(record2);
                }
            }
            result.reasons.addAll(dnssecClient.verifySignedRecords(question, rrsig2, records));
            if (question.name.equals(rrsig2.signerName) && rrsig2.typeCovered == TYPE.DNSKEY) {
                Iterator<Record<? extends Data>> iterator = records.iterator();
                while (iterator.hasNext()) {
                    DNSKEY dnskey = (DNSKEY) ((Record) iterator.next()).ifPossibleAs(DNSKEY.class).payloadData;
                    iterator.remove();
                    if (dnskey.getKeyTag() == rrsig2.keyTag) {
                        result.sepSignaturePresent = true;
                    }
                }
                result.sepSignatureRequired = true;
            }
            if (!isParentOrSelf(sigRecord.name.ace, rrsig2.signerName.ace)) {
                Logger logger = LOGGER;
                StringBuilder sb = new StringBuilder();
                sb.append("Records at ");
                sb.append(sigRecord.name);
                sb.append(" are cross-signed with a key from ");
                sb.append(rrsig2.signerName);
                logger.finer(sb.toString());
            } else {
                list.removeAll(records);
            }
            list.remove(sigRecord);
            dnssecClient = this;
        }
        return result;
    }

    private static boolean isParentOrSelf(String child, String parent) {
        if (child.equals(parent) || parent.isEmpty()) {
            return true;
        }
        String str = "\\.";
        String[] childSplit = child.split(str);
        String[] parentSplit = parent.split(str);
        if (parentSplit.length > childSplit.length) {
            return false;
        }
        for (int i = 1; i <= parentSplit.length; i++) {
            if (!parentSplit[parentSplit.length - i].equals(childSplit[childSplit.length - i])) {
                return false;
            }
        }
        return true;
    }

    private Set<DnssecUnverifiedReason> verifySignedRecords(Question q, RRSIG rrsig, List<Record<? extends Data>> records) throws IOException {
        Set<DnssecUnverifiedReason> result = new HashSet<>();
        DNSKEY dnskey = null;
        if (rrsig.typeCovered == TYPE.DNSKEY) {
            Iterator it = Record.filter(DNSKEY.class, records).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Record<DNSKEY> dnsKeyRecord = (Record) it.next();
                if (((DNSKEY) dnsKeyRecord.payloadData).getKeyTag() == rrsig.keyTag) {
                    dnskey = dnsKeyRecord.payloadData;
                    break;
                }
            }
        } else if (q.type != TYPE.DS || !rrsig.signerName.equals(q.name)) {
            DnssecQueryResult dnskeyRes = queryDnssec(rrsig.signerName, TYPE.DNSKEY);
            result.addAll(dnskeyRes.getUnverifiedReasons());
            Iterator it2 = dnskeyRes.dnsQueryResult.response.filterAnswerSectionBy(DNSKEY.class).iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                Record<DNSKEY> dnsKeyRecord2 = (Record) it2.next();
                if (((DNSKEY) dnsKeyRecord2.payloadData).getKeyTag() == rrsig.keyTag) {
                    dnskey = dnsKeyRecord2.payloadData;
                    break;
                }
            }
        } else {
            result.add(new NoTrustAnchorReason(q.name));
            return result;
        }
        if (dnskey != null) {
            DnssecUnverifiedReason unverifiedReason = Verifier.verify(records, rrsig, dnskey);
            if (unverifiedReason != null) {
                result.add(unverifiedReason);
            }
            return result;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(records.size());
        sb.append(" ");
        sb.append(rrsig.typeCovered);
        sb.append(" record(s) are signed using an unknown key.");
        throw new DnssecValidationFailedException(q, sb.toString());
    }

    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Set<org.minidns.dnssec.DnssecUnverifiedReason> verifySecureEntryPoint(org.minidns.dnsmessage.Question r13, org.minidns.record.Record<org.minidns.record.DNSKEY> r14) throws java.io.IOException {
        /*
            r12 = this;
            D r0 = r14.payloadData
            org.minidns.record.DNSKEY r0 = (org.minidns.record.DNSKEY) r0
            java.util.HashSet r1 = new java.util.HashSet
            r1.<init>()
            java.util.HashSet r2 = new java.util.HashSet
            r2.<init>()
            java.util.Map<org.minidns.dnsname.DnsName, byte[]> r3 = r12.knownSeps
            org.minidns.dnsname.DnsName r4 = r14.name
            boolean r3 = r3.containsKey(r4)
            if (r3 == 0) goto L_0x0032
            java.util.Map<org.minidns.dnsname.DnsName, byte[]> r3 = r12.knownSeps
            org.minidns.dnsname.DnsName r4 = r14.name
            java.lang.Object r3 = r3.get(r4)
            byte[] r3 = (byte[]) r3
            boolean r3 = r0.keyEquals(r3)
            if (r3 == 0) goto L_0x0029
            return r1
        L_0x0029:
            org.minidns.dnssec.DnssecUnverifiedReason$ConflictsWithSep r3 = new org.minidns.dnssec.DnssecUnverifiedReason$ConflictsWithSep
            r3.<init>(r14)
            r1.add(r3)
            return r1
        L_0x0032:
            org.minidns.dnsname.DnsName r3 = r14.name
            boolean r3 = r3.isRootLabel()
            if (r3 == 0) goto L_0x0043
            org.minidns.dnssec.DnssecUnverifiedReason$NoRootSecureEntryPointReason r3 = new org.minidns.dnssec.DnssecUnverifiedReason$NoRootSecureEntryPointReason
            r3.<init>()
            r1.add(r3)
            return r1
        L_0x0043:
            r3 = 0
            org.minidns.dnsname.DnsName r4 = r14.name
            org.minidns.record.Record$TYPE r5 = org.minidns.record.Record.TYPE.DS
            org.minidns.dnssec.DnssecQueryResult r4 = r12.queryDnssec(r4, r5)
            java.util.Set r5 = r4.getUnverifiedReasons()
            r1.addAll(r5)
            org.minidns.dnsqueryresult.DnsQueryResult r5 = r4.dnsQueryResult
            org.minidns.dnsmessage.DnsMessage r5 = r5.response
            java.lang.Class<org.minidns.record.DS> r6 = org.minidns.record.DS.class
            java.util.List r5 = r5.filterAnswerSectionBy(r6)
            java.util.Iterator r6 = r5.iterator()
        L_0x0061:
            boolean r7 = r6.hasNext()
            if (r7 == 0) goto L_0x0080
            java.lang.Object r7 = r6.next()
            org.minidns.record.Record r7 = (org.minidns.record.Record) r7
            D r8 = r7.payloadData
            org.minidns.record.DS r8 = (org.minidns.record.DS) r8
            int r9 = r0.getKeyTag()
            int r10 = r8.keyTag
            if (r9 != r10) goto L_0x007f
            r3 = r8
            java.util.Set r2 = r4.getUnverifiedReasons()
            goto L_0x0080
        L_0x007f:
            goto L_0x0061
        L_0x0080:
            if (r3 != 0) goto L_0x009f
            java.util.logging.Logger r6 = LOGGER
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "There is no DS record for "
            r7.append(r8)
            org.minidns.dnsname.DnsName r8 = r14.name
            r7.append(r8)
            java.lang.String r8 = ", server gives empty result"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.fine(r7)
        L_0x009f:
            if (r3 != 0) goto L_0x0114
            org.minidns.dnsname.DnsName r6 = r12.dlv
            if (r6 == 0) goto L_0x0114
            org.minidns.dnsname.DnsName r7 = r14.name
            boolean r6 = r6.isChildOf(r7)
            if (r6 != 0) goto L_0x0114
            org.minidns.dnsname.DnsName r6 = r14.name
            org.minidns.dnsname.DnsName r7 = r12.dlv
            org.minidns.dnsname.DnsName r6 = org.minidns.dnsname.DnsName.from(r6, r7)
            org.minidns.record.Record$TYPE r7 = org.minidns.record.Record.TYPE.DLV
            org.minidns.dnssec.DnssecQueryResult r6 = r12.queryDnssec(r6, r7)
            java.util.Set r7 = r6.getUnverifiedReasons()
            r1.addAll(r7)
            org.minidns.dnsqueryresult.DnsQueryResult r7 = r6.dnsQueryResult
            org.minidns.dnsmessage.DnsMessage r7 = r7.response
            java.lang.Class<org.minidns.record.DLV> r8 = org.minidns.record.DLV.class
            java.util.List r7 = r7.filterAnswerSectionBy(r8)
            java.util.Iterator r8 = r7.iterator()
        L_0x00d0:
            boolean r9 = r8.hasNext()
            if (r9 == 0) goto L_0x0114
            java.lang.Object r9 = r8.next()
            org.minidns.record.Record r9 = (org.minidns.record.Record) r9
            D r10 = r14.payloadData
            org.minidns.record.DNSKEY r10 = (org.minidns.record.DNSKEY) r10
            int r10 = r10.getKeyTag()
            D r11 = r9.payloadData
            org.minidns.record.DLV r11 = (org.minidns.record.DLV) r11
            int r11 = r11.keyTag
            if (r10 != r11) goto L_0x0113
            java.util.logging.Logger r8 = LOGGER
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Found DLV for "
            r10.append(r11)
            org.minidns.dnsname.DnsName r11 = r14.name
            r10.append(r11)
            java.lang.String r11 = ", awesome."
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r8.fine(r10)
            D r8 = r9.payloadData
            r3 = r8
            org.minidns.record.DelegatingDnssecRR r3 = (org.minidns.record.DelegatingDnssecRR) r3
            java.util.Set r2 = r6.getUnverifiedReasons()
            goto L_0x0114
        L_0x0113:
            goto L_0x00d0
        L_0x0114:
            if (r3 == 0) goto L_0x0122
            org.minidns.dnssec.DnssecUnverifiedReason r6 = org.minidns.dnssec.Verifier.verify(r14, r3)
            if (r6 == 0) goto L_0x0120
            r1.add(r6)
            goto L_0x0121
        L_0x0120:
            r1 = r2
        L_0x0121:
            goto L_0x0132
        L_0x0122:
            boolean r6 = r1.isEmpty()
            if (r6 == 0) goto L_0x0121
            org.minidns.dnssec.DnssecUnverifiedReason$NoTrustAnchorReason r6 = new org.minidns.dnssec.DnssecUnverifiedReason$NoTrustAnchorReason
            org.minidns.dnsname.DnsName r7 = r14.name
            r6.<init>(r7)
            r1.add(r6)
        L_0x0132:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.minidns.dnssec.DnssecClient.verifySecureEntryPoint(org.minidns.dnsmessage.Question, org.minidns.record.Record):java.util.Set");
    }

    /* access modifiers changed from: protected */
    public Builder newQuestion(Builder message) {
        message.getEdnsBuilder().setUdpPayloadSize(this.dataSource.getUdpPayloadSize()).setDnssecOk();
        message.setCheckingDisabled(true);
        return super.newQuestion(message);
    }

    /* access modifiers changed from: protected */
    public String isResponseAcceptable(DnsMessage response) {
        if (!response.isDnssecOk()) {
            return "DNSSEC OK (DO) flag not set in response";
        }
        if (!response.checkingDisabled) {
            return "CHECKING DISABLED (CD) flag not set in response";
        }
        return super.isResponseAcceptable(response);
    }

    public void addSecureEntryPoint(DnsName name, byte[] key) {
        this.knownSeps.put(name, key);
    }

    public void removeSecureEntryPoint(DnsName name) {
        this.knownSeps.remove(name);
    }

    public void clearSecureEntryPoints() {
        this.knownSeps.clear();
    }

    public boolean isStripSignatureRecords() {
        return this.stripSignatureRecords;
    }

    public void setStripSignatureRecords(boolean stripSignatureRecords2) {
        this.stripSignatureRecords = stripSignatureRecords2;
    }

    public void enableLookasideValidation() {
        configureLookasideValidation(DEFAULT_DLV);
    }

    public void disableLookasideValidation() {
        configureLookasideValidation(null);
    }

    public void configureLookasideValidation(DnsName dlv2) {
        this.dlv = dlv2;
    }
}
