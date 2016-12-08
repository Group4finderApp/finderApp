package com.codepath.finderapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        // Get access to our TextView
        TextView txt = (TextView) findViewById(R.id.custom_font);
        // Create the TypeFace from the TTF asset
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/BOOKOSB.TTF");
        // Assign the typeface to the view
        txt.setTypeface(font);
        playAnimation();
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

    public void playAnimation() {

        final ImageView marker1 = (ImageView) findViewById(R.id.marker1);
        final ImageView marker2 = (ImageView) findViewById(R.id.marker2);
        final ImageView marker3 = (ImageView) findViewById(R.id.marker3);
        final ImageView marker4 = (ImageView) findViewById(R.id.marker4);
        final ImageView marker5 = (ImageView) findViewById(R.id.marker5);
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);

        marker1.setVisibility(View.VISIBLE);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(marker1, "alpha", 0f, 1f);
        fadeAnim.setDuration(2000);
        fadeAnim.start();
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                marker2.setVisibility(View.VISIBLE);
                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(marker2, "alpha", 0f, 1f);
                fadeAnim.setDuration(2000);
                fadeAnim.start();
                fadeAnim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        marker3.setVisibility(View.VISIBLE);
                        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(marker3, "alpha", 0f, 1f);
                        fadeAnim.setDuration(2000);
                        fadeAnim.start();

                        fadeAnim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                marker4.setVisibility(View.VISIBLE);
                                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(marker4, "alpha", 0f, 1f);
                                fadeAnim.setDuration(2000);
                                fadeAnim.start();
                                fadeAnim.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        marker5.setVisibility(View.VISIBLE);
                                        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(marker5, "alpha", 0f, 1f);
                                        fadeAnim.setDuration(2000);
                                        fadeAnim.start();

                                        fadeAnim.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                rl.setVisibility(View.VISIBLE);
                                                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(rl, "alpha", 0f, 1f);
                                                fadeAnim.setDuration(2000);
                                                fadeAnim.start();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });


        /*final ImageView pawPrint1 = (ImageView) findViewById(R.id.pawPrint1);
        final ImageView pawPrint2 = (ImageView) findViewById(R.id.pawPrint2);

        pawPrint1.setVisibility(View.INVISIBLE);
        pawPrint1.setVisibility(View.INVISIBLE);
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(pawPrint1, "rotation", 45f);
        ObjectAnimator rotateAnim1 = ObjectAnimator.ofFloat(pawPrint2, "rotation", 45f);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(rotateAnim, rotateAnim1);
        set1.start();

        pawPrint1.setVisibility(View.VISIBLE);
        pawPrint1.setVisibility(View.VISIBLE);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(pawPrint1, "alpha", 0f);
        fadeAnim.setDuration(2000);
        fadeAnim.start();
        fadeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                pawPrint1.setVisibility(View.INVISIBLE);
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(pawPrint1, "translationX", 100f);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(pawPrint1, "translationY", -5f);
                ObjectAnimator anim3 = ObjectAnimator.ofFloat(pawPrint1, "rotation", 40f);
                AnimatorSet set1 = new AnimatorSet();
                set1.playTogether(anim1, anim2, anim3);
                set1.start();
                set1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pawPrint1.setVisibility(View.VISIBLE);
                        ObjectAnimator anim3 = ObjectAnimator.ofFloat(pawPrint1, "alpha", 0f, 1f);
                        anim3.start();
                        anim3.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(pawPrint2, "alpha", 0f);
                                fadeAnim.setDuration(2000);
                                fadeAnim.start();
                                fadeAnim.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        pawPrint2.setVisibility(View.INVISIBLE);
                                        ObjectAnimator anim1 = ObjectAnimator.ofFloat(pawPrint2, "translationX", 100f);
                                        ObjectAnimator anim2 = ObjectAnimator.ofFloat(pawPrint2, "translationY", -5f);
                                        ObjectAnimator anim3 = ObjectAnimator.ofFloat(pawPrint2, "rotation", 40f);
                                        AnimatorSet set1 = new AnimatorSet();
                                        set1.playTogether(anim1, anim2, anim3);
                                        set1.start();
                                        set1.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                pawPrint2.setVisibility(View.VISIBLE);
                                                ObjectAnimator anim3 = ObjectAnimator.ofFloat(pawPrint2, "alpha", 0f, 1f);
                                                anim3.start();


                                            }
                                        });

                                    }
                                });


                            }
                        });
                    }

                    //fadeAnim.start();




                });


            }
        });*/
    }
}

