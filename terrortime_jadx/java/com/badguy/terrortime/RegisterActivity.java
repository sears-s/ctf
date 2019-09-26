package com.badguy.terrortime;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private boolean cancel = false;
    /* access modifiers changed from: private */
    public ClientDBHandlerClass clientDB = null;
    TextView mCancelView = null;
    /* access modifiers changed from: private */
    public EditText mChatUserNameField = null;
    /* access modifiers changed from: private */
    public EditText mOAUTH2ServerIPAddrField = null;
    /* access modifiers changed from: private */
    public EditText mPasswordField = null;
    /* access modifiers changed from: private */
    public EditText mPinField = null;
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar = null;
    Button mRegisterButton = null;
    private View mRegisterFormView = null;
    /* access modifiers changed from: private */
    public EditText mXMPPServerIPAddrField = null;

    private static class KeyGenerationTask extends AsyncTask<Void, Integer, Client> {
        WeakReference<RegisterActivity> activityWeakReference;
        private String errorMsg = BuildConfig.FLAVOR;
        private Client mClient;

        KeyGenerationTask(RegisterActivity context) throws Exception {
            this.activityWeakReference = new WeakReference<>(context);
            RegisterActivity activity = (RegisterActivity) this.activityWeakReference.get();
            this.mClient = new Client(activity.mChatUserNameField.getText().toString());
            this.mClient.setXmppUserName(activity.mChatUserNameField.getText().toString());
            this.mClient.setXmppServerIP(activity.mXMPPServerIPAddrField.getText().toString());
            this.mClient.setOAuth2ServerIP(activity.mOAUTH2ServerIPAddrField.getText().toString());
            this.mClient.setEncryptPin(activity.mPinField.getText().toString());
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
            RegisterActivity activity = (RegisterActivity) this.activityWeakReference.get();
            View currentView = activity.getCurrentFocus();
            if (currentView != null) {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
        }

        /* access modifiers changed from: protected */
        public Client doInBackground(Void... params) {
            try {
                this.mClient.generateSymmetricKey();
                this.mClient.generatePublicPrivateKeys();
                Client toRtn = this.mClient;
                this.errorMsg = "SUCCESS";
            } catch (Exception e) {
                this.mClient = null;
                this.errorMsg = e.getMessage();
                Log.e("EXCEPTION LOG", "KeyGenerationTask: ", e);
            }
            return this.mClient;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Client client) {
            super.onPostExecute(client);
            final RegisterActivity activity = (RegisterActivity) this.activityWeakReference.get();
            boolean success = true;
            activity.mProgressBar.setVisibility(8);
            String str = "Registration failed. Select OK to close window. ";
            String str2 = "ERROR LOG";
            if (client != null) {
                try {
                    client.setOAuth2ClientSecret(activity.mPinField.getText().toString(), activity.mPasswordField.getText().toString().getBytes());
                    activity.clientDB.addOrUpdateClient(client);
                } catch (Exception e) {
                    success = false;
                    StringBuilder sb = new StringBuilder();
                    sb.append(str);
                    sb.append(e.getMessage());
                    this.errorMsg = sb.toString();
                    Log.e(str2, e.getMessage());
                }
            } else {
                success = false;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Registration failed. ");
                sb2.append(this.errorMsg);
                Log.e(str2, sb2.toString());
            }
            if (!success) {
                if (activity.clientDB != null) {
                    activity.clientDB.dropAllTables();
                    activity.clientDB.close();
                }
                Builder title = new Builder(activity).setTitle((CharSequence) "ERROR");
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                sb3.append(this.errorMsg);
                title.setMessage((CharSequence) sb3.toString()).setCancelable(false).setPositiveButton((CharSequence) "OK", (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("keygen task", "Key generation failed");
                        dialog.dismiss();
                        activity.finish();
                    }
                }).create().show();
                return;
            }
            Log.v("keygen task", "Key generation completed successfully.");
            activity.finish();
        }
    }

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_register);
        this.cancel = false;
        this.mXMPPServerIPAddrField = (EditText) findViewById(R.id.xmpp_server_ip);
        this.mOAUTH2ServerIPAddrField = (EditText) findViewById(R.id.oauth2_server_ip);
        this.mChatUserNameField = (EditText) findViewById(R.id.userName);
        this.mPasswordField = (EditText) findViewById(R.id.password);
        this.mPinField = (EditText) findViewById(R.id.pin);
        this.mRegisterFormView = findViewById(R.id.register_form);
        this.mCancelView = (TextView) findViewById(R.id.register_cancel);
        this.mRegisterButton = (Button) findViewById(R.id.register_button);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        this.mProgressBar.setVisibility(4);
        final Map<EditText, String> fMap = new HashMap<>();
        fMap.put(this.mXMPPServerIPAddrField, "xmppServerIPField");
        fMap.put(this.mOAUTH2ServerIPAddrField, "oauth2ServerIPField");
        fMap.put(this.mChatUserNameField, "clientIdField");
        fMap.put(this.mPasswordField, "clientSecretField");
        fMap.put(this.mPinField, "pinField");
        this.clientDB = ClientDBHandlerClass.getInstance(this.mPinField.getText().toString(), getApplicationContext());
        this.mRegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean existingRecord = false;
                String str = "setOnClickListener: ";
                String str2 = "EXCEPTION LOG";
                if (RegisterActivity.this.validateFields(fMap)) {
                    RegisterActivity.this.mRegisterButton.setEnabled(false);
                    try {
                        if (RegisterActivity.this.clientDB.nClients().intValue() != 0) {
                            existingRecord = true;
                            RegisterActivity.this.mRegisterButton.setEnabled(true);
                            new Builder(RegisterActivity.this).setTitle((CharSequence) "WARNING").setMessage((CharSequence) "Select 'Continue' to reconfigure Terrortime for new user. All current user data will be lost.").setCancelable(false).setNegativeButton((CharSequence) "Continue", (OnClickListener) new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    RegisterActivity.this.clientDB.dropAllTables();
                                    RegisterActivity.this.clientDB.close();
                                    RegisterActivity.this.clientDB = ClientDBHandlerClass.getInstance(RegisterActivity.this.mPinField.getText().toString(), RegisterActivity.this.getApplicationContext());
                                    RegisterActivity.this.mRegisterButton.performClick();
                                }
                            }).setPositiveButton((CharSequence) "Cancel", (OnClickListener) new OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    for (EditText field : fMap.keySet()) {
                                        field.getText().clear();
                                        field.setError(null);
                                    }
                                    RegisterActivity.this.mCancelView.performClick();
                                }
                            }).create().show();
                        }
                        if (RegisterActivity.this.clientDB == null) {
                            throw new RuntimeException("Failed to connect to Client database. Try again or select \"cancel\".");
                        } else if (!existingRecord) {
                            RegisterActivity.this.registerNewTerrorist();
                        }
                    } catch (Exception e) {
                        Log.e(str2, str, e);
                        Toast.makeText(RegisterActivity.this.getApplicationContext(), e.getMessage(), 1).show();
                    }
                } else {
                    for (EditText field : fMap.keySet()) {
                        try {
                            String fName = (String) fMap.get(field);
                            if (field == null) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("Fatal Error: ");
                                sb.append(fName);
                                sb.append(" was null.");
                                throw new RuntimeException(sb.toString());
                            } else if (field.getError() != null) {
                                field.requestFocus();
                                Toast.makeText(RegisterActivity.this.getApplicationContext(), field.getError().toString(), 1).show();
                                return;
                            }
                        } catch (Exception e2) {
                            Log.e(str2, str, e2);
                            Toast.makeText(RegisterActivity.this.getApplicationContext(), e2.getMessage(), 1).show();
                        }
                    }
                }
            }
        });
        this.mCancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                v.requestFocus();
                RegisterActivity.this.finish();
            }
        });
    }

    /* access modifiers changed from: protected */
    public final void onDestroy() {
        super.onDestroy();
        ClientDBHandlerClass clientDBHandlerClass = this.clientDB;
        if (clientDBHandlerClass != null) {
            clientDBHandlerClass.close();
            this.clientDB = null;
        }
    }

    /* access modifiers changed from: private */
    public void registerNewTerrorist() throws Exception {
        if (this.clientDB != null) {
            this.mProgressBar.setVisibility(0);
            new KeyGenerationTask(this).execute(new Void[]{null});
            return;
        }
        throw new RuntimeException("Failed to connect to Client database. Try again or select \"cancel\".");
    }

    /* access modifiers changed from: private */
    public boolean validateFields(Map<EditText, String> fieldMap) {
        ParameterValidatorClass pvalidator = new ParameterValidatorClass();
        this.mXMPPServerIPAddrField.setError(null);
        this.mChatUserNameField.setError(null);
        this.mPasswordField.setError(null);
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
                    } else if (field == this.mXMPPServerIPAddrField && !pvalidator.isValidIpAddress(fText)) {
                        field.setError(getString(R.string.error_invalid_server_ip));
                        return false;
                    } else if (field == this.mOAUTH2ServerIPAddrField && !pvalidator.isValidIpAddress(fText)) {
                        field.setError(getString(R.string.error_invalid_server_ip));
                        return false;
                    } else if (field == this.mChatUserNameField && !pvalidator.isValidUserName(fText)) {
                        field.setError(getString(R.string.error_invalid_userName));
                        return false;
                    } else if (field == this.mPasswordField && !pvalidator.isValidPassword(fText)) {
                        field.setError(getString(R.string.error_invalid_password));
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
}
