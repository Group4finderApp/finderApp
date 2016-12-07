package com.codepath.finderapp.utils;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.annotation.Dimension;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Make sure you have included the Android Maps Utility library
 * See: https://developers.google.com/maps/documentation/android-api/utility/
 *
 * Gradle config:
 *
 * dependencies {
 *   'com.google.maps.android:android-maps-utils:0.4+'
 * }
 */

public class MapUtils {

    public static BitmapDescriptor createBubble(Context context, int style, String title) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setStyle(style);
        Bitmap bitmap = iconGenerator.makeIcon(title);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        return bitmapDescriptor;
    }

    public static ImageView createImageViewForMarker(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(AppUtils.dpToPixels(50, context.getResources()), AppUtils.dpToPixels(50, context.getResources())));
        imageView.setPadding(AppUtils.dpToPixels(4, context.getResources()), AppUtils.dpToPixels(4, context.getResources()), AppUtils.dpToPixels(4, context.getResources()), AppUtils.dpToPixels(4, context.getResources()));
        return imageView;
    }

    public static Marker addMarker(GoogleMap map, LatLng point, String title,
                                   String snippet,
                                   BitmapDescriptor marker) {
        // Creates and adds marker to the map
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .title(title)
                .snippet(snippet)
                .icon(marker);
        return map.addMarker(options);
    }

    public static Marker addMarker(GoogleMap map, LatLng point, String title,
                                   String snippet,
                                   Bitmap icon) {
        // Creates and adds marker to the map
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.fromBitmap(icon));
        return map.addMarker(options);
    }

    public static void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 800;

        // Use the bounce interpolator
//        final android.view.animation.Interpolator interpolator =
//                new BounceInterpolator();

        final Interpolator interpolator = new AccelerateInterpolator();


        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 35 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
//                    marker.showInfoWindow();
                }
            }
        });
    }
}