package com.codepath.finderapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = {User.class})
@ParseClassName("_User")
public class User extends ParseUser{

    //Dummy class for now just to add the structure to github

    public String getProfilePicUrl() {
        return getString("profilePictureUrl");
    }

    public void setProfilePicUrl(String value) {
        put("profilePictureUrl", value);
    }

}
