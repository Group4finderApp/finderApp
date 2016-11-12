package com.codepath.finderapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.finderapp.R;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    private ParseGeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        
        // New test creation of object below
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("fooManisha", "barManisha");
        testObject.saveInBackground();
    }
}
