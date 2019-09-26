package com.badguy.terrortime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientDBHandlerClass extends SQLiteOpenHelper {
    private static final String ACCESS_TOKEN = "atok";
    private static final String ACCESS_TOKEN_EXP = "atokexp";
    private static final String AUTH_SERVER_IP = "asip";
    private static final String CHECK_PIN = "checkpin";
    private static final String CLIENT_ID = "cid";
    private static final String CLIENT_SECRET = "csecret";
    private static final String CONTACT_ID = "contactid";
    private static String CREATE_CLIENTS_TABLE = "CREATE TABLE IF NOT EXISTS Clients(cid TEXT NOT NULL,rsip TEXT,xname TEXT,xsip TEXT,csecret BLOB,atok BLOB,rtok BLOB,asip TEXT,atokexp INTEGER,rtokexp INTEGER,pubkey BLOB,privkey BLOB,checkpin BLOB)";
    private static String CREATE_CLIENT_CONTACTS = "CREATE TABLE IF NOT EXISTS Contacts(contactid TEXT NOT NULL, cid TEXT NOT NULL )";
    private static String CREATE_CLIENT_MESSAGES = "CREATE TABLE IF NOT EXISTS Messages(tstamp INTEGER, cid TEXT NOT NULL,contactid TEXT NOT NULL, fromclient INTEGER,msg BLOB )";
    private static final String DATABASE_NAME = "clientDB.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FROM_CLIENT = "fromclient";
    private static final String MESSAGE_CONTENT = "msg";
    private static final String PRIVATE_KEY = "privkey";
    private static final String PUBLIC_KEY = "pubkey";
    private static String QUERY_CLIENTS_TABLE = "SELECT * FROM Clients WHERE ";
    private static String QUERY_CONTACTS_TABLE = "SELECT * FROM Contacts WHERE ";
    private static String QUERY_MESSAGE_TABLE = "SELECT * FROM Messages WHERE ";
    private static final String REG_SERVER_IP = "rsip";
    private static final String RENEW_TOKEN = "rtok";
    private static final String RENEW_TOKEN_EXP = "rtokexp";
    public static final String TABLE_CLIENTS = "Clients";
    public static final String TABLE_CONTACTS = "Contacts";
    public static final String TABLE_MESSAGES = "Messages";
    private static final String TIMESTAMP = "tstamp";
    private static final String XMPP_NAME = "xname";
    private static final String XMPP_SERVER_IP = "xsip";
    private static final List<String> clientsColumnNames = Arrays.asList(new String[]{"cid", REG_SERVER_IP, XMPP_NAME, XMPP_SERVER_IP, ACCESS_TOKEN, RENEW_TOKEN, AUTH_SERVER_IP, ACCESS_TOKEN_EXP, RENEW_TOKEN_EXP, PUBLIC_KEY, PRIVATE_KEY, CHECK_PIN});
    private static ClientDBHandlerClass sInstance;
    private String cryptPin = null;

    public static ClientDBHandlerClass getInstance(String pin, Context context) {
        if (sInstance == null) {
            synchronized (ClientDBHandlerClass.class) {
                if (sInstance == null) {
                    sInstance = new ClientDBHandlerClass(pin, context.getApplicationContext());
                }
            }
        }
        ClientDBHandlerClass clientDBHandlerClass = sInstance;
        clientDBHandlerClass.cryptPin = pin;
        return clientDBHandlerClass;
    }

    private ClientDBHandlerClass(String pin, Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.cryptPin = pin;
    }

    public final void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLIENTS_TABLE);
        db.execSQL(CREATE_CLIENT_CONTACTS);
        db.execSQL(CREATE_CLIENT_MESSAGES);
    }

    public final void onOpen(SQLiteDatabase db) {
        db.execSQL(CREATE_CLIENTS_TABLE);
        db.execSQL(CREATE_CLIENT_CONTACTS);
        db.execSQL(CREATE_CLIENT_MESSAGES);
    }

    public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            dropAllTables();
            db.close();
            onCreate(db);
        }
    }

    public final void dropAllTables() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS Clients");
            db.execSQL("DROP TABLE IF EXISTS Contacts");
            db.execSQL("DROP TABLE IF EXISTS Messages");
        }
    }

    public final Integer nClients() {
        return countAllTableRecords(TABLE_CLIENTS);
    }

    public final Integer countAllTableRecords(String tablename) {
        Integer cnt = Integer.valueOf(0);
        if (tablename == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(tablename);
        sb.append(";");
        Cursor cursor = db.rawQuery(sb.toString(), null);
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    public final void addOrUpdateClient(Client client) throws Exception {
        String str = "cid";
        if (client != null) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                Integer nClientRecs = countClients(client.getOAuth2ClientId());
                db.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put(str, client.getOAuth2ClientId());
                    values.put(REG_SERVER_IP, client.getRegisterServerIP());
                    values.put(XMPP_NAME, client.getXmppUserName());
                    values.put(XMPP_SERVER_IP, client.getXmppServerIP());
                    values.put(CLIENT_SECRET, client.getEncrypted_oAuth2ClientSecret());
                    values.put(ACCESS_TOKEN, client.getEncrypted_oAuth2AccessToken());
                    values.put(RENEW_TOKEN, client.getEncrypted_oAuth2RenewToken());
                    values.put(AUTH_SERVER_IP, client.getOAuth2ServerIP());
                    values.put(ACCESS_TOKEN_EXP, client.getOAuth2AccessTokenExpiration());
                    values.put(RENEW_TOKEN_EXP, client.getOAuth2RenewTokenExpiration());
                    values.put(PUBLIC_KEY, client.getPublicKey());
                    values.put(PRIVATE_KEY, client.getEncrypted_privateKey());
                    values.put(CHECK_PIN, client.getCheckPin());
                    int intValue = nClientRecs.intValue();
                    String str2 = TABLE_CLIENTS;
                    if (intValue == 0) {
                        db.insertOrThrow(str2, null, values);
                    } else if (countClients(client.getOAuth2ClientId()).intValue() != 0) {
                        values.remove(str);
                        db.update(str2, values, "cid=?", new String[]{client.getOAuth2ClientId()});
                    } else {
                        throw new RuntimeException("Terrortime is configured for another user");
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                } catch (Exception e) {
                    Log.e("ERROR", "addOrUpdateClient: ", e);
                    throw new RuntimeException(e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
            }
        }
    }

    public final Integer countClients(String clientId) {
        Integer cnt = Integer.valueOf(0);
        if (clientId == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_CLIENTS_TABLE);
        sb.append("cid");
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId});
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0107, code lost:
        if (r2.isClosed() == false) goto L_0x0109;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0109, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x011e, code lost:
        if (r2.isClosed() == false) goto L_0x0109;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.badguy.terrortime.Client getClient(java.lang.String r8) {
        /*
            r7 = this;
            java.util.List<java.lang.String> r0 = clientsColumnNames
            java.util.stream.Stream r0 = r0.stream()
            java.lang.String r1 = ","
            java.util.stream.Collector r1 = java.util.stream.Collectors.joining(r1)
            java.lang.Object r0 = r0.collect(r1)
            java.lang.String r0 = (java.lang.String) r0
            r1 = 0
            r2 = 0
            if (r8 == 0) goto L_0x012e
            java.lang.Integer r3 = r7.countClients(r8)
            int r3 = r3.intValue()
            if (r3 != 0) goto L_0x0022
            goto L_0x012e
        L_0x0022:
            android.database.sqlite.SQLiteDatabase r3 = r7.getReadableDatabase()
            if (r3 != 0) goto L_0x0029
            return r1
        L_0x0029:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x010f }
            r4.<init>()     // Catch:{ Exception -> 0x010f }
            java.lang.String r5 = QUERY_CLIENTS_TABLE     // Catch:{ Exception -> 0x010f }
            r4.append(r5)     // Catch:{ Exception -> 0x010f }
            java.lang.String r5 = "cid"
            r4.append(r5)     // Catch:{ Exception -> 0x010f }
            java.lang.String r5 = " =? "
            r4.append(r5)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x010f }
            r5 = 1
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ Exception -> 0x010f }
            r6 = 0
            r5[r6] = r8     // Catch:{ Exception -> 0x010f }
            android.database.Cursor r4 = r3.rawQuery(r4, r5)     // Catch:{ Exception -> 0x010f }
            r2 = r4
            boolean r4 = r2.moveToFirst()     // Catch:{ Exception -> 0x010f }
            if (r4 == 0) goto L_0x0101
            com.badguy.terrortime.Client r4 = new com.badguy.terrortime.Client     // Catch:{ Exception -> 0x010f }
            r4.<init>(r8)     // Catch:{ Exception -> 0x010f }
            r1 = r4
            java.lang.String r4 = r7.cryptPin     // Catch:{ Exception -> 0x010f }
            r1.setEncryptPin(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "checkpin"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setCheckPin(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "rsip"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = r2.getString(r4)     // Catch:{ Exception -> 0x010f }
            r1.setRegisterServerIP(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "xname"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = r2.getString(r4)     // Catch:{ Exception -> 0x010f }
            r1.setXmppUserName(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "xsip"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = r2.getString(r4)     // Catch:{ Exception -> 0x010f }
            r1.setXmppServerIP(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "csecret"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setEncrypted_oAuth2ClientSecret(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "atok"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setEncrypted_oAuth2AccessToken(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "rtok"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setEncrypted_oAuth2RenewToken(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "asip"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = r2.getString(r4)     // Catch:{ Exception -> 0x010f }
            r1.setOAuth2ServerIP(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "atokexp"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            int r4 = r2.getInt(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x010f }
            r1.setOAuth2AccessTokenExpiration(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "rtokexp"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            int r4 = r2.getInt(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x010f }
            r1.setOAuth2RenewTokenExpiration(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "pubkey"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setPublicKey(r4)     // Catch:{ Exception -> 0x010f }
            java.lang.String r4 = "privkey"
            int r4 = r2.getColumnIndex(r4)     // Catch:{ Exception -> 0x010f }
            byte[] r4 = r2.getBlob(r4)     // Catch:{ Exception -> 0x010f }
            r1.setEncrypted_privateKey(r4)     // Catch:{ Exception -> 0x010f }
        L_0x0101:
            if (r2 == 0) goto L_0x0121
            boolean r4 = r2.isClosed()
            if (r4 != 0) goto L_0x0121
        L_0x0109:
            r2.close()
            goto L_0x0121
        L_0x010d:
            r4 = move-exception
            goto L_0x0122
        L_0x010f:
            r4 = move-exception
            java.lang.String r5 = "EXCEPTION LOG"
            java.lang.String r6 = "updateClientSettings: "
            android.util.Log.e(r5, r6, r4)     // Catch:{ all -> 0x010d }
            if (r2 == 0) goto L_0x0121
            boolean r4 = r2.isClosed()
            if (r4 != 0) goto L_0x0121
            goto L_0x0109
        L_0x0121:
            return r1
        L_0x0122:
            if (r2 == 0) goto L_0x012d
            boolean r5 = r2.isClosed()
            if (r5 != 0) goto L_0x012d
            r2.close()
        L_0x012d:
            throw r4
        L_0x012e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badguy.terrortime.ClientDBHandlerClass.getClient(java.lang.String):com.badguy.terrortime.Client");
    }

    public final void deleteClient(String clientId) {
        String str = TABLE_CLIENTS;
        if (clientId != null) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    if (countAllTableRecords(str).intValue() > 0 && countClients(clientId).intValue() > 0) {
                        db.delete(str, "cid=?", new String[]{clientId});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("EXCEPTION LOG", "deleteClient: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final void clearClientDB() {
        String str = TABLE_MESSAGES;
        String str2 = TABLE_CONTACTS;
        String str3 = TABLE_CLIENTS;
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.beginTransaction();
            try {
                if (countAllTableRecords(str3).intValue() > 0) {
                    db.delete(str3, null, null);
                }
                if (countAllTableRecords(str2).intValue() > 0) {
                    db.delete(str2, null, null);
                }
                if (countAllTableRecords(str).intValue() > 0) {
                    db.delete(str, null, null);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("EXCEPTION LOG", "clearClientDB: ", e);
            } catch (Throwable th) {
                db.endTransaction();
                throw th;
            }
            db.endTransaction();
        }
    }

    public final void addContact(Contact contact) {
        if (contact != null) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put(CONTACT_ID, contact.getContactId());
                    values.put("cid", contact.getClientId());
                    db.insertOrThrow(TABLE_CONTACTS, null, values);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "addContact: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final Integer countContacts(String clientId, String contactId) {
        Integer cnt = Integer.valueOf(0);
        if (contactId == null || clientId == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_CONTACTS_TABLE);
        sb.append(CONTACT_ID);
        sb.append(" =? AND ");
        sb.append("cid");
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{contactId, clientId});
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    public final Integer countContacts(String clientId) {
        Integer cnt = Integer.valueOf(0);
        if (clientId == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_CONTACTS_TABLE);
        sb.append("cid");
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId});
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    public final void deleteContact(String clientId, String contactId) {
        String str = TABLE_CONTACTS;
        if (contactId != null && clientId != null && countContacts(clientId, contactId).intValue() != 0) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    if (countAllTableRecords(str).intValue() > 0) {
                        db.delete(str, "contactid =? AND cid =? ", new String[]{contactId, clientId});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "deleteContact: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final void deleteContact(String clientId) {
        String str = TABLE_CONTACTS;
        if (clientId != null && countContacts(clientId).intValue() != 0) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    if (countAllTableRecords(str).intValue() > 0) {
                        db.delete(str, "cid =? ", new String[]{clientId});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "deleteContact: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final ArrayList<Contact> getContacts(String clientId) {
        ArrayList<Contact> retcons = new ArrayList<>();
        if (clientId == null) {
            return retcons;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return retcons;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_CONTACTS_TABLE);
        String str = "cid";
        sb.append(str);
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId});
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                retcons.add(new Contact(cursor.getString(cursor.getColumnIndex(str)), cursor.getString(cursor.getColumnIndex(CONTACT_ID))));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return retcons;
    }

    public final void addMessage(Message msg) {
        if (msg != null) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    ContentValues values = new ContentValues();
                    values.put(CONTACT_ID, msg.getContactId());
                    values.put("cid", msg.getClientId());
                    values.put("msg", msg.getContent());
                    boolean isFromClient = msg.isFromClient();
                    String str = FROM_CLIENT;
                    if (!isFromClient) {
                        values.put(str, Integer.valueOf(0));
                    } else {
                        values.put(str, Integer.valueOf(1));
                    }
                    values.put(TIMESTAMP, Integer.valueOf(0));
                    db.insertOrThrow(TABLE_MESSAGES, null, values);
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "addMessage: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final Integer countMessages(String clientId, String contactId) {
        Integer cnt = Integer.valueOf(0);
        if (contactId == null || clientId == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_MESSAGE_TABLE);
        sb.append("cid");
        sb.append(" =? AND ");
        sb.append(CONTACT_ID);
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId, contactId});
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    public final Integer countMessages(String clientId) {
        Integer cnt = Integer.valueOf(0);
        if (clientId == null) {
            return cnt;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return cnt;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_MESSAGE_TABLE);
        sb.append("cid");
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId});
        if (cursor != null && !cursor.isClosed()) {
            cnt = Integer.valueOf(cursor.getCount());
            cursor.close();
        }
        return cnt;
    }

    public final void deleteMessage(String clientId, String contactId) {
        String str = TABLE_MESSAGES;
        if (contactId != null && clientId != null && countMessages(clientId, contactId).intValue() != 0) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    if (countAllTableRecords(str).intValue() > 0) {
                        db.delete(str, "contactid =? AND cid =? ", new String[]{contactId, clientId});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "deleteMessage: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final void deleteMessage(String clientId) {
        String str = TABLE_MESSAGES;
        if (clientId != null && countMessages(clientId).intValue() != 0) {
            SQLiteDatabase db = getWritableDatabase();
            if (db != null) {
                db.beginTransaction();
                try {
                    if (countAllTableRecords(str).intValue() > 0) {
                        db.delete(str, "cid =? ", new String[]{clientId});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    Log.e("EXCEPTION LOG", "deleteMessage: ", e);
                } catch (Throwable th) {
                    db.endTransaction();
                    throw th;
                }
                db.endTransaction();
            }
        }
    }

    public final ArrayList<Message> getMessages(String clientId) {
        ArrayList<Message> retmsgs = new ArrayList<>();
        if (clientId == null) {
            return retmsgs;
        }
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            return retmsgs;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(QUERY_MESSAGE_TABLE);
        String str = "cid";
        sb.append(str);
        sb.append(" =? ");
        Cursor cursor = db.rawQuery(sb.toString(), new String[]{clientId});
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String contactName = cursor.getString(cursor.getColumnIndex(CONTACT_ID));
                String clientName = cursor.getString(cursor.getColumnIndex(str));
                byte[] content = cursor.getBlob(cursor.getColumnIndex("msg"));
                boolean fromClient = true;
                if (cursor.getInt(cursor.getColumnIndex(FROM_CLIENT)) == 0) {
                    fromClient = false;
                }
                retmsgs.add(new Message(clientName, contactName, content, fromClient));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return retmsgs;
    }
}
