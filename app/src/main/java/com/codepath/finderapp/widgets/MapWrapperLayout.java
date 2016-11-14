package com.codepath.finderapp.widgets;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by hison7463 on 11/13/16.
 */

public class MapWrapperLayout extends RelativeLayout {

    private GoogleMap map;
    private int bottomOffsetPixels;
    private Marker marker;
    private View infoWindow;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //must to call before route touch event
    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;
    }

    //call in InfoWindowAdapter.getInfoContents
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean res = false;
        if(marker != null && marker.isInfoWindowShown() && map != null && infoWindow != null) {
            Point point = map.getProjection().toScreenLocation(marker.getPosition());

            MotionEvent motionEvent = MotionEvent.obtain(ev);
            motionEvent.offsetLocation(-point.x + (infoWindow.getWidth() / 2), -point.y + infoWindow.getHeight() + bottomOffsetPixels);

            res = infoWindow.dispatchTouchEvent(motionEvent);
        }
        return res || super.dispatchTouchEvent(ev);
    }

}
