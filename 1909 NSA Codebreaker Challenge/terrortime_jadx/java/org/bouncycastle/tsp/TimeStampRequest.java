package org.bouncycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampRequest {
    private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
    private Extensions extensions;
    private TimeStampReq req;

    public TimeStampRequest(InputStream inputStream) throws IOException {
        this(loadRequest(inputStream));
    }

    public TimeStampRequest(TimeStampReq timeStampReq) {
        this.req = timeStampReq;
        this.extensions = timeStampReq.getExtensions();
    }

    public TimeStampRequest(byte[] bArr) throws IOException {
        this((InputStream) new ByteArrayInputStream(bArr));
    }

    private Set convert(Set set) {
        if (set == null) {
            return set;
        }
        HashSet hashSet = new HashSet(set.size());
        for (Object next : set) {
            if (next instanceof String) {
                hashSet.add(new ASN1ObjectIdentifier((String) next));
            } else {
                hashSet.add(next);
            }
        }
        return hashSet;
    }

    private static TimeStampReq loadRequest(InputStream inputStream) throws IOException {
        String str = "malformed request: ";
        try {
            return TimeStampReq.getInstance(new ASN1InputStream(inputStream).readObject());
        } catch (ClassCastException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(e);
            throw new IOException(sb.toString());
        } catch (IllegalArgumentException e2) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(e2);
            throw new IOException(sb2.toString());
        }
    }

    public boolean getCertReq() {
        if (this.req.getCertReq() != null) {
            return this.req.getCertReq().isTrue();
        }
        return false;
    }

    public Set getCriticalExtensionOIDs() {
        Extensions extensions2 = this.extensions;
        return extensions2 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList(extensions2.getCriticalExtensionOIDs())));
    }

    public byte[] getEncoded() throws IOException {
        return this.req.getEncoded();
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        Extensions extensions2 = this.extensions;
        if (extensions2 != null) {
            return extensions2.getExtension(aSN1ObjectIdentifier);
        }
        return null;
    }

    public List getExtensionOIDs() {
        return TSPUtil.getExtensionOIDs(this.extensions);
    }

    /* access modifiers changed from: 0000 */
    public Extensions getExtensions() {
        return this.extensions;
    }

    public ASN1ObjectIdentifier getMessageImprintAlgOID() {
        return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
    }

    public byte[] getMessageImprintDigest() {
        return this.req.getMessageImprint().getHashedMessage();
    }

    public Set getNonCriticalExtensionOIDs() {
        Extensions extensions2 = this.extensions;
        return extensions2 == null ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList(extensions2.getNonCriticalExtensionOIDs())));
    }

    public BigInteger getNonce() {
        if (this.req.getNonce() != null) {
            return this.req.getNonce().getValue();
        }
        return null;
    }

    public ASN1ObjectIdentifier getReqPolicy() {
        if (this.req.getReqPolicy() != null) {
            return this.req.getReqPolicy();
        }
        return null;
    }

    public int getVersion() {
        return this.req.getVersion().getValue().intValue();
    }

    public boolean hasExtensions() {
        return this.extensions != null;
    }

    public void validate(Set set, Set set2, Set set3) throws TSPException {
        Set convert = convert(set);
        Set convert2 = convert(set2);
        Set convert3 = convert(set3);
        if (!convert.contains(getMessageImprintAlgOID())) {
            throw new TSPValidationException("request contains unknown algorithm", 128);
        } else if (convert2 == null || getReqPolicy() == null || convert2.contains(getReqPolicy())) {
            if (!(getExtensions() == null || convert3 == null)) {
                Enumeration oids = getExtensions().oids();
                while (oids.hasMoreElements()) {
                    if (!convert3.contains((ASN1ObjectIdentifier) oids.nextElement())) {
                        throw new TSPValidationException("request contains unknown extension", 8388608);
                    }
                }
            }
            if (TSPUtil.getDigestLength(getMessageImprintAlgOID().getId()) != getMessageImprintDigest().length) {
                throw new TSPValidationException("imprint digest the wrong length", 4);
            }
        } else {
            throw new TSPValidationException("request contains unknown policy", 256);
        }
    }
}
