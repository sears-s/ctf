package com.badguy.terrortime;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class Message implements Parcelable {
    public static final Creator<Message> CREATOR = new Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    private TextAppField clientId;
    private TextAppField contactId;
    private BlobAppField content;
    private boolean fromClient;
    private Date mNow;
    private String timestamp;

    public Message() {
        this.contactId = new TextAppField();
        this.clientId = new TextAppField();
        this.content = new BlobAppField();
        this.fromClient = true;
        this.timestamp = null;
        this.mNow = null;
        this.mNow = Calendar.getInstance().getTime();
        this.timestamp = new SimpleDateFormat("HH:mm z").format(this.mNow);
    }

    public Message(Message msg) {
        this(msg.getClientId(), msg.getContactId(), (byte[]) msg.getContent().clone(), msg.fromClient, msg.timestamp);
    }

    public Message(String clientId2, String contactId2, boolean fromClient2) {
        this();
        if (contactId2 != null) {
            this.contactId.setValue(contactId2);
        }
        if (clientId2 != null) {
            this.clientId.setValue(clientId2);
        }
        this.fromClient = fromClient2;
    }

    public Message(String clientId2, String contactId2, byte[] content2, boolean fromClient2) {
        this();
        if (contactId2 != null) {
            this.contactId.setValue(contactId2);
        }
        if (clientId2 != null) {
            this.clientId.setValue(clientId2);
        }
        if (content2 != null) {
            this.content.setValue(content2);
        }
        this.fromClient = fromClient2;
    }

    public Message(String clientId2, String contactId2, byte[] content2, boolean fromClient2, String timestamp2) {
        this.contactId = new TextAppField();
        this.clientId = new TextAppField();
        this.content = new BlobAppField();
        this.fromClient = true;
        this.timestamp = null;
        this.mNow = null;
        if (contactId2 != null) {
            this.contactId.setValue(contactId2);
        }
        if (clientId2 != null) {
            this.clientId.setValue(clientId2);
        }
        if (content2 != null) {
            this.content.setValue(content2);
        }
        this.fromClient = fromClient2;
        this.timestamp = timestamp2;
    }

    public final Optional<String> getCreatedAt() {
        return Optional.ofNullable(this.timestamp);
    }

    public final Date getCreationDate() {
        return this.mNow;
    }

    public final String getContactId() {
        return this.contactId.getValue();
    }

    public final void setContactId(String contactId2) {
        if (contactId2 != null) {
            this.contactId.setValue(contactId2);
        }
    }

    public final String getClientId() {
        return this.clientId.getValue();
    }

    public final void setClientId(String clientId2) {
        if (clientId2 != null) {
            this.clientId.setValue(clientId2);
        }
    }

    public final byte[] getContent() {
        return this.content.getValue();
    }

    public final void setContent(byte[] content2) {
        if (content2 != null) {
            this.content.setValue(content2);
        }
    }

    public final boolean isFromClient() {
        return this.fromClient;
    }

    public final void setFromClient(boolean fromClient2) {
        this.fromClient = fromClient2;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message other = (Message) Message.class.cast(o);
        if (this.contactId.equals(other.contactId) && this.clientId.equals(other.clientId) && this.fromClient == other.fromClient) {
            return true;
        }
        return false;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contactId.getValue());
        dest.writeString(this.clientId.getValue());
        dest.writeByteArray(this.content.getValue());
        dest.writeInt(this.fromClient ? 1 : 0);
        dest.writeString(this.timestamp);
        Date date = this.mNow;
        dest.writeLong(date == null ? -1 : date.getTime());
    }

    private Message(Parcel in) {
        this.contactId = new TextAppField();
        this.clientId = new TextAppField();
        this.content = new BlobAppField();
        boolean z = true;
        this.fromClient = true;
        Date date = null;
        this.timestamp = null;
        this.mNow = null;
        this.contactId = new TextAppField(in.readString());
        this.clientId = new TextAppField(in.readString());
        this.content = new BlobAppField();
        this.content.setValue(in.createByteArray());
        if (in.readInt() != 1) {
            z = false;
        }
        this.fromClient = z;
        this.timestamp = in.readString();
        long tmpDate = in.readLong();
        if (tmpDate != -1) {
            date = new Date(tmpDate);
        }
        this.mNow = date;
    }
}
