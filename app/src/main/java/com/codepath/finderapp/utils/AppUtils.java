package com.codepath.finderapp.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;

/**
 * Created by hison7463 on 11/12/16.
 */

public class AppUtils {

    public static void disPlayFragment(FragmentManager fragmentManager, Fragment fragment, int layout) {
        fragmentManager.beginTransaction().replace(layout, fragment).commit();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return (int) px;
    }
}
