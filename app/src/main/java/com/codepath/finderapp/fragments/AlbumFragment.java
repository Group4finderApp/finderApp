package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.finderapp.R;

/**
 * Created by phoen on 11/23/2016.
 */

public class AlbumFragment extends Fragment {

    public static AlbumFragment newInstance( ) {
        AlbumFragment newAlbums = new AlbumFragment();
        return newAlbums;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photoView = inflater.inflate(R.layout.fragment_albums, parent, false);
        return photoView;
    }
}
