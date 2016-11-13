package com.codepath.finderapp.network;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseUser;

import org.json.JSONException;

/**
 * Created by phoen on 11/12/2016.
 */

public class FacebookGraphClient {

    public static void getUserDetailsFromFB() {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
         /* handle the result */
                        try {
                            String email = response.getJSONObject().getString("email");
                            String name = response.getJSONObject().getString("name");
                            //save new user
                            ParseUser parseUser = ParseUser.getCurrentUser();
                            parseUser.setUsername(name);
                            parseUser.setEmail(email);
                            parseUser.saveInBackground();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

}
