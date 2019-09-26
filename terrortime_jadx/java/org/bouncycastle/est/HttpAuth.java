package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.AuthMechanism;
import org.jivesoftware.smack.sasl.packet.SaslStreamElements.Response;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.reference.element.ReferenceElement;
import org.jivesoftware.smackx.xdatavalidation.packet.ValidateElement.BasicValidateElement;

public class HttpAuth implements ESTAuth {
    private static final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private static final Set<String> validParts;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private final SecureRandom nonceGenerator;
    /* access modifiers changed from: private */
    public final char[] password;
    /* access modifiers changed from: private */
    public final String realm;
    /* access modifiers changed from: private */
    public final String username;

    static {
        HashSet hashSet = new HashSet();
        hashSet.add("realm");
        hashSet.add("nonce");
        hashSet.add("opaque");
        hashSet.add("algorithm");
        hashSet.add("qop");
        validParts = Collections.unmodifiableSet(hashSet);
    }

    public HttpAuth(String str, String str2, char[] cArr) {
        this(str, str2, cArr, null, null);
    }

    public HttpAuth(String str, String str2, char[] cArr, SecureRandom secureRandom, DigestCalculatorProvider digestCalculatorProvider2) {
        this.realm = str;
        this.username = str2;
        this.password = cArr;
        this.nonceGenerator = secureRandom;
        this.digestCalculatorProvider = digestCalculatorProvider2;
    }

    public HttpAuth(String str, char[] cArr) {
        this(null, str, cArr, null, null);
    }

    public HttpAuth(String str, char[] cArr, SecureRandom secureRandom, DigestCalculatorProvider digestCalculatorProvider2) {
        this(null, str, cArr, secureRandom, digestCalculatorProvider2);
    }

    /* access modifiers changed from: private */
    public ESTResponse doDigestFunction(ESTResponse eSTResponse) throws IOException {
        String str;
        String str2;
        String str3;
        Object next;
        ESTResponse eSTResponse2 = eSTResponse;
        String str4 = "WWW-Authenticate";
        String str5 = "Digest";
        eSTResponse.close();
        ESTRequest originalRequest = eSTResponse.getOriginalRequest();
        try {
            Map splitCSL = HttpUtil.splitCSL(str5, eSTResponse2.getHeader(str4));
            try {
                String path = originalRequest.getURL().toURI().getPath();
                Iterator it = splitCSL.keySet().iterator();
                do {
                    str = "'";
                    if (it.hasNext()) {
                        next = it.next();
                    } else {
                        String method = originalRequest.getMethod();
                        String str6 = "realm";
                        String str7 = (String) splitCSL.get(str6);
                        String str8 = "nonce";
                        String str9 = (String) splitCSL.get(str8);
                        String str10 = "opaque";
                        String str11 = (String) splitCSL.get(str10);
                        String str12 = "algorithm";
                        String str13 = (String) splitCSL.get(str12);
                        String str14 = "qop";
                        String str15 = (String) splitCSL.get(str14);
                        String str16 = str5;
                        ArrayList arrayList = new ArrayList();
                        String str17 = str10;
                        String str18 = this.realm;
                        String str19 = str11;
                        if (str18 == null || str18.equals(str7)) {
                            if (str13 == null) {
                                str13 = StringUtils.MD5;
                            }
                            if (str13.length() != 0) {
                                String upperCase = Strings.toUpperCase(str13);
                                if (str15 == null) {
                                    throw new ESTException("Qop is not defined in WWW-Authenticate header.");
                                } else if (str15.length() != 0) {
                                    String[] split = Strings.toLowerCase(str15).split(",");
                                    int i = 0;
                                    while (true) {
                                        int length = split.length;
                                        String str20 = str12;
                                        String str21 = AuthMechanism.ELEMENT;
                                        String str22 = str14;
                                        String str23 = "auth-int";
                                        if (i == length) {
                                            AlgorithmIdentifier lookupDigest = lookupDigest(upperCase);
                                            if (lookupDigest == null || lookupDigest.getAlgorithm() == null) {
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("auth digest algorithm unknown: ");
                                                sb.append(upperCase);
                                                throw new IOException(sb.toString());
                                            }
                                            DigestCalculator digestCalculator = getDigestCalculator(upperCase, lookupDigest);
                                            OutputStream outputStream = digestCalculator.getOutputStream();
                                            String makeNonce = makeNonce(10);
                                            String str24 = str8;
                                            update(outputStream, this.username);
                                            String str25 = ":";
                                            update(outputStream, str25);
                                            update(outputStream, str7);
                                            update(outputStream, str25);
                                            String str26 = str6;
                                            update(outputStream, this.password);
                                            outputStream.close();
                                            byte[] digest = digestCalculator.getDigest();
                                            if (upperCase.endsWith("-SESS")) {
                                                DigestCalculator digestCalculator2 = getDigestCalculator(upperCase, lookupDigest);
                                                OutputStream outputStream2 = digestCalculator2.getOutputStream();
                                                update(outputStream2, Hex.toHexString(digest));
                                                update(outputStream2, str25);
                                                update(outputStream2, str9);
                                                update(outputStream2, str25);
                                                update(outputStream2, makeNonce);
                                                outputStream2.close();
                                                digest = digestCalculator2.getDigest();
                                            }
                                            String hexString = Hex.toHexString(digest);
                                            DigestCalculator digestCalculator3 = getDigestCalculator(upperCase, lookupDigest);
                                            OutputStream outputStream3 = digestCalculator3.getOutputStream();
                                            String str27 = str7;
                                            if (((String) arrayList.get(0)).equals(str23)) {
                                                DigestCalculator digestCalculator4 = getDigestCalculator(upperCase, lookupDigest);
                                                str2 = str23;
                                                OutputStream outputStream4 = digestCalculator4.getOutputStream();
                                                originalRequest.writeData(outputStream4);
                                                outputStream4.close();
                                                byte[] digest2 = digestCalculator4.getDigest();
                                                update(outputStream3, method);
                                                update(outputStream3, str25);
                                                update(outputStream3, path);
                                                update(outputStream3, str25);
                                                update(outputStream3, Hex.toHexString(digest2));
                                            } else {
                                                str2 = str23;
                                                if (((String) arrayList.get(0)).equals(str21)) {
                                                    update(outputStream3, method);
                                                    update(outputStream3, str25);
                                                    update(outputStream3, path);
                                                }
                                            }
                                            outputStream3.close();
                                            String hexString2 = Hex.toHexString(digestCalculator3.getDigest());
                                            DigestCalculator digestCalculator5 = getDigestCalculator(upperCase, lookupDigest);
                                            OutputStream outputStream5 = digestCalculator5.getOutputStream();
                                            boolean contains = arrayList.contains("missing");
                                            String str28 = "00000001";
                                            update(outputStream5, hexString);
                                            update(outputStream5, str25);
                                            update(outputStream5, str9);
                                            update(outputStream5, str25);
                                            if (contains) {
                                                update(outputStream5, hexString2);
                                                str3 = str2;
                                            } else {
                                                update(outputStream5, str28);
                                                update(outputStream5, str25);
                                                update(outputStream5, makeNonce);
                                                update(outputStream5, str25);
                                                str3 = str2;
                                                if (((String) arrayList.get(0)).equals(str3)) {
                                                    update(outputStream5, str3);
                                                } else {
                                                    update(outputStream5, str21);
                                                }
                                                update(outputStream5, str25);
                                                update(outputStream5, hexString2);
                                            }
                                            outputStream5.close();
                                            String hexString3 = Hex.toHexString(digestCalculator5.getDigest());
                                            HashMap hashMap = new HashMap();
                                            hashMap.put("username", this.username);
                                            hashMap.put(str26, str27);
                                            hashMap.put(str24, str9);
                                            hashMap.put(ReferenceElement.ATTR_URI, path);
                                            hashMap.put(Response.ELEMENT, hexString3);
                                            String str29 = "nc";
                                            if (((String) arrayList.get(0)).equals(str3)) {
                                                hashMap.put(str22, str3);
                                            } else {
                                                String str30 = str22;
                                                if (((String) arrayList.get(0)).equals(str21)) {
                                                    hashMap.put(str30, str21);
                                                }
                                                hashMap.put(str20, upperCase);
                                                if (str19 == null || str19.length() == 0) {
                                                    hashMap.put(str17, makeNonce(20));
                                                }
                                                ESTRequestBuilder withHijacker = new ESTRequestBuilder(originalRequest).withHijacker(null);
                                                withHijacker.setHeader("Authorization", HttpUtil.mergeCSL(str16, hashMap));
                                                return originalRequest.getClient().doRequest(withHijacker.build());
                                            }
                                            hashMap.put(str29, str28);
                                            hashMap.put("cnonce", makeNonce);
                                            hashMap.put(str20, upperCase);
                                            hashMap.put(str17, makeNonce(20));
                                            ESTRequestBuilder withHijacker2 = new ESTRequestBuilder(originalRequest).withHijacker(null);
                                            withHijacker2.setHeader("Authorization", HttpUtil.mergeCSL(str16, hashMap));
                                            return originalRequest.getClient().doRequest(withHijacker2.build());
                                        } else if (split[i].equals(str21) || split[i].equals(str23)) {
                                            String trim = split[i].trim();
                                            if (!arrayList.contains(trim)) {
                                                arrayList.add(trim);
                                            }
                                            i++;
                                            str12 = str20;
                                            str14 = str22;
                                        } else {
                                            StringBuilder sb2 = new StringBuilder();
                                            sb2.append("QoP value unknown: '");
                                            sb2.append(i);
                                            sb2.append(str);
                                            throw new ESTException(sb2.toString());
                                        }
                                    }
                                } else {
                                    throw new ESTException("QoP value is empty.");
                                }
                            } else {
                                throw new ESTException("WWW-Authenticate no algorithm defined.");
                            }
                        } else {
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Supplied realm '");
                            sb3.append(this.realm);
                            sb3.append("' does not match server realm '");
                            sb3.append(str7);
                            sb3.append(str);
                            throw new ESTException(sb3.toString(), null, 401, null);
                        }
                    }
                } while (validParts.contains(next));
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Unrecognised entry in WWW-Authenticate header: '");
                sb4.append(next);
                sb4.append(str);
                throw new ESTException(sb4.toString());
            } catch (Exception e) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("unable to process URL in request: ");
                sb5.append(e.getMessage());
                throw new IOException(sb5.toString());
            }
        } catch (Throwable th) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append("Parsing WWW-Authentication header: ");
            sb6.append(th.getMessage());
            throw new ESTException(sb6.toString(), th, eSTResponse.getStatusCode(), new ByteArrayInputStream(eSTResponse2.getHeader(str4).getBytes()));
        }
    }

    private DigestCalculator getDigestCalculator(String str, AlgorithmIdentifier algorithmIdentifier) throws IOException {
        try {
            return this.digestCalculatorProvider.get(algorithmIdentifier);
        } catch (OperatorCreationException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("cannot create digest calculator for ");
            sb.append(str);
            sb.append(": ");
            sb.append(e.getMessage());
            throw new IOException(sb.toString());
        }
    }

    private AlgorithmIdentifier lookupDigest(String str) {
        String str2 = "-SESS";
        if (str.endsWith(str2)) {
            str = str.substring(0, str.length() - str2.length());
        }
        return str.equals("SHA-512-256") ? new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE) : digestAlgorithmIdentifierFinder.find(str);
    }

    private String makeNonce(int i) {
        byte[] bArr = new byte[i];
        this.nonceGenerator.nextBytes(bArr);
        return Hex.toHexString(bArr);
    }

    private void update(OutputStream outputStream, String str) throws IOException {
        outputStream.write(Strings.toUTF8ByteArray(str));
    }

    private void update(OutputStream outputStream, char[] cArr) throws IOException {
        outputStream.write(Strings.toUTF8ByteArray(cArr));
    }

    public void applyAuth(ESTRequestBuilder eSTRequestBuilder) {
        eSTRequestBuilder.withHijacker(new ESTHijacker() {
            public ESTResponse hijack(ESTRequest eSTRequest, Source source) throws IOException {
                ESTResponse eSTResponse;
                ESTResponse eSTResponse2 = new ESTResponse(eSTRequest, source);
                if (eSTResponse2.getStatusCode() != 401) {
                    return eSTResponse2;
                }
                String str = "WWW-Authenticate";
                String header = eSTResponse2.getHeader(str);
                if (header != null) {
                    String lowerCase = Strings.toLowerCase(header);
                    if (lowerCase.startsWith(CMSAttributeTableGenerator.DIGEST)) {
                        eSTResponse = HttpAuth.this.doDigestFunction(eSTResponse2);
                    } else if (lowerCase.startsWith(BasicValidateElement.METHOD)) {
                        eSTResponse2.close();
                        Map splitCSL = HttpUtil.splitCSL("Basic", eSTResponse2.getHeader(str));
                        if (HttpAuth.this.realm != null) {
                            String str2 = "realm";
                            if (!HttpAuth.this.realm.equals(splitCSL.get(str2))) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Supplied realm '");
                                sb.append(HttpAuth.this.realm);
                                sb.append("' does not match server realm '");
                                sb.append((String) splitCSL.get(str2));
                                sb.append("'");
                                throw new ESTException(sb.toString(), null, 401, null);
                            }
                        }
                        ESTRequestBuilder withHijacker = new ESTRequestBuilder(eSTRequest).withHijacker(null);
                        if (HttpAuth.this.realm != null && HttpAuth.this.realm.length() > 0) {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Basic realm=\"");
                            sb2.append(HttpAuth.this.realm);
                            sb2.append("\"");
                            withHijacker.setHeader(str, sb2.toString());
                        }
                        if (!HttpAuth.this.username.contains(":")) {
                            char[] cArr = new char[(HttpAuth.this.username.length() + 1 + HttpAuth.this.password.length)];
                            System.arraycopy(HttpAuth.this.username.toCharArray(), 0, cArr, 0, HttpAuth.this.username.length());
                            cArr[HttpAuth.this.username.length()] = ':';
                            System.arraycopy(HttpAuth.this.password, 0, cArr, HttpAuth.this.username.length() + 1, HttpAuth.this.password.length);
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Basic ");
                            sb3.append(Base64.toBase64String(Strings.toByteArray(cArr)));
                            withHijacker.setHeader("Authorization", sb3.toString());
                            eSTResponse = eSTRequest.getClient().doRequest(withHijacker.build());
                            Arrays.fill(cArr, 0);
                        } else {
                            throw new IllegalArgumentException("User must not contain a ':'");
                        }
                    } else {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("Unknown auth mode: ");
                        sb4.append(lowerCase);
                        throw new ESTException(sb4.toString());
                    }
                    return eSTResponse;
                }
                throw new ESTException("Status of 401 but no WWW-Authenticate header");
            }
        });
    }
}
