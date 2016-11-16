package com.codepath.finderapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.PinAdapter;
import com.codepath.finderapp.common.Constants;
import com.codepath.finderapp.finderAppApplication;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.models.Pin;
import com.codepath.finderapp.utils.AppUtils;
import com.codepath.finderapp.utils.MapUtils;
import com.codepath.finderapp.widgets.MapWrapperLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by hison7463 on 11/12/16.
 */

public class HomeMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.CancelableCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = HomeMapFragment.class.getSimpleName();

    private SupportMapFragment mapFragment;
    public static GoogleMap map;
    private GoogleApiClient googleApiClient;
    public static Location lastLocation;

    @BindView(R.id.home_map_wrapper)
    MapWrapperLayout wrapperLayout;

    private ViewGroup infoWindow;
    private ImageView thumbDown;
    private ImageView userProfile;
    private ImageView image;
    private TextView caption;
    private static List<PicturePost> pinList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.infoWindow = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.pin_view, null);
        this.thumbDown = (ImageView) infoWindow.findViewById(R.id.pin_view_thumb_down);
        this.userProfile = (ImageView) infoWindow.findViewById(R.id.pin_view_profile);
        this.image = (ImageView) infoWindow.findViewById(R.id.pin_view_image);
        this.caption = (TextView) infoWindow.findViewById(R.id.pin_view_caption);
//        Picasso.with(getActivity()).load("https://s3-us-west-1.amazonaws.com/appfinder123/0x0ss-85.jpg").transform(new CropCircleTransformation()).into(userProfile);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_map_view, container, false);
        ButterKnife.bind(this, view);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "connect");
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "disconnect");
        googleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        checkPermission();
        map.setMyLocationEnabled(true);

        wrapperLayout.init(googleMap, AppUtils.getPixelsFromDp(getActivity(), 39 + 20));
        PinAdapter adapter = new PinAdapter(getActivity().getLayoutInflater(), getActivity(), infoWindow);
        adapter.setMapWrapperLayout(wrapperLayout);
        map.setInfoWindowAdapter(adapter);

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                PicturePost pin = findMatchedPin(marker.getPosition().latitude, marker.getPosition().longitude);
                try {
                    Picasso.with(getActivity()).load(pin.getImage().getFile()).fit().centerCrop().into(image);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//                Picasso.with(getActivity()).load(pin.getProfile()).transform(new CropCircleTransformation()).into(userProfile);
//                if(pin.isThumbUp()) {
//                    thumbDown.setImageResource(R.drawable.thumb_up);
//                }
//                else {
//                    thumbDown.setImageResource(R.drawable.thumb_down);
//                }
//                Log.d(TAG, pin.isThumbUp() + "");
                caption.setText(pin.getText());
                marker.showInfoWindow();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        marker.showInfoWindow();
                    }
                }, 800);
                return false;
            }
        });
//        map.setOnInfoWindowClickListener(this);
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(perms, Constants.locationPermission);
            }
        }
    }

    private void getKNearestPins(int k) {
        //TODO call back-end service
        //fake data
//        if(map != null) {
//            map.clear();
//            for(int i = 0; i < 5; i++) {
//                Pin pin = pinList.get(i);
//                BitmapDescriptor icon = MapUtils.createBubble(getActivity(), IconGenerator.STYLE_BLUE, "title");
//                Marker marker = MapUtils.addMarker(map, new LatLng(pin.getLatitude(), pin.getLongitude()), "test", "test", icon);
//                MapUtils.dropPinEffect(marker);
//            }
//        }
        ParseGeoPoint currentLoc = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        query.whereNear("location", currentLoc);
        query.setLimit(k);
        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                pinList.clear();
                pinList = objects;
                Log.d(TAG, objects.size() + "");
                if(map != null) {
                    map.clear();
                    for(PicturePost post : pinList) {
                        Log.d(TAG, "inside loop");
                        BitmapDescriptor icon = MapUtils.createBubble(getActivity(), IconGenerator.STYLE_BLUE, "pin");
                        Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
                        MapUtils.dropPinEffect(marker);
                    }
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "connected");
        checkPermission();
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        moveCamera(lastLocation);
        //TODO test
        getKNearestPins(5);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void moveCamera(Location location) {
        if(location == null) {
            return;
        }
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10);
        map.animateCamera(update, this);
    }

    @Override
    public void onFinish() {
        Log.d(TAG, "finish");
    }

    @Override
    public void onCancel() {
//        getKNearestPins(1);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(TAG, "my location");
        moveCamera(lastLocation);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d(TAG, "info window");
    }

    private PicturePost findMatchedPin(double latitude, double longitude) {
        for(int i = 0; i < pinList.size(); i++) {
            PicturePost post = pinList.get(i);
            if(post.getLocation().getLatitude() == latitude && post.getLocation().getLongitude() == longitude) {
                return post;
            }
        }
        return null;
    }

    public List<PicturePost> getPinList() {
        return pinList;
    }

    public void addMarker(PicturePost post) {
        if(lastLocation == null) {
            return;
        }
        pinList.add(post);
        BitmapDescriptor icon = MapUtils.createBubble(finderAppApplication.getApplication().getApplicationContext(), IconGenerator.STYLE_BLUE, "title");
        Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
        MapUtils.dropPinEffect(marker);
    }
}
