package com.badguy.terrortime.crypto;

import android.support.v4.app.NotificationCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import com.badguy.terrortime.Message;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Messaging {
    private static final Optional<SecretKey> generateMessageKey() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        String str = "AES";
        SecretKey aes = null;
        try {
            aes = SecretKeyFactory.getInstance(str, new BouncyCastleProvider()).generateSecret(new SecretKeySpec(keyBytes, str));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e("generateMessageKey", "Unable to generate message key", e);
        }
        return Optional.ofNullable(aes);
    }

    public static final Optional<byte[]> encryptMessage(Message msg, Set<PublicKey> clientKeys, Set<PublicKey> contactKeys) {
        try {
            SecretKey msgKey = (SecretKey) generateMessageKey().orElseThrow($$Lambda$Messaging$peSwfA0s4V_o_UFWxRkaqv6FvqY.INSTANCE);
            HashMap<String, String> contactKeyMap = new HashMap<>();
            HashMap<String, String> clientKeyMap = new HashMap<>();
            for (PublicKey key : contactKeys) {
                contactKeyMap.put(CryptHelper.computeKeyFingerprint(key.getEncoded()), CryptHelper.wrapKey(key, msgKey));
            }
            for (PublicKey key2 : clientKeys) {
                clientKeyMap.put(CryptHelper.computeKeyFingerprint(key2.getEncoded()), CryptHelper.wrapKey(key2, msgKey));
            }
            JSONObject messageDoc = new JSONObject();
            JSONObject msgKeys = new JSONObject();
            JSONObject message = new JSONObject();
            JSONObject internalMessage = new JSONObject();
            JSONArray internalContactFingerprints = new JSONArray();
            JSONArray internalClientFingerprints = new JSONArray();
            for (Entry<String, String> entry : contactKeyMap.entrySet()) {
                msgKeys.put((String) entry.getKey(), entry.getValue());
                internalContactFingerprints.put(entry.getKey());
            }
            for (Entry<String, String> entry2 : clientKeyMap.entrySet()) {
                msgKeys.put((String) entry2.getKey(), entry2.getValue());
                internalClientFingerprints.put(entry2.getKey());
            }
            internalMessage.put(msg.getClientId(), internalClientFingerprints);
            internalMessage.put(msg.getContactId(), internalContactFingerprints);
            internalMessage.put("body", new String(msg.getContent()));
            Pair<byte[], byte[]> encMsg = CryptHelper.aesEncrypt(msgKey, internalMessage.toString().getBytes());
            String iv = Base64.getEncoder().encodeToString((byte[]) encMsg.first);
            byte[] messageSigBytes = CryptHelper.hmacSHA256(msgKey, (byte[]) encMsg.second);
            String encodedMsg = Base64.getEncoder().encodeToString((byte[]) encMsg.second);
            String encodedSig = Base64.getEncoder().encodeToString(messageSigBytes);
            message.put("iv", iv);
            message.put(NotificationCompat.CATEGORY_MESSAGE, encodedMsg);
            messageDoc.put("messageKey", msgKeys);
            messageDoc.put(org.jivesoftware.smack.packet.Message.ELEMENT, message);
            messageDoc.put("messageSig", encodedSig);
            return Optional.ofNullable(messageDoc.toString().getBytes());
        } catch (Throwable e) {
            Log.e("encryptMessage", "unable to encrypt message", e);
            return Optional.empty();
        }
    }

    static /* synthetic */ Exception lambda$encryptMessage$0() {
        return new Exception("Message key generation failed");
    }

    public static final Optional<byte[]> decryptMessage(Message msg, PrivateKey privateKey, String publicFingerprint) {
        String str = publicFingerprint;
        String str2 = "body";
        String str3 = "iv";
        String str4 = NotificationCompat.CATEGORY_MESSAGE;
        String str5 = "messageSig";
        String str6 = org.jivesoftware.smack.packet.Message.ELEMENT;
        String str7 = "messageKey";
        String str8 = "decryptMessage";
        try {
            String jsonString = new String(msg.getContent());
            JSONObject json = new JSONObject(jsonString);
            String str9 = "Invalid message spec";
            if (json.has(str7)) {
                if (json.has(str6)) {
                    if (!json.has(str5)) {
                        PrivateKey privateKey2 = privateKey;
                        String str10 = jsonString;
                        Log.v(str8, str9);
                        return Optional.empty();
                    }
                    JSONObject keyDict = json.getJSONObject(str7);
                    JSONObject messageDict = json.getJSONObject(str6);
                    if (!messageDict.has(str4)) {
                        PrivateKey privateKey3 = privateKey;
                        JSONObject jSONObject = messageDict;
                        String str11 = jsonString;
                    } else if (!messageDict.has(str3)) {
                        PrivateKey privateKey4 = privateKey;
                        JSONObject jSONObject2 = messageDict;
                        String str12 = jsonString;
                    } else if (!keyDict.has(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("No fingerprint found matching ");
                        sb.append(str);
                        Log.v(str8, sb.toString());
                        return Optional.empty();
                    } else {
                        try {
                            SecretKey msgKey = CryptHelper.unwrapKey(privateKey, keyDict.getString(str));
                            String encMessage = messageDict.getString(str4);
                            byte[] iv = Base64.getDecoder().decode(messageDict.getString(str3));
                            String sig = json.getString(str5);
                            byte[] decodedMessage = Base64.getDecoder().decode(encMessage);
                            byte[] hmacDigest = CryptHelper.hmacSHA256(msgKey, decodedMessage);
                            byte[] decodedSig = Base64.getDecoder().decode(sig);
                            if (Arrays.equals(hmacDigest, decodedSig)) {
                                byte[] bArr = decodedSig;
                                byte[] bArr2 = iv;
                                String str13 = encMessage;
                                byte[] decryptedMsg = CryptHelper.aesDecrypt(msgKey, decodedMessage, iv);
                                JSONObject internalMsg = new JSONObject(new String(decryptedMsg));
                                if (!internalMsg.has(msg.getClientId()) || !internalMsg.has(msg.getContactId()) || !internalMsg.has(str2)) {
                                    String str14 = sig;
                                    JSONObject jSONObject3 = messageDict;
                                    String str15 = jsonString;
                                    Log.v(str8, "Invalid internal msg format");
                                    return Optional.empty();
                                }
                                JSONArray clientFingerprints = internalMsg.getJSONArray(msg.getClientId());
                                byte[] bArr3 = decryptedMsg;
                                JSONArray contactFingerprints = internalMsg.getJSONArray(msg.getContactId());
                                String str16 = sig;
                                int i = 0;
                                while (true) {
                                    JSONObject messageDict2 = messageDict;
                                    String jsonString2 = jsonString;
                                    String jsonString3 = "Mismatched recipients";
                                    if (i >= clientFingerprints.length()) {
                                        for (int i2 = 0; i2 < contactFingerprints.length(); i2++) {
                                            if (!keyDict.has(contactFingerprints.getString(i2))) {
                                                Log.v(str8, jsonString3);
                                                return Optional.empty();
                                            }
                                        }
                                        return Optional.of(internalMsg.getString(str2).getBytes());
                                    } else if (!keyDict.has(clientFingerprints.getString(i))) {
                                        Log.v(str8, jsonString3);
                                        return Optional.empty();
                                    } else {
                                        i++;
                                        messageDict = messageDict2;
                                        jsonString = jsonString2;
                                    }
                                }
                            } else {
                                byte[] bArr4 = iv;
                                String str17 = encMessage;
                                String str18 = sig;
                                JSONObject jSONObject4 = messageDict;
                                String str19 = jsonString;
                                Log.v(str8, "HMAC signature does not match");
                                return Optional.empty();
                            }
                        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | JSONException e) {
                            e = e;
                            Log.e(str8, "unable to decrypt message", e);
                            return Optional.empty();
                        }
                    }
                    Log.v(str8, str9);
                    return Optional.empty();
                }
            }
            PrivateKey privateKey5 = privateKey;
            String str20 = jsonString;
            Log.v(str8, str9);
            return Optional.empty();
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | JSONException e2) {
            e = e2;
            PrivateKey privateKey6 = privateKey;
            Log.e(str8, "unable to decrypt message", e);
            return Optional.empty();
        }
    }
}
