package com.codepath.finderapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.HomeMapView;
import com.codepath.finderapp.utils.AppUtils;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        AppUtils.disPlayFragment(getSupportFragmentManager(), new HomeMapView(), R.id.content_container);
    }
}
