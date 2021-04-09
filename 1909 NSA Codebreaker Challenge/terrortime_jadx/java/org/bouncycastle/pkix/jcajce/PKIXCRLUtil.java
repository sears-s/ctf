package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.jcajce.PKIXCRLStoreSelector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

class PKIXCRLUtil {
    PKIXCRLUtil() {
    }

    private final Collection findCRLs(PKIXCRLStoreSelector pKIXCRLStoreSelector, List list) throws AnnotatedException {
        AnnotatedException annotatedException;
        HashSet hashSet = new HashSet();
        Throwable th = null;
        boolean z = false;
        for (Object next : list) {
            String str = "Exception searching in X.509 CRL store.";
            if (next instanceof Store) {
                try {
                    hashSet.addAll(((Store) next).getMatches(pKIXCRLStoreSelector));
                } catch (StoreException e) {
                    annotatedException = new AnnotatedException(str, e);
                    th = annotatedException;
                }
            } else {
                try {
                    hashSet.addAll(PKIXCRLStoreSelector.getCRLs(pKIXCRLStoreSelector, (CertStore) next));
                } catch (CertStoreException e2) {
                    annotatedException = new AnnotatedException(str, e2);
                }
            }
            z = true;
        }
        if (z || th == null) {
            return hashSet;
        }
        throw th;
    }

    public Set findCRLs(PKIXCRLStoreSelector pKIXCRLStoreSelector, Date date, List list, List list2) throws AnnotatedException {
        HashSet<X509CRL> hashSet = new HashSet<>();
        try {
            hashSet.addAll(findCRLs(pKIXCRLStoreSelector, list2));
            hashSet.addAll(findCRLs(pKIXCRLStoreSelector, list));
            HashSet hashSet2 = new HashSet();
            for (X509CRL x509crl : hashSet) {
                if (x509crl.getNextUpdate().after(date)) {
                    X509Certificate certificateChecking = pKIXCRLStoreSelector.getCertificateChecking();
                    if (certificateChecking == null || x509crl.getThisUpdate().before(certificateChecking.getNotAfter())) {
                        hashSet2.add(x509crl);
                    }
                }
            }
            return hashSet2;
        } catch (AnnotatedException e) {
            throw new AnnotatedException("Exception obtaining complete CRLs.", e);
        }
    }
}
