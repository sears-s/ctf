package com.badguy.terrortime;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class MessageListAdapter extends Adapter {
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Message> mMessageList;

    public class ReceivedMessageHolder extends ViewHolder {
        TextView messageText;
        TextView nameText;
        TextView timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            this.messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            this.timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            this.nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Message message) {
            this.messageText.setText(new String(message.getContent()));
            this.timeText.setText((CharSequence) message.getCreatedAt().orElse(BuildConfig.FLAVOR));
            this.nameText.setText((message.isFromClient() ? message.getClientId() : message.getContactId()).split("@")[0]);
        }
    }

    public class SentMessageHolder extends ViewHolder {
        TextView messageText;
        TextView timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            this.messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            this.timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        /* access modifiers changed from: 0000 */
        public void bind(Message message) {
            this.messageText.setText(new String(message.getContent()));
            this.timeText.setText((CharSequence) message.getCreatedAt().orElse(BuildConfig.FLAVOR));
        }
    }

    public MessageListAdapter(Context context, List<Message> messageList) {
        this.mContext = context;
        this.mMessageList = messageList;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getItemCount() {
        List<Message> list = this.mMessageList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int getItemViewType(int position) {
        if (((Message) this.mMessageList.get(position)).isFromClient()) {
            return 1;
        }
        return 2;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new SentMessageHolder(this.mInflater.inflate(R.layout.item_message_sent, parent, false));
        }
        if (viewType == 2) {
            return new ReceivedMessageHolder(this.mInflater.inflate(R.layout.item_message_received, parent, false));
        }
        return null;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = (Message) this.mMessageList.get(position);
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 1) {
            ((SentMessageHolder) holder).bind(message);
        } else if (itemViewType == 2) {
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }
}
