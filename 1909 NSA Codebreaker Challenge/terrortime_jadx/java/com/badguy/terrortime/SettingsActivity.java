package com.badguy.terrortime;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private ClientDBHandlerClass clientDB = null;
    /* access modifiers changed from: private */
    public EditText mChatUserNameField = null;
    public TextView mClearTokenView = null;
    private EditText mOAUTH2ServerIPAddrField = null;
    private EditText mPasswordField = null;
    /* access modifiers changed from: private */
    public EditText mPinField = null;
    public ProgressBar mProgressBar = null;
    public Button mSettingsButton = null;
    private EditText mXMPPServerIPAddrField = null;
    /* access modifiers changed from: private */
    public String name;
    /* access modifiers changed from: private */
    public String pin;

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_settings);
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar3);
        this.mProgressBar.setVisibility(4);
        this.mChatUserNameField = (EditText) findViewById(R.id.loginUserName_settings);
        this.mPinField = (EditText) findViewById(R.id.loginpin_settings);
        this.mXMPPServerIPAddrField = (EditText) findViewById(R.id.xmpp_server_ip_settings);
        this.mOAUTH2ServerIPAddrField = (EditText) findViewById(R.id.oauth2_server_ip_settings);
        this.mPasswordField = (EditText) findViewById(R.id.password_settings);
        this.mSettingsButton = (Button) findViewById(R.id.update_button);
        this.mClearTokenView = (TextView) findViewById(R.id.clear_token);
        final Map<EditText, String> fMap = new HashMap<>();
        fMap.put(this.mChatUserNameField, "chatUserNameField");
        fMap.put(this.mPinField, "pinField");
        fMap.put(this.mXMPPServerIPAddrField, "xmppServerIPField");
        fMap.put(this.mOAUTH2ServerIPAddrField, "oauth2ServerIPField");
        fMap.put(this.mPasswordField, "passwordField");
        this.clientDB = null;
        this.mClearTokenView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SettingsActivity.this.mSettingsButton.setEnabled(true);
                SettingsActivity.this.mClearTokenView.setEnabled(true);
                if (SettingsActivity.this.validateFields(fMap)) {
                    SettingsActivity settingsActivity = SettingsActivity.this;
                    settingsActivity.name = settingsActivity.mChatUserNameField.getText().toString();
                    SettingsActivity settingsActivity2 = SettingsActivity.this;
                    settingsActivity2.pin = settingsActivity2.mPinField.getText().toString();
                    SettingsActivity.this.updateClientSettings(fMap);
                    Log.d("SettingsActivity", "Completed activity without error.");
                    return;
                }
                for (EditText field : fMap.keySet()) {
                    try {
                        if (field.getError() != null) {
                            field.requestFocus();
                            Toast.makeText(SettingsActivity.this.getApplicationContext(), field.getError().toString(), 1).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("EXCEPTION LOG", "setOnClickListener: ", e);
                        Toast.makeText(SettingsActivity.this.getApplicationContext(), e.getMessage(), 1).show();
                    }
                }
            }
        });
        this.mSettingsButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (SettingsActivity.this.validateFields(fMap)) {
                    SettingsActivity.this.mSettingsButton.setEnabled(true);
                    SettingsActivity.this.mClearTokenView.setEnabled(true);
                    SettingsActivity settingsActivity = SettingsActivity.this;
                    settingsActivity.name = settingsActivity.mChatUserNameField.getText().toString();
                    SettingsActivity settingsActivity2 = SettingsActivity.this;
                    settingsActivity2.pin = settingsActivity2.mPinField.getText().toString();
                    SettingsActivity.this.updateClientSettings(fMap);
                    Log.d("SettingsActivity", "Completed activity without error.");
                    return;
                }
                for (EditText field : fMap.keySet()) {
                    try {
                        if (field.getError() != null) {
                            field.requestFocus();
                            Toast.makeText(SettingsActivity.this.getApplicationContext(), field.getError().toString(), 1).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("EXCEPTION LOG", "setOnClickListener: ", e);
                        Toast.makeText(SettingsActivity.this.getApplicationContext(), e.getMessage(), 1).show();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateClientSettings(Map<EditText, String> fMap) {
        new CheckedClient(this, this.mChatUserNameField.getText().toString(), this.mPinField.getText().toString(), fMap).updateClientSettings();
    }

    /* access modifiers changed from: private */
    public boolean validateFields(Map<EditText, String> fieldMap) {
        ParameterValidatorClass pvalidator = new ParameterValidatorClass();
        this.mXMPPServerIPAddrField.setError(null);
        this.mPasswordField.setError(null);
        for (EditText field : fieldMap.keySet()) {
            try {
                String fName = (String) fieldMap.get(field);
                if (field != null) {
                    String fText = field.getText().toString();
                    if (!fText.isEmpty() && field == this.mXMPPServerIPAddrField && !pvalidator.isValidIpAddress(fText)) {
                        field.setError(getString(R.string.error_invalid_server_ip));
                        return false;
                    } else if (!fText.isEmpty() && field == this.mOAUTH2ServerIPAddrField && !pvalidator.isValidIpAddress(fText)) {
                        field.setError(getString(R.string.error_invalid_server_ip));
                        return false;
                    } else if (!fText.isEmpty() && field == this.mPasswordField && !pvalidator.isValidPassword(fText)) {
                        field.setError(getString(R.string.error_invalid_password));
                        return false;
                    } else if (field == this.mChatUserNameField) {
                        if (field.getText().toString().isEmpty()) {
                            field.setError(getString(R.string.error_field_required));
                            return false;
                        } else if (!pvalidator.isValidUserName(fText)) {
                            field.setError(getString(R.string.error_invalid_userName));
                            return false;
                        }
                    } else if (field == this.mPinField) {
                        if (field.getText().toString().isEmpty()) {
                            field.setError(getString(R.string.error_field_required));
                            return false;
                        } else if (!pvalidator.isValidPin(fText)) {
                            field.setError(getString(R.string.error_invalid_pin));
                            return false;
                        }
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Fatal Error: ");
                    sb.append(fName);
                    sb.append(" was null.");
                    throw new RuntimeException(sb.toString());
                }
            } catch (Exception e) {
                Log.e("EXCEPTION LOG", "validateFields: ", e);
                Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
            }
        }
        return true;
    }

    public void alertAndFinish(String errorMsg) {
        String dialogMsg = errorMsg;
        if (dialogMsg == null) {
            dialogMsg = BuildConfig.FLAVOR;
        }
        Builder title = new Builder(this).setTitle((CharSequence) "ERROR");
        StringBuilder sb = new StringBuilder();
        sb.append("Settings failed. Select OK to close window. ");
        sb.append(dialogMsg);
        title.setMessage((CharSequence) sb.toString()).setCancelable(false).setPositiveButton((CharSequence) "OK", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.v("SettingsActivity", "Closing SettingsActivity after error.");
                dialog.dismiss();
                SettingsActivity.this.finish();
            }
        }).create().show();
    }
}
