package com.codepath.finderapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.widgets.MapWrapperLayout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chmanish on 11/11/16.
 */
public class PinAdapter implements GoogleMap.InfoWindowAdapter{

    private static final String TAG = PinAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private Context context;
    private View infoWindowView;
    private MapWrapperLayout mapWrapperLayout;

    @BindView(R.id.pin_view_caption)
    TextView caption;
    @BindView(R.id.pin_view_thumb_down)
    ImageView thumbDown;

    public PinAdapter(LayoutInflater inflater, Context context, View infoWindowView) {
        this.inflater = inflater;
        this.context = context;
        this.infoWindowView = infoWindowView;
    }

    public void setMapWrapperLayout(MapWrapperLayout mapWrapperLayout) {
        this.mapWrapperLayout = mapWrapperLayout;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = this.infoWindowView;
        ButterKnife.bind(this, view);
        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindowView);
        return view;
    }

}
