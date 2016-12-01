package com.codepath.finderapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.CameraFragment;
import com.codepath.finderapp.fragments.HomeMapFragment;

/**
 * Created by hison7463 on 11/14/16.
 */

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;
    private String[] titles = {"MAP", "CAM"};
    private int[] imageResId = {R.drawable.google_maps, R.drawable.camera};
    private Context context;

    public HomeViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new HomeMapFragment();
            }
            case 1: {
                return new CameraFragment();
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return titles[position];
        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString("  ");
        ImageSpan span = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
