package org.jivesoftware.smack.sasl.provided;

import com.badguy.terrortime.BuildConfig;
import java.io.UnsupportedEncodingException;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.ByteUtils;
import org.jivesoftware.smack.util.MD5;
import org.jivesoftware.smack.util.StringUtils;

public class SASLDigestMD5Mechanism extends SASLMechanism {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String INITAL_NONCE = "00000001";
    public static final String NAME = "DIGEST-MD5";
    private static final String QOP_VALUE = "auth";
    private static boolean verifyServerResponse = true;
    private String cnonce;
    private String digestUri;
    private String hex_hashed_a1;
    private String nonce;
    private State state = State.INITIAL;

    /* renamed from: org.jivesoftware.smack.sasl.provided.SASLDigestMD5Mechanism$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$sasl$provided$SASLDigestMD5Mechanism$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$sasl$provided$SASLDigestMD5Mechanism$State[State.INITIAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$sasl$provided$SASLDigestMD5Mechanism$State[State.RESPONSE_SENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private enum DigestType {
        ClientResponse,
        ServerResponse
    }

    private enum State {
        INITIAL,
        RESPONSE_SENT,
        VALID_SERVER_RESPONSE
    }

    public static void setVerifyServerResponse(boolean verifyServerResponse2) {
        verifyServerResponse = verifyServerResponse2;
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
        throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        return null;
    }

    public String getName() {
        return "DIGEST-MD5";
    }

    public int getPriority() {
        return 210;
    }

    public SASLDigestMD5Mechanism newInstance() {
        return new SASLDigestMD5Mechanism();
    }

    public boolean authzidSupported() {
        return true;
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
        if (verifyServerResponse && this.state != State.VALID_SERVER_RESPONSE) {
            throw new SmackException("DIGEST-MD5 no valid server response");
        }
    }

    /* access modifiers changed from: protected */
    public byte[] evaluateChallenge(byte[] challenge) throws SmackException {
        String authzid;
        byte[] bArr = challenge;
        if (bArr.length != 0) {
            try {
                String[] challengeParts = new String(bArr, StringUtils.UTF8).split(",");
                int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$sasl$provided$SASLDigestMD5Mechanism$State[this.state.ordinal()];
                String str = "=";
                int i2 = 2;
                char c = 0;
                if (i == 1) {
                    int length = challengeParts.length;
                    int i3 = 0;
                    while (i3 < length) {
                        String[] keyValue = challengeParts[i3].split(str, i2);
                        String key = keyValue[c];
                        String str2 = BuildConfig.FLAVOR;
                        String key2 = key.replaceFirst("^\\s+", str2);
                        String value = keyValue[1];
                        String str3 = "\"";
                        if ("nonce".equals(key2)) {
                            if (this.nonce == null) {
                                this.nonce = value.replace(str3, str2);
                            } else {
                                throw new SmackException("Nonce value present multiple times");
                            }
                        } else if ("qop".equals(key2)) {
                            String value2 = value.replace(str3, str2);
                            if (!value2.equals("auth")) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Unsupported qop operation: ");
                                sb.append(value2);
                                throw new SmackException(sb.toString());
                            }
                        } else {
                            continue;
                        }
                        i3++;
                        i2 = 2;
                        c = 0;
                    }
                    if (this.nonce != null) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(this.authenticationId);
                        sb2.append(':');
                        sb2.append(this.serviceName);
                        sb2.append(':');
                        sb2.append(this.password);
                        byte[] a1FirstPart = MD5.bytes(sb2.toString());
                        this.cnonce = StringUtils.randomString(32);
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(':');
                        sb3.append(this.nonce);
                        sb3.append(':');
                        sb3.append(this.cnonce);
                        byte[] a1 = ByteUtils.concat(a1FirstPart, toBytes(sb3.toString()));
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("xmpp/");
                        sb4.append(this.serviceName);
                        this.digestUri = sb4.toString();
                        this.hex_hashed_a1 = StringUtils.encodeHex(MD5.bytes(a1));
                        String responseValue = calcResponse(DigestType.ClientResponse);
                        if (this.authorizationId == null) {
                            authzid = BuildConfig.FLAVOR;
                        } else {
                            StringBuilder sb5 = new StringBuilder();
                            sb5.append(",authzid=\"");
                            sb5.append(this.authorizationId);
                            sb5.append('\"');
                            authzid = sb5.toString();
                        }
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("username=\"");
                        sb6.append(quoteBackslash(this.authenticationId));
                        sb6.append('\"');
                        sb6.append(authzid);
                        sb6.append(",realm=\"");
                        sb6.append(this.serviceName);
                        sb6.append('\"');
                        sb6.append(",nonce=\"");
                        sb6.append(this.nonce);
                        sb6.append('\"');
                        sb6.append(",cnonce=\"");
                        sb6.append(this.cnonce);
                        sb6.append('\"');
                        sb6.append(",nc=");
                        sb6.append(INITAL_NONCE);
                        sb6.append(",qop=auth,digest-uri=\"");
                        sb6.append(this.digestUri);
                        sb6.append('\"');
                        sb6.append(",response=");
                        sb6.append(responseValue);
                        sb6.append(",charset=utf-8");
                        byte[] response = toBytes(sb6.toString());
                        this.state = State.RESPONSE_SENT;
                        return response;
                    }
                    throw new SmackException("nonce value not present in initial challenge");
                } else if (i == 2) {
                    if (verifyServerResponse) {
                        String serverResponse = null;
                        int length2 = challengeParts.length;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= length2) {
                                break;
                            }
                            String[] keyValue2 = challengeParts[i4].split(str);
                            String key3 = keyValue2[0];
                            String value3 = keyValue2[1];
                            if ("rspauth".equals(key3)) {
                                serverResponse = value3;
                                break;
                            }
                            i4++;
                        }
                        if (serverResponse == null) {
                            throw new SmackException("No server response received while performing DIGEST-MD5 authentication");
                        } else if (!serverResponse.equals(calcResponse(DigestType.ServerResponse))) {
                            throw new SmackException("Invalid server response  while performing DIGEST-MD5 authentication");
                        }
                    }
                    this.state = State.VALID_SERVER_RESPONSE;
                    return null;
                } else {
                    throw new IllegalStateException();
                }
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
        } else {
            throw new SmackException("Initial challenge has zero length");
        }
    }

    private String calcResponse(DigestType digestType) {
        StringBuilder a2 = new StringBuilder();
        if (digestType == DigestType.ClientResponse) {
            a2.append("AUTHENTICATE");
        }
        a2.append(':');
        a2.append(this.digestUri);
        String hex_hashed_a2 = StringUtils.encodeHex(MD5.bytes(a2.toString()));
        StringBuilder kd_argument = new StringBuilder();
        kd_argument.append(this.hex_hashed_a1);
        kd_argument.append(':');
        kd_argument.append(this.nonce);
        kd_argument.append(':');
        kd_argument.append(INITAL_NONCE);
        kd_argument.append(':');
        kd_argument.append(this.cnonce);
        kd_argument.append(':');
        kd_argument.append("auth");
        kd_argument.append(':');
        kd_argument.append(hex_hashed_a2);
        return StringUtils.encodeHex(MD5.bytes(kd_argument.toString()));
    }

    public static String quoteBackslash(String string) {
        return string.replace("\\", "\\\\");
    }
}
