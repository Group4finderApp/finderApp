package com.codepath.finderapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.finderapp.R;
import com.codepath.finderapp.network.FacebookGraphClient;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        // Sign up with facebook
        Button facebookLoginButton = (Button) findViewById(R.id.facebooklogin_button);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(
                        WelcomeActivity.this, "", "Logging in...", true);
                ParseFacebookUtils.logInWithReadPermissionsInBackground(WelcomeActivity.this,
                        Arrays.asList("email", "public_profile"), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                progressDialog.dismiss();
                                if (err != null) {
                                    Log.d("Debug", "Error occurred" + err.toString());
                                } else if (user == null) {
                                    Log.d("Debug", "The user cancelled the Facebook login.");
                                } else {
                                    if (user.isNew()) {
                                        Log.d("Debug", "User signed up and logged in through Facebook!");
                                    } else {
                                        Log.d("Debug", "Logged in " + user.getUsername());
                                        Log.d("Debug", "User logged in through Facebook!");
                                    }
                                    FacebookGraphClient.getUserDetailsFromFB();
                                    // Start an intent for the dispatch activity
                                    startMainActivity();
                                }
                            }
                        });
            }
        });

        if (ParseUser.getCurrentUser() != null) {
            startMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
