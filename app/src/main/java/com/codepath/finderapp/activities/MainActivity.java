package com.codepath.finderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.finderapp.DispatchActivity;
import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.HomeMapView;
import com.codepath.finderapp.utils.AppUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, CameraPostActivity.class);
        startActivity(i);

        // New test creation of object below
        //ParseObject testObject = new ParseObject("TestObject");
        //testObject.put("fooManisha", "barManisha");
        //testObject.put("sinkari", "Kassim");
        //testObject.saveInBackground();

        //Log.d("debug", ParseUser.getCurrentUser().getUsername() + " " + ParseUser.getCurrentUser().getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                onLogout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogout() {
        // Log the user out
        ParseUser.logOut();
        // close this user's session
        LoginManager.getInstance().logOut();
        // Go to the login view
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                onLogout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogout() {
        // Log the user out
        ParseUser.logOut();
        // close this user's session
        LoginManager.getInstance().logOut();
        // Go to the login view
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

