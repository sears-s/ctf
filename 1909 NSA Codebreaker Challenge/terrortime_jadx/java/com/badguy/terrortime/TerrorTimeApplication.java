package com.badguy.terrortime;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration.Builder;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties;

public class TerrorTimeApplication extends Application {
    private static Context mContext;
    private static TerrorTimeApplication mThis;
    /* access modifiers changed from: private */
    public Client mClient = null;
    /* access modifiers changed from: private */
    public AbstractXMPPConnection mConnection = null;
    /* access modifiers changed from: private */
    public ContactList mContactList = null;
    /* access modifiers changed from: private */
    public MamManager mMamManager = null;
    /* access modifiers changed from: private */
    public ReconnectionManager mReconnectionManager = null;
    /* access modifiers changed from: private */
    public VCardManager mVcardManager = null;

    public static class XMPPLoginTask extends AsyncTask<Void, Integer, Optional<AbstractXMPPConnection>> {
        private WeakReference<TerrorTimeApplication> mWeakApp;

        public XMPPLoginTask(Pair<TerrorTimeApplication, Client> context) {
            ((TerrorTimeApplication) context.first).mClient = (Client) context.second;
            this.mWeakApp = new WeakReference<>(context.first);
        }

        /* access modifiers changed from: protected */
        public Optional<AbstractXMPPConnection> doInBackground(Void... voids) {
            TerrorTimeApplication app = (TerrorTimeApplication) this.mWeakApp.get();
            String[] username_host = app.mClient.getXmppUserName().split("@");
            try {
                app.mClient.validateAccessToken(app.getApplicationContext());
                Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
                configBuilder.setUsernameAndPassword(username_host[0], new String(app.mClient.getOAuth2AccessToken(app.mClient.getEncryptPin())));
                configBuilder.setResource((CharSequence) "chat");
                String[] serverIpAndPort = app.mClient.getXmppServerIP().split(":");
                int port = serverIpAndPort.length == 2 ? Integer.valueOf(serverIpAndPort[1]).intValue() : 443;
                configBuilder.setHostAddress(InetAddress.getByName(serverIpAndPort[0]));
                configBuilder.setXmppDomain(username_host[1]);
                configBuilder.setPort(port);
                configBuilder.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                configBuilder.setCustomX509TrustManager(new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                });
                AndroidUsingLinkProperties.setup(TerrorTimeApplication.getAppContext());
                app.mConnection = new XMPPTCPConnection(configBuilder.build());
                app.mConnection.setReplyTimeout(30000);
                app.mVcardManager = VCardManager.getInstanceFor(app.mConnection);
                app.mContactList = new ContactList(Roster.getInstanceFor(app.mConnection), app.mClient);
                app.mReconnectionManager = ReconnectionManager.getInstanceFor(app.mConnection);
                app.mReconnectionManager.enableAutomaticReconnection();
                app.mConnection.connect();
                app.mConnection.login();
                app.mMamManager = MamManager.getInstanceFor((XMPPConnection) app.mConnection);
                if (!app.mMamManager.isSupported()) {
                    Log.d("MAM", "not supported");
                    app.mMamManager = null;
                } else {
                    app.mClient.setMessageList(MamHelper.getMessageArchive());
                }
                app.mClient.addPublicKeys(VCardHelper.getPublicKeys(app.mClient.getXmppUserName()));
                return Optional.ofNullable(app.mConnection);
            } catch (Exception e) {
                Log.e("EXCEPTION", "Error during xmpp connection setup", e);
                if (app.mConnection != null && app.mConnection.isConnected()) {
                    app.disconnect();
                }
                return Optional.empty();
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Optional<AbstractXMPPConnection> connection) {
            Intent intent = new Intent();
            intent.setAction("XMPP_INITIALIZE");
            intent.putExtra(MamResultExtension.ELEMENT, connection != null && connection.isPresent());
            Log.d("xmpptask", "sending initialize");
            LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).sendBroadcast(intent);
        }
    }

    public static TerrorTimeApplication getInstance() {
        return mThis;
    }

    public static Context getAppContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mThis = this;
        mContext = getApplicationContext();
        AndroidSmackInitializer.initialize(mContext);
        Security.addProvider(new BouncyCastleProvider());
    }

    public void initializeXMPPTCPConnection(Client client) {
        new XMPPLoginTask(new Pair(this, client)).execute(new Void[0]);
    }

    public Optional<AbstractXMPPConnection> getXMPPTCPConnection() {
        return Optional.ofNullable(this.mConnection);
    }

    public Optional<Client> getClient() {
        return Optional.ofNullable(this.mClient);
    }

    public Optional<ContactList> getContactList() {
        return Optional.ofNullable(this.mContactList);
    }

    public void disconnect() {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... params) {
                if (TerrorTimeApplication.this.mConnection != null) {
                    if (TerrorTimeApplication.this.mConnection.isConnected()) {
                        TerrorTimeApplication.this.mConnection.disconnect();
                    }
                    TerrorTimeApplication.this.mConnection = null;
                }
                TerrorTimeApplication.this.mVcardManager = null;
                TerrorTimeApplication.this.mMamManager = null;
                TerrorTimeApplication.this.mContactList = null;
                TerrorTimeApplication.this.mReconnectionManager = null;
                return null;
            }
        }.execute(new Void[0]);
    }

    public Optional<VCardManager> getVCardManager() {
        return Optional.ofNullable(this.mVcardManager);
    }

    public Optional<MamManager> getMamManager() {
        return Optional.ofNullable(this.mMamManager);
    }

    public Optional<ReconnectionManager> getReconnectionManager() {
        return Optional.ofNullable(this.mReconnectionManager);
    }
}
