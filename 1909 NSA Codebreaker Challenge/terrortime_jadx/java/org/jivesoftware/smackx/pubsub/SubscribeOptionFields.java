package org.jivesoftware.smackx.pubsub;

public enum SubscribeOptionFields {
    deliver,
    digest,
    digest_frequency,
    expire,
    include_body,
    show_values,
    subscription_type,
    subscription_depth;

    public String getFieldName() {
        String str = "pubsub#";
        if (this == show_values) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(toString().replace('_', '-'));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(toString());
        return sb2.toString();
    }

    public static SubscribeOptionFields valueOfFromElement(String elementName) {
        String portion = elementName.substring(elementName.lastIndexOf(36));
        if ("show-values".equals(portion)) {
            return show_values;
        }
        return valueOf(portion);
    }
}
