package com.badguy.terrortime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.jxmpp.jid.Jid;

public class ContactListAdapter extends ArrayAdapter {
    private HashMap<String, Boolean> mAvailabilityMap;
    private ContactList mContactList;
    private List<String> mContactNames;
    private Context mContext;
    private HashMap<String, Integer> mMessageCountMap = new HashMap<>();
    private HashMap<String, List<Message>> mMessageMap;

    public ContactListAdapter(Context context, int resource, int textViewResourceId, List objects, HashMap availabilityMap, HashMap messageMap, ContactList contactList) {
        super(context, resource, textViewResourceId, objects);
        this.mContactNames = objects;
        this.mAvailabilityMap = availabilityMap;
        this.mMessageMap = messageMap;
        for (Entry<String, List<Message>> entry : this.mMessageMap.entrySet()) {
            this.mMessageCountMap.put(entry.getKey(), Integer.valueOf(((List) entry.getValue()).size()));
        }
        this.mContext = context;
        this.mContactList = contactList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem;
        int i = position;
        View listItem2 = convertView;
        if (listItem2 == null) {
            listItem = LayoutInflater.from(this.mContext).inflate(R.layout.contact_row_layout, parent, false);
        } else {
            ViewGroup viewGroup = parent;
            listItem = listItem2;
        }
        Boolean available = (Boolean) this.mAvailabilityMap.get((String) this.mContactNames.get(i));
        ImageView availability = (ImageView) listItem.findViewById(R.id.availability);
        ((TextView) listItem.findViewById(R.id.contact_name)).setText((CharSequence) this.mContactNames.get(i));
        TextView newMessageCount = (TextView) listItem.findViewById(R.id.message_count);
        ImageView newMessageIcon = (ImageView) listItem.findViewById(R.id.new_message);
        if (available.booleanValue()) {
            availability.setImageResource(R.drawable.circle_green);
        } else {
            availability.setImageResource(R.drawable.circle_grey);
        }
        try {
            Jid jid = (Jid) this.mContactList.getJidAtIndex(i).orElseThrow(new Supplier(i) {
                private final /* synthetic */ int f$0;

                {
                    this.f$0 = r1;
                }

                public final Object get() {
                    return ContactListAdapter.lambda$getView$0(this.f$0);
                }
            });
            Integer count = (Integer) this.mMessageCountMap.get(jid.asBareJid().toString());
            List<Message> messages = (List) this.mMessageMap.get(jid.asBareJid().toString());
            if (!(messages == null || count == null)) {
                Integer size = Integer.valueOf(messages.size());
                if (count.intValue() < size.intValue()) {
                    newMessageCount.setText(Integer.valueOf(size.intValue() - count.intValue()).toString());
                    newMessageCount.setVisibility(0);
                    newMessageIcon.setVisibility(0);
                }
            }
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Unable to update message count", e);
        }
        return listItem;
    }

    static /* synthetic */ Exception lambda$getView$0(int position) {
        StringBuilder sb = new StringBuilder();
        sb.append("No jid at position ");
        sb.append(position);
        return new Exception(sb.toString());
    }

    public void updateCount(String jid, Integer count) {
        this.mMessageCountMap.put(jid, count);
    }

    public void incrementCount(String jid) {
        Integer count;
        Integer count2 = (Integer) this.mMessageCountMap.get(jid);
        if (count2 == null) {
            count = Integer.valueOf(1);
        } else {
            count = Integer.valueOf(count2.intValue() + 1);
        }
        this.mMessageCountMap.put(jid, count);
    }
}
