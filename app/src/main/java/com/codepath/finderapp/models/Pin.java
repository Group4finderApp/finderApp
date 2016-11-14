package com.codepath.finderapp.models;

/**
 * Created by hison7463 on 11/13/16.
 */

public class Pin {

    private double latitude;
    private double longitude;
    private String profile;
    private String image;
    private String caption;
    private boolean thumbUp;

    public Pin() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(boolean thumbUp) {
        this.thumbUp = thumbUp;
    }

    @Override
    public String toString() {
        return "Pin{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", profile='" + profile + '\'' +
                ", image='" + image + '\'' +
                ", caption='" + caption + '\'' +
                ", thumbUp=" + thumbUp +
                '}';
    }
}
