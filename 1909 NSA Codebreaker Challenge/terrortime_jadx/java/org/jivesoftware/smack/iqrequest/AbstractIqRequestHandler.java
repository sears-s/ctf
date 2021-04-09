package org.jivesoftware.smack.iqrequest;

import org.jivesoftware.smack.iqrequest.IQRequestHandler.Mode;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

public abstract class AbstractIqRequestHandler implements IQRequestHandler {
    private final String element;
    private final Mode mode;
    private final String namespace;
    private final Type type;

    /* renamed from: org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler$1 reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jivesoftware$smack$packet$IQ$Type = new int[Type.values().length];

        static {
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.set.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$jivesoftware$smack$packet$IQ$Type[Type.get.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public abstract IQ handleIQRequest(IQ iq);

    protected AbstractIqRequestHandler(String element2, String namespace2, Type type2, Mode mode2) {
        int i = AnonymousClass1.$SwitchMap$org$jivesoftware$smack$packet$IQ$Type[type2.ordinal()];
        if (i == 1 || i == 2) {
            this.element = element2;
            this.namespace = namespace2;
            this.type = type2;
            this.mode = mode2;
            return;
        }
        throw new IllegalArgumentException("Only get and set IQ type allowed");
    }

    public Mode getMode() {
        return this.mode;
    }

    public Type getType() {
        return this.type;
    }

    public String getElement() {
        return this.element;
    }

    public String getNamespace() {
        return this.namespace;
    }
}
