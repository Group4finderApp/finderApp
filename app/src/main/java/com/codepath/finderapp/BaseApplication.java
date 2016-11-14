package com.codepath.finderapp;

import android.app.Application;

/**
 * Created by hison7463 on 11/12/16.
 */

public class BaseApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Application getApplication() {
        return application;
    }
}
