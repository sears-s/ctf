package com.badguy.terrortime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import org.jivesoftware.smackx.mam.element.MamElements.MamResultExtension;

public class LoginActivity extends AppCompatActivity {
    /* access modifiers changed from: private */
    public EditText mChatUserNameField = null;
    /* access modifiers changed from: private */
    public Button mLoginButton = null;
    private BroadcastReceiver mLoginReceiver = null;
    /* access modifiers changed from: private */
    public UserLoginTask mLoginTask = null;
    /* access modifiers changed from: private */
    public EditText mPinField = null;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar = null;

    public static class UserLoginTask extends AsyncTask<Void, Integer, Client> {
        private WeakReference<LoginActivity> activityWeakReference;
        private Client client;
        private ClientDBHandlerClass clientDB;
        private String errorMsg = BuildConfig.FLAVOR;
        private boolean success = true;

        /* JADX WARNING: Removed duplicated region for block: B:23:0x00bd  */
        /* JADX WARNING: Removed duplicated region for block: B:25:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        UserLoginTask(com.badguy.terrortime.LoginActivity r11) {
            /*
                r10 = this;
                java.lang.String r0 = "Check client id and pin."
                java.lang.String r1 = "ERROR"
                r10.<init>()
                java.lang.String r2 = ""
                r10.errorMsg = r2
                r2 = 1
                r10.success = r2
                java.lang.ref.WeakReference r3 = new java.lang.ref.WeakReference
                r3.<init>(r11)
                r10.activityWeakReference = r3
                java.lang.ref.WeakReference<com.badguy.terrortime.LoginActivity> r3 = r10.activityWeakReference
                java.lang.Object r3 = r3.get()
                com.badguy.terrortime.LoginActivity r3 = (com.badguy.terrortime.LoginActivity) r3
                r4 = 2131230824(0x7f080068, float:1.8077712E38)
                android.view.View r4 = r3.findViewById(r4)
                android.widget.EditText r4 = (android.widget.EditText) r4
                r5 = 2131230830(0x7f08006e, float:1.8077724E38)
                android.view.View r5 = r3.findViewById(r5)
                android.widget.EditText r5 = (android.widget.EditText) r5
                r6 = 0
                r10.client = r6
                r6 = 0
                android.text.Editable r7 = r5.getText()     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x00a7 }
                android.content.Context r8 = r11.getApplicationContext()     // Catch:{ Exception -> 0x00a7 }
                com.badguy.terrortime.ClientDBHandlerClass r7 = com.badguy.terrortime.ClientDBHandlerClass.getInstance(r7, r8)     // Catch:{ Exception -> 0x00a7 }
                r10.clientDB = r7     // Catch:{ Exception -> 0x00a7 }
                com.badguy.terrortime.ClientDBHandlerClass r7 = r10.clientDB     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r8 = "LoginActivity"
                if (r7 != 0) goto L_0x0057
                r10.success = r6     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r7 = "Unknown application database error. Could not connect to database."
                r10.errorMsg = r7     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r7 = "Failed to open client database. clientDB was null."
                android.util.Log.e(r1, r7)     // Catch:{ Exception -> 0x00a7 }
                goto L_0x006c
            L_0x0057:
                com.badguy.terrortime.ClientDBHandlerClass r7 = r10.clientDB     // Catch:{ Exception -> 0x00a7 }
                android.text.Editable r9 = r4.getText()     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00a7 }
                com.badguy.terrortime.Client r7 = r7.getClient(r9)     // Catch:{ Exception -> 0x00a7 }
                r10.client = r7     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r7 = "Connected to client database successfully."
                android.util.Log.d(r8, r7)     // Catch:{ Exception -> 0x00a7 }
            L_0x006c:
                boolean r7 = r10.success     // Catch:{ Exception -> 0x00a7 }
                if (r7 == 0) goto L_0x0095
                com.badguy.terrortime.Client r7 = r10.client     // Catch:{ Exception -> 0x00a7 }
                if (r7 != 0) goto L_0x0095
                r10.success = r6     // Catch:{ Exception -> 0x00a7 }
                r10.errorMsg = r0     // Catch:{ Exception -> 0x00a7 }
                java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00a7 }
                r7.<init>()     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r9 = "Bad Client id: "
                r7.append(r9)     // Catch:{ Exception -> 0x00a7 }
                android.text.Editable r9 = r4.getText()     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00a7 }
                r7.append(r9)     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x00a7 }
                android.util.Log.e(r8, r7)     // Catch:{ Exception -> 0x00a7 }
                goto L_0x00a6
            L_0x0095:
                boolean r7 = r10.success     // Catch:{ Exception -> 0x00a7 }
                if (r7 == 0) goto L_0x00a6
                com.badguy.terrortime.Client r7 = r10.client     // Catch:{ Exception -> 0x00a7 }
                android.text.Editable r8 = r5.getText()     // Catch:{ Exception -> 0x00a7 }
                java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x00a7 }
                r7.setEncryptPin(r8)     // Catch:{ Exception -> 0x00a7 }
            L_0x00a6:
                goto L_0x00b9
            L_0x00a7:
                r7 = move-exception
                java.lang.String r8 = "UserLoginTask: "
                android.util.Log.e(r1, r8, r7)
                java.lang.String r1 = r10.errorMsg
                boolean r1 = r1.isEmpty()
                if (r1 == 0) goto L_0x00b7
                r10.errorMsg = r0
            L_0x00b7:
                r10.success = r6
            L_0x00b9:
                boolean r0 = r10.success
                if (r0 != 0) goto L_0x00c0
                r10.cancel(r2)
            L_0x00c0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.badguy.terrortime.LoginActivity.UserLoginTask.<init>(com.badguy.terrortime.LoginActivity):void");
        }

        /* access modifiers changed from: protected */
        public final void onPreExecute() {
            super.onPreExecute();
            LoginActivity activity = (LoginActivity) this.activityWeakReference.get();
            View currentView = activity.getCurrentFocus();
            if (currentView != null) {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
            if (!isCancelled() && this.success) {
                activity.mLoginButton.setEnabled(false);
                activity.mProgressBar.setVisibility(0);
            }
        }

        /* access modifiers changed from: protected */
        public final Client doInBackground(Void... params) {
            String str = "ERROR";
            LoginActivity activity = (LoginActivity) this.activityWeakReference.get();
            try {
                if (!this.success || this.client != null) {
                    if (this.success) {
                        this.client.generateSymmetricKey();
                        this.client.validateAccessToken(activity.getApplicationContext());
                        Log.d("LOGINACTIVITY", "Token request successful. ");
                    }
                    Log.d("LoginActivity", "Login background thread success.");
                    return this.client;
                }
                this.success = false;
                this.errorMsg = "Unknown error: Null Client";
                Log.e(str, "LoginActivity: Null client in UserLoginTask background thread");
                Log.d("LoginActivity", "Login background thread success.");
                return this.client;
            } catch (Exception e) {
                this.success = false;
                if (this.errorMsg.isEmpty()) {
                    this.errorMsg = "Check client id and pin.";
                }
                StringBuilder sb = new StringBuilder();
                sb.append("LoginActivitiy: ");
                sb.append(e.getMessage());
                Log.e(str, sb.toString());
            }
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
        }

        /* access modifiers changed from: protected */
        public final void onPostExecute(Client client2) {
            super.onPostExecute(client2);
            LoginActivity activity = (LoginActivity) this.activityWeakReference.get();
            if (isCancelled() || client2 == null) {
                ClientDBHandlerClass clientDBHandlerClass = this.clientDB;
                if (clientDBHandlerClass != null) {
                    clientDBHandlerClass.close();
                }
            } else {
                try {
                    this.clientDB.addOrUpdateClient(client2);
                } catch (Exception e) {
                    this.success = false;
                    this.errorMsg = "Client Database Error";
                }
                ClientDBHandlerClass clientDBHandlerClass2 = this.clientDB;
                if (clientDBHandlerClass2 != null) {
                    clientDBHandlerClass2.close();
                }
            }
            if (!this.success) {
                activity.mProgressBar.setVisibility(8);
                StringBuilder sb = new StringBuilder();
                sb.append("Login failed. Select OK to close window. ");
                sb.append(this.errorMsg);
                activity.showAlertAndFinishActivity(sb.toString());
                return;
            }
            ((TerrorTimeApplication) activity.getApplication()).initializeXMPPTCPConnection(client2);
        }

        /* access modifiers changed from: protected */
        public final void onCancelled() {
            super.onCancelled();
            LoginActivity activity = (LoginActivity) this.activityWeakReference.get();
            this.success = false;
            if (this.errorMsg.length() == 0) {
                this.errorMsg = "Login failed for unknown reason.";
            }
            Log.v("LoginActivity", "Login cancelled");
            ClientDBHandlerClass clientDBHandlerClass = this.clientDB;
            if (clientDBHandlerClass != null) {
                clientDBHandlerClass.close();
            }
            activity.mChatUserNameField.getText().clear();
            activity.mPinField.getText().clear();
            activity.mChatUserNameField.setError(null);
            activity.mPinField.setError(null);
            StringBuilder sb = new StringBuilder();
            sb.append("Login failed. Select OK to close window. ");
            sb.append(this.errorMsg);
            activity.showAlertAndFinishActivity(sb.toString());
        }
    }

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.mChatUserNameField = (EditText) findViewById(R.id.loginUserName);
        this.mPinField = (EditText) findViewById(R.id.loginpin);
        this.mLoginButton = (Button) findViewById(R.id.login_button);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
        this.mProgressBar.setVisibility(4);
        final Map<EditText, String> fMap = new HashMap<>();
        fMap.put(this.mChatUserNameField, "chatUserNameField");
        fMap.put(this.mPinField, "pinField");
        this.mLoginReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                LoginActivity.this.mProgressBar.setVisibility(8);
                try {
                    if (intent.getBooleanExtra(MamResultExtension.ELEMENT, false)) {
                        LoginActivity.this.savePublicKeyToVCard((Client) ((TerrorTimeApplication) LoginActivity.this.getApplication()).getClient().orElseThrow($$Lambda$LoginActivity$1$nC2Mlzaksg0IlR_njHSIBo11qtU.INSTANCE));
                        LoginActivity.this.launchContactActivityAndFinish();
                        return;
                    }
                    throw new Exception("Connection failed");
                } catch (Throwable th) {
                    LoginActivity.this.showAlertAndFinishActivity("Unable to connect to chat server");
                }
            }

            static /* synthetic */ Exception lambda$onReceive$0() {
                return new Exception("No client object");
            }
        };
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).registerReceiver(this.mLoginReceiver, new IntentFilter("XMPP_INITIALIZE"));
        this.mLoginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.mLoginTask = null;
                if (LoginActivity.this.validateFields(fMap)) {
                    for (EditText field : fMap.keySet()) {
                        field.setError(null);
                    }
                    LoginActivity.this.execTask();
                    for (EditText field2 : fMap.keySet()) {
                        if (field2.getError() != null) {
                            field2.requestFocus();
                            Toast.makeText(LoginActivity.this.getApplicationContext(), field2.getError().toString(), 1).show();
                            return;
                        }
                    }
                    return;
                }
                for (EditText field3 : fMap.keySet()) {
                    try {
                        if (field3.getError() != null) {
                            field3.requestFocus();
                            Toast.makeText(LoginActivity.this.getApplicationContext(), field3.getError().toString(), 1).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("EXCEPTION LOG", "setOnClickListener: ", e);
                        Toast.makeText(LoginActivity.this.getApplicationContext(), e.getMessage(), 1).show();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public final void onDestroy() {
        LocalBroadcastManager.getInstance(TerrorTimeApplication.getAppContext()).unregisterReceiver(this.mLoginReceiver);
        UserLoginTask userLoginTask = this.mLoginTask;
        if (userLoginTask != null) {
            userLoginTask.cancel(true);
        }
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void savePublicKeyToVCard(Client client) {
        boolean result = false;
        try {
            result = VCardHelper.savePublicKey((PublicKey) client.getRsaPublicKey().orElseThrow($$Lambda$LoginActivity$YcawHEfealIw5425nWdRe1UadVY.INSTANCE));
        } catch (Throwable e) {
            Log.e("EXCEPTION", "Public key error", e);
        }
        if (!result) {
            showAlertAndFinishActivity("Unable to save public key to server");
        }
    }

    static /* synthetic */ Exception lambda$savePublicKeyToVCard$0() {
        return new Exception("Missing public key");
    }

    /* access modifiers changed from: private */
    public void launchContactActivityAndFinish() {
        startActivity(new Intent(this, ContactActivity.class));
        finish();
    }

    /* access modifiers changed from: private */
    public boolean validateFields(Map<EditText, String> fieldMap) {
        ParameterValidatorClass pvalidator = new ParameterValidatorClass();
        this.mChatUserNameField.setError(null);
        this.mPinField.setError(null);
        for (EditText field : fieldMap.keySet()) {
            try {
                String fName = (String) fieldMap.get(field);
                if (field != null) {
                    String fText = field.getText().toString();
                    if (fText.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(fName);
                        sb.append(": ");
                        sb.append(getString(R.string.error_field_required));
                        field.setError(sb.toString());
                        return false;
                    } else if (field == this.mChatUserNameField && !pvalidator.isValidUserName(fText)) {
                        field.setError(getString(R.string.error_invalid_userName));
                        return false;
                    } else if (field == this.mPinField && !pvalidator.isValidPin(fText)) {
                        field.setError(getString(R.string.error_invalid_pin));
                        return false;
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Fatal Error: ");
                    sb2.append(fName);
                    sb2.append(" was null.");
                    throw new RuntimeException(sb2.toString());
                }
            } catch (Exception e) {
                Log.e("EXCEPTION LOG", "validateFields: ", e);
                Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void execTask() {
        if (this.mLoginTask == null) {
            this.mLoginTask = new UserLoginTask(this);
            this.mLoginTask.execute(new Void[]{null});
        }
    }

    public final void launchSettingsActivity(String pin, String name) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("pin", pin);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void showAlertAndFinishActivity(String errorMessage) {
        new Builder(this).setTitle((CharSequence) "ERROR").setMessage((CharSequence) errorMessage).setCancelable(false).setPositiveButton((CharSequence) "Close", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                LoginActivity.this.finish();
            }
        }).create().show();
    }
}
