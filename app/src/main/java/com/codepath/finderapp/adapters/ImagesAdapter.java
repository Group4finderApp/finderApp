package com.codepath.finderapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.models.PicturePost;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by phoen on 11/21/2016.
 */

public class ImagesAdapter extends
        RecyclerView.Adapter<ImagesAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView onePhotoPost;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            onePhotoPost = (ImageView) itemView.findViewById(R.id.photo_image);
        }
    }

    // Store a member variable for the contacts
    private List<PicturePost> mPictures;
    private Context mContext;

    // Pass in the contact array into the constructor
    public ImagesAdapter(Context context, List<PicturePost> images) {
        mPictures = images;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.photo_view, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ImagesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        PicturePost picPost = mPictures.get(position);

        // Set item views based on your views and data model
        ImageView ivPic = viewHolder.onePhotoPost;
        try {
            Picasso.with(getContext()).load(picPost.getImage().getFile())
                    .into(ivPic);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPictures.size();
    }
}