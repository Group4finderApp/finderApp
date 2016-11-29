package com.codepath.finderapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Arrays;

/**
 * Created by phoen on 11/23/2016.
 */

@ParseClassName("Albums")
public class ImageAlbum extends ParseObject {

    public void addPicIds (String [] picIds) {
        addAllUnique("pictureIds", Arrays.asList(picIds));
    }

    public void removePicIds (String [] picIds) {
        removeAll("pictureIds", Arrays.asList(picIds));
    }
    public String [] getPicIds ( ) {
        return (String []) get("pictureIds");
    }
}
