package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.ImagesAdapter;
import com.codepath.finderapp.models.PicturePost;
import com.parse.DeleteCallback;
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
    ImagesAdapter imageAdapter;
    public MultiSelector mMultiSelector;
    public ModalMultiSelectorCallback mActionModeCallback;
    int i;


    public interface ImagesListener {
        void onImageClick(PicturePost picPost, int type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photoView = inflater.inflate(R.layout.fragment_photos, parent, false);
        rvPhotos = (RecyclerView) photoView.findViewById(R.id.rvPhotos);
        setHasOptionsMenu(true);
        mMultiSelector = new MultiSelector();
        return photoView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mActionModeCallback = new ModalMultiSelectorCallback(mMultiSelector) {

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                super.onCreateActionMode(actionMode, menu);
                //actionMode.getMenuInflater().inflate(R.menu.menu_items_select, menu);
                getActivity().getMenuInflater().inflate(R.menu.menu_items_select, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.deletePics) {
                    actionMode.finish();
                    for (i = myPhotos.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            // remove item from list
                            PicturePost pic = myPhotos.get(i);
                            Log.d("DEBUG", pic.toString());
                            pic.deleteInBackground(new DeleteCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        myPhotos.remove(i);
                                        imageAdapter.notifyItemRemoved(i);
                                        Log.d("DEBUG", "Item " + i + " deleted");
                                    } else {
                                        Toast.makeText(getActivity(),"  not deleted",
                                                Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    mMultiSelector.clearSelections();
                    return true;
                } else if (menuItem.getItemId() == R.id.addAlbum) {
                    actionMode.finish();
                    for (i = myPhotos.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            PicturePost pic = myPhotos.get(i);
                            Log.d("DEBUG", pic.toString());
                        }
                    }
                    mMultiSelector.clearSelections();
                    return true;
                }
                return false;
            }
        };

        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        // Define our query conditions
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", objects.size() + "");
                    myPhotos = objects;
                    if (myPhotos.size() > 0) {
                        // Create adapter passing in the pics
                        imageAdapter = new ImagesAdapter(getActivity(), myPhotos, mMultiSelector, mActionModeCallback);
                        // Attach the adapter to the recyclerview to populate items
                        rvPhotos.setAdapter(imageAdapter);
                        // Set layout manager to position the items
                        rvPhotos.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                        //add click listener to adapter
                        imageAdapter.setOnItemClickListener(new ImagesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, int type) {
                                if(type == 0) {
                                    Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show();
                                    sendRequestToActivity (myPhotos.get(position), type);
                                }
                            }

                        });
                        //add long click listener to adapter
                        imageAdapter.setOnItemLongClickListener(new ImagesAdapter.OnItemLongClickListener() {
                            @Override
                            public void onItemLongClick(View itemView, int position) {
                                Toast.makeText(getActivity(), "Image long clicked", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }
        });

    }

    public void sendRequestToActivity (PicturePost picPost, int type) {
        //Pass click request to activity
        ImagesListener listener = (ImagesListener) getActivity();
        listener.onImageClick(picPost, type);
    }
}
