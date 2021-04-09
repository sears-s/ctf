package com.badguy.terrortime;

import android.content.Context;
import android.support.v4.util.Pair;
import android.util.Log;
import com.badguy.terrortime.crypto.CryptHelper;
import com.badguy.terrortime.crypto.Keygen;
import com.badguy.terrortime.crypto.Messaging;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.jivesoftware.smack.util.StringUtils;

public class Client {
    private BlobAppField checkPin = new BlobAppField();
    private Set<PublicKey> clientKeySet = new HashSet();
    private List<Contact> contacts = new ArrayList();
    private TextAppField encryptPin = new TextAppField();
    private List<Message> messages = new ArrayList();
    private BlobAppField oAuth2AccessToken = new BlobAppField();
    private IntAppField oAuth2AccessTokenExpiration = new IntAppField();
    private TextAppField oAuth2ClientId = new TextAppField();
    private BlobAppField oAuth2ClientSecret = new BlobAppField();
    private BlobAppField oAuth2RenewToken = new BlobAppField();
    private IntAppField oAuth2RenewTokenExpiration = new IntAppField();
    private TextAppField oAuth2ServerIP = new TextAppField();
    private BlobAppField privateKey = new BlobAppField();
    private BlobAppField publicKey = new BlobAppField();
    private String publicKeyFingerprint = null;
    private TextAppField registerServerIP = new TextAppField();
    private PrivateKey rsaPrivateKey = null;
    private PublicKey rsaPublicKey = null;
    private TextAppField xmppServerIP = new TextAppField();
    private TextAppField xmppUserName = new TextAppField();

    public static /* synthetic */ ArrayList lambda$OGSS2qx6njxlnp0dnKb4lA3jnw8() {
        return new ArrayList();
    }

    public final Optional<PublicKey> getRsaPublicKey() {
        return Optional.ofNullable(this.rsaPublicKey);
    }

    public final Optional<PrivateKey> getRsaPrivateKey() {
        return Optional.ofNullable(this.rsaPrivateKey);
    }

    public final Optional<String> getPublicKeyFingerprint() {
        return Optional.ofNullable(this.publicKeyFingerprint);
    }

    public final Set<PublicKey> getPublicKeys() {
        return this.clientKeySet;
    }

    public final void setPublicKeys(Set<PublicKey> keySet) {
        this.clientKeySet = keySet;
    }

    public final boolean addPublicKey(PublicKey key) {
        return this.clientKeySet.add(key);
    }

    public final boolean addPublicKeys(Set<PublicKey> keySet) {
        return this.clientKeySet.addAll(keySet);
    }

    public final boolean removePublicKey(PublicKey key) {
        return this.clientKeySet.remove(key);
    }

    public final void setRsaPublicKey(PublicKey rsaPublicKey2) {
        this.rsaPublicKey = rsaPublicKey2;
        addPublicKey(rsaPublicKey2);
    }

    public final void setRsaPrivateKey(PrivateKey rsaPrivateKey2) {
        this.rsaPrivateKey = rsaPrivateKey2;
    }

    public final void setPublicKeyFingerprint(String publicKeyFingerprint2) {
        this.publicKeyFingerprint = publicKeyFingerprint2;
    }

    public final boolean generatePublicPrivateKeys() {
        try {
            Pair<String, String> keyPair = Keygen.generatePublicPrivateKeys();
            setPublicKey(((String) keyPair.first).getBytes());
            setPrivateKey(getEncryptPin(), ((String) keyPair.second).getBytes());
            Pair decodePEMKeyPair = CryptHelper.decodePEMKeyPair((String) keyPair.first, (String) keyPair.second);
            setRsaPublicKey((PublicKey) decodePEMKeyPair.first);
            setRsaPrivateKey((PrivateKey) decodePEMKeyPair.second);
            setPublicKeyFingerprint(CryptHelper.computeKeyFingerprint(this.rsaPublicKey.getEncoded()));
            return true;
        } catch (Throwable e) {
            Log.e("generatePublicPrivateKeys", "Failed to generate keypair", e);
            return false;
        }
    }

    static /* synthetic */ Exception lambda$getContactKeys$0() {
        return new Exception("Contact not found");
    }

    public final Set<PublicKey> getContactKeys(String contactId) {
        try {
            return ((Contact) getContact(contactId).orElseThrow($$Lambda$Client$oEoeCWrVIKmkWCtANnm11MAbGGc.INSTANCE)).getPublicKeys();
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to get contact's public key", e);
            return Collections.emptySet();
        }
    }

    public final boolean encryptMessage(Message msg) {
        if (msg == null) {
            return false;
        }
        try {
            Set<PublicKey> contactKeys = getContactKeys(msg.getContactId());
            if (!contactKeys.isEmpty()) {
                Set<PublicKey> clientKeys = getPublicKeys();
                if (!clientKeys.isEmpty()) {
                    msg.setContent((byte[]) Messaging.encryptMessage(msg, clientKeys, contactKeys).orElseThrow($$Lambda$Client$nqavyqI0yM7vNejtlFscX73W0w.INSTANCE));
                    return true;
                }
                throw new Exception("No public key for client");
            }
            throw new Exception("No public key for contact");
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to encrypt message", e);
            msg.setContent("<encryption failure>".getBytes());
            return false;
        }
    }

    static /* synthetic */ Exception lambda$encryptMessage$1() {
        return new Exception("Encryption error");
    }

    public final Optional<byte[]> decryptMessage(byte[] bytes) {
        Message msg = new Message(getXmppUserName(), BuildConfig.FLAVOR, bytes, true);
        if (!decryptMessage(msg)) {
            return Optional.empty();
        }
        return Optional.of(msg.getContent());
    }

    public final boolean decryptMessage(Message msg) {
        if (msg == null) {
            return false;
        }
        try {
            msg.setContent((byte[]) Messaging.decryptMessage(msg, (PrivateKey) getRsaPrivateKey().orElseThrow($$Lambda$Client$CA4KDL4ayDNawQixHsotezqSM.INSTANCE), (String) getPublicKeyFingerprint().orElseThrow($$Lambda$Client$K_BfC2wVH_wVfQH2jGk9LYOSi8I.INSTANCE)).orElseThrow($$Lambda$Client$6isRKf6DuVm8vF1iOvC0uukRRY4.INSTANCE));
            return true;
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to decrypt message", e);
            msg.setContent("<decryption failure>".getBytes());
            return false;
        }
    }

    static /* synthetic */ Exception lambda$decryptMessage$2() {
        return new Exception("No client private key");
    }

    static /* synthetic */ Exception lambda$decryptMessage$3() {
        return new Exception("No client fingerprint");
    }

    static /* synthetic */ Exception lambda$decryptMessage$4() {
        return new Exception("Decryption error");
    }

    public Client(String clientId) {
        if (clientId != null) {
            this.oAuth2ClientId.setValue(clientId);
            this.xmppUserName.setValue(clientId);
        }
    }

    public byte[] encryptClientBytes(String pin, byte[] data) throws Exception {
        byte[] rst = null;
        SecretKey dbKey = generateSymmetricKey();
        String str = "encryptClientBytes";
        if (data == null) {
            Log.d(str, "Empty (NULL) Client variable passed to function. This very well might NOT be an error.");
        } else {
            try {
                rst = CryptHelper.aesEncrypt_ECB(dbKey, data);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        if (data != null) {
            Log.d(str, "Ran successfully on non-NULL value.");
        }
        return rst;
    }

    public byte[] decryptClientBytes(String pin, byte[] data) throws Exception {
        byte[] rst = null;
        SecretKey dbKey = generateSymmetricKey();
        String str = "decryptClientBytes";
        if (data == null) {
            Log.d(str, "Empty (NULL) Client variable passed to function. This very well might NOT be an error.");
        } else {
            try {
                rst = CryptHelper.aesDecrypt_ECB(dbKey, data);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        if (data != null) {
            Log.d(str, "Ran successfully on non-NULL value.");
        }
        return rst;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client other = (Client) Client.class.cast(o);
        if (!this.registerServerIP.equals(other.registerServerIP) || !this.xmppUserName.equals(other.xmppUserName) || !this.xmppServerIP.equals(other.xmppServerIP) || !this.oAuth2ClientId.equals(other.oAuth2ClientId) || !this.oAuth2ClientSecret.equals(other.oAuth2ClientSecret) || !this.oAuth2AccessToken.equals(other.oAuth2AccessToken) || !this.oAuth2ServerIP.equals(other.oAuth2ServerIP) || !this.oAuth2AccessTokenExpiration.equals(other.oAuth2AccessTokenExpiration) || !this.oAuth2RenewTokenExpiration.equals(other.oAuth2RenewTokenExpiration) || !this.privateKey.equals(other.privateKey) || !this.publicKey.equals(other.publicKey) || !this.checkPin.equals(other.checkPin) || !this.encryptPin.equals(other.encryptPin)) {
            return false;
        }
        PrivateKey privateKey2 = this.rsaPrivateKey;
        if (privateKey2 == null || other.rsaPrivateKey == null) {
            if (this.rsaPrivateKey != null && other.rsaPrivateKey == null) {
                return false;
            }
            if (this.rsaPrivateKey == null && other.rsaPrivateKey != null) {
                return false;
            }
        } else if (!Arrays.equals(privateKey2.toString().getBytes(), other.rsaPrivateKey.toString().getBytes())) {
            return false;
        }
        PublicKey publicKey2 = this.rsaPublicKey;
        if (publicKey2 == null || other.rsaPublicKey == null) {
            if (this.rsaPublicKey != null && other.rsaPublicKey == null) {
                return false;
            }
            if (this.rsaPublicKey != null || other.rsaPublicKey == null) {
                return true;
            }
            return false;
        } else if (!Arrays.equals(publicKey2.toString().getBytes(), other.rsaPublicKey.toString().getBytes())) {
            return false;
        }
        return true;
    }

    public final SecretKey generateSymmetricKey() throws Exception {
        String str = "generateSymmetricKey: ";
        String str2 = "EXCEPTION LOG";
        String ePin = this.encryptPin.getValue();
        if (!this.encryptPin.isDefaultValue()) {
            byte[] salt = this.checkPin.getValue();
            byte[] hash = null;
            try {
                hash = MessageDigest.getInstance("SHA-256").digest(ePin.getBytes(StringUtils.UTF8));
                if (salt == null) {
                    salt = hash;
                    this.checkPin.setValue(salt);
                }
            } catch (Exception e) {
                Log.e(str2, str, e);
            }
            if (new BlobAppField(hash).equals(this.checkPin)) {
                try {
                    return new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(ePin.toCharArray(), salt, 10000, 256)).getEncoded(), "AES");
                } catch (Exception e2) {
                    Log.e(str2, str, e2);
                    throw new RuntimeException(e2);
                }
            } else {
                throw new RuntimeException("Invalid Pin");
            }
        } else {
            throw new RuntimeException("Unset Pin");
        }
    }

    public final String getEncryptPin() {
        return this.encryptPin.getValue();
    }

    public final void setEncryptPin(String pin) {
        if (pin != null) {
            this.encryptPin.setValue(pin);
        }
    }

    public final void unsetCheckPin() {
        this.checkPin.setValue(null);
    }

    public final byte[] getPrivateKey(String pin) throws Exception {
        return decryptClientBytes(pin, this.privateKey.getValue());
    }

    public final void setPrivateKey(String pin, byte[] privKey) throws Exception {
        if (privKey != null && pin != null) {
            this.privateKey.setValue(encryptClientBytes(pin, privKey));
            try {
                setRsaPrivateKey((PrivateKey) CryptHelper.convertPrivatePEMtoPrivateKey(new String(privKey)).orElseThrow($$Lambda$Client$63WDeVJkarpIOu65SgJm9WCk9ec.INSTANCE));
            } catch (Throwable e) {
                Log.e("EXCEPTION", e.getMessage(), e);
            }
        }
    }

    static /* synthetic */ Exception lambda$setPrivateKey$5() {
        return new Exception("Unable to convert key");
    }

    public final byte[] getPublicKey() {
        return this.publicKey.getValue();
    }

    public final void setPublicKey(byte[] pubKey) {
        if (pubKey != null) {
            this.publicKey.setValue(pubKey);
            try {
                setRsaPublicKey((PublicKey) CryptHelper.convertPublicPEMtoPublicKey(new String(pubKey)).orElseThrow($$Lambda$Client$OqAgwdbZoVbn91jchC8nAsBPmRw.INSTANCE));
                if (this.rsaPublicKey != null) {
                    setPublicKeyFingerprint(CryptHelper.computeKeyFingerprint(this.rsaPublicKey.getEncoded()));
                }
            } catch (Throwable e) {
                Log.e("EXCEPTION", "unable to set public key", e);
            }
        }
    }

    static /* synthetic */ Exception lambda$setPublicKey$6() {
        return new Exception("Unable to convert key");
    }

    public final byte[] getCheckPin() {
        return this.checkPin.getValue();
    }

    public final void setCheckPin(byte[] chkPin) {
        if (chkPin != null) {
            this.checkPin.setValue(chkPin);
        }
    }

    public final String getRegisterServerIP() {
        return this.registerServerIP.getValue();
    }

    public final void setRegisterServerIP(String regIP) {
        if (regIP != null) {
            this.registerServerIP.setValue(regIP);
        }
    }

    public final String getXmppUserName() {
        return this.xmppUserName.getValue();
    }

    public final void setXmppUserName(String userName) {
        TextAppField textAppField = this.xmppUserName;
        if (textAppField != null) {
            textAppField.setValue(userName);
        }
    }

    public final String getXmppServerIP() {
        return this.xmppServerIP.getValue();
    }

    public final void setXmppServerIP(String xmppIP) {
        if (xmppIP != null) {
            this.xmppServerIP.setValue(xmppIP);
        }
    }

    public final String getOAuth2ClientId() {
        return this.oAuth2ClientId.getValue();
    }

    public final void setOAuth2ClientId(String clientId) {
        if (clientId != null) {
            this.oAuth2ClientId.setValue(clientId);
        }
    }

    public final byte[] getOAuth2ClientSecret(String pin) throws Exception {
        return decryptClientBytes(pin, this.oAuth2ClientSecret.getValue());
    }

    public final void setOAuth2ClientSecret(String pin, byte[] clientSecret) throws Exception {
        if (clientSecret != null) {
            this.oAuth2ClientSecret.setValue(encryptClientBytes(pin, clientSecret));
        }
    }

    public final byte[] getOAuth2AccessToken(String pin) throws Exception {
        return decryptClientBytes(pin, this.oAuth2AccessToken.getValue());
    }

    public final void setOAuth2AccessToken(String pin, byte[] token) throws Exception {
        if (token != null) {
            this.oAuth2AccessToken.setValue(encryptClientBytes(pin, token));
        }
    }

    public final byte[] getOAuth2RenewToken(String pin) throws Exception {
        return decryptClientBytes(pin, this.oAuth2RenewToken.getValue());
    }

    public final void setOAuth2RenewToken(String pin, byte[] token) throws Exception {
        if (token != null) {
            this.oAuth2RenewToken.setValue(encryptClientBytes(pin, token));
        }
    }

    public final String getOAuth2ServerIP() {
        return this.oAuth2ServerIP.getValue();
    }

    public final void setOAuth2ServerIP(String oAuth2IP) {
        if (oAuth2IP != null) {
            this.oAuth2ServerIP.setValue(oAuth2IP);
        }
    }

    public final Integer getOAuth2AccessTokenExpiration() {
        return this.oAuth2AccessTokenExpiration.getValue();
    }

    public final void setOAuth2AccessTokenExpiration(Integer epochTime) {
        if (epochTime != null) {
            this.oAuth2AccessTokenExpiration.setValue(epochTime);
        }
    }

    public final Integer getOAuth2RenewTokenExpiration() {
        return this.oAuth2RenewTokenExpiration.getValue();
    }

    public final void setOAuth2RenewTokenExpiration(Integer epochTime) {
        if (epochTime != null) {
            this.oAuth2RenewTokenExpiration.setValue(epochTime);
        }
    }

    public final List<Contact> getContactList() {
        return this.contacts;
    }

    public final void setContactList(List<Contact> contactList) {
        if (contactList != null) {
            this.contacts = contactList;
        }
    }

    public final void addContact(String contactId) {
        if (contactId != null && !getContact(contactId).isPresent()) {
            this.contacts.add(new Contact(getOAuth2ClientId(), contactId));
        }
    }

    public final void addContact(Contact contact) {
        if (contact != null && !getContact(contact.getContactId()).isPresent()) {
            this.contacts.add(contact);
        }
    }

    public final Optional<Contact> getContact(String contactId) {
        if (contactId != null) {
            return this.contacts.stream().filter(new Predicate(contactId) {
                private final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return ((Contact) obj).getContactId().equals(this.f$0);
                }
            }).findFirst();
        }
        return Optional.empty();
    }

    public final void addAllContacts(List<Contact> contactList) {
        if (contactList != null) {
            contactList.forEach(new Consumer() {
                public final void accept(Object obj) {
                    Client.this.lambda$addAllContacts$8$Client((Contact) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$addAllContacts$8$Client(Contact contact) {
        contact.setClientId(getOAuth2ClientId());
        if (!getContact(contact.getContactId()).isPresent()) {
            this.contacts.add(contact);
        }
    }

    public final Long countContact(String contactId) {
        return Long.valueOf(this.messages.stream().filter(new Predicate(contactId) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((Message) obj).getContactId().equals(this.f$0);
            }
        }).count());
    }

    public final List<Message> getMessageList() {
        if (this.messages == null) {
            this.messages = new ArrayList();
        }
        return this.messages;
    }

    public final void setMessageList(List<Message> messagetList) {
        if (messagetList != null) {
            this.messages = messagetList;
        }
    }

    public final void addMessage(Message msg) {
        if (msg != null) {
            this.messages.add(new Message(getOAuth2ClientId(), msg.getContactId(), msg.getContent(), msg.isFromClient()));
        }
    }

    public final void addAllMessages(List<Message> messageList) {
        if (messageList != null) {
            for (Message m : messageList) {
                addMessage(m);
            }
        }
    }

    public final Integer countMessages(String contactId, boolean fromClient) {
        return Integer.valueOf(getMessages(contactId, fromClient).size());
    }

    public final List<Message> getMessages(String contactId, boolean fromClient) {
        return (List) this.messages.stream().filter(new Predicate(contactId, fromClient) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ boolean f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return Client.lambda$getMessages$10(this.f$0, this.f$1, (Message) obj);
            }
        }).collect(Collectors.toCollection($$Lambda$Client$OGSS2qx6njxlnp0dnKb4lA3jnw8.INSTANCE));
    }

    static /* synthetic */ boolean lambda$getMessages$10(String contactId, boolean fromClient, Message m) {
        return m.getContactId().equals(contactId) && m.isFromClient() == fromClient;
    }

    public final List<Message> getMessages(String contactId) {
        return (List) this.messages.stream().filter(new Predicate(contactId) {
            private final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return ((Message) obj).getContactId().equals(this.f$0);
            }
        }).collect(Collectors.toCollection($$Lambda$Client$OGSS2qx6njxlnp0dnKb4lA3jnw8.INSTANCE));
    }

    public void validateAccessToken(Context applicationContext) throws Exception {
        Context context = applicationContext;
        String tokenEndpoint = "/oauth2/token";
        String scope = "chat";
        if (context != null) {
            String pin = getEncryptPin();
            String[] serverIpAndPort = getOAuth2ServerIP().split(":");
            StringBuilder sb = new StringBuilder();
            sb.append("https://");
            sb.append(serverIpAndPort[0]);
            String httpsSite = sb.toString();
            String clientId = getOAuth2ClientId();
            String clientSecret = new String(getOAuth2ClientSecret(pin));
            int port = serverIpAndPort.length == 2 ? Integer.valueOf(serverIpAndPort[1]).intValue() : 443;
            try {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(httpsSite);
                sb2.append(tokenEndpoint);
                String sb3 = sb2.toString();
                r2 = r2;
                String str = httpsSite;
                String[] strArr = serverIpAndPort;
                try {
                    ClientCredentialTokenRequest clientCredentialTokenRequest = new ClientCredentialTokenRequest(applicationContext, sb3, clientId, clientSecret, "client_credentials", scope, BuildConfig.FLAVOR, port);
                    if (clientCredentialTokenRequest.getValidTokenAsByteArray(this, context) == null) {
                        throw new RuntimeException("Token Request failed.");
                    }
                } catch (Exception e) {
                    e = e;
                    throw new RuntimeException(e);
                }
            } catch (Exception e2) {
                e = e2;
                String str2 = httpsSite;
                String[] strArr2 = serverIpAndPort;
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Null context");
        }
    }

    public byte[] getEncrypted_oAuth2ClientSecret() {
        return this.oAuth2ClientSecret.getValue();
    }

    public void setEncrypted_oAuth2ClientSecret(byte[] value) {
        if (value != null) {
            this.oAuth2ClientSecret.setValue(value);
        }
    }

    public byte[] getEncrypted_oAuth2AccessToken() {
        return this.oAuth2AccessToken.getValue();
    }

    public void setEncrypted_oAuth2AccessToken(byte[] value) {
        this.oAuth2AccessToken.setValue(value);
    }

    public byte[] getEncrypted_privateKey() {
        return this.privateKey.getValue();
    }

    public void setEncrypted_privateKey(byte[] value) {
        if (value != null) {
            this.privateKey.setValue(value);
            try {
                setRsaPrivateKey((PrivateKey) CryptHelper.convertPrivatePEMtoPrivateKey(new String(getPrivateKey(getEncryptPin()))).orElseThrow($$Lambda$Client$pBdDMCuCdoHjV0KLOJ8jHAtVEag.INSTANCE));
            } catch (Throwable e) {
                Log.e("EXCEPTION", "unable to set private key", e);
            }
        }
    }

    static /* synthetic */ Exception lambda$setEncrypted_privateKey$12() {
        return new Exception("Unable to convert key");
    }

    public byte[] getEncrypted_oAuth2RenewToken() {
        return this.oAuth2RenewToken.getValue();
    }

    public void setEncrypted_oAuth2RenewToken(byte[] value) {
        if (value != null) {
            this.oAuth2RenewToken.setValue(value);
        }
    }
}
