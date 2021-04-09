package org.jivesoftware.smack.util;

import java.util.Collection;

public class CollectionUtil {
    public static <T> Collection<T> requireNotEmpty(Collection<T> collection, String collectionName) {
        if (collection == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(collectionName);
            sb.append(" must not be null.");
            throw new NullPointerException(sb.toString());
        } else if (!collection.isEmpty()) {
            return collection;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(collectionName);
            sb2.append(" must not be empty.");
            throw new IllegalArgumentException(sb2.toString());
        }
    }
}
