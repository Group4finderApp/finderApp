package com.codepath.finderapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Sign up with facebook
        Button facebookLoginButton = (Button) findViewById(R.id.facebooklogin_button);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseFacebookUtils.logInWithReadPermissionsInBackground(WelcomeActivity.this,
                        Arrays.asList("email", "public_profile"), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void startMainActivity() {
        Intent intent = new Intent(WelcomeActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
