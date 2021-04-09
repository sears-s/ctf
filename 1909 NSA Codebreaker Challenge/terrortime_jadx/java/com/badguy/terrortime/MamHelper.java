package com.badguy.terrortime;

import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Body;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.MamManager.MamQueryArgs;
import org.jivesoftware.smackx.mam.MamManager.MamQueryArgs.Builder;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

public class MamHelper {
    public static /* synthetic */ ArrayList lambda$OGSS2qx6njxlnp0dnKb4lA3jnw8() {
        return new ArrayList();
    }

    public static List<Message> getMessageArchive() {
        TerrorTimeApplication app = TerrorTimeApplication.getInstance();
        try {
            MamManager mamManager = (MamManager) app.getMamManager().orElseThrow($$Lambda$MamHelper$TzcYVUR15MlXmeFc7Ce_u9xlcSI.INSTANCE);
            Jid clientJid = (Jid) ((ContactList) app.getContactList().orElseThrow($$Lambda$MamHelper$0ioGksqsduZg6APM76XVutM6p20.INSTANCE)).getUserJid().orElseThrow($$Lambda$MamHelper$IujRZy8pipkcFtIBvyXzqpoCoq8.INSTANCE);
            Builder queryArgs = MamQueryArgs.builder();
            queryArgs.setResultPageSizeTo(10000);
            return (List) mamManager.queryArchive(queryArgs.build()).getMessages().stream().map(new Function() {
                public final Object apply(Object obj) {
                    return MamHelper.lambda$getMessageArchive$4(Jid.this, (Message) obj);
                }
            }).collect(Collectors.toCollection($$Lambda$MamHelper$OGSS2qx6njxlnp0dnKb4lA3jnw8.INSTANCE));
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Failed to get message archive", e);
            return Collections.emptyList();
        }
    }

    static /* synthetic */ Exception lambda$getMessageArchive$0() {
        return new Exception("No mam manager object");
    }

    static /* synthetic */ Exception lambda$getMessageArchive$1() {
        return new Exception("No contact list object");
    }

    static /* synthetic */ Exception lambda$getMessageArchive$2() {
        return new Exception("No user jid");
    }

    static /* synthetic */ Message lambda$getMessageArchive$4(Jid clientJid, Message msg) {
        String body;
        BareJid to = msg.getTo().asBareJid();
        BareJid from = msg.getFrom().asBareJid();
        boolean fromClient = to.compareTo(clientJid.asBareJid()) != 0;
        String body2 = msg.getBody();
        if (body2 == null) {
            Set<Body> bodies = msg.getBodies();
            body = bodies == null ? BuildConfig.FLAVOR : (String) bodies.stream().filter($$Lambda$MamHelper$Ncanx43zkIa2eF9FP7dskyobBek.INSTANCE).map($$Lambda$MamHelper$l5OVETKytjsMGqc4b1miPg8OOk.INSTANCE).filter($$Lambda$MamHelper$ysW_fvT8H1f4ly8te8Kd64ujJxw.INSTANCE).collect(Collectors.joining());
        } else {
            body = body2;
        }
        Message ttMessage = new Message(clientJid.asBareJid().toString(), (fromClient ? to.asBareJid() : from.asBareJid()).toString(), body.getBytes(), fromClient, null);
        return ttMessage;
    }
}
