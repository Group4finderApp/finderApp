package com.codepath.finderapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.codepath.finderapp.activities.MainActivity;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";
    public static final String intentAction = "com.parse.push.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            Log.d(TAG, "Receiver intent null");
        } else {
            // Parse push message and handle accordingly
            processPush(context, intent);
        }
    }

    private void processPush(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "got action " + action);
        if (action.equals(intentAction)) {
            String channel = intent.getExtras().getString("com.parse.Channel");
            try {
                JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                Log.d(TAG, "got action " + action + " on channel " + channel + " with:");

                String key = "customdata";
                String value = json.getString(key);
                if (value != null){

                    String locationLat = json.getString("locationLat");
                    String locationLong = json.getString("locationLong");
                    String currentUser = ParseUser.getCurrentUser().getUsername();
                    if (!value.equalsIgnoreCase(currentUser)) {
                        // Don't create a notification for the same user
                        createNotification(context, value, locationLat, locationLong );
                    }
                }

            } catch (JSONException ex) {
                Log.d(TAG, "JSON failed!");
            }
        }
    }

    public static final int NOTIFICATION_ID = 45;
    // Create a local dashboard notification to tell user about the event
    // See: http://guides.codepath.com/android/Notifications

    private void createNotification(Context context, String datavalue, String locationLat, String locationLong) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("locationLat", locationLat);
        intent.putExtra("locationLong", locationLong);
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one
        PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intent, flags);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(
                R.mipmap.ic_launcher).setContentTitle("Notification").setContentText(datavalue + " just pinned!")
                .setContentIntent(pIntent)
                .setAutoCancel(true); // Hides the notification after its been selected;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());


    }

}

