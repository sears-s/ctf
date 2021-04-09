package org.jivesoftware.smack.roster;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jivesoftware.smack.SmackException.FeatureNotSupportedException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.SubscribeListener.SubscribeAnswer;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;

public class RosterUtil {
    public static void waitUntilOtherEntityIsSubscribed(Roster roster, BareJid otherEntity, long timeoutMillis) throws InterruptedException, TimeoutException {
        waitUntilOtherEntityIsSubscribed(roster, otherEntity, new Date(System.currentTimeMillis() + timeoutMillis));
    }

    public static void waitUntilOtherEntityIsSubscribed(Roster roster, BareJid otherEntity, Date deadline) throws InterruptedException, TimeoutException {
        final Lock lock = new ReentrantLock();
        final Condition maybeSubscribed = lock.newCondition();
        RosterListener rosterListener = new AbstractRosterListener() {
            private void signal() {
                lock.lock();
                try {
                    maybeSubscribed.signal();
                } finally {
                    lock.unlock();
                }
            }

            public void entriesAdded(Collection<Jid> collection) {
                signal();
            }

            public void entriesUpdated(Collection<Jid> collection) {
                signal();
            }
        };
        roster.addRosterListener(rosterListener);
        boolean stillWaiting = true;
        lock.lock();
        while (!roster.isSubscribedToMyPresence(otherEntity)) {
            try {
                if (stillWaiting) {
                    stillWaiting = maybeSubscribed.awaitUntil(deadline);
                } else {
                    throw new TimeoutException();
                }
            } finally {
                lock.unlock();
                roster.removeRosterListener(rosterListener);
            }
        }
    }

    public static void preApproveSubscriptionIfRequiredAndPossible(Roster roster, BareJid jid) throws NotLoggedInException, NotConnectedException, InterruptedException {
        if (roster.isSubscriptionPreApprovalSupported()) {
            RosterEntry entry = roster.getEntry(jid);
            if (entry == null || (!entry.canSeeMyPresence() && !entry.isApproved())) {
                try {
                    roster.preApprove(jid);
                } catch (FeatureNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }
        }
    }

    public static void askForSubscriptionIfRequired(Roster roster, BareJid jid) throws NotLoggedInException, NotConnectedException, InterruptedException {
        RosterEntry entry = roster.getEntry(jid);
        if (entry == null || (!entry.canSeeHisPresence() && !entry.isSubscriptionPending())) {
            roster.sendSubscriptionRequest(jid);
        }
    }

    public static void ensureNotSubscribedToEachOther(XMPPConnection connectionOne, XMPPConnection connectionTwo) throws NotConnectedException, InterruptedException {
        Roster rosterOne = Roster.getInstanceFor(connectionOne);
        BareJid jidOne = connectionOne.getUser().asBareJid();
        Roster rosterTwo = Roster.getInstanceFor(connectionTwo);
        ensureNotSubscribed(rosterOne, connectionTwo.getUser().asBareJid());
        ensureNotSubscribed(rosterTwo, jidOne);
    }

    public static void ensureNotSubscribed(Roster roster, BareJid jid) throws NotConnectedException, InterruptedException {
        RosterEntry entry = roster.getEntry(jid);
        if (entry != null && entry.canSeeMyPresence()) {
            entry.cancelSubscription();
        }
    }

    public static void ensureSubscribed(XMPPConnection connectionOne, XMPPConnection connectionTwo, long timeout) throws NotLoggedInException, NotConnectedException, InterruptedException, TimeoutException {
        ensureSubscribedTo(connectionOne, connectionTwo, timeout);
        ensureSubscribedTo(connectionTwo, connectionOne, timeout);
    }

    public static void ensureSubscribedTo(XMPPConnection connectionOne, XMPPConnection connectionTwo, long timeout) throws NotLoggedInException, NotConnectedException, InterruptedException, TimeoutException {
        ensureSubscribedTo(connectionOne, connectionTwo, new Date(System.currentTimeMillis() + timeout));
    }

    public static void ensureSubscribedTo(XMPPConnection connectionOne, XMPPConnection connectionTwo, Date deadline) throws NotLoggedInException, NotConnectedException, InterruptedException, TimeoutException {
        Roster rosterOne = Roster.getInstanceFor(connectionOne);
        BareJid jidTwo = connectionTwo.getUser().asBareJid();
        if (!rosterOne.iAmSubscribedTo(jidTwo)) {
            final BareJid jidOne = connectionOne.getUser().asBareJid();
            SubscribeListener subscribeListener = new SubscribeListener() {
                public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
                    if (from.equals((CharSequence) BareJid.this)) {
                        return SubscribeAnswer.Approve;
                    }
                    return null;
                }
            };
            Roster rosterTwo = Roster.getInstanceFor(connectionTwo);
            rosterTwo.addSubscribeListener(subscribeListener);
            try {
                rosterOne.sendSubscriptionRequest(jidTwo);
                waitUntilOtherEntityIsSubscribed(rosterTwo, jidOne, deadline);
            } finally {
                rosterTwo.removeSubscribeListener(subscribeListener);
            }
        }
    }
}
