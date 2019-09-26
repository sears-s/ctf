package org.jivesoftware.smackx.iot.discovery;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smackx.iot.element.NodeInfo;
import org.jxmpp.jid.BareJid;

public class ThingState {
    /* access modifiers changed from: private */
    public final List<ThingStateChangeListener> listeners = new CopyOnWriteArrayList();
    private final NodeInfo nodeInfo;
    private BareJid owner;
    private BareJid registry;
    private boolean removed;

    ThingState(NodeInfo nodeInfo2) {
        this.nodeInfo = nodeInfo2;
    }

    /* access modifiers changed from: 0000 */
    public void setRegistry(BareJid registry2) {
        this.registry = registry2;
    }

    /* access modifiers changed from: 0000 */
    public void setUnregistered() {
        this.registry = null;
    }

    /* access modifiers changed from: 0000 */
    public void setOwner(final BareJid owner2) {
        this.owner = owner2;
        Async.go(new Runnable() {
            public void run() {
                for (ThingStateChangeListener thingStateChangeListener : ThingState.this.listeners) {
                    thingStateChangeListener.owned(owner2);
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void setUnowned() {
        this.owner = null;
    }

    /* access modifiers changed from: 0000 */
    public void setRemoved() {
        this.removed = true;
    }

    public NodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    public BareJid getRegistry() {
        return this.registry;
    }

    public BareJid getOwner() {
        return this.owner;
    }

    public boolean isOwned() {
        return this.owner != null;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public boolean setThingStateChangeListener(ThingStateChangeListener thingStateChangeListener) {
        return this.listeners.add(thingStateChangeListener);
    }

    public boolean removeThingStateChangeListener(ThingStateChangeListener thingStateChangeListener) {
        return this.listeners.remove(thingStateChangeListener);
    }
}
