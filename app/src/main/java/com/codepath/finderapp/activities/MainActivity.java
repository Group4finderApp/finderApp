package com.codepath.finderapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.CameraFragment;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseGeoPoint;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    private ParseGeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CameraFragment())
                    .commit();
        }
        /*
        byte[] data = "some file is working yohoo!".getBytes();
        ParseFile file = new ParseFile("image.txt", data);
        file.saveInBackground();

        /*
        // Create a post.
        PicturePost post = new PicturePost();

        post.setImage(file);

        geoPoint = new ParseGeoPoint(50, -150);
        // Set the location to the current user's location
        post.setLocation(geoPoint);
        post.setText("Manisha testing PicturePost from new database");
        //ParseUser user = ParseUser.getCurrentUser();
        //post.setUser(user);
        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        post.setACL(acl);

        // Save the post
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getApplicationContext(), "Posted picture post", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

}

