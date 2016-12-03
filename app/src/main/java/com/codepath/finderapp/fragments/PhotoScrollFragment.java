package com.codepath.finderapp.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.PhotoScrollPagerAdapter;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.models.PicturePostCollection;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by phoen on 12/2/2016.
 */

public class PhotoScrollFragment extends Fragment {
    public static String startIndexKey = "indexKey";
    public static String collectionKey = "collectionKey";

    List<PicturePost> myPhotos;
    ViewPager photosViewPager;
    LinearLayout thumbnailsContainer;
    int previousChosen = -1;

    public static PhotoScrollFragment newInstance(PicturePostCollection picturePostCollection,
                                                  int startIndex) {
        PhotoScrollFragment pSFrag = new PhotoScrollFragment();
        Bundle args = new Bundle();
        args.putParcelable(collectionKey, Parcels.wrap(picturePostCollection));
        args.putInt(startIndexKey, startIndex);
        pSFrag.setArguments(args);
        return pSFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View photosScrollView = inflater.inflate(R.layout.fragment_photo_scroll, parent, false);
        //get picture post data
        PicturePostCollection c = Parcels.unwrap(getArguments().getParcelable(collectionKey));
        myPhotos = c.getPostList();
        photosViewPager = (ViewPager) photosScrollView.findViewById(R.id.vpPicPager);
        thumbnailsContainer = (LinearLayout) photosScrollView.findViewById(R.id.thumbnailcontainer);

        //setup the images pager
        setupViewPager();

        //setup thumbnailscroll
        try {
            inflateThumbnails(inflater, parent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return photosScrollView;
    }

    private void setupViewPager () {
        photosViewPager.setAdapter(new PhotoScrollPagerAdapter(getActivity(), myPhotos));
        photosViewPager.setClipToPadding(false);
        photosViewPager.setPageMargin(4);
        photosViewPager.setCurrentItem(getArguments().getInt(startIndexKey));
        photosViewPager.addOnPageChangeListener(picListener);
    }

    private void inflateThumbnails(LayoutInflater inflater, ViewGroup parent) throws ParseException {

        for (int i = 0; i < myPhotos.size(); i++) {
            final View imageLayout = inflater.inflate(R.layout.image_thumbnail, parent, false);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_thumb);
            thumbnailsContainer.addView(imageLayout);
            imageView.setOnClickListener(onChagePageClickListener(i));
            loadBitmap(i, imageView);
        }
    }

    private void loadBitmap(final int index, final ImageView v) {
        try {
            Picasso.with(getContext())
                    .load(myPhotos.get(index).getImage().getFile())
                    .resize(60, 60)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            int initialSelection = getArguments().getInt(startIndexKey, -1);
                            Bitmap scaledbitmap = Bitmap.createScaledBitmap(bitmap,60,60,false);
                            //set to image view
                            v.setImageBitmap(scaledbitmap);
                            if (initialSelection == index) {
                                previousChosen = index;
                                setThumbnailSelection(v, true);
                            }
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private View.OnClickListener onChagePageClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int prvidx = previousChosen;
                //previousChosen = i;
                //if (prvidx != -1) {
                //   ImageView prevView = (ImageView) thumbnailsContainer.getChildAt(prvidx)
                //            .findViewById(R.id.img_thumb);
                //    setThumbnailSelection(prevView, false);
                //}
                //    setThumbnailSelection(v, true);
                photosViewPager.setCurrentItem(i);
            }

        };
    }

    private void setThumbnailSelection(View v, boolean selected) {
        if (selected) {
            //add padding to highlight
            v.setPadding(5, 5, 5, 5);
            v.setBackgroundColor(Color.MAGENTA);
        } else {
            v.setPadding(1, 1, 1, 1);
            v.setBackgroundColor(Color.WHITE);
        }
    }

    //Listener for showing / hiding fab
    ViewPager.OnPageChangeListener picListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            if (previousChosen != -1) {
                ImageView imageView = (ImageView) thumbnailsContainer.getChildAt(position)
                        .findViewById(R.id.img_thumb);
                setThumbnailSelection(imageView, true);
                imageView = (ImageView) thumbnailsContainer.getChildAt(previousChosen)
                        .findViewById(R.id.img_thumb);
                setThumbnailSelection(imageView, false);
                previousChosen = position;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
