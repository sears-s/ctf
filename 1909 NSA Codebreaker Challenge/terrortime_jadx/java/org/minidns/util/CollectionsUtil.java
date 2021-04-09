package org.minidns.util;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class CollectionsUtil {
    public static <T> T getRandomFrom(Set<T> set, Random random) {
        int randomIndex = random.nextInt(set.size());
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < randomIndex && iterator.hasNext(); i++) {
            iterator.next();
        }
        return iterator.next();
    }
}
