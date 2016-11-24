package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.ImagesAdapter;
import com.codepath.finderapp.models.PicturePost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by phoen on 11/21/2016.
 */

public class PhotosFragment extends Fragment {

    public static PhotosFragment newInstance( ) {
        PhotosFragment newPhotos = new PhotosFragment();
        return newPhotos;
    }

    List<PicturePost> myPhotos;
    RecyclerView rvPhotos;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photoView = inflater.inflate(R.layout.fragment_photos, parent, false);
        rvPhotos = (RecyclerView) photoView.findViewById(R.id.rvPhotos);
        return photoView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        // Define our query conditions
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", objects.size() + "");
                    try {
                        Log.d("DEBUG", objects.get(0).getImage().getFile() + "");
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    myPhotos = objects;
                    if (myPhotos.size() > 0) {
                        // Create adapter passing in the pics
                        ImagesAdapter adapter = new ImagesAdapter(getActivity(), myPhotos);
                        // Attach the adapter to the recyclerview to populate items
                        rvPhotos.setAdapter(adapter);
                        // Set layout manager to position the items
                        rvPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });

    }
}
