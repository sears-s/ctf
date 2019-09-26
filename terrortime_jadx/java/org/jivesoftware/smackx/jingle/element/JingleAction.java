package org.jivesoftware.smackx.jingle.element;

import java.util.HashMap;
import java.util.Map;

public enum JingleAction {
    content_accept,
    content_add,
    content_modify,
    content_reject,
    content_remove,
    description_info,
    security_info,
    session_accept,
    session_info,
    session_initiate,
    session_terminate,
    transport_accept,
    transport_info,
    transport_reject,
    transport_replace;
    
    private static final Map<String, JingleAction> map = null;
    private final String asString;

    static {
        int i;
        JingleAction[] values;
        map = new HashMap(values().length);
        for (JingleAction jingleAction : values()) {
            map.put(jingleAction.toString(), jingleAction);
        }
    }

    public String toString() {
        return this.asString;
    }

    public static JingleAction fromString(String string) {
        JingleAction jingleAction = (JingleAction) map.get(string);
        if (jingleAction != null) {
            return jingleAction;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown jingle action: ");
        sb.append(string);
        throw new IllegalArgumentException(sb.toString());
    }
}
