package org.jivesoftware.smackx.disco.packet;

import com.badguy.terrortime.BuildConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.IQChildElementXmlStringBuilder;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.TypedCloneable;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jxmpp.util.XmppStringUtils;

public class DiscoverInfo extends IQ implements TypedCloneable<DiscoverInfo> {
    public static final String ELEMENT = "query";
    public static final String NAMESPACE = "http://jabber.org/protocol/disco#info";
    private boolean containsDuplicateFeatures;
    private final List<Feature> features = new LinkedList();
    private final Set<Feature> featuresSet = new HashSet();
    private final List<Identity> identities = new LinkedList();
    private final Set<String> identitiesSet = new HashSet();
    private String node;

    public static class Feature implements TypedCloneable<Feature> {
        private final String variable;

        public Feature(Feature feature) {
            this.variable = feature.variable;
        }

        public Feature(CharSequence variable2) {
            this(variable2.toString());
        }

        public Feature(String variable2) {
            this.variable = (String) StringUtils.requireNotNullOrEmpty(variable2, "variable cannot be null");
        }

        public String getVar() {
            return this.variable;
        }

        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement("feature");
            xml.attribute("var", this.variable);
            xml.closeEmptyElement();
            return xml;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            return this.variable.equals(((Feature) obj).variable);
        }

        public int hashCode() {
            return this.variable.hashCode();
        }

        public Feature clone() {
            return new Feature(this);
        }

        public String toString() {
            return toXML().toString();
        }
    }

    public static class Identity implements Comparable<Identity>, TypedCloneable<Identity> {
        private final String category;
        private final String key;
        private final String lang;
        private final String name;
        private final String type;

        public Identity(Identity identity) {
            this.category = identity.category;
            this.type = identity.type;
            this.key = identity.type;
            this.name = identity.name;
            this.lang = identity.lang;
        }

        public Identity(String category2, String type2) {
            this(category2, type2, null, null);
        }

        public Identity(String category2, String name2, String type2) {
            this(category2, type2, name2, null);
        }

        public Identity(String category2, String type2, String name2, String lang2) {
            this.category = (String) StringUtils.requireNotNullOrEmpty(category2, "category cannot be null");
            this.type = (String) StringUtils.requireNotNullOrEmpty(type2, "type cannot be null");
            this.key = XmppStringUtils.generateKey(category2, type2);
            this.name = name2;
            this.lang = lang2;
        }

        public String getCategory() {
            return this.category;
        }

        public String getName() {
            return this.name;
        }

        public String getType() {
            return this.type;
        }

        public String getLanguage() {
            return this.lang;
        }

        /* access modifiers changed from: private */
        public String getKey() {
            return this.key;
        }

        public boolean isOfCategoryAndType(String category2, String type2) {
            return this.category.equals(category2) && this.type.equals(type2);
        }

        public XmlStringBuilder toXML() {
            XmlStringBuilder xml = new XmlStringBuilder();
            xml.halfOpenElement("identity");
            xml.xmllangAttribute(this.lang);
            xml.attribute("category", this.category);
            xml.optAttribute("name", this.name);
            xml.optAttribute("type", this.type);
            xml.closeEmptyElement();
            return xml;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            Identity other = (Identity) obj;
            if (!this.key.equals(other.key)) {
                return false;
            }
            String otherLang = other.lang;
            String thisName = BuildConfig.FLAVOR;
            if (otherLang == null) {
                otherLang = thisName;
            }
            String thisLang = this.lang;
            if (thisLang == null) {
                thisLang = thisName;
            }
            if (!otherLang.equals(thisLang)) {
                return false;
            }
            String otherName = other.name;
            if (otherName == null) {
                otherName = thisName;
            }
            if (this.name != null) {
                thisName = other.name;
            }
            if (!thisName.equals(otherName)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int result = ((1 * 37) + this.key.hashCode()) * 37;
            String str = this.lang;
            int i = 0;
            int result2 = (result + (str == null ? 0 : str.hashCode())) * 37;
            String str2 = this.name;
            if (str2 != null) {
                i = str2.hashCode();
            }
            return result2 + i;
        }

        public int compareTo(Identity other) {
            String otherLang = other.lang;
            String thisType = BuildConfig.FLAVOR;
            if (otherLang == null) {
                otherLang = thisType;
            }
            String thisLang = this.lang;
            if (thisLang == null) {
                thisLang = thisType;
            }
            String otherType = other.type;
            if (otherType == null) {
                otherType = thisType;
            }
            String str = this.type;
            if (str != null) {
                thisType = str;
            }
            if (!this.category.equals(other.category)) {
                return this.category.compareTo(other.category);
            }
            if (!thisType.equals(otherType)) {
                return thisType.compareTo(otherType);
            }
            if (thisLang.equals(otherLang)) {
                return 0;
            }
            return thisLang.compareTo(otherLang);
        }

        public Identity clone() {
            return new Identity(this);
        }

        public String toString() {
            return toXML().toString();
        }
    }

    public DiscoverInfo() {
        super("query", NAMESPACE);
    }

    public DiscoverInfo(DiscoverInfo d) {
        super((IQ) d);
        setNode(d.getNode());
        for (Feature f : d.features) {
            addFeature(f.clone());
        }
        for (Identity i : d.identities) {
            addIdentity(i.clone());
        }
    }

    public boolean addFeature(String feature) {
        return addFeature(new Feature(feature));
    }

    public void addFeatures(Collection<String> featuresToAdd) {
        if (featuresToAdd != null) {
            for (String feature : featuresToAdd) {
                addFeature(feature);
            }
        }
    }

    public boolean addFeature(Feature feature) {
        this.features.add(feature);
        boolean featureIsNew = this.featuresSet.add(feature);
        if (!featureIsNew) {
            this.containsDuplicateFeatures = true;
        }
        return featureIsNew;
    }

    public List<Feature> getFeatures() {
        return Collections.unmodifiableList(this.features);
    }

    public void addIdentity(Identity identity) {
        this.identities.add(identity);
        this.identitiesSet.add(identity.getKey());
    }

    public void addIdentities(Collection<Identity> identitiesToAdd) {
        if (identitiesToAdd != null) {
            for (Identity identity : identitiesToAdd) {
                addIdentity(identity);
            }
        }
    }

    public List<Identity> getIdentities() {
        return Collections.unmodifiableList(this.identities);
    }

    public boolean hasIdentity(String category, String type) {
        return this.identitiesSet.contains(XmppStringUtils.generateKey(category, type));
    }

    public List<Identity> getIdentities(String category, String type) {
        List<Identity> res = new ArrayList<>(this.identities.size());
        for (Identity identity : this.identities) {
            if (identity.getCategory().equals(category) && identity.getType().equals(type)) {
                res.add(identity);
            }
        }
        return res;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String node2) {
        this.node = node2;
    }

    public boolean containsFeature(CharSequence feature) {
        return this.features.contains(new Feature(feature));
    }

    /* access modifiers changed from: protected */
    public IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.optAttribute(NodeElement.ELEMENT, getNode());
        xml.rightAngleBracket();
        for (Identity identity : this.identities) {
            xml.append(identity.toXML());
        }
        for (Feature feature : this.features) {
            xml.append(feature.toXML());
        }
        return xml;
    }

    public boolean containsDuplicateIdentities() {
        List<Identity> checkedIdentities = new LinkedList<>();
        for (Identity i : this.identities) {
            for (Identity i2 : checkedIdentities) {
                if (i.equals(i2)) {
                    return true;
                }
            }
            checkedIdentities.add(i);
        }
        return false;
    }

    public boolean containsDuplicateFeatures() {
        return this.containsDuplicateFeatures;
    }

    public DiscoverInfo clone() {
        return new DiscoverInfo(this);
    }
}
