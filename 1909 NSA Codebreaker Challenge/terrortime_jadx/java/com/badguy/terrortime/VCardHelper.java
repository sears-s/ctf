package com.badguy.terrortime;

import android.util.Log;
import com.badguy.terrortime.crypto.CryptHelper;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public class VCardHelper {
    public static boolean savePublicKey(PublicKey publicKey) {
        try {
            return savePublicKey(CryptHelper.convertKeyToPEM(publicKey));
        } catch (IOException e) {
            Log.e("EXCEPTION", "Failed to convert public key to PEM", e);
            return false;
        }
    }

    public static boolean savePublicKey(String publicKey) {
        String str = publicKey;
        String str2 = ":";
        String str3 = "DESC";
        TerrorTimeApplication app = TerrorTimeApplication.getInstance();
        try {
            VCardManager vCardManager = (VCardManager) app.getVCardManager().orElseThrow($$Lambda$VCardHelper$MFzlOa40tqBYrCyHPzQ0thn4IQ.INSTANCE);
            VCard vCard = vCardManager.loadVCard(((AbstractXMPPConnection) app.getXMPPTCPConnection().orElseThrow($$Lambda$VCardHelper$a8pL86RXMnQJ05gZCNGN1wTSYWI.INSTANCE)).getUser().asEntityBareJid());
            if (vCard == null) {
                vCard = new VCard();
            }
            String desc = vCard.getField(str3);
            if (desc != null) {
                for (String key : desc.split(str2)) {
                    if (Arrays.equals(key.getBytes(), publicKey.getBytes())) {
                        return true;
                    }
                }
                StringBuilder sb = new StringBuilder(str);
                sb.append(str2);
                sb.append(desc);
                vCard.setField(str3, sb.toString());
            } else {
                vCard.setField(str3, str);
            }
            vCardManager.saveVCard(vCard);
            return true;
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Error saving public key", e);
            return false;
        }
    }

    static /* synthetic */ Exception lambda$savePublicKey$0() {
        return new Exception("Unable to get VCardManager");
    }

    static /* synthetic */ Exception lambda$savePublicKey$1() {
        return new Exception("Connection is null");
    }

    public static Set<PublicKey> getPublicKeys(String jid) {
        TerrorTimeApplication app = TerrorTimeApplication.getInstance();
        try {
            return getPublicKeys(((Jid) Optional.ofNullable(((ContactList) app.getContactList().orElseThrow($$Lambda$VCardHelper$P6PWv0GkVlERoGvnHZoELRpWgaA.INSTANCE)).getJidFromString(jid).orElseGet(new Supplier(jid) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final Object get() {
                    return VCardHelper.lambda$getPublicKeys$4(AbstractXMPPConnection.this, this.f$1);
                }
            })).orElseThrow($$Lambda$VCardHelper$b5RGKcmB5jnxOAbd20Qar3Rppk.INSTANCE)).asEntityBareJidIfPossible());
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to get public key", e);
            return Collections.emptySet();
        }
    }

    static /* synthetic */ Exception lambda$getPublicKeys$2() {
        return new Exception("Connection is null");
    }

    static /* synthetic */ Exception lambda$getPublicKeys$3() {
        return new Exception("Contact list is null");
    }

    static /* synthetic */ Jid lambda$getPublicKeys$4(AbstractXMPPConnection connection, String jid) {
        if (connection.getUser().asBareJid().toString().equals(jid)) {
            return connection.getUser();
        }
        return null;
    }

    static /* synthetic */ Exception lambda$getPublicKeys$5() {
        return new Exception("No matching jid object found");
    }

    public static Set<PublicKey> getPublicKeys(EntityBareJid jid) {
        try {
            VCard vCard = ((VCardManager) TerrorTimeApplication.getInstance().getVCardManager().orElseThrow($$Lambda$VCardHelper$ceFVjXeHgtag1LCjQSmFTNzmU.INSTANCE)).loadVCard(jid);
            if (vCard == null) {
                return Collections.emptySet();
            }
            String publicKey = vCard.getField("DESC");
            if (publicKey == null) {
                return Collections.emptySet();
            }
            String[] keyArray = publicKey.split(":");
            Set<PublicKey> keySet = new HashSet<>();
            for (String key : keyArray) {
                keySet.add(CryptHelper.convertPublicPEMtoPublicKey(key).orElseThrow($$Lambda$VCardHelper$e79pltK3ySt8jVR8Oyuz_Ft4Mo0.INSTANCE));
            }
            return keySet;
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Error retrieving public key", e);
            return Collections.emptySet();
        }
    }

    static /* synthetic */ Exception lambda$getPublicKeys$6() {
        return new Exception("Unable to get VCardManager");
    }

    static /* synthetic */ Exception lambda$getPublicKeys$7() {
        return new Exception("Unable to convert key");
    }
}
