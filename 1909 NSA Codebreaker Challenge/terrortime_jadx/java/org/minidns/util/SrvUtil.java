package org.minidns.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.minidns.dnsname.DnsName;
import org.minidns.record.SRV;

public class SrvUtil {
    public static List<SRV> sortSrvRecords(Collection<SRV> srvRecords) {
        int selectedPosition;
        if (srvRecords.size() == 1 && ((SRV) srvRecords.iterator().next()).target.equals(DnsName.ROOT)) {
            return Collections.emptyList();
        }
        SortedMap<Integer, List<SRV>> buckets = new TreeMap<>();
        for (SRV srvRecord : srvRecords) {
            Integer priority = Integer.valueOf(srvRecord.priority);
            List list = (List) buckets.get(priority);
            if (list == null) {
                list = new LinkedList();
                buckets.put(priority, list);
            }
            list.add(srvRecord);
        }
        List<SRV> sortedSrvRecords = new ArrayList<>(srvRecords.size());
        for (List<SRV> bucket : buckets.values()) {
            while (true) {
                int size = bucket.size();
                int bucketSize = size;
                if (size > 0) {
                    int[] totals = new int[bucketSize];
                    int zeroWeight = 1;
                    Iterator it = bucket.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (((SRV) it.next()).weight > 0) {
                                zeroWeight = 0;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    int bucketWeightSum = 0;
                    int count = 0;
                    for (SRV srv : bucket) {
                        bucketWeightSum += srv.weight + zeroWeight;
                        int count2 = count + 1;
                        totals[count] = bucketWeightSum;
                        count = count2;
                    }
                    if (bucketWeightSum == 0) {
                        selectedPosition = (int) (Math.random() * ((double) bucketSize));
                    } else {
                        selectedPosition = bisect(totals, Math.random() * ((double) bucketWeightSum));
                    }
                    sortedSrvRecords.add((SRV) bucket.remove(selectedPosition));
                }
            }
        }
        return sortedSrvRecords;
    }

    private static int bisect(int[] array, double value) {
        int pos = 0;
        int length = array.length;
        int i = 0;
        while (i < length && value >= ((double) array[i])) {
            pos++;
            i++;
        }
        return pos;
    }
}
