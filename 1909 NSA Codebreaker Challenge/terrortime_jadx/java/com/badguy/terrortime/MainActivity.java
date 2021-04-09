package com.badguy.terrortime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    String msg = "Android : ";

    static {
        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("terrortime");
    }

    /* access modifiers changed from: protected */
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_main);
    }

    public final void launchRegisterActivity(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public final void launchLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        Log.d(this.msg, "The onStart() event");
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Log.d(this.msg, "The onResume() event");
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        Log.d(this.msg, "The onPause() event");
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Log.d(this.msg, "The onStop() event");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(this.msg, "The onDestroy() event");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.menu_settings) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }
}
