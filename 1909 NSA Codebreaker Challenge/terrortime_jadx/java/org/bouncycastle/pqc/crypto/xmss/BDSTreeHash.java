package org.bouncycastle.pqc.crypto.xmss;

import java.io.Serializable;
import java.util.Stack;

class BDSTreeHash implements Serializable {
    private static final long serialVersionUID = 1;
    private boolean finished = false;
    private int height;
    private final int initialHeight;
    private boolean initialized = false;
    private int nextIndex;
    private XMSSNode tailNode;

    BDSTreeHash(int i) {
        this.initialHeight = i;
    }

    /* access modifiers changed from: 0000 */
    public int getHeight() {
        return (!this.initialized || this.finished) ? ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED : this.height;
    }

    /* access modifiers changed from: 0000 */
    public int getIndexLeaf() {
        return this.nextIndex;
    }

    public XMSSNode getTailNode() {
        return this.tailNode.clone();
    }

    /* access modifiers changed from: 0000 */
    public void initialize(int i) {
        this.tailNode = null;
        this.height = this.initialHeight;
        this.nextIndex = i;
        this.initialized = true;
        this.finished = false;
    }

    /* access modifiers changed from: 0000 */
    public boolean isFinished() {
        return this.finished;
    }

    /* access modifiers changed from: 0000 */
    public boolean isInitialized() {
        return this.initialized;
    }

    /* access modifiers changed from: 0000 */
    public void setNode(XMSSNode xMSSNode) {
        this.tailNode = xMSSNode;
        this.height = xMSSNode.getHeight();
        if (this.height == this.initialHeight) {
            this.finished = true;
        }
    }

    /* access modifiers changed from: 0000 */
    public void update(Stack<XMSSNode> stack, WOTSPlus wOTSPlus, byte[] bArr, byte[] bArr2, OTSHashAddress oTSHashAddress) {
        if (oTSHashAddress == null) {
            throw new NullPointerException("otsHashAddress == null");
        } else if (this.finished || !this.initialized) {
            throw new IllegalStateException("finished or not initialized");
        } else {
            OTSHashAddress oTSHashAddress2 = (OTSHashAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress.getLayerAddress())).withTreeAddress(oTSHashAddress.getTreeAddress())).withOTSAddress(this.nextIndex).withChainAddress(oTSHashAddress.getChainAddress()).withHashAddress(oTSHashAddress.getHashAddress()).withKeyAndMask(oTSHashAddress.getKeyAndMask())).build();
            LTreeAddress lTreeAddress = (LTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress2.getLayerAddress())).withTreeAddress(oTSHashAddress2.getTreeAddress())).withLTreeAddress(this.nextIndex).build();
            HashTreeAddress hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) new Builder().withLayerAddress(oTSHashAddress2.getLayerAddress())).withTreeAddress(oTSHashAddress2.getTreeAddress())).withTreeIndex(this.nextIndex).build();
            wOTSPlus.importKeys(wOTSPlus.getWOTSPlusSecretKey(bArr2, oTSHashAddress2), bArr);
            XMSSNode lTree = XMSSNodeUtil.lTree(wOTSPlus, wOTSPlus.getPublicKey(oTSHashAddress2), lTreeAddress);
            while (!stack.isEmpty() && ((XMSSNode) stack.peek()).getHeight() == lTree.getHeight() && ((XMSSNode) stack.peek()).getHeight() != this.initialHeight) {
                HashTreeAddress hashTreeAddress2 = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress.getLayerAddress())).withTreeAddress(hashTreeAddress.getTreeAddress())).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask())).build();
                XMSSNode randomizeHash = XMSSNodeUtil.randomizeHash(wOTSPlus, (XMSSNode) stack.pop(), lTree, hashTreeAddress2);
                hashTreeAddress = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress2.getLayerAddress())).withTreeAddress(hashTreeAddress2.getTreeAddress())).withTreeHeight(hashTreeAddress2.getTreeHeight() + 1).withTreeIndex(hashTreeAddress2.getTreeIndex()).withKeyAndMask(hashTreeAddress2.getKeyAndMask())).build();
                lTree = new XMSSNode(randomizeHash.getHeight() + 1, randomizeHash.getValue());
            }
            XMSSNode xMSSNode = this.tailNode;
            if (xMSSNode == null) {
                this.tailNode = lTree;
            } else if (xMSSNode.getHeight() == lTree.getHeight()) {
                HashTreeAddress hashTreeAddress3 = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress.getLayerAddress())).withTreeAddress(hashTreeAddress.getTreeAddress())).withTreeHeight(hashTreeAddress.getTreeHeight()).withTreeIndex((hashTreeAddress.getTreeIndex() - 1) / 2).withKeyAndMask(hashTreeAddress.getKeyAndMask())).build();
                lTree = new XMSSNode(this.tailNode.getHeight() + 1, XMSSNodeUtil.randomizeHash(wOTSPlus, this.tailNode, lTree, hashTreeAddress3).getValue());
                this.tailNode = lTree;
                HashTreeAddress hashTreeAddress4 = (HashTreeAddress) ((Builder) ((Builder) ((Builder) new Builder().withLayerAddress(hashTreeAddress3.getLayerAddress())).withTreeAddress(hashTreeAddress3.getTreeAddress())).withTreeHeight(hashTreeAddress3.getTreeHeight() + 1).withTreeIndex(hashTreeAddress3.getTreeIndex()).withKeyAndMask(hashTreeAddress3.getKeyAndMask())).build();
            } else {
                stack.push(lTree);
            }
            if (this.tailNode.getHeight() == this.initialHeight) {
                this.finished = true;
                return;
            }
            this.height = lTree.getHeight();
            this.nextIndex++;
        }
    }
}
