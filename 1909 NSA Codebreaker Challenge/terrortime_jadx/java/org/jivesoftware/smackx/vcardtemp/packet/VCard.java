package org.jivesoftware.smackx.vcardtemp.packet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.stringencoder.Base64;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jxmpp.jid.EntityBareJid;

public class VCard extends IQ {
    private static final String DEFAULT_MIME_TYPE = "image/jpeg";
    public static final String ELEMENT = "vCard";
    private static final Logger LOGGER = Logger.getLogger(VCard.class.getName());
    public static final String NAMESPACE = "vcard-temp";
    private String emailHome;
    private String emailWork;
    private String firstName;
    private final Map<String, String> homeAddr = new HashMap();
    private final Map<String, String> homePhones = new HashMap();
    private String lastName;
    private String middleName;
    private String organization;
    private String organizationUnit;
    private final Map<String, String> otherSimpleFields = new HashMap();
    private final Map<String, String> otherUnescapableFields = new HashMap();
    private String photoBinval;
    private String photoMimeType;
    private String prefix;
    private String suffix;
    private final Map<String, String> workAddr = new HashMap();
    private final Map<String, String> workPhones = new HashMap();

    public VCard() {
        super("vCard", "vcard-temp");
    }

    public String getField(String field) {
        return (String) this.otherSimpleFields.get(field);
    }

    public void setField(String field, String value) {
        setField(field, value, false);
    }

    public void setField(String field, String value, boolean isUnescapable) {
        if (!isUnescapable) {
            this.otherSimpleFields.put(field, value);
        } else {
            this.otherUnescapableFields.put(field, value);
        }
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName2) {
        this.firstName = firstName2;
        updateFN();
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName2) {
        this.lastName = lastName2;
        updateFN();
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName2) {
        this.middleName = middleName2;
        updateFN();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix2) {
        this.prefix = prefix2;
        updateFN();
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix2) {
        this.suffix = suffix2;
        updateFN();
    }

    public String getNickName() {
        return (String) this.otherSimpleFields.get("NICKNAME");
    }

    public void setNickName(String nickName) {
        this.otherSimpleFields.put("NICKNAME", nickName);
    }

    public String getEmailHome() {
        return this.emailHome;
    }

    public void setEmailHome(String email) {
        this.emailHome = email;
    }

    public String getEmailWork() {
        return this.emailWork;
    }

    public void setEmailWork(String emailWork2) {
        this.emailWork = emailWork2;
    }

    public String getJabberId() {
        return (String) this.otherSimpleFields.get("JABBERID");
    }

    public void setJabberId(CharSequence jabberId) {
        this.otherSimpleFields.put("JABBERID", jabberId.toString());
    }

    public String getOrganization() {
        return this.organization;
    }

    public void setOrganization(String organization2) {
        this.organization = organization2;
    }

    public String getOrganizationUnit() {
        return this.organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit2) {
        this.organizationUnit = organizationUnit2;
    }

    public String getAddressFieldHome(String addrField) {
        return (String) this.homeAddr.get(addrField);
    }

    public void setAddressFieldHome(String addrField, String value) {
        this.homeAddr.put(addrField, value);
    }

    public String getAddressFieldWork(String addrField) {
        return (String) this.workAddr.get(addrField);
    }

    public void setAddressFieldWork(String addrField, String value) {
        this.workAddr.put(addrField, value);
    }

    public void setPhoneHome(String phoneType, String phoneNum) {
        this.homePhones.put(phoneType, phoneNum);
    }

    public String getPhoneHome(String phoneType) {
        return (String) this.homePhones.get(phoneType);
    }

    public void setPhoneWork(String phoneType, String phoneNum) {
        this.workPhones.put(phoneType, phoneNum);
    }

    public String getPhoneWork(String phoneType) {
        return (String) this.workPhones.get(phoneType);
    }

    public void setAvatar(URL avatarURL) {
        byte[] bytes = new byte[0];
        try {
            bytes = getBytes(avatarURL);
        } catch (IOException e) {
            Logger logger = LOGGER;
            Level level = Level.SEVERE;
            StringBuilder sb = new StringBuilder();
            sb.append("Error getting bytes from URL: ");
            sb.append(avatarURL);
            logger.log(level, sb.toString(), e);
        }
        setAvatar(bytes);
    }

    public void removeAvatar() {
        this.photoBinval = null;
        this.photoMimeType = null;
    }

    public void setAvatar(byte[] bytes) {
        setAvatar(bytes, DEFAULT_MIME_TYPE);
    }

    public void setAvatar(byte[] bytes, String mimeType) {
        if (bytes == null) {
            removeAvatar();
        } else {
            setAvatar(Base64.encodeToString(bytes), mimeType);
        }
    }

    public void setAvatar(String encodedImage, String mimeType) {
        this.photoBinval = encodedImage;
        this.photoMimeType = mimeType;
    }

    @Deprecated
    public void setEncodedImage(String encodedAvatar) {
        setAvatar(encodedAvatar, DEFAULT_MIME_TYPE);
    }

    public byte[] getAvatar() {
        String str = this.photoBinval;
        if (str == null) {
            return null;
        }
        return Base64.decode(str);
    }

    public String getAvatarMimeType() {
        return this.photoMimeType;
    }

    public static byte[] getBytes(URL url) throws IOException {
        File file = new File(url.getPath());
        if (file.exists()) {
            return getFileBytes(file);
        }
        return null;
    }

    private static byte[] getFileBytes(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[((int) file.length())];
            if (bis.read(buffer) == buffer.length) {
                bis.close();
                return buffer;
            }
            throw new IOException("Entire file not read");
        } catch (Throwable th) {
            if (bis != null) {
                bis.close();
            }
            throw th;
        }
    }

    public String getAvatarHash() {
        byte[] bytes = getAvatar();
        if (bytes == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(bytes);
            return StringUtils.encodeHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Failed to get message digest", e);
            return null;
        }
    }

    private void updateFN() {
        StringBuilder sb = new StringBuilder();
        String str = this.firstName;
        if (str != null) {
            sb.append(StringUtils.escapeForXml(str));
            sb.append(' ');
        }
        String str2 = this.middleName;
        if (str2 != null) {
            sb.append(StringUtils.escapeForXml(str2));
            sb.append(' ');
        }
        String str3 = this.lastName;
        if (str3 != null) {
            sb.append(StringUtils.escapeForXml(str3));
        }
        setField("FN", sb.toString());
    }

    @Deprecated
    public void save(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        VCardManager.getInstanceFor(connection).saveVCard(this);
    }

    @Deprecated
    public void load(XMPPConnection connection) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        load(connection, null);
    }

    @Deprecated
    public void load(XMPPConnection connection, EntityBareJid user) throws NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
        copyFieldsFrom(VCardManager.getInstanceFor(connection).loadVCard(user));
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        String str;
        String str2;
        if (!hasContent()) {
            xml.setEmptyElement();
            return xml;
        }
        xml.rightAngleBracket();
        if (hasNameField()) {
            String str3 = "N";
            xml.openElement(str3);
            xml.optElement("FAMILY", this.lastName);
            xml.optElement("GIVEN", this.firstName);
            xml.optElement("MIDDLE", this.middleName);
            xml.optElement("PREFIX", this.prefix);
            xml.optElement("SUFFIX", this.suffix);
            xml.closeElement(str3);
        }
        if (hasOrganizationFields()) {
            String str4 = "ORG";
            xml.openElement(str4);
            xml.optElement("ORGNAME", this.organization);
            xml.optElement("ORGUNIT", this.organizationUnit);
            xml.closeElement(str4);
        }
        for (Entry<String, String> entry : this.otherSimpleFields.entrySet()) {
            xml.optElement((String) entry.getKey(), (String) entry.getValue());
        }
        for (Entry<String, String> entry2 : this.otherUnescapableFields.entrySet()) {
            String value = (String) entry2.getValue();
            if (value != null) {
                xml.openElement((String) entry2.getKey());
                xml.append((CharSequence) value);
                xml.closeElement((String) entry2.getKey());
            }
        }
        if (this.photoBinval != null) {
            String str5 = "PHOTO";
            xml.openElement(str5);
            xml.escapedElement("BINVAL", this.photoBinval);
            xml.element("TYPE", this.photoMimeType);
            xml.closeElement(str5);
        }
        String str6 = "USERID";
        String str7 = "PREF";
        String str8 = "INTERNET";
        String str9 = "WORK";
        String str10 = "EMAIL";
        if (this.emailWork != null) {
            xml.openElement(str10);
            xml.emptyElement(str9);
            xml.emptyElement(str8);
            xml.emptyElement(str7);
            xml.element(str6, this.emailWork);
            xml.closeElement(str10);
        }
        String str11 = "HOME";
        if (this.emailHome != null) {
            xml.openElement(str10);
            xml.emptyElement(str11);
            xml.emptyElement(str8);
            xml.emptyElement(str7);
            xml.element(str6, this.emailHome);
            xml.closeElement(str10);
        }
        Iterator it = this.workPhones.entrySet().iterator();
        while (true) {
            str = "NUMBER";
            str2 = "TEL";
            if (!it.hasNext()) {
                break;
            }
            Entry<String, String> phone = (Entry) it.next();
            String number = (String) phone.getValue();
            if (number != null) {
                xml.openElement(str2);
                xml.emptyElement(str9);
                xml.emptyElement((String) phone.getKey());
                xml.element(str, number);
                xml.closeElement(str2);
            }
        }
        for (Entry<String, String> phone2 : this.homePhones.entrySet()) {
            String number2 = (String) phone2.getValue();
            if (number2 != null) {
                xml.openElement(str2);
                xml.emptyElement(str11);
                xml.emptyElement((String) phone2.getKey());
                xml.element(str, number2);
                xml.closeElement(str2);
            }
        }
        String str12 = "ADR";
        if (!this.workAddr.isEmpty()) {
            xml.openElement(str12);
            xml.emptyElement(str9);
            for (Entry<String, String> entry3 : this.workAddr.entrySet()) {
                String value2 = (String) entry3.getValue();
                if (value2 != null) {
                    xml.element((String) entry3.getKey(), value2);
                }
            }
            xml.closeElement(str12);
        }
        if (!this.homeAddr.isEmpty()) {
            xml.openElement(str12);
            xml.emptyElement(str11);
            for (Entry<String, String> entry4 : this.homeAddr.entrySet()) {
                String value3 = (String) entry4.getValue();
                if (value3 != null) {
                    xml.element((String) entry4.getKey(), value3);
                }
            }
            xml.closeElement(str12);
        }
        return xml;
    }

    private void copyFieldsFrom(VCard from) {
        Field[] fields;
        Class<VCard> cls = VCard.class;
        for (Field field : cls.getDeclaredFields()) {
            if (field.getDeclaringClass() == cls && !Modifier.isFinal(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    field.set(this, field.get(from));
                } catch (IllegalAccessException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("This cannot happen:");
                    sb.append(field);
                    throw new RuntimeException(sb.toString(), e);
                }
            }
        }
    }

    private boolean hasContent() {
        return hasNameField() || hasOrganizationFields() || this.emailHome != null || this.emailWork != null || this.otherSimpleFields.size() > 0 || this.otherUnescapableFields.size() > 0 || this.homeAddr.size() > 0 || this.homePhones.size() > 0 || this.workAddr.size() > 0 || this.workPhones.size() > 0 || this.photoBinval != null;
    }

    private boolean hasNameField() {
        return (this.firstName == null && this.lastName == null && this.middleName == null && this.prefix == null && this.suffix == null) ? false : true;
    }

    private boolean hasOrganizationFields() {
        return (this.organization == null && this.organizationUnit == null) ? false : true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VCard vCard = (VCard) o;
        String str = this.emailHome;
        if (str == null ? vCard.emailHome != null : !str.equals(vCard.emailHome)) {
            return false;
        }
        String str2 = this.emailWork;
        if (str2 == null ? vCard.emailWork != null : !str2.equals(vCard.emailWork)) {
            return false;
        }
        String str3 = this.firstName;
        if (str3 == null ? vCard.firstName != null : !str3.equals(vCard.firstName)) {
            return false;
        }
        if (!this.homeAddr.equals(vCard.homeAddr) || !this.homePhones.equals(vCard.homePhones)) {
            return false;
        }
        String str4 = this.lastName;
        if (str4 == null ? vCard.lastName != null : !str4.equals(vCard.lastName)) {
            return false;
        }
        String str5 = this.middleName;
        if (str5 == null ? vCard.middleName != null : !str5.equals(vCard.middleName)) {
            return false;
        }
        String str6 = this.organization;
        if (str6 == null ? vCard.organization != null : !str6.equals(vCard.organization)) {
            return false;
        }
        String str7 = this.organizationUnit;
        if (str7 == null ? vCard.organizationUnit != null : !str7.equals(vCard.organizationUnit)) {
            return false;
        }
        if (!this.otherSimpleFields.equals(vCard.otherSimpleFields) || !this.workAddr.equals(vCard.workAddr)) {
            return false;
        }
        String str8 = this.photoBinval;
        if (str8 == null ? vCard.photoBinval == null : str8.equals(vCard.photoBinval)) {
            return this.workPhones.equals(vCard.workPhones);
        }
        return false;
    }

    public int hashCode() {
        int result = ((((((this.homePhones.hashCode() * 29) + this.workPhones.hashCode()) * 29) + this.homeAddr.hashCode()) * 29) + this.workAddr.hashCode()) * 29;
        String str = this.firstName;
        int i = 0;
        int result2 = (result + (str != null ? str.hashCode() : 0)) * 29;
        String str2 = this.lastName;
        int result3 = (result2 + (str2 != null ? str2.hashCode() : 0)) * 29;
        String str3 = this.middleName;
        int result4 = (result3 + (str3 != null ? str3.hashCode() : 0)) * 29;
        String str4 = this.emailHome;
        int result5 = (result4 + (str4 != null ? str4.hashCode() : 0)) * 29;
        String str5 = this.emailWork;
        int result6 = (result5 + (str5 != null ? str5.hashCode() : 0)) * 29;
        String str6 = this.organization;
        int result7 = (result6 + (str6 != null ? str6.hashCode() : 0)) * 29;
        String str7 = this.organizationUnit;
        int result8 = (((result7 + (str7 != null ? str7.hashCode() : 0)) * 29) + this.otherSimpleFields.hashCode()) * 29;
        String str8 = this.photoBinval;
        if (str8 != null) {
            i = str8.hashCode();
        }
        return result8 + i;
    }
}
