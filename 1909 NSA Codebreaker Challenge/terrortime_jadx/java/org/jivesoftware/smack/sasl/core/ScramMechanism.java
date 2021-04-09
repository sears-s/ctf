package org.jivesoftware.smack.sasl.core;

import com.badguy.terrortime.BuildConfig;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.security.auth.callback.CallbackHandler;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.ByteUtils;
import org.jivesoftware.smack.util.SHA1;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jxmpp.util.cache.Cache;
import org.jxmpp.util.cache.LruCache;

public abstract class ScramMechanism extends SASLMechanism {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Cache<String, Keys> CACHE = new LruCache(10);
    private static final byte[] CLIENT_KEY_BYTES = toBytes("Client Key");
    private static final byte[] ONE = {0, 0, 0, 1};
    private static final int RANDOM_ASCII_BYTE_COUNT = 32;
    private static final ThreadLocal<SecureRandom> SECURE_RANDOM = new ThreadLocal<SecureRandom>() {
        /* access modifiers changed from: protected */
        public SecureRandom initialValue() {
            return new SecureRandom();
        }
    };
    private static final byte[] SERVER_KEY_BYTES = toBytes("Server Key");
    private String clientFirstMessageBare;
    private String clientRandomAscii;
    private final ScramHmac scramHmac;
    private byte[] serverSignature;
    private State state = State.INITIAL;

    /* renamed from: org.jivesoftware.smack.sasl.core.ScramMechanism$2 reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$sasl$core$ScramMechanism$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$sasl$core$ScramMechanism$State[State.AUTH_TEXT_SENT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$sasl$core$ScramMechanism$State[State.RESPONSE_SENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private static class Keys {
        /* access modifiers changed from: private */
        public final byte[] clientKey;
        /* access modifiers changed from: private */
        public final byte[] serverKey;

        Keys(byte[] clientKey2, byte[] serverKey2) {
            this.clientKey = clientKey2;
            this.serverKey = serverKey2;
        }
    }

    private enum State {
        INITIAL,
        AUTH_TEXT_SENT,
        RESPONSE_SENT,
        VALID_SERVER_RESPONSE
    }

    protected ScramMechanism(ScramHmac scramHmac2) {
        this.scramHmac = scramHmac2;
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal(CallbackHandler cbh) throws SmackException {
        throw new UnsupportedOperationException("CallbackHandler not (yet) supported");
    }

    /* access modifiers changed from: protected */
    public byte[] getAuthenticationText() throws SmackException {
        this.clientRandomAscii = getRandomAscii();
        String saslPrepedAuthcId = saslPrep(this.authenticationId);
        StringBuilder sb = new StringBuilder();
        sb.append("n=");
        sb.append(escape(saslPrepedAuthcId));
        sb.append(",r=");
        sb.append(this.clientRandomAscii);
        this.clientFirstMessageBare = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(getGS2Header());
        sb2.append(this.clientFirstMessageBare);
        String clientFirstMessage = sb2.toString();
        this.state = State.AUTH_TEXT_SENT;
        return toBytes(clientFirstMessage);
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append("SCRAM-");
        sb.append(this.scramHmac.getHmacName());
        return sb.toString();
    }

    public void checkIfSuccessfulOrThrow() throws SmackException {
        if (this.state != State.VALID_SERVER_RESPONSE) {
            throw new SmackException("SCRAM-SHA1 is missing valid server response");
        }
    }

    public boolean authzidSupported() {
        return true;
    }

    /* access modifiers changed from: protected */
    public byte[] evaluateChallenge(byte[] challenge) throws SmackException {
        byte[] serverKey;
        byte[] clientKey;
        try {
            String challengeString = new String(challenge, StringUtils.UTF8);
            int i = AnonymousClass2.$SwitchMap$org$jivesoftware$smack$sasl$core$ScramMechanism$State[this.state.ordinal()];
            if (i == 1) {
                String serverFirstMessage = challengeString;
                Map<Character, String> attributes = parseAttributes(challengeString);
                String rvalue = (String) attributes.get(Character.valueOf('r'));
                if (rvalue == null) {
                    throw new SmackException("Server random ASCII is null");
                } else if (rvalue.length() <= this.clientRandomAscii.length()) {
                    throw new SmackException("Server random ASCII is shorter then client random ASCII");
                } else if (rvalue.substring(0, this.clientRandomAscii.length()).equals(this.clientRandomAscii)) {
                    String iterationsString = (String) attributes.get(Character.valueOf('i'));
                    if (iterationsString != null) {
                        try {
                            int iterations = Integer.parseInt(iterationsString);
                            String salt = (String) attributes.get(Character.valueOf('s'));
                            if (salt != null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("c=");
                                sb.append(Base64.encodeToString(getCBindInput()));
                                String channelBinding = sb.toString();
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(channelBinding);
                                sb2.append(",r=");
                                sb2.append(rvalue);
                                String clientFinalMessageWithoutProof = sb2.toString();
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(this.clientFirstMessageBare);
                                sb3.append(',');
                                sb3.append(serverFirstMessage);
                                sb3.append(',');
                                sb3.append(clientFinalMessageWithoutProof);
                                byte[] authMessage = toBytes(sb3.toString());
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(this.password);
                                sb4.append(',');
                                sb4.append(salt);
                                sb4.append(',');
                                sb4.append(getName());
                                String cacheKey = sb4.toString();
                                Keys keys = (Keys) CACHE.lookup(cacheKey);
                                if (keys == null) {
                                    String str = challengeString;
                                    byte[] saltedPassword = hi(saslPrep(this.password), Base64.decode(salt), iterations);
                                    serverKey = hmac(saltedPassword, SERVER_KEY_BYTES);
                                    int i2 = iterations;
                                    clientKey = hmac(saltedPassword, CLIENT_KEY_BYTES);
                                    byte[] bArr = saltedPassword;
                                    CACHE.put(cacheKey, new Keys(clientKey, serverKey));
                                } else {
                                    String str2 = challengeString;
                                    serverKey = keys.serverKey;
                                    clientKey = keys.clientKey;
                                }
                                this.serverSignature = hmac(serverKey, authMessage);
                                byte[] storedKey = SHA1.bytes(clientKey);
                                byte[] clientSignature = hmac(storedKey, authMessage);
                                byte[] bArr2 = storedKey;
                                byte[] clientProof = new byte[clientKey.length];
                                int i3 = 0;
                                while (true) {
                                    String serverFirstMessage2 = serverFirstMessage;
                                    if (i3 < clientProof.length) {
                                        clientProof[i3] = (byte) (clientKey[i3] ^ clientSignature[i3]);
                                        i3++;
                                        serverFirstMessage = serverFirstMessage2;
                                    } else {
                                        StringBuilder sb5 = new StringBuilder();
                                        sb5.append(clientFinalMessageWithoutProof);
                                        sb5.append(",p=");
                                        sb5.append(Base64.encodeToString(clientProof));
                                        String clientFinalMessage = sb5.toString();
                                        this.state = State.RESPONSE_SENT;
                                        return toBytes(clientFinalMessage);
                                    }
                                }
                            } else {
                                String str3 = challengeString;
                                throw new SmackException("SALT not send");
                            }
                        } catch (NumberFormatException e) {
                            String str4 = challengeString;
                            String str5 = serverFirstMessage;
                            throw new SmackException("Exception parsing iterations", e);
                        }
                    } else {
                        throw new SmackException("Iterations attribute not set");
                    }
                } else {
                    throw new SmackException("Received client random ASCII does not match client random ASCII");
                }
            } else if (i == 2) {
                StringBuilder sb6 = new StringBuilder();
                sb6.append("v=");
                sb6.append(Base64.encodeToString(this.serverSignature));
                if (sb6.toString().equals(challengeString)) {
                    this.state = State.VALID_SERVER_RESPONSE;
                    return null;
                }
                throw new SmackException("Server final message does not match calculated one");
            } else {
                throw new SmackException("Invalid state");
            }
        } catch (UnsupportedEncodingException e2) {
            throw new AssertionError(e2);
        }
    }

    private String getGS2Header() {
        String authzidPortion = BuildConfig.FLAVOR;
        if (this.authorizationId != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("a=");
            sb.append(this.authorizationId);
            authzidPortion = sb.toString();
        }
        String cbName = getChannelBindingName();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(cbName);
        sb2.append(',');
        sb2.append(authzidPortion);
        sb2.append(",");
        return sb2.toString();
    }

    private byte[] getCBindInput() throws SmackException {
        byte[] cbindData = getChannelBindingData();
        byte[] gs2Header = toBytes(getGS2Header());
        if (cbindData == null) {
            return gs2Header;
        }
        return ByteUtils.concat(gs2Header, cbindData);
    }

    /* access modifiers changed from: protected */
    public String getChannelBindingName() {
        if (this.sslSession != null) {
            ConnectionConfiguration connectionConfiguration = this.connectionConfiguration;
            StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append("-PLUS");
            if (connectionConfiguration.isEnabledSaslMechanism(sb.toString())) {
                return "y";
            }
        }
        return "n";
    }

    /* access modifiers changed from: protected */
    public byte[] getChannelBindingData() throws SmackException {
        return null;
    }

    private static Map<Character, String> parseAttributes(String string) throws SmackException {
        if (string.length() == 0) {
            return Collections.emptyMap();
        }
        String[] keyValuePairs = string.split(",");
        Map<Character, String> res = new HashMap<>(keyValuePairs.length, 1.0f);
        int length = keyValuePairs.length;
        int i = 0;
        while (i < length) {
            String keyValuePair = keyValuePairs[i];
            String str = "Invalid Key-Value pair: ";
            if (keyValuePair.length() >= 3) {
                char key = keyValuePair.charAt(0);
                if (keyValuePair.charAt(1) == '=') {
                    res.put(Character.valueOf(key), keyValuePair.substring(2));
                    i++;
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(keyValuePair);
                    throw new SmackException(sb.toString());
                }
            } else {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append(keyValuePair);
                throw new SmackException(sb2.toString());
            }
        }
        return res;
    }

    /* access modifiers changed from: 0000 */
    public String getRandomAscii() {
        int count = 0;
        char[] randomAscii = new char[32];
        Random random = (Random) SECURE_RANDOM.get();
        while (count < 32) {
            char c = (char) random.nextInt(128);
            if (isPrintableNonCommaAsciiChar(c)) {
                int count2 = count + 1;
                randomAscii[count] = c;
                count = count2;
            }
        }
        return new String(randomAscii);
    }

    private static boolean isPrintableNonCommaAsciiChar(char c) {
        boolean z = false;
        if (c == ',') {
            return false;
        }
        if (c > ' ' && c < 127) {
            z = true;
        }
        return z;
    }

    private static String escape(String string) {
        StringBuilder sb = new StringBuilder((int) (((double) string.length()) * 1.1d));
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c == ',') {
                sb.append("=2C");
            } else if (c != '=') {
                sb.append(c);
            } else {
                sb.append("=3D");
            }
        }
        return sb.toString();
    }

    private byte[] hmac(byte[] key, byte[] str) throws SmackException {
        try {
            return this.scramHmac.hmac(key, str);
        } catch (InvalidKeyException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(getName());
            sb.append(" Exception");
            throw new SmackException(sb.toString(), e);
        }
    }

    private byte[] hi(String normalizedPassword, byte[] salt, int iterations) throws SmackException {
        try {
            byte[] key = normalizedPassword.getBytes(StringUtils.UTF8);
            byte[] u = hmac(key, ByteUtils.concat(salt, ONE));
            byte[] res = (byte[]) u.clone();
            for (int i = 1; i < iterations; i++) {
                u = hmac(key, u);
                for (int j = 0; j < u.length; j++) {
                    res[j] = (byte) (res[j] ^ u[j]);
                }
            }
            return res;
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError();
        }
    }
}
