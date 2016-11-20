package com.codepath.finderapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.finderapp.fragments.CameraFragment;
import com.codepath.finderapp.fragments.HomeMapFragment;

/**
 * Created by hison7463 on 11/14/16.
 */

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private static int NUM_ITEMS = 2;
    private String[] titles = {"MAP", "CAM"};

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
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
        return titles[position];
    }
}
