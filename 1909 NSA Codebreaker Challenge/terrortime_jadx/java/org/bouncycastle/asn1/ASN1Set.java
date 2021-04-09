package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Set extends ASN1Primitive implements Iterable<ASN1Encodable> {
    private boolean isSorted;
    private Vector set;

    protected ASN1Set() {
        this.set = new Vector();
        this.isSorted = false;
    }

    protected ASN1Set(ASN1Encodable aSN1Encodable) {
        this.set = new Vector();
        this.isSorted = false;
        this.set.addElement(aSN1Encodable);
    }

    protected ASN1Set(ASN1EncodableVector aSN1EncodableVector, boolean z) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != aSN1EncodableVector.size(); i++) {
            this.set.addElement(aSN1EncodableVector.get(i));
        }
        if (z) {
            sort();
        }
    }

    protected ASN1Set(ASN1Encodable[] aSN1EncodableArr, boolean z) {
        this.set = new Vector();
        this.isSorted = false;
        for (int i = 0; i != aSN1EncodableArr.length; i++) {
            this.set.addElement(aSN1EncodableArr[i]);
        }
        if (z) {
            sort();
        }
    }

    private byte[] getDEREncoded(ASN1Encodable aSN1Encodable) {
        try {
            return aSN1Encodable.toASN1Primitive().getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }
    }

    public static ASN1Set getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1Set)) {
            return (ASN1Set) obj;
        }
        if (obj instanceof ASN1SetParser) {
            return getInstance(((ASN1SetParser) obj).toASN1Primitive());
        }
        if (obj instanceof byte[]) {
            try {
                return getInstance(ASN1Primitive.fromByteArray((byte[]) obj));
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("failed to construct set from byte[]: ");
                sb.append(e.getMessage());
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            if (obj instanceof ASN1Encodable) {
                ASN1Primitive aSN1Primitive = ((ASN1Encodable) obj).toASN1Primitive();
                if (aSN1Primitive instanceof ASN1Set) {
                    return (ASN1Set) aSN1Primitive;
                }
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("unknown object in getInstance: ");
            sb2.append(obj.getClass().getName());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    public static ASN1Set getInstance(ASN1TaggedObject aSN1TaggedObject, boolean z) {
        if (!z) {
            ASN1Primitive object = aSN1TaggedObject.getObject();
            if (aSN1TaggedObject.isExplicit()) {
                return aSN1TaggedObject instanceof BERTaggedObject ? new BERSet((ASN1Encodable) object) : new DLSet((ASN1Encodable) object);
            }
            if (object instanceof ASN1Set) {
                return (ASN1Set) object;
            }
            if (object instanceof ASN1Sequence) {
                ASN1Sequence aSN1Sequence = (ASN1Sequence) object;
                return aSN1TaggedObject instanceof BERTaggedObject ? new BERSet(aSN1Sequence.toArray()) : new DLSet(aSN1Sequence.toArray());
            }
            StringBuilder sb = new StringBuilder();
            sb.append("unknown object in getInstance: ");
            sb.append(aSN1TaggedObject.getClass().getName());
            throw new IllegalArgumentException(sb.toString());
        } else if (aSN1TaggedObject.isExplicit()) {
            return (ASN1Set) aSN1TaggedObject.getObject();
        } else {
            throw new IllegalArgumentException("object implicit - explicit expected.");
        }
    }

    private ASN1Encodable getNext(Enumeration enumeration) {
        ASN1Encodable aSN1Encodable = (ASN1Encodable) enumeration.nextElement();
        return aSN1Encodable == null ? DERNull.INSTANCE : aSN1Encodable;
    }

    private boolean lessThanOrEqual(byte[] bArr, byte[] bArr2) {
        int min = Math.min(bArr.length, bArr2.length);
        boolean z = false;
        for (int i = 0; i != min; i++) {
            if (bArr[i] != bArr2[i]) {
                if ((bArr[i] & 255) < (bArr2[i] & 255)) {
                    z = true;
                }
                return z;
            }
        }
        if (min == bArr.length) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1Set)) {
            return false;
        }
        ASN1Set aSN1Set = (ASN1Set) aSN1Primitive;
        if (size() != aSN1Set.size()) {
            return false;
        }
        Enumeration objects = getObjects();
        Enumeration objects2 = aSN1Set.getObjects();
        while (objects.hasMoreElements()) {
            ASN1Encodable next = getNext(objects);
            ASN1Encodable next2 = getNext(objects2);
            ASN1Primitive aSN1Primitive2 = next.toASN1Primitive();
            ASN1Primitive aSN1Primitive3 = next2.toASN1Primitive();
            if (aSN1Primitive2 != aSN1Primitive3 && !aSN1Primitive2.equals(aSN1Primitive3)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public abstract void encode(ASN1OutputStream aSN1OutputStream) throws IOException;

    public ASN1Encodable getObjectAt(int i) {
        return (ASN1Encodable) this.set.elementAt(i);
    }

    public Enumeration getObjects() {
        return this.set.elements();
    }

    public int hashCode() {
        Enumeration objects = getObjects();
        int size = size();
        while (objects.hasMoreElements()) {
            size = (size * 17) ^ getNext(objects).hashCode();
        }
        return size;
    }

    /* access modifiers changed from: 0000 */
    public boolean isConstructed() {
        return true;
    }

    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator(toArray());
    }

    public ASN1SetParser parser() {
        return new ASN1SetParser() {
            private int index;
            private final int max = ASN1Set.this.size();

            public ASN1Primitive getLoadedObject() {
                return this;
            }

            public ASN1Encodable readObject() throws IOException {
                int i = this.index;
                if (i == this.max) {
                    return null;
                }
                ASN1Set aSN1Set = ASN1Set.this;
                this.index = i + 1;
                ASN1Encodable objectAt = aSN1Set.getObjectAt(i);
                if (objectAt instanceof ASN1Sequence) {
                    return ((ASN1Sequence) objectAt).parser();
                }
                if (objectAt instanceof ASN1Set) {
                    objectAt = ((ASN1Set) objectAt).parser();
                }
                return objectAt;
            }

            public ASN1Primitive toASN1Primitive() {
                return this;
            }
        };
    }

    public int size() {
        return this.set.size();
    }

    /* access modifiers changed from: protected */
    /*  JADX ERROR: JadxOverflowException in pass: LoopRegionVisitor
        jadx.core.utils.exceptions.JadxOverflowException: LoopRegionVisitor.assignOnlyInLoop endless recursion
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:29)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:30)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
        	at jadx.core.ProcessClass.process(ProcessClass.java:35)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
        */
    public void sort() {
        /*
            r9 = this;
            boolean r0 = r9.isSorted
            if (r0 != 0) goto L_0x005d
            r0 = 1
            r9.isSorted = r0
            java.util.Vector r1 = r9.set
            int r1 = r1.size()
            if (r1 <= r0) goto L_0x005d
            java.util.Vector r1 = r9.set
            int r1 = r1.size()
            int r1 = r1 - r0
            r2 = r1
            r1 = r0
        L_0x0018:
            if (r1 == 0) goto L_0x005d
            java.util.Vector r1 = r9.set
            r3 = 0
            java.lang.Object r1 = r1.elementAt(r3)
            org.bouncycastle.asn1.ASN1Encodable r1 = (org.bouncycastle.asn1.ASN1Encodable) r1
            byte[] r1 = r9.getDEREncoded(r1)
            r5 = r1
            r1 = r3
            r4 = r1
        L_0x002a:
            if (r3 == r2) goto L_0x005a
            java.util.Vector r6 = r9.set
            int r7 = r3 + 1
            java.lang.Object r6 = r6.elementAt(r7)
            org.bouncycastle.asn1.ASN1Encodable r6 = (org.bouncycastle.asn1.ASN1Encodable) r6
            byte[] r6 = r9.getDEREncoded(r6)
            boolean r8 = r9.lessThanOrEqual(r5, r6)
            if (r8 == 0) goto L_0x0042
            r5 = r6
            goto L_0x0058
        L_0x0042:
            java.util.Vector r1 = r9.set
            java.lang.Object r1 = r1.elementAt(r3)
            java.util.Vector r4 = r9.set
            java.lang.Object r6 = r4.elementAt(r7)
            r4.setElementAt(r6, r3)
            java.util.Vector r4 = r9.set
            r4.setElementAt(r1, r7)
            r4 = r0
            r1 = r3
        L_0x0058:
            r3 = r7
            goto L_0x002a
        L_0x005a:
            r2 = r1
            r1 = r4
            goto L_0x0018
        L_0x005d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.asn1.ASN1Set.sort():void");
    }

    public ASN1Encodable[] toArray() {
        ASN1Encodable[] aSN1EncodableArr = new ASN1Encodable[size()];
        for (int i = 0; i != size(); i++) {
            aSN1EncodableArr[i] = getObjectAt(i);
        }
        return aSN1EncodableArr;
    }

    /* access modifiers changed from: 0000 */
    public ASN1Primitive toDERObject() {
        if (this.isSorted) {
            DERSet dERSet = new DERSet();
            dERSet.set = this.set;
            return dERSet;
        }
        Vector vector = new Vector();
        for (int i = 0; i != this.set.size(); i++) {
            vector.addElement(this.set.elementAt(i));
        }
        DERSet dERSet2 = new DERSet();
        dERSet2.set = vector;
        dERSet2.sort();
        return dERSet2;
    }

    /* access modifiers changed from: 0000 */
    public ASN1Primitive toDLObject() {
        DLSet dLSet = new DLSet();
        dLSet.set = this.set;
        return dLSet;
    }

    public String toString() {
        return this.set.toString();
    }
}
