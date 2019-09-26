package com.badguy.terrortime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import java.util.List;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.Jid;

public class ChatActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public List<Message> mAdapterMessageList = null;
    /* access modifiers changed from: private */
    public Chat mChat = null;
    private ChatManager mChatManager = null;
    private Client mClient = null;
    private AbstractXMPPConnection mConnection = null;
    /* access modifiers changed from: private */
    public Jid mContactJid = null;
    private ContactList mContactList = null;
    private BroadcastReceiver mContactReceiver = null;
    /* access modifiers changed from: private */
    public MessageListAdapter mMessageAdapter = null;
    /* access modifiers changed from: private */
    public RecyclerView mMessageRecycler = null;
    private Button mSend = null;
    /* access modifiers changed from: private */
    public EditText mSendText = null;

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String jid = intent.getStringExtra("jid");
        this.mAdapterMessageList = intent.getParcelableArrayListExtra("messages");
        setTitle(jid);
        setContentView((int) R.layout.activity_chat);
        TerrorTimeApplication app = (TerrorTimeApplication) getApplication();
        try {
            this.mConnection = (AbstractXMPPConnection) app.getXMPPTCPConnection().orElseThrow($$Lambda$ChatActivity$Ksr1XlDga0IT16Tq4YQHv_4QI.INSTANCE);
            this.mClient = (Client) app.getClient().orElseThrow($$Lambda$ChatActivity$8yUyKQwn0DFL8EEckDrdCx6pB0.INSTANCE);
            this.mContactList = (ContactList) app.getContactList().orElseThrow($$Lambda$ChatActivity$QoDALnkTLgb2ljruKgBNgrQWrHA.INSTANCE);
            this.mContactJid = (Jid) this.mContactList.getJidFromString(jid).orElseThrow($$Lambda$ChatActivity$LrcnlbksNlGWUc_CyR_VLf7Bww.INSTANCE);
            this.mChatManager = ChatManager.getInstanceFor(this.mConnection);
            if (this.mChatManager != null) {
                this.mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerview_message_list);
                this.mMessageAdapter = new MessageListAdapter(this, this.mAdapterMessageList);
                this.mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
                this.mMessageRecycler.setAdapter(this.mMessageAdapter);
                this.mChat = this.mChatManager.chatWith(this.mContactJid.asEntityBareJidIfPossible());
                this.mSend = (Button) findViewById(R.id.button_chatbox_send);
                this.mSendText = (EditText) findViewById(R.id.edittext_chatbox);
                this.mMessageRecycler.smoothScrollToPosition(this.mAdapterMessageList.size());
                this.mContactReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        Message msg = (Message) intent.getParcelableExtra(Message.ELEMENT);
                        if (msg.getContactId().equals(ChatActivity.this.mContactJid.asBareJid().toString())) {
                            ChatActivity.this.mAdapterMessageList.add(msg);
                            ChatActivity.this.mMessageAdapter.notifyItemInserted(ChatActivity.this.mAdapterMessageList.size());
                            ChatActivity.this.mMessageRecycler.smoothScrollToPosition(ChatActivity.this.mAdapterMessageList.size());
                        }
                    }
                };
                LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).registerReceiver(this.mContactReceiver, new IntentFilter("XMPP_CHAT_MESSAGE"));
                this.mSend.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        try {
                            ChatActivity.this.mChat.send((CharSequence) ChatActivity.this.mSendText.getText().toString());
                            ChatActivity.this.mSendText.getText().clear();
                        } catch (InterruptedException | NotConnectedException e) {
                            Log.e("EXCEPTION", "Unable to send message", e);
                            new Builder(ChatActivity.this).setTitle((CharSequence) "ERROR").setMessage((CharSequence) "Unable to send message. Please check your connection and try again.").setCancelable(false).setPositiveButton((CharSequence) "Continue", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                        }
                    }
                });
                return;
            }
            throw new Exception("Chat manager is null");
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Error getting connection info", e);
            Builder title = new Builder(this).setTitle((CharSequence) "ERROR");
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to setup chat with ");
            sb.append(jid);
            title.setMessage((CharSequence) sb.toString()).setCancelable(false).setPositiveButton((CharSequence) "Continue", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ChatActivity.this.finish();
                }
            }).create().show();
        }
    }

    static /* synthetic */ Exception lambda$onCreate$0() {
        return new Exception("No connection object");
    }

    static /* synthetic */ Exception lambda$onCreate$1() {
        return new Exception("No client object");
    }

    static /* synthetic */ Exception lambda$onCreate$2() {
        return new Exception("No contact list object");
    }

    static /* synthetic */ Exception lambda$onCreate$3() {
        return new Exception("No jid for contact");
    }

    public void onDestroy() {
        Intent intent = new Intent();
        intent.setAction("XMPP_CHAT_CLOSED");
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).sendBroadcast(intent);
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).unregisterReceiver(this.mContactReceiver);
        this.mMessageAdapter = null;
        this.mConnection = null;
        this.mChatManager = null;
        this.mChat = null;
        this.mSend = null;
        this.mAdapterMessageList = null;
        this.mClient = null;
        this.mContactList = null;
        this.mContactJid = null;
        this.mContactReceiver = null;
        super.onDestroy();
    }
}
