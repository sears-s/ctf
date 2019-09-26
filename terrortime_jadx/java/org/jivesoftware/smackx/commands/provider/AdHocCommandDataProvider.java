package org.jivesoftware.smackx.commands.provider;

import com.badguy.terrortime.BuildConfig;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.commands.AdHocCommand.Action;
import org.jivesoftware.smackx.commands.AdHocCommand.SpecificErrorCondition;
import org.jivesoftware.smackx.commands.AdHocCommand.Status;
import org.jivesoftware.smackx.commands.AdHocCommandNote;
import org.jivesoftware.smackx.commands.AdHocCommandNote.Type;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData;
import org.jivesoftware.smackx.commands.packet.AdHocCommandData.SpecificError;
import org.jivesoftware.smackx.iot.data.element.NodeElement;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.xmlpull.v1.XmlPullParser;

public class AdHocCommandDataProvider extends IQProvider<AdHocCommandData> {

    public static class BadActionError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.badAction);
        }
    }

    public static class BadLocaleError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.badLocale);
        }
    }

    public static class BadPayloadError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.badPayload);
        }
    }

    public static class BadSessionIDError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.badSessionid);
        }
    }

    public static class MalformedActionError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.malformedAction);
        }
    }

    public static class SessionExpiredError extends ExtensionElementProvider<SpecificError> {
        public SpecificError parse(XmlPullParser parser, int initialDepth) {
            return new SpecificError(SpecificErrorCondition.sessionExpired);
        }
    }

    public AdHocCommandData parse(XmlPullParser parser, int initialDepth) throws Exception {
        Type type;
        boolean done = false;
        AdHocCommandData adHocCommandData = new AdHocCommandData();
        DataFormProvider dataFormProvider = new DataFormProvider();
        String str = BuildConfig.FLAVOR;
        adHocCommandData.setSessionID(parser.getAttributeValue(str, "sessionid"));
        adHocCommandData.setNode(parser.getAttributeValue(str, NodeElement.ELEMENT));
        String status = parser.getAttributeValue(str, "status");
        if (Status.executing.toString().equalsIgnoreCase(status)) {
            adHocCommandData.setStatus(Status.executing);
        } else if (Status.completed.toString().equalsIgnoreCase(status)) {
            adHocCommandData.setStatus(Status.completed);
        } else if (Status.canceled.toString().equalsIgnoreCase(status)) {
            adHocCommandData.setStatus(Status.canceled);
        }
        String action = parser.getAttributeValue(str, "action");
        if (action != null) {
            Action realAction = Action.valueOf(action);
            if (realAction == null || realAction.equals(Action.unknown)) {
                adHocCommandData.setAction(Action.unknown);
            } else {
                adHocCommandData.setAction(realAction);
            }
        }
        while (!done) {
            int eventType = parser.next();
            String elementName = parser.getName();
            String namespace = parser.getNamespace();
            if (eventType == 2) {
                if (parser.getName().equals("actions")) {
                    String execute = parser.getAttributeValue(str, "execute");
                    if (execute != null) {
                        adHocCommandData.setExecuteAction(Action.valueOf(execute));
                    }
                } else if (parser.getName().equals("next")) {
                    adHocCommandData.addAction(Action.next);
                } else if (parser.getName().equals("complete")) {
                    adHocCommandData.addAction(Action.complete);
                } else if (parser.getName().equals("prev")) {
                    adHocCommandData.addAction(Action.prev);
                } else if (elementName.equals("x") && namespace.equals("jabber:x:data")) {
                    adHocCommandData.setForm((DataForm) dataFormProvider.parse(parser));
                } else if (parser.getName().equals("note")) {
                    String typeString = parser.getAttributeValue(str, "type");
                    if (typeString != null) {
                        type = Type.valueOf(typeString);
                    } else {
                        type = Type.info;
                    }
                    adHocCommandData.addNote(new AdHocCommandNote(type, parser.nextText()));
                } else if (parser.getName().equals("error")) {
                    adHocCommandData.setError(PacketParserUtils.parseError(parser));
                }
            } else if (eventType == 3 && parser.getName().equals(AdHocCommandData.ELEMENT)) {
                done = true;
            }
        }
        return adHocCommandData;
    }
}
