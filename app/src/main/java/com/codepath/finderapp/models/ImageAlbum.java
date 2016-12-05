package com.codepath.finderapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by phoen on 11/23/2016.
 */

@ParseClassName("Albums")
public class ImageAlbum extends ParseObject {

    //public void addPicIds (String [] picIds) {
    //    addAllUnique("pictureIds", Arrays.asList(picIds));
    //}

    //public void removePicIds (String [] picIds) {
    //    removeAll("pictureIds", Arrays.asList(picIds));
    //}
    //public String [] getPicIds ( ) {
    //    return (String []) get("pictureIds");
    //}
    public void setAlbumName (String albumName) {put ("albumname", albumName);}
    public void setPictureCount (int count) {put ("piccount", count);}
    public void setCoverPic(ParseFile image) { put("coverImage", image); }
    public ParseFile getCoverPic() { return getParseFile("coverImage"); }
    public void setOwner(ParseUser value) {
        put("owner", value);
    }
    public String getAlbumName() {
        return getString("albumname");
    }
    public int getPictureCount() {
        return getInt("piccount");
    }
    public ParseUser getOwner() {
        return getParseUser("owner");
    }
}
