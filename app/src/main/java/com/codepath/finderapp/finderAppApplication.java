package com.codepath.finderapp;

import android.app.Application;

import com.codepath.finderapp.models.PicturePost;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseFacebookUtils;
import com.parse.interceptors.ParseLogInterceptor;

/**
 * Created by chmanish on 11/12/16.
 */
public class finderAppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        ParseObject.registerSubclass(PicturePost.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("finderkeeper") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server("http://finderkeeper.herokuapp.com/parse").build());

        // ParseFacebookUtils should initialize the Facebook SDK for you
        ParseFacebookUtils.initialize(this);
    }



}
