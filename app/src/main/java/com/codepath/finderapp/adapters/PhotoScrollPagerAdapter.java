package com.codepath.finderapp.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
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
 * Created by phoen on 12/2/2016.
 */

public class PhotoScrollPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<PicturePost> mPicPosts;

    public PhotoScrollPagerAdapter(Context context, List<PicturePost> picPosts) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mPicPosts = picPosts;
    }

    // Returns the number of pages to be displayed in the ViewPager.
    @Override
    public int getCount() {
        return mPicPosts.size();
    }

    // Returns true if a particular object (page) is from a particular page
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // This method should create the page for the given position passed to it as an argument.
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate the layout for the page
        View itemView = mLayoutInflater.inflate(R.layout.photo_dialog_fragment_view, container, false);
        // Find and populate data into the page (i.e set the image)
        ImageView imageView = (ImageView) itemView.findViewById(R.id.selectPhoto);
        try {
            Picasso.with(mContext).load(mPicPosts.get(position).getImage().getFile()).into(imageView);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Add the page to the container
        container.addView(itemView);
        // Return the page
        return itemView;
    }

    // Removes the page from the container for the given position.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
