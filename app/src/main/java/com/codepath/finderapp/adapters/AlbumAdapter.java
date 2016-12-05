package com.codepath.finderapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.models.ImageAlbum;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by phoen on 12/3/2016.
 */

public class AlbumAdapter extends
        RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView albumCoverImage;
        public TextView albumName;
        public TextView pictureCount;

        public ViewHolder(View itemView) {
            super(itemView);
            albumCoverImage = (ImageView) itemView.findViewById(R.id.album_cover);
            albumName = (TextView) itemView.findViewById(R.id.tvAlbumName);
            pictureCount = (TextView) itemView.findViewById(R.id.tvPicCount);
        }
    }

    List<ImageAlbum> mAlbums;
    Context mContext;

    public AlbumAdapter(Context context, List<ImageAlbum> albums) {
        mAlbums = albums;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View albumView = inflater.inflate(R.layout.albumview_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(albumView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        ImageAlbum album = mAlbums.get(position);

        // Set item views based on your views and data model
        try {
            Picasso.with(getContext()).load(album.getCoverPic().getFile())
                    .fit()
                    .into(viewHolder.albumCoverImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.albumName.setText(album.getAlbumName());
        viewHolder.pictureCount.setText(Integer.toString(album.getPictureCount()));
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }
}
