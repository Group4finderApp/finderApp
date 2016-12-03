package com.codepath.finderapp.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by phoen on 12/2/2016.
 */
@Parcel
public class PicturePostCollection {

        List<PicturePost> postList;

        public PicturePostCollection () {

        }

        public PicturePostCollection(List posts) {
            this.postList = posts;
        }

        public List<PicturePost> getPostList() {
            return postList;
        }
}
