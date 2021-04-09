package org.jivesoftware.smackx.rsm;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.PacketUtil;
import org.jivesoftware.smackx.rsm.packet.RSMSet;
import org.jivesoftware.smackx.rsm.packet.RSMSet.PageDirection;

public class RSMManager {
    /* access modifiers changed from: 0000 */
    public Collection<ExtensionElement> page(int max) {
        List<ExtensionElement> packetExtensions = new LinkedList<>();
        packetExtensions.add(new RSMSet(max));
        return packetExtensions;
    }

    /* access modifiers changed from: 0000 */
    public Collection<ExtensionElement> continuePage(int max, Collection<ExtensionElement> returnedExtensions) {
        return continuePage(max, returnedExtensions, null);
    }

    /* access modifiers changed from: 0000 */
    public Collection<ExtensionElement> continuePage(int max, Collection<ExtensionElement> returnedExtensions, Collection<ExtensionElement> additionalExtensions) {
        if (returnedExtensions != null) {
            if (additionalExtensions == null) {
                additionalExtensions = new LinkedList<>();
            }
            RSMSet resultRsmSet = (RSMSet) PacketUtil.extensionElementFrom(returnedExtensions, "set", RSMSet.NAMESPACE);
            if (resultRsmSet != null) {
                additionalExtensions.add(new RSMSet(max, resultRsmSet.getLast(), PageDirection.after));
                return additionalExtensions;
            }
            throw new IllegalArgumentException("returnedExtensions did not contain a RSMset");
        }
        throw new IllegalArgumentException("returnedExtensions must no be null");
    }
}
