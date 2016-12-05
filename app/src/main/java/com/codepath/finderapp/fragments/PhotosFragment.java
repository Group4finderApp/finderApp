package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.codepath.finderapp.models.ImageAlbum;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.models.PicturePostCollection;
import com.codepath.finderapp.utils.AppUtils;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by phoen on 11/21/2016.
 */

public class PhotosFragment extends Fragment
    implements NewAlbumDialogFragment.CreateAlbumDialogListener {
    public static String postListKey = "postList";

    public static PhotosFragment newInstance(PicturePostCollection postCollection) {
        PhotosFragment newPhotos = new PhotosFragment();
        Bundle args = new Bundle();
        args.putParcelable(postListKey, Parcels.wrap(postCollection));
        newPhotos.setArguments(args);
        return newPhotos;
    }

    List<PicturePost> myPhotos = null;
    RecyclerView rvPhotos;
    ImagesAdapter imageAdapter;
    public MultiSelector mMultiSelector;
    public ModalMultiSelectorCallback mActionModeCallback;
    int i;


    public interface ImagesListener {
        void onImageClick(PicturePostCollection picPosts, int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photoView = inflater.inflate(R.layout.fragment_photos, parent, false);
        rvPhotos = (RecyclerView) photoView.findViewById(R.id.rvPhotos);
        setHasOptionsMenu(true);
        mMultiSelector = new MultiSelector();
        //get picture post data
        if (Parcels.unwrap(getArguments().getParcelable(postListKey)) != null) {
            PicturePostCollection c =
                    Parcels.unwrap(getArguments().getParcelable(postListKey));
            myPhotos = c.getPostList();
        }

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
                                        Log.d("DEBUG", "Post deleted");
                                    } else {
                                        Toast.makeText(getActivity(),"Oops, one or more pictures not deleted",
                                                Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                            myPhotos.remove(i);
                            imageAdapter.notifyItemRemoved(i);
                        }
                    }
                    mMultiSelector.clearSelections();
                    return true;
                } else if (menuItem.getItemId() == R.id.addAlbum) {
                    actionMode.finish();

                    PicturePost coverPost = null;

                    for (i = myPhotos.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            coverPost = myPhotos.get(i);
                            break;
                        }
                    }

                    if (coverPost != null) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        NewAlbumDialogFragment frag = NewAlbumDialogFragment
                                .newInstance(coverPost);
                        frag.setTargetFragment(PhotosFragment.this, 300);
                        frag.show(fm, "singlephotofragment");
                    }
                    return true;
                }
                return false;
            }
        };

        if (myPhotos == null) {
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
                            setupAdapter();
                        }
                    } else {
                        Log.d("DEBUG", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            setupAdapter();
        }

    }

    public void setupAdapter( ) {
        // Create adapter passing in the pics
        imageAdapter = new ImagesAdapter(getActivity(), myPhotos, mMultiSelector, mActionModeCallback);
        // Attach the adapter to the recyclerview to populate items
        rvPhotos.setAdapter(imageAdapter);
        // Set layout manager to position the items
        rvPhotos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //add click listener to adapter
        imageAdapter.setOnItemClickListener(new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int type) {
                if (type == 0) {
                    //Toast.makeText(getActivity(), "Image clicked", Toast.LENGTH_SHORT).show();
                    Log.d("DEBUG", "Image clicked");
                    sendRequestToActivity(new PicturePostCollection(myPhotos), position);
                }
            }

        });
        //add long click listener to adapter
        imageAdapter.setOnItemLongClickListener(new ImagesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //Toast.makeText(getActivity(), "Image long clicked", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "Image long clicked");
            }
        });
    }

    public void sendRequestToActivity (PicturePostCollection picPosts, int position) {
        //Pass click request to activity
        ImagesListener listener = (ImagesListener) getActivity();
        listener.onImageClick(picPosts, position);
    }

    @Override
    public void onFinishDialog(final String albumName) {
        if (!TextUtils.isEmpty(albumName)) {
            ImageAlbum album = new ImageAlbum();
            album.setOwner(ParseUser.getCurrentUser());
            album.setACL(AppUtils.getObjectReadWritePermissions());
            boolean coverSet = false;
            int numPics = 0;
            PicturePost pic;
            for (i = myPhotos.size(); i >= 0; i--) {
                if (mMultiSelector.isSelected(i, 0)) {
                    if (!coverSet) {
                        album.setCoverPic(myPhotos.get(i).getImage());
                        coverSet = true;
                    }
                    pic = myPhotos.get(i);
                    pic.setACL(AppUtils.getObjectReadWritePermissions());
                    pic.addtoAlbum(albumName);
                    pic.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("DEBUG", e.toString());

                            } else {
                                Log.d("DEBUG", "pic successfully added to album");
                            }
                        }
                    });
                    numPics++;
                }
            }
            if (numPics > 0) {
                album.setAlbumName(albumName);
                album.setPictureCount(numPics);
                album.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("DEBUG", "album saved");
                        } else {
                            Toast.makeText(
                                    getActivity(),
                                    "Error saving album: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                });
            }
            mMultiSelector.clearSelections();
        }
    }
}
