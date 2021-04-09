package com.badguy.terrortime.crypto;

import android.support.v4.util.Pair;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

public class CryptHelper {
    public static Pair<PublicKey, PrivateKey> decodePEMKeyPair(String publicKey, String privateKey) throws Throwable {
        return new Pair<>((PublicKey) convertPublicPEMtoPublicKey(publicKey).orElseThrow($$Lambda$CryptHelper$vbNDnHFXcHxBXi36SJEI309CQ4E.INSTANCE), (PrivateKey) convertPrivatePEMtoPrivateKey(privateKey).orElseThrow($$Lambda$CryptHelper$NPqLhrm_ALq6B6zs86mS0C8aXlo.INSTANCE));
    }

    static /* synthetic */ Exception lambda$decodePEMKeyPair$0() {
        return new Exception("Bad key format");
    }

    static /* synthetic */ Exception lambda$decodePEMKeyPair$1() {
        return new Exception("Bad key format");
    }

    public static Optional<PublicKey> convertPublicPEMtoPublicKey(String publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PemObject pemPubKey = new PemReader(new StringReader(publicKey)).readPemObject();
        if (pemPubKey == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(KeyFactory.getInstance("RSA", new BouncyCastleProvider()).generatePublic(new X509EncodedKeySpec(pemPubKey.getContent())));
    }

    public static Optional<PrivateKey> convertPrivatePEMtoPrivateKey(String privateKey) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        PemObject pemPrivKey = new PemReader(new StringReader(privateKey)).readPemObject();
        if (pemPrivKey == null) {
            return Optional.ofNullable(null);
        }
        return Optional.ofNullable(KeyFactory.getInstance("RSA", new BouncyCastleProvider()).generatePrivate(new PKCS8EncodedKeySpec(pemPrivKey.getContent())));
    }

    public static String convertKeyToPEM(Key key) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(new JcaMiscPEMGenerator(key));
        pemWriter.flush();
        pemWriter.close();
        return stringWriter.toString();
    }

    public static final byte[] hmacSHA256(SecretKey key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        return mac.doFinal(message);
    }

    public static String computeKeyFingerprint(byte[] keyBytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(keyBytes);
        return Base64.getEncoder().encodeToString(md.digest());
    }

    public static String wrapKey(PublicKey publicKey, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException {
        Cipher keyCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        keyCipher.init(3, publicKey);
        return Base64.getEncoder().encodeToString(keyCipher.wrap(secretKey));
    }

    public static SecretKey unwrapKey(PrivateKey privateKey, String encKeyBlob) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, NoSuchProviderException {
        byte[] encKey = Base64.getDecoder().decode(encKeyBlob);
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        rsa.init(4, privateKey);
        return (SecretKey) rsa.unwrap(encKey, "AES", 3);
    }

    public static byte[] generateRandom(int bytes) {
        byte[] keyBytes = new byte[bytes];
        new SecureRandom().nextBytes(keyBytes);
        return keyBytes;
    }

    public static Pair<byte[], byte[]> aesEncrypt(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher msgCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = generateRandom(16);
        msgCipher.init(1, key, new IvParameterSpec(iv));
        return new Pair<>(iv, msgCipher.doFinal(data));
    }

    public static byte[] aesDecrypt(SecretKey key, byte[] data, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher msgCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        msgCipher.init(2, key, new IvParameterSpec(iv));
        return msgCipher.doFinal(data);
    }

    public static byte[] aesEncrypt_ECB(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher msgCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        msgCipher.init(1, key);
        return msgCipher.doFinal(data);
    }

    public static byte[] aesDecrypt_ECB(SecretKey key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher msgCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        msgCipher.init(2, key);
        return msgCipher.doFinal(data);
    }
}
