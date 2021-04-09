package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;

public class IQResultReplyFilter extends IQReplyFilter {
    public IQResultReplyFilter(IQ iqPacket, XMPPConnection conn) {
        super(iqPacket, conn);
    }

    public boolean accept(Stanza packet) {
        if (!super.accept(packet)) {
            return false;
        }
        return IQTypeFilter.RESULT.accept(packet);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" (");
        sb2.append(super.toString());
        sb2.append(')');
        sb.append(sb2.toString());
        return sb.toString();
    }
}
