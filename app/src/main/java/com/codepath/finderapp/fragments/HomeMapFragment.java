package com.codepath.finderapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.PinAdapter;
import com.codepath.finderapp.common.Constants;
import com.codepath.finderapp.finderAppApplication;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.utils.AppUtils;
import com.codepath.finderapp.utils.MapUtils;
import com.codepath.finderapp.widgets.MapWrapperLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hison7463 on 11/12/16.
 */

public class HomeMapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleMap.CancelableCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationListener {

    private static final String TAG = HomeMapFragment.class.getSimpleName();

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INTERVAL = 5;
    private static final int FAST_CEILING_IN_SECONDS = 1;
    private static final int UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL * MILLISECONDS_PER_SECOND;
    private static final int FAST_CEILING_IN_MILLISECONDS = FAST_CEILING_IN_SECONDS * MILLISECONDS_PER_SECOND;
    private static final double FETCH_DATA_THREHOLD = 0.1;
    private static final double ZOOM_LEVEL_THREHOLD = 0.6;

    private SupportMapFragment mapFragment;
    public static GoogleMap map;
    private GoogleApiClient googleApiClient;
    public static Location lastLocation;
    private LatLngBounds bounds;
    private double zoomLevel = 15;
    private LocationRequest locationRequest;

    @BindView(R.id.home_map_wrapper)
    MapWrapperLayout wrapperLayout;

    private ViewGroup infoWindow;
    private ImageView thumbDown;
    private ImageView userProfile;
    private ImageView image;
    private TextView caption;
    private static Set<PicturePost> pinList = new HashSet<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(FAST_CEILING_IN_MILLISECONDS);

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
        if(googleApiClient.isConnected()) {
            stopPeriodUpdates();
        }
        pinList.clear();
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

                if(pin.getThumbsUp() == null) {
                    Log.d(TAG, "thumbs null");
                    thumbDown.setVisibility(View.INVISIBLE);
                }
                else if(pin.getThumbsUp().equals("true")) {
                    Log.d(TAG, "thumb up");
                    thumbDown.setVisibility(View.VISIBLE);
                    thumbDown.setImageResource(R.drawable.thumb_up);
                }
                else {
                    Log.d(TAG, "thumb down");
                    Log.d(TAG, pin.getThumbsUp());
                    thumbDown.setVisibility(View.VISIBLE);
                    thumbDown.setImageResource(R.drawable.thumb_down);
                }
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

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                Log.d(TAG, "camera update");
                Log.d(TAG, "camera zoom: " + map.getCameraPosition().zoom);
                Log.d(TAG, "zoom level: " + zoomLevel);
                LatLngBounds lastBound = bounds;
                bounds = map.getProjection().getVisibleRegion().latLngBounds;
                //TODO
//                if(Math.abs(map.getCameraPosition().zoom - zoomLevel) <= ZOOM_LEVEL_THREHOLD) {
//                    return;
//                }
//                zoomLevel = map.getCameraPosition().zoom;
                fetchDataAfterZoom(bounds, 10);
            }
        });
//        map.setOnInfoWindowClickListener(this);
    }

    private void fetchDataAfterZoom(LatLngBounds bounds, int k) {
        ParseGeoPoint northEast = new ParseGeoPoint(bounds.northeast.latitude, bounds.northeast.longitude);
        ParseGeoPoint southWest = new ParseGeoPoint(bounds.southwest.latitude, bounds.southwest.longitude);
        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        query.whereWithinGeoBox("location", southWest, northEast);
        if(k != -1) {
            query.setLimit(k);
        }
        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                if(e != null) {
                    Toast.makeText(getActivity(), "server error", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean found = false;
                for(PicturePost post : objects) {
                    if(!pinList.contains(post)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    Log.d(TAG, "no change");
                    return;
                }
                map.clear();
                pinList.clear();
                pinList.addAll(objects);
                for(PicturePost post : pinList) {
                    BitmapDescriptor icon = MapUtils.createBubble(getActivity(), IconGenerator.STYLE_BLUE, "zoomPin");
                    Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
                    MapUtils.dropPinEffect(marker);
                }
            }
        });
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(perms, Constants.locationPermission);
            }
        }
    }

    private void getKNearestPins(double Latitude, double Longitude, int k) {
        ParseGeoPoint currentLoc = new ParseGeoPoint(Latitude, Longitude);
        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        query.whereNear("location", currentLoc);
        query.setLimit(k);
        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                pinList.clear();
                pinList.addAll(objects);
                Log.d(TAG, objects.size() + "");
                if(map != null) {
                    //map.clear();
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
        if (lastLocation != null) {
            moveCamera(lastLocation);
        } else {
            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
        }
        startPeriodUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void startPeriodUpdates() {
        checkPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    private void stopPeriodUpdates() {
        googleApiClient.disconnect();
    }

    private void updateCameraLocation (LatLng latlng) {
        map.clear();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        map.animateCamera(update, this);
//        getKNearestPins(latlng.latitude, latlng.longitude, 5);
    }

    private void moveCamera(Location location) {
        if(location == null) {
            return;
        }
        updateCameraLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void moveToSearchLocation(String searchString) throws IOException {
        List<Address> searchAddresses = null;
        if (searchString != null && !TextUtils.isEmpty(searchString)) {
            Geocoder geocoder = new Geocoder(this.getActivity());
            try {
                searchAddresses = geocoder.getFromLocationName(searchString, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (searchAddresses != null && searchAddresses.size() > 0) {
                LatLng latlng = new LatLng(searchAddresses.get(0).getLatitude(),
                        searchAddresses.get(0).getLongitude());
                updateCameraLocation(latlng);
                map.addMarker(new MarkerOptions().position(latlng).title("searchLoc"));
            } else {
                Toast.makeText(getActivity(), "Oops, something went wrong!!",
                        Toast.LENGTH_SHORT).show();
            }
        }
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
        for(PicturePost post : pinList) {
            if(post.getLocation().getLatitude() == latitude && post.getLocation().getLongitude() == longitude) {
                return post;
            }
        }
        return null;
    }

    public Set<PicturePost> getPinList() {
        return pinList;
    }

    public void addMarker(PicturePost post) {
        if(lastLocation == null) {
            return;
        }
        pinList.add(post);
        moveCamera(lastLocation);
        BitmapDescriptor icon = MapUtils.createBubble(finderAppApplication.getApplication().getApplicationContext(), IconGenerator.STYLE_BLUE, "title");
        Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
        MapUtils.dropPinEffect(marker);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location period update");
        lastLocation = location;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
}
