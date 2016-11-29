package com.codepath.finderapp.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
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

    // Define listener member variable
    private OnItemClickListener listener;
    private OnItemLongClickListener longlistener;
    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, int id);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemLongClickListener(OnItemLongClickListener longlistener) {
        this.longlistener = longlistener;
    }

    public class ViewHolder extends SwappingHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private ImageView onePhotoPost;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView, mMultiSelector); // (2)
            itemView.setLongClickable(true);

            onePhotoPost = (ImageView) itemView.findViewById(R.id.photo_image);

            // Setup the click listener for detail view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (!mMultiSelector.tapSelection(ViewHolder.this)) {
                                listener.onItemClick(itemView, position, 0);
                            }
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (longlistener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (!mMultiSelector.isSelectable()) {
                                ((AppCompatActivity) getContext()).startSupportActionMode(mActionModeCallback);
                                mMultiSelector.setSelectable(true);
                                mMultiSelector.setSelected(ViewHolder.this, true);
                            }
                            longlistener.onItemLongClick(itemView, position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    // Store a member variable for the contacts
    private List<PicturePost> mPictures;
    private Context mContext;
    protected MultiSelector mMultiSelector;
    protected ModalMultiSelectorCallback mActionModeCallback;

    // Pass in the contact array into the constructor
    public ImagesAdapter(Context context, List<PicturePost> images, MultiSelector selector, ModalMultiSelectorCallback actionMode) {
        mPictures = images;
        mContext = context;
        mMultiSelector = selector;
        mActionModeCallback = actionMode;
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