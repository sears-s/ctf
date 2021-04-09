package com.badguy.terrortime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.StreamErrorException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public class ContactActivity extends AppCompatActivity {
    private ChatManager mChatManager = null;
    private BroadcastReceiver mChatReceiver = null;
    /* access modifiers changed from: private */
    public Client mClient = null;
    /* access modifiers changed from: private */
    public List<Message> mClientMessageList = null;
    private AbstractXMPPConnection mConnection = null;
    private TerrorTimeConnectionListener mConnectionListener = null;
    /* access modifiers changed from: private */
    public ContactList mContactList = null;
    /* access modifiers changed from: private */
    public ContactListAdapter mContactListAdapter = null;
    private List<String> mContactNames = null;
    private BroadcastReceiver mContactReceiver = null;
    /* access modifiers changed from: private */
    public String mCurrentChatJid = null;
    private IncomingChatMessageListener mIncomingListener = null;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<Message>> mMessageMap = null;
    private OutgoingChatMessageListener mOutgoingListener = null;
    private TerrorTimeReconnectionListener mReconnectionListener = null;
    /* access modifiers changed from: private */
    public ReconnectionManager mReconnectionManager = null;

    private class ChatIncomingMessageListener implements IncomingChatMessageListener {
        private ChatIncomingMessageListener() {
        }

        public void newIncomingMessage(final EntityBareJid from, final Message message, Chat chat) {
            ContactActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Message ttMessage = new Message(ContactActivity.this.mClient.getXmppUserName(), from.asBareJid().toString(), message.getBody().getBytes(), false);
                    if (ContactActivity.this.mCurrentChatJid != null && from.asBareJid().toString().equals(ContactActivity.this.mCurrentChatJid)) {
                        ContactActivity.this.mContactListAdapter.incrementCount(ContactActivity.this.mCurrentChatJid);
                    }
                    if (ContactActivity.this.mClient.decryptMessage(ttMessage)) {
                        ContactActivity.this.mClientMessageList.add(new Message(ContactActivity.this.mClient.getXmppUserName(), from.asBareJid().toString(), message.getBody().getBytes(), false));
                    } else {
                        ContactActivity.this.mClientMessageList.add(ttMessage);
                    }
                    ContactActivity.this.sendChatBroadcast(from.asBareJid().toString(), ttMessage);
                }
            });
        }
    }

    private class ChatOutgoingMessageListener implements OutgoingChatMessageListener {
        private ChatOutgoingMessageListener() {
        }

        public void newOutgoingMessage(final EntityBareJid to, final Message message, Chat chat) {
            ContactActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Message ttMessage = new Message(ContactActivity.this.mClient.getXmppUserName(), to.asBareJid().toString(), message.getBody().getBytes(), true);
                    if (ContactActivity.this.mCurrentChatJid != null && to.asBareJid().toString().equals(ContactActivity.this.mCurrentChatJid)) {
                        ContactActivity.this.mContactListAdapter.incrementCount(ContactActivity.this.mCurrentChatJid);
                    }
                    Message plainTextMessage = new Message(ContactActivity.this.mClient.getXmppUserName(), to.asBareJid().toString(), message.getBody().getBytes(), true);
                    if (!ContactActivity.this.mClient.encryptMessage(ttMessage)) {
                        plainTextMessage = ttMessage;
                    }
                    ContactActivity.this.sendChatBroadcast(to.asBareJid().toString(), plainTextMessage);
                    ContactActivity.this.mClientMessageList.add(ttMessage);
                    message.setBody(new String(ttMessage.getContent()));
                }
            });
        }
    }

    private class TerrorTimeConnectionListener implements ConnectionListener {
        private TerrorTimeConnectionListener() {
        }

        public void connected(XMPPConnection connection) {
            Log.d("connection_status", "connected");
        }

        public void authenticated(XMPPConnection connection, boolean resumed) {
            Log.d("connection_status", "authenticated");
        }

        public void connectionClosed() {
            Log.d("connection_status", "closed");
        }

        public void connectionClosedOnError(final Exception e) {
            Log.e("connection_status", "closed on error ", e);
            ContactActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(ContactActivity.this.getApplicationContext(), "Lost connection to TerrorTime server.", 1);
                    Exception exc = e;
                    if ((exc instanceof StreamErrorException) && ((StreamErrorException) exc).getStreamError().getCondition().name().equals("conflict")) {
                        toast = Toast.makeText(ContactActivity.this.getApplicationContext(), "Disconnected from TerrorTime. Only one device may be signed in at a time.", 1);
                        ContactActivity.this.startActivity(new Intent(ContactActivity.this, LoginActivity.class));
                        ContactActivity.this.finish();
                    }
                    toast.setGravity(17, 0, 0);
                    toast.show();
                }
            });
        }
    }

    private class TerrorTimeReconnectionListener implements ReconnectionListener {
        private TerrorTimeReconnectionListener() {
        }

        public void reconnectingIn(final int seconds) {
            ContactActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Context applicationContext = ContactActivity.this.getApplicationContext();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Reconnecting in ");
                    sb.append(seconds);
                    sb.append(" seconds.");
                    Toast toast = Toast.makeText(applicationContext, sb.toString(), 1);
                    toast.setGravity(17, 0, 0);
                    toast.show();
                }
            });
        }

        public void reconnectionFailed(Exception e) {
            ContactActivity.this.mReconnectionManager.disableAutomaticReconnection();
            ContactActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(ContactActivity.this.getApplicationContext(), "Unable to reconnect to TerrorTime. Returning to login screen.", 1);
                    toast.setGravity(17, 0, 0);
                    toast.show();
                    ContactActivity.this.startActivity(new Intent(ContactActivity.this, LoginActivity.class));
                    ContactActivity.this.finish();
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_contact);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        TerrorTimeApplication app = (TerrorTimeApplication) getApplication();
        ListView listview = (ListView) findViewById(R.id.ContactListView);
        this.mMessageMap = new HashMap<>();
        try {
            this.mConnection = (AbstractXMPPConnection) app.getXMPPTCPConnection().orElseThrow($$Lambda$ContactActivity$D4F499BgZVEklXb226uaj3IXBnI.INSTANCE);
            this.mReconnectionManager = (ReconnectionManager) app.getReconnectionManager().orElseThrow($$Lambda$ContactActivity$90Q2hWxX0EVjE2LXELHIYLAKCs.INSTANCE);
            this.mContactList = (ContactList) app.getContactList().orElseThrow($$Lambda$ContactActivity$qVLZd6Duq1UMne65D4aTeBipKc.INSTANCE);
            this.mClient = (Client) app.getClient().orElseThrow($$Lambda$ContactActivity$_ZcPEsTSp8t6NQjXBYyfGzNOOY.INSTANCE);
            this.mChatManager = ChatManager.getInstanceFor(this.mConnection);
            if (this.mChatManager != null) {
                this.mConnectionListener = new TerrorTimeConnectionListener();
                this.mConnection.addConnectionListener(this.mConnectionListener);
                this.mReconnectionListener = new TerrorTimeReconnectionListener();
                this.mReconnectionManager.addReconnectionListener(this.mReconnectionListener);
                this.mContactNames = this.mContactList.getContactNames();
                for (Jid contactJid : this.mContactList.getContactJids()) {
                    this.mMessageMap.computeIfAbsent(contactJid.asBareJid().toString(), $$Lambda$ContactActivity$Abp1UWZl8642CTt7XId3R1NFrZ4.INSTANCE);
                }
                this.mClientMessageList = this.mClient.getMessageList();
                Stream stream = ((List) this.mClientMessageList.stream().map($$Lambda$ContactActivity$Y__yy_3w9K5VEeJUPl9He0_AkV0.INSTANCE).collect(Collectors.toList())).stream();
                Client client = this.mClient;
                client.getClass();
                stream.filter(new Predicate() {
                    public final boolean test(Object obj) {
                        return Client.this.decryptMessage((Message) obj);
                    }
                }).forEach(new Consumer() {
                    public final void accept(Object obj) {
                        ContactActivity.this.lambda$onCreate$7$ContactActivity((Message) obj);
                    }
                });
                ContactListAdapter contactListAdapter = new ContactListAdapter(this, R.layout.contact_row_layout, R.id.contact_name, this.mContactNames, this.mContactList.getAvailabilityMap(), this.mMessageMap, this.mContactList);
                this.mContactListAdapter = contactListAdapter;
                this.mContactListAdapter.setNotifyOnChange(true);
                listview.setAdapter(this.mContactListAdapter);
                this.mOutgoingListener = new ChatOutgoingMessageListener();
                this.mIncomingListener = new ChatIncomingMessageListener();
                this.mChatManager.addOutgoingListener(this.mOutgoingListener);
                this.mChatManager.addIncomingListener(this.mIncomingListener);
                this.mChatReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        ContactActivity.this.mCurrentChatJid = null;
                    }
                };
                LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).registerReceiver(this.mChatReceiver, new IntentFilter("XMPP_CHAT_CLOSED"));
                this.mContactReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        ContactActivity.this.mContactListAdapter.notifyDataSetChanged();
                    }
                };
                LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).registerReceiver(this.mContactReceiver, new IntentFilter("XMPP_CONTACTS_CHANGED"));
                listview.setOnItemClickListener(new OnItemClickListener() {
                    static /* synthetic */ Exception lambda$onItemClick$0(int position) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("No jid at position ");
                        sb.append(position);
                        return new Exception(sb.toString());
                    }

                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        try {
                            ContactActivity.this.mCurrentChatJid = ((Jid) ContactActivity.this.mContactList.getJidAtIndex(position).orElseThrow(new Supplier(position) {
                                private final /* synthetic */ int f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final Object get() {
                                    return AnonymousClass3.lambda$onItemClick$0(this.f$0);
                                }
                            })).asBareJid().toString();
                            ArrayList<Message> messageList = (ArrayList) ContactActivity.this.mMessageMap.computeIfAbsent(ContactActivity.this.mCurrentChatJid, $$Lambda$ContactActivity$3$IoGMCdBuM1l5Yod757VGhOYsa7U.INSTANCE);
                            Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
                            intent.putExtra("jid", ContactActivity.this.mCurrentChatJid);
                            intent.putExtra("messages", messageList);
                            ContactActivity.this.startActivity(intent);
                            ImageView newMessageIcon = (ImageView) view.findViewById(R.id.new_message);
                            ((TextView) view.findViewById(R.id.message_count)).setVisibility(4);
                            newMessageIcon.setVisibility(4);
                            ContactActivity.this.mContactListAdapter.updateCount(ContactActivity.this.mCurrentChatJid, Integer.valueOf(messageList.size()));
                        } catch (Throwable e) {
                            Log.e("EXCEPTION", "Unable to load chat activity", e);
                            new Builder(ContactActivity.this).setTitle((CharSequence) "ERROR").setMessage((CharSequence) "Unable to load chat window. Please try again.").setCancelable(false).setPositiveButton((CharSequence) "Continue", (OnClickListener) new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                        }
                    }

                    static /* synthetic */ ArrayList lambda$onItemClick$1(String k) {
                        return new ArrayList();
                    }
                });
                return;
            }
            throw new Exception("Chat manager is null");
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to get connection info", e);
            new Builder(this).setTitle((CharSequence) "ERROR").setMessage((CharSequence) "Unable to setup contact list").setCancelable(false).setPositiveButton((CharSequence) "Continue", (OnClickListener) new OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ContactActivity.this.startActivity(new Intent(ContactActivity.this, LoginActivity.class));
                    ContactActivity.this.finish();
                }
            }).create().show();
        }
    }

    static /* synthetic */ Exception lambda$onCreate$0() {
        return new Exception("No connection object");
    }

    static /* synthetic */ Exception lambda$onCreate$1() {
        return new Exception("No reconnection manager");
    }

    static /* synthetic */ Exception lambda$onCreate$2() {
        return new Exception("No contact list object");
    }

    static /* synthetic */ Exception lambda$onCreate$3() {
        return new Exception("No client object");
    }

    static /* synthetic */ ArrayList lambda$onCreate$4(String k) {
        return new ArrayList();
    }

    static /* synthetic */ Message lambda$onCreate$5(Message msg) {
        return new Message(msg);
    }

    public /* synthetic */ void lambda$onCreate$7$ContactActivity(Message msg) {
        ArrayList arrayList = (ArrayList) this.mMessageMap.compute(msg.getContactId(), new BiFunction() {
            public final Object apply(Object obj, Object obj2) {
                return ContactActivity.lambda$null$6(Message.this, (String) obj, (ArrayList) obj2);
            }
        });
    }

    static /* synthetic */ ArrayList lambda$null$6(Message msg, String k, ArrayList v) {
        ArrayList arrayList = v;
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        arrayList.add(msg);
        return arrayList;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.logout) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        return true;
    }

    public void onDestroy() {
        ChatManager chatManager = this.mChatManager;
        if (chatManager != null) {
            IncomingChatMessageListener incomingChatMessageListener = this.mIncomingListener;
            if (!(incomingChatMessageListener == null || this.mOutgoingListener == null)) {
                chatManager.removeIncomingListener(incomingChatMessageListener);
                this.mChatManager.removeOutgoingListener(this.mOutgoingListener);
            }
        }
        AbstractXMPPConnection abstractXMPPConnection = this.mConnection;
        if (abstractXMPPConnection != null) {
            TerrorTimeConnectionListener terrorTimeConnectionListener = this.mConnectionListener;
            if (terrorTimeConnectionListener != null) {
                abstractXMPPConnection.removeConnectionListener(terrorTimeConnectionListener);
            }
        }
        ReconnectionManager reconnectionManager = this.mReconnectionManager;
        if (reconnectionManager != null) {
            TerrorTimeReconnectionListener terrorTimeReconnectionListener = this.mReconnectionListener;
            if (terrorTimeReconnectionListener != null) {
                reconnectionManager.removeReconnectionListener(terrorTimeReconnectionListener);
            }
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.mChatReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.mContactReceiver);
        this.mConnectionListener = null;
        this.mReconnectionManager = null;
        this.mReconnectionListener = null;
        this.mChatReceiver = null;
        this.mContactReceiver = null;
        this.mContactList = null;
        this.mContactNames = null;
        this.mClient = null;
        this.mClientMessageList = null;
        this.mMessageMap = null;
        this.mOutgoingListener = null;
        this.mIncomingListener = null;
        this.mChatManager = null;
        this.mContactListAdapter = null;
        this.mCurrentChatJid = null;
        AbstractXMPPConnection abstractXMPPConnection2 = this.mConnection;
        if (abstractXMPPConnection2 != null && abstractXMPPConnection2.isConnected()) {
            ((TerrorTimeApplication) getApplication()).disconnect();
        }
        super.onDestroy();
    }

    public void sendChatBroadcast(String contact, Message msg) {
        this.mMessageMap.compute(contact, new BiFunction() {
            public final Object apply(Object obj, Object obj2) {
                return ContactActivity.lambda$sendChatBroadcast$8(Message.this, (String) obj, (ArrayList) obj2);
            }
        });
        Intent intent = new Intent();
        intent.setAction("XMPP_CHAT_MESSAGE");
        intent.putExtra(Message.ELEMENT, msg);
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).sendBroadcast(intent);
        Intent contactIntent = new Intent();
        contactIntent.setAction("XMPP_CONTACTS_CHANGED");
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).sendBroadcast(contactIntent);
    }

    static /* synthetic */ ArrayList lambda$sendChatBroadcast$8(Message msg, String k, ArrayList v) {
        ArrayList arrayList = v;
        if (v == null) {
            arrayList = new ArrayList();
        }
        arrayList.add(msg);
        return arrayList;
    }
}
