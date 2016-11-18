package com.codepath.finderapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;

import bolts.AppLinks;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

  public DispatchActivity() {
  }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(this, WelcomeActivity.class);
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            i.putExtra("hide_login", false);
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            i.putExtra("hide_login", true);
        } else {
            // Start and intent for the logged out activity
            i.putExtra("hide_login", false);
        }
        startActivity(i);
    }

}
