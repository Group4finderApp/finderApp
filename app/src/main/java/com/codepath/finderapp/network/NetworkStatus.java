package com.codepath.finderapp.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class NetworkStatus {

    private static NetworkStatus sInstance;

    public static synchronized NetworkStatus getSharedInstance() {
        if (sInstance == null) {
            sInstance = new NetworkStatus();
        }
        return sInstance;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkNetworkStatus(Context c) {
        return isNetworkAvailable(c) && isOnline();
    }
}

