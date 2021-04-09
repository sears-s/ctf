package org.jivesoftware.smack.sm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.Stanza;

public abstract class StreamManagementException extends SmackException {
    private static final long serialVersionUID = 3767590115788821101L;

    public static class StreamIdDoesNotMatchException extends StreamManagementException {
        private static final long serialVersionUID = 1191073341336559621L;

        public StreamIdDoesNotMatchException(String expected, String got) {
            StringBuilder sb = new StringBuilder();
            sb.append("Stream IDs do not match. Expected '");
            sb.append(expected);
            sb.append("', but got '");
            sb.append(got);
            sb.append("'");
            super(sb.toString());
        }
    }

    public static class StreamManagementCounterError extends StreamManagementException {
        private static final long serialVersionUID = 1;
        private final long ackedStanzaCount;
        private final List<Stanza> ackedStanzas;
        private final long handledCount;
        private final int outstandingStanzasCount;
        private final long previousServerHandledCount;

        public StreamManagementCounterError(long handledCount2, long previousServerHandlerCount, long ackedStanzaCount2, List<Stanza> ackedStanzas2) {
            String str;
            StringBuilder sb = new StringBuilder();
            sb.append("There was an error regarding the Stream Management counters. Server reported ");
            sb.append(handledCount2);
            sb.append(" handled stanzas, which means that the ");
            sb.append(ackedStanzaCount2);
            sb.append(" recently send stanzas by client are now acked by the server. But Smack had only ");
            sb.append(ackedStanzas2.size());
            sb.append(" to acknowledge. The stanza id of the last acked outstanding stanza is ");
            if (ackedStanzas2.isEmpty()) {
                str = "<no acked stanzas>";
            } else {
                str = ((Stanza) ackedStanzas2.get(ackedStanzas2.size() - 1)).getStanzaId();
            }
            sb.append(str);
            super(sb.toString());
            this.handledCount = handledCount2;
            this.previousServerHandledCount = previousServerHandlerCount;
            this.ackedStanzaCount = ackedStanzaCount2;
            this.outstandingStanzasCount = ackedStanzas2.size();
            this.ackedStanzas = Collections.unmodifiableList(ackedStanzas2);
        }

        public long getHandledCount() {
            return this.handledCount;
        }

        public long getPreviousServerHandledCount() {
            return this.previousServerHandledCount;
        }

        public long getAckedStanzaCount() {
            return this.ackedStanzaCount;
        }

        public int getOutstandingStanzasCount() {
            return this.outstandingStanzasCount;
        }

        public List<Stanza> getAckedStanzas() {
            return this.ackedStanzas;
        }
    }

    public static class StreamManagementNotEnabledException extends StreamManagementException {
        private static final long serialVersionUID = 2624821584352571307L;
    }

    public static final class UnacknowledgedQueueFullException extends StreamManagementException {
        private static final long serialVersionUID = 1;
        private final int droppedElements;
        private final List<Element> elements;
        private final int overflowElementNum;
        private final List<Stanza> unacknowledgesStanzas;

        private UnacknowledgedQueueFullException(String message, int overflowElementNum2, int droppedElements2, List<Element> elements2, List<Stanza> unacknowledgesStanzas2) {
            super(message);
            this.overflowElementNum = overflowElementNum2;
            this.droppedElements = droppedElements2;
            this.elements = elements2;
            this.unacknowledgesStanzas = unacknowledgesStanzas2;
        }

        public int getOverflowElementNum() {
            return this.overflowElementNum;
        }

        public int getDroppedElements() {
            return this.droppedElements;
        }

        public List<Element> getElements() {
            return this.elements;
        }

        public List<Stanza> getUnacknowledgesStanzas() {
            return this.unacknowledgesStanzas;
        }

        public static UnacknowledgedQueueFullException newWith(int overflowElementNum2, List<Element> elements2, BlockingQueue<Stanza> unacknowledgedStanzas) {
            int unacknowledgesStanzasQueueSize = unacknowledgedStanzas.size();
            ArrayList arrayList = new ArrayList(unacknowledgesStanzasQueueSize);
            arrayList.addAll(unacknowledgedStanzas);
            int droppedElements2 = (elements2.size() - overflowElementNum2) - 1;
            StringBuilder sb = new StringBuilder();
            sb.append("The queue size ");
            sb.append(unacknowledgesStanzasQueueSize);
            sb.append(" is not able to fit another ");
            sb.append(droppedElements2);
            sb.append(" potential stanzas type top-level stream-elements.");
            UnacknowledgedQueueFullException unacknowledgedQueueFullException = new UnacknowledgedQueueFullException(sb.toString(), overflowElementNum2, droppedElements2, elements2, arrayList);
            return unacknowledgedQueueFullException;
        }
    }

    public StreamManagementException() {
    }

    public StreamManagementException(String message) {
        super(message);
    }
}
