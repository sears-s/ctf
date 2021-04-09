package com.badguy.terrortime;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.lang.ref.WeakReference;
import java.util.Map;

public class CheckedClient {
    private String clientId = null;
    private SettingsActivity context = null;
    private Map<EditText, String> fMap;
    private String pin = null;

    private static class modCheckedClient extends AsyncTask<Void, Integer, Boolean> {
        private WeakReference<SettingsActivity> activityWeakReference;
        Client client = null;
        String errorMsg = BuildConfig.FLAVOR;
        Map<EditText, String> withThis = null;

        public modCheckedClient(SettingsActivity context, Client client2, Map<EditText, String> withThis2) {
            this.activityWeakReference = new WeakReference<>(context);
            SettingsActivity settingsActivity = (SettingsActivity) this.activityWeakReference.get();
            this.client = client2;
            this.withThis = withThis2;
            Log.d("CheckedClient", "Task constructor complete");
        }

        /* access modifiers changed from: protected */
        public final void onPreExecute() {
            SettingsActivity activity = (SettingsActivity) this.activityWeakReference.get();
            activity.mSettingsButton.setEnabled(false);
            activity.mClearTokenView.setEnabled(false);
            activity.mProgressBar.setVisibility(0);
        }

        /* access modifiers changed from: protected */
        public final Boolean doInBackground(Void... params) {
            boolean success = true;
            String str = "CheckedClient";
            if (!isCancelled()) {
                try {
                    this.client.generateSymmetricKey();
                    Log.d(str, "Task generateSymmetricKey complete");
                } catch (Exception e) {
                    success = false;
                    String str2 = "Bad Pin";
                    this.errorMsg = str2;
                    Log.e("EXCEPTION LOG", str2);
                }
                if (success && !updateClientDataBase()) {
                    this.errorMsg = "Failed to update client database";
                    success = false;
                }
            }
            Log.d(str, "Task doInBackground complete");
            return Boolean.valueOf(success);
        }

        private final boolean updateClientDataBase() {
            SettingsActivity activity = (SettingsActivity) this.activityWeakReference.get();
            boolean success = true;
            ClientDBHandlerClass clientDB = null;
            String str = "CheckedClient";
            Log.d(str, "Starting updateClientDataBase");
            if (this.client != null) {
                for (EditText field : this.withThis.keySet()) {
                    try {
                        String fName = (String) this.withThis.get(field);
                        StringBuilder sb = new StringBuilder();
                        sb.append("UpdateClientDataBase, field name processing: ");
                        sb.append(fName);
                        Log.d(str, sb.toString());
                        if (field != null) {
                            String fText = field.getText().toString();
                            if (fName.equals("xmppServerIPField") && !field.getText().toString().isEmpty()) {
                                this.client.setXmppServerIP(field.getText().toString());
                            } else if (fName.equals("oauth2ServerIPField") && !field.getText().toString().isEmpty()) {
                                this.client.setOAuth2ServerIP(field.getText().toString());
                            } else if (fName.equals("passwordField") && !field.getText().toString().isEmpty()) {
                                this.client.setOAuth2ClientSecret(this.client.getEncryptPin(), field.getText().toString().getBytes());
                            }
                            Log.d(str, "UpdateClientDataBase, completed client object update.");
                        } else {
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Fatal Error: ");
                            sb2.append(fName);
                            sb2.append(" was null.");
                            throw new RuntimeException(sb2.toString());
                        }
                    } catch (Exception e) {
                        Log.e("EXCEPTION LOG", "updateClientDataBase: ", e);
                        this.errorMsg = "Unknown error: Failed to update client settings";
                        success = false;
                    }
                }
                if (success) {
                    clientDB = ClientDBHandlerClass.getInstance(this.client.getEncryptPin(), activity.getApplicationContext());
                    if (clientDB == null) {
                        this.errorMsg = "Activity: Failed to open client database. clientDB was null";
                        success = false;
                    }
                    Log.d(str, "UpdateClientDataBase, completed clientDB acquire.");
                }
                if (success) {
                    try {
                        this.client.setEncrypted_oAuth2AccessToken(null);
                        this.client.setOAuth2AccessTokenExpiration(Integer.valueOf(0));
                        clientDB.addOrUpdateClient(this.client);
                    } catch (Exception e2) {
                        this.errorMsg = "Activity: Failed to update client in database";
                        success = false;
                    }
                    Log.d(str, "UpdateClientDataBase, completed client update in database.");
                }
            } else {
                this.errorMsg = "Unknown error: client is null";
                success = false;
            }
            if (clientDB != null) {
                clientDB.close();
            }
            Log.d(str, "Task updateClientDataBase complete");
            return success;
        }

        /* access modifiers changed from: protected */
        public final void onPostExecute(Boolean result) {
            SettingsActivity activity = (SettingsActivity) this.activityWeakReference.get();
            activity.mProgressBar.setVisibility(8);
            if (!result.booleanValue()) {
                activity.alertAndFinish(this.errorMsg);
            } else {
                activity.finish();
            }
        }

        /* access modifiers changed from: protected */
        public final void onCancelled() {
            ((SettingsActivity) this.activityWeakReference.get()).finish();
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... progress) {
        }
    }

    public CheckedClient(SettingsActivity context2, String clientId2, String pin2, Map<EditText, String> fMap2) {
        String str = "ERROR";
        if (context2 == null) {
            Log.e(str, "Activity context was null.");
        } else if (pin2 == null) {
            Log.e(str, "Pin was null.");
        } else if (clientId2 == null) {
            Log.e(str, "Client id was null.");
        } else if (fMap2 == null) {
            Log.e(str, "Field map was null.");
        } else {
            this.context = context2;
            this.clientId = clientId2;
            this.pin = pin2;
            this.fMap = fMap2;
            View currentView = context2.getCurrentFocus();
            if (currentView != null) {
                ((InputMethodManager) context2.getSystemService("input_method")).hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
        }
    }

    private boolean clientIdIsCorrect() {
        boolean success = true;
        ClientDBHandlerClass clientDB = ClientDBHandlerClass.getInstance(this.pin, this.context.getApplicationContext());
        if (clientDB == null) {
            success = false;
            taskAlertAndFinish("Activity: Failed to open client database. clientDB was null.");
        }
        if (success && clientDB.countClients(this.clientId).intValue() == 0) {
            success = false;
        }
        if (clientDB != null) {
            clientDB.close();
        }
        return success;
    }

    public void updateClientSettings() {
        if (!clientIdIsCorrect()) {
            this.context.alertAndFinish("Bad Client Id");
            return;
        }
        ClientDBHandlerClass clientDB = ClientDBHandlerClass.getInstance(this.pin, this.context.getApplicationContext());
        if (clientDB == null) {
            this.context.alertAndFinish("Activity: Failed to open client database. clientDB was null.");
            return;
        }
        Client client = clientDB.getClient(this.clientId);
        if (client == null) {
            clientDB.close();
            taskAlertAndFinish("Activity: Unknown error. Did not get client from database.");
            return;
        }
        client.setEncryptPin(this.pin);
        if (clientDB != null) {
            clientDB.close();
        }
        new modCheckedClient(this.context, client, this.fMap).execute(new Void[]{null});
    }

    private void taskAlertAndFinish(String errorMsg) {
        final SettingsActivity activity = this.context;
        String dialogMsg = errorMsg;
        if (dialogMsg == null) {
            dialogMsg = BuildConfig.FLAVOR;
        }
        Builder title = new Builder(activity).setTitle((CharSequence) "ERROR");
        StringBuilder sb = new StringBuilder();
        sb.append("Settings failed. Select OK to close window. ");
        sb.append(dialogMsg);
        title.setMessage((CharSequence) sb.toString()).setCancelable(false).setPositiveButton((CharSequence) "OK", (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.v("SettingsActivity", "Closing SettingsActivity after error.");
                dialog.dismiss();
                activity.finish();
            }
        }).create().show();
    }
}
