package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.AlbumAdapter;
import com.codepath.finderapp.adapters.ItemClickSupport;
import com.codepath.finderapp.adapters.SpacesItemDecoration;
import com.codepath.finderapp.models.ImageAlbum;
import com.codepath.finderapp.models.PicturePost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by phoen on 11/23/2016.
 */

public class AlbumFragment extends Fragment {

    List<ImageAlbum> myAlbums;
    List<PicturePost> albumPosts;
    RecyclerView rvPhotos;
    AlbumAdapter albumCoverAdapter;
    SpacesItemDecoration decoration;

    public static AlbumFragment newInstance( ) {
        AlbumFragment newAlbums = new AlbumFragment();
        return newAlbums;
    }

    public interface AlbumListener {
        void onAlbumClick(List<PicturePost> albumpics);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photoView = inflater.inflate(R.layout.fragment_albumsview, parent, false);
        rvPhotos = (RecyclerView) photoView.findViewById(R.id.rvAlbums);

        return photoView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ItemClickSupport.addTo(rvPhotos).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String albumName = myAlbums.get(position).getAlbumName();

                        ParseQuery<PicturePost> postquery = ParseQuery.getQuery("Posts");
                        // Define our query conditions
                        //postquery.whereEqualTo("user", ParseUser.getCurrentUser());
                        postquery.whereEqualTo("albums", albumName);
                        postquery.findInBackground(new FindCallback<PicturePost>() {
                            @Override
                            public void done(List<PicturePost> objects, ParseException e) {
                                if (e == null) {
                                    Log.d("DEBUG", objects.size() + "");
                                    albumPosts = objects;
                                    if (albumPosts.size() > 0) {
                                        AlbumListener al = (AlbumListener) getActivity();
                                        al.onAlbumClick(albumPosts);
                                    }
                                } else {
                                    Log.d("DEBUG", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
        );

        ParseQuery<ImageAlbum> query = ParseQuery.getQuery("Albums");
        // Define our query conditions
        query.whereEqualTo("owner", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ImageAlbum>() {
            @Override
            public void done(List<ImageAlbum> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", objects.size() + "");
                    myAlbums = objects;
                    if (myAlbums.size() > 0) {
                        // Create adapter passing in the pics
                        albumCoverAdapter = new AlbumAdapter(getActivity(), myAlbums);
                        // Attach the adapter to the recyclerview to populate items
                        rvPhotos.setAdapter(albumCoverAdapter);
                        // Set layout manager to position the items
                        rvPhotos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        //rvPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));

                        decoration = new SpacesItemDecoration(16);
                        rvPhotos.addItemDecoration(decoration);
                        //rvPhotos.setItemAnimator(new SlideInUpAnimator());
                    }
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });



    }


}
