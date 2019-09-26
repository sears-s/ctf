package org.bouncycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.Digest;

public final class BDS implements Serializable {
    private static final long serialVersionUID = 1;
    private List<XMSSNode> authenticationPath;
    private int index;
    private int k;
    private Map<Integer, XMSSNode> keep;
    private Map<Integer, LinkedList<XMSSNode>> retain;
    private XMSSNode root;
    private Stack<XMSSNode> stack;
    private final List<BDSTreeHash> treeHashInstances;
    private final int treeHeight;
    private boolean used;
    private transient WOTSPlus wotsPlus;

    private BDS(BDS bds, Digest digest) {
        this.wotsPlus = new WOTSPlus(new WOTSPlusParameters(digest));
        this.treeHeight = bds.treeHeight;
        this.k = bds.k;
        this.root = bds.root;
        this.authenticationPath = new ArrayList();
        this.authenticationPath.addAll(bds.authenticationPath);
        this.retain = bds.retain;
        this.stack = new Stack<>();
        this.stack.addAll(bds.stack);
        this.treeHashInstances = bds.treeHashInstances;
        this.keep = new TreeMap(bds.keep);
        this.index = bds.index;
        this.used = bds.used;
        validate();
    }

    private BDS(BDS bds, byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        this.wotsPlus = bds.wotsPlus;
        this.treeHeight = bds.treeHeight;
        this.k = bds.k;
        this.root = bds.root;
        this.authenticationPath = new ArrayList();
        this.authenticationPath.addAll(bds.authenticationPath);
        this.retain = bds.retain;
        this.stack = new Stack<>();
        this.stack.addAll(bds.stack);
        this.treeHashInstances = bds.treeHashInstances;
        this.keep = new TreeMap(bds.keep);
        this.index = bds.index;
        nextAuthenticationPath(bArr, bArr2, oTSHashAddress);
        bds.used = true;
    }

    private BDS(WOTSPlus wOTSPlus, int i, int i2) {
        this.wotsPlus = wOTSPlus;
        this.treeHeight = i;
        this.k = i2;
        if (i2 <= i && i2 >= 2) {
            int i3 = i - i2;
            if (i3 % 2 == 0) {
                this.authenticationPath = new ArrayList();
                this.retain = new TreeMap();
                this.stack = new Stack<>();
                this.treeHashInstances = new ArrayList();
                for (int i4 = 0; i4 < i3; i4++) {
                    this.treeHashInstances.add(new BDSTreeHash(i4));
                }
                this.keep = new TreeMap();
                this.index = 0;
                this.used = false;
                return;
            }
        }
        throw new IllegalArgumentException("illegal value for BDS parameter k");
    }

    BDS(XMSSParameters xMSSParameters, int i) {
        this(xMSSParameters.getWOTSPlus(), xMSSParameters.getHeight(), xMSSParameters.getK());
        this.index = i;
        this.used = true;
    }

    BDS(XMSSParameters xMSSParameters, byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        this(xMSSParameters.getWOTSPlus(), xMSSParameters.getHeight(), xMSSParameters.getK());
        initialize(bArr, bArr2, oTSHashAddress);
    }

    BDS(XMSSParameters xMSSParameters, byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress, int i) {
        this(xMSSParameters.getWOTSPlus(), xMSSParameters.getHeight(), xMSSParameters.getK());
        initialize(bArr, bArr2, oTSHashAddress);
        while (this.index < i) {
            nextAuthenticationPath(bArr, bArr2, oTSHashAddress);
            this.used = false;
        }
    }

    private BDSTreeHash getBDSTreeHashInstanceForUpdate() {
        BDSTreeHash bDSTreeHash = null;
        for (BDSTreeHash bDSTreeHash2 : this.treeHashInstances) {
            if (!bDSTreeHash2.isFinished() && bDSTreeHash2.isInitialized()) {
                if (bDSTreeHash == null || bDSTreeHash2.getHeight() < bDSTreeHash.getHeight() || (bDSTreeHash2.getHeight() == bDSTreeHash.getHeight() && bDSTreeHash2.getIndexLeaf() < bDSTreeHash.getIndexLeaf())) {
                    bDSTreeHash = bDSTreeHash2;
                }
            }
        }
        return bDSTreeHash;
    }

    private void initialize(byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        if (oTSHashAddress != null) {
            LTreeAddress lTreeAddress = (LTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).build();
            HashTreeAddress hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).build();
            for (int i = 0; i < (1 << this.treeHeight); i++) {
                oTSHashAddress = (OTSHashAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).withOTSAddress(i).withChainAddress(oTSHashAddress.getChainAddress()).withHashAddress(oTSHashAddress.getHashAddress()).withKeyAndMask(oTSHashAddress.getKeyAndMask())).build();
                WOTSPlus wOTSPlus = this.wotsPlus;
                wOTSPlus.importKeys(wOTSPlus.getWOTSPlusSecretKey(bArr2, oTSHashAddress), bArr);
                lTreeAddress = (LTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(lTreeAddress.getLayerAddress())).withTreeAddress(lTreeAddress.getTreeAddress())).withLTreeAddress(i).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask())).build();
                XMSSNode lTree = XMSSNodeUtil.lTree(this.wotsPlus, this.wotsPlus.getPublicKey(oTSHashAddress), lTreeAddress);
                hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress.getLayerAddress())).withTreeAddress(hashTreeAddress.getTreeAddress())).withTreeIndex(i).withKeyAndMask(hashTreeAddress.getKeyAndMask())).build();
                while (!this.stack.isEmpty() && ((XMSSNode) this.stack.peek()).getHeight() == lTree.getHeight()) {
                    int height = i / (1 << lTree.getHeight());
                    if (height == 1) {
                        this.authenticationPath.add(lTree.clone());
                    }
                    if (height == 3 && lTree.getHeight() < this.treeHeight - this.k) {
                        ((BDSTreeHash) this.treeHashInstances.get(lTree.getHeight())).setNode(lTree.clone());
                    }
                    if (height >= 3 && (height & 1) == 1 && lTree.getHeight() >= this.treeHeight - this.k && lTree.getHeight() <= this.treeHeight - 2) {
                        if (this.retain.get(Integer.valueOf(lTree.getHeight())) == null) {
                            LinkedList linkedList = new LinkedList();
                            linkedList.add(lTree.clone());
                            this.retain.put(Integer.valueOf(lTree.getHeight()), linkedList);
                        } else {
                            ((LinkedList) this.retain.get(Integer.valueOf(lTree.getHeight()))).add(lTree.clone());
                        }
                    }
                    HashTreeAddress hashTreeAddress2 = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress.getLayerAddress())).withTreeAddress(hashTreeAddress.getTreeAddress())).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask())).build();
                    XMSSNode randomizeHash = XMSSNodeUtil.randomizeHash(this.wotsPlus, (XMSSNode) this.stack.pop(), lTree, hashTreeAddress2);
                    hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress2.getLayerAddress())).withTreeAddress(hashTreeAddress2.getTreeAddress())).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask())).build();
                    lTree = new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue());
                }
                this.stack.push(lTree);
            }
            this.root = (XMSSNode) this.stack.pop();
            return;
        }
        throw new NullPointerException("otsHashAddress == null");
    }

    private void nextAuthenticationPath(byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        Object obj;
        List<XMSSNode> list;
        if (oTSHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        } else if (!this.used) {
            int i = this.index;
            int i2 = this.treeHeight;
            if (i <= (1 << i2) - 2) {
                int calculateTau = XMSSUtil.calculateTau(i, i2);
                if (((this.index >> (calculateTau + 1)) & 1) == 0 && calculateTau < this.treeHeight - 1) {
                    this.keep.put(Integer.valueOf(calculateTau), ((XMSSNode) this.authenticationPath.get(calculateTau)).clone());
                }
                LTreeAddress lTreeAddress = (LTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).build();
                HashTreeAddress hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).build();
                if (calculateTau == 0) {
                    oTSHashAddress = (OTSHashAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).withOTSAddress(this.index).withChainAddress(oTSHashAddress.getChainAddress()).withHashAddress(oTSHashAddress.getHashAddress()).withKeyAndMask(oTSHashAddress.getKeyAndMask())).build();
                    WOTSPlus wOTSPlus = this.wotsPlus;
                    wOTSPlus.importKeys(wOTSPlus.getWOTSPlusSecretKey(bArr2, oTSHashAddress), bArr);
                    this.authenticationPath.set(0, XMSSNodeUtil.lTree(this.wotsPlus, this.wotsPlus.getPublicKey(oTSHashAddress), (LTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(lTreeAddress.getLayerAddress())).withTreeAddress(lTreeAddress.getTreeAddress())).withLTreeAddress(this.index).withTreeHeight(lTreeAddress.getTreeHeight()).withTreeIndex(lTreeAddress.getTreeIndex()).withKeyAndMask(lTreeAddress.getKeyAndMask())).build()));
                } else {
                    int i3 = calculateTau - 1;
                    HashTreeAddress hashTreeAddress2 = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress.getLayerAddress())).withTreeAddress(hashTreeAddress.getTreeAddress())).withTreeHeight(i3).withTreeIndex(this.index >> calculateTau).withKeyAndMask(hashTreeAddress.getKeyAndMask())).build();
                    WOTSPlus wOTSPlus2 = this.wotsPlus;
                    wOTSPlus2.importKeys(wOTSPlus2.getWOTSPlusSecretKey(bArr2, oTSHashAddress), bArr);
                    XMSSNode randomizeHash = XMSSNodeUtil.randomizeHash(this.wotsPlus, (XMSSNode) this.authenticationPath.get(i3), (XMSSNode) this.keep.get(Integer.valueOf(i3)), hashTreeAddress2);
                    this.authenticationPath.set(calculateTau, new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue()));
                    this.keep.remove(Integer.valueOf(i3));
                    for (int i4 = 0; i4 < calculateTau; i4++) {
                        if (i4 < this.treeHeight - this.k) {
                            list = this.authenticationPath;
                            obj = ((BDSTreeHash) this.treeHashInstances.get(i4)).getTailNode();
                        } else {
                            list = this.authenticationPath;
                            obj = ((LinkedList) this.retain.get(Integer.valueOf(i4))).removeFirst();
                        }
                        list.set(i4, obj);
                    }
                    int min = Math.min(calculateTau, this.treeHeight - this.k);
                    for (int i5 = 0; i5 < min; i5++) {
                        int i6 = this.index + 1 + ((1 << i5) * 3);
                        if (i6 < (1 << this.treeHeight)) {
                            ((BDSTreeHash) this.treeHashInstances.get(i5)).initialize(i6);
                        }
                    }
                }
                for (int i7 = 0; i7 < ((this.treeHeight - this.k) >> 1); i7++) {
                    BDSTreeHash bDSTreeHashInstanceForUpdate = getBDSTreeHashInstanceForUpdate();
                    if (bDSTreeHashInstanceForUpdate != null) {
                        bDSTreeHashInstanceForUpdate.update(this.stack, this.wotsPlus, bArr, bArr2, oTSHashAddress);
                    }
                }
                this.index++;
                return;
            }
            throw new IllegalStateException("index out of bounds");
        } else {
            throw new IllegalStateException("index already used");
        }
    }

    private void validate() {
        if (this.authenticationPath == null) {
            throw new IllegalStateException("authenticationPath == null");
        } else if (this.retain == null) {
            throw new IllegalStateException("retain == null");
        } else if (this.stack == null) {
            throw new IllegalStateException("stack == null");
        } else if (this.treeHashInstances == null) {
            throw new IllegalStateException("treeHashInstances == null");
        } else if (this.keep == null) {
            throw new IllegalStateException("keep == null");
        } else if (!XMSSUtil.isIndexValid(this.treeHeight, (long) this.index)) {
            throw new IllegalStateException("index in BDS state out of bounds");
        }
    }

    /* access modifiers changed from: protected */
    public List<XMSSNode> getAuthenticationPath() {
        ArrayList arrayList = new ArrayList();
        for (XMSSNode clone : this.authenticationPath) {
            arrayList.add(clone.clone());
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public int getIndex() {
        return this.index;
    }

    public BDS getNextState(byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        return new BDS(this, bArr, bArr2, oTSHashAddress);
    }

    /* access modifiers changed from: protected */
    public XMSSNode getRoot() {
        return this.root.clone();
    }

    /* access modifiers changed from: protected */
    public int getTreeHeight() {
        return this.treeHeight;
    }

    /* access modifiers changed from: 0000 */
    public boolean isUsed() {
        return this.used;
    }

    public BDS withWOTSDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return new BDS(this, DigestUtil.getDigest(aSN1ObjectIdentifier));
    }
}
