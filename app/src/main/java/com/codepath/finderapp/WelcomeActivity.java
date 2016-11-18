package com.codepath.finderapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.finderapp.activities.MainActivity;
import com.codepath.finderapp.network.FacebookGraphClient;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
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
        boolean hideLogin = getIntent().getBooleanExtra("hide_login", false);
        Button appInvite = (Button) findViewById(R.id.invite_button);
        Button skipInvite = (Button) findViewById(R.id.continue_button);
        // Sign up with facebook
        Button facebookLoginButton = (Button) findViewById(R.id.facebooklogin_button);
        if (hideLogin) {
            facebookLoginButton.setVisibility(View.GONE);
        } else {
            skipInvite.setVisibility(View.GONE);
        }
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
                                } else if (user.isNew()) {
                                    Log.d("Debug", "User signed up and logged in through Facebook!");
                                    FacebookGraphClient.getUserDetailsFromFB();
                                    // Start an intent for the dispatch activity
                                    Toast.makeText(WelcomeActivity.this, user.getEmail(), Toast.LENGTH_SHORT)
                                            .show();
                                    startMainActivity();
                                } else {
                                    Toast.makeText(WelcomeActivity.this, "Logged in " + user.getUsername(), Toast.LENGTH_SHORT)
                                            .show();
                                    Log.d("Debug", "User logged in through Facebook!");
                                    startMainActivity();
                                }
                            }
                        });
            }
        });
        if (hideLogin)
            facebookLoginButton.setVisibility(View.INVISIBLE);

        appInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendInvite();
            }
        });

        skipInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
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

    public void SendInvite() {
        String appLinkUrl =  "https://fb.me/161679120966718";
        String previewImageUrl = "https://i.imgur.com/XgxWfyF.png";
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }

}
