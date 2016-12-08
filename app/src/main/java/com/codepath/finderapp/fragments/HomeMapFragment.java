package com.codepath.finderapp.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

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
    private static final double ZOOM_LEVEL_THREHOLD = 0.2;
    private static final double maxRadius = 1;
    private static final double offsetOfPic = 0.46;
    private static final double offsetOfLat = 0.2;

    private SupportMapFragment mapFragment;
    public static GoogleMap map;
    private GoogleApiClient googleApiClient;
    public static Location lastLocation;
    private LatLngBounds bounds;
    private double zoomLevel = 15;
    private LocationRequest locationRequest;
    private Marker currentMarker;
    private Set<Marker> clicked = new HashSet<>();
    private static Map<PicturePost, Marker> postToMarkers = new HashMap<>();
    private static AlertDialog.Builder builder;
    private IconGenerator iconGenerator = new IconGenerator(finderAppApplication.getApplication());
    private boolean filterExpand = false;
    private float filterContainerWidth;
    private boolean filterFood = false;
    private boolean filterSight = false;

    @BindView(R.id.home_map_wrapper)
    MapWrapperLayout wrapperLayout;
    @BindView(R.id.home_current_loc)
    FloatingActionButton currentLoc;
    @BindView(R.id.home_filter)
    FloatingActionButton filterBtn;
    @BindView(R.id.home_filter_chooser_container)
    LinearLayout filterChooser;
    @BindView(R.id.home_map_food_filter_btn)
    ImageView foodFilter;
    @BindView(R.id.home_map_view_filter_btn)
    ImageView sightFilter;

    private ViewGroup infoWindow;
    private ImageView thumbDown;
    private ImageView userProfile;
    private ImageView image;
    private TextView caption;
    private static ImageView imageForMarker;
    private static Set<PicturePost> pinList = new HashSet<>();


    public static HomeMapFragment newInstance(String locationLat, String locationLong) {
        HomeMapFragment myFragment = new HomeMapFragment();

        Bundle args = new Bundle();
        args.putString("locationLat", locationLat);

        args.putString("locationLong", locationLong);
        myFragment.setArguments(args);
        return myFragment;
    }

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

        filterContainerWidth = filterChooser.getWidth();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "connect");
        googleApiClient.connect();
        builder = new AlertDialog.Builder(getActivity());
        imageForMarker = MapUtils.createImageViewForMarker(getActivity());
        iconGenerator.setContentView(imageForMarker);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "disconnect");
        if (googleApiClient.isConnected()) {
            stopPeriodUpdates();
        }
        pinList.clear();
        clicked.clear();
        postToMarkers.clear();
        map.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        checkPermission();
        map.setMyLocationEnabled(true);
        //hide default my location button
        map.getUiSettings().setMyLocationButtonEnabled(false);

        wrapperLayout.init(googleMap, AppUtils.getPixelsFromDp(getActivity(), 39 + 20));
        PinAdapter adapter = new PinAdapter(getActivity().getLayoutInflater(), getActivity(), infoWindow);
        adapter.setMapWrapperLayout(wrapperLayout);
        map.setInfoWindowAdapter(adapter);

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d(TAG, "marker click");
                currentMarker = marker;
                PicturePost pin = findMatchedPin(marker.getPosition().latitude, marker.getPosition().longitude);
                try {
                    if (pin != null && pin.getUser().fetchIfNeeded().getString("profilePictureUrl") != null) {
//                        userProfile.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(pin.getUser().getString("profilePictureUrl")).transform(new CropCircleTransformation()).into(userProfile);
                    } else {
//                        userProfile.setVisibility(View.INVISIBLE);
                        userProfile.setImageResource(R.drawable.ic_profile_image);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    userProfile.setVisibility(View.INVISIBLE);
                }
                try {
                    if (clicked.contains(marker)) {
                        Log.d(TAG, image + " image width");
                        marker.showInfoWindow();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(image.getWidth(), getHeightOfImage(pin.getImage().getData(), image));
                        image.setLayoutParams(params);
                        marker.showInfoWindow();
                        Log.d(TAG, params.width + " width");
                        Log.d(TAG, params.height + " height");
                        Picasso.with(getActivity()).load(pin.getImage().getFile()).fit().centerCrop().into(image);
                    } else {
                        //TODO
                        clicked.add(marker);
                        marker.showInfoWindow();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(image.getWidth(), getHeightOfImage(pin.getImage().getData(), image));
                        image.setLayoutParams(params);
                        marker.showInfoWindow();
                        Log.d(TAG, params.width + " width");
                        Log.d(TAG, params.height + " height");
                        Picasso.with(getActivity()).load(pin.getImage().getFile()).fit().centerCrop().into(image, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "image load successfully");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentMarker.showInfoWindow();
                                    }
                                }, 200);
                            }

                            @Override
                            public void onError() {
                                Log.d(TAG, "image load fail");
                            }
                        });
//                        Picasso.with(getActivity()).load(pin.getImage().getFile()).into(new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                image.setImageBitmap(bitmap);
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        currentMarker.showInfoWindow();
//                                    }
//                                }, 200);
//                            }
//
//                            @Override
//                            public void onBitmapFailed(Drawable errorDrawable) {
//
//                            }
//
//                            @Override
//                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                            }
//                        });
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (pin.getThumbsUp() == null) {
                    Log.d(TAG, "thumbs null");
                    thumbDown.setVisibility(View.INVISIBLE);
                } else if (pin.getThumbsUp().equals("true")) {
                    Log.d(TAG, "thumb up");
                    thumbDown.setVisibility(View.VISIBLE);
                    thumbDown.setImageResource(R.drawable.thumb_up);
                } else {
                    Log.d(TAG, "thumb down");
                    Log.d(TAG, pin.getThumbsUp());
                    thumbDown.setVisibility(View.VISIBLE);
                    thumbDown.setImageResource(R.drawable.thumb_up_outline_white);
                }
                caption.setText(pin.getText());
                marker.showInfoWindow();
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                LatLng moveToPos = getLocationWithOffset(marker, bounds);
                updateCameraLocationWithOffset(moveToPos);
                return true;
            }
        });

        map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

            @Override
            public void onCameraIdle() {
                Log.d(TAG, "camera update");
                Log.d(TAG, "camera zoom: " + map.getCameraPosition().zoom);
                Log.d(TAG, "zoom level: " + zoomLevel);

                bounds = map.getProjection().getVisibleRegion().latLngBounds;
                fetchDataAfterZoom(bounds, -1);

                if (Math.abs(map.getCameraPosition().zoom - zoomLevel) <= ZOOM_LEVEL_THREHOLD) {
                    return;
                }
                zoomLevel = map.getCameraPosition().zoom;

                if (currentMarker != null && currentMarker.isInfoWindowShown()) {
                    currentMarker.hideInfoWindow();
                }
            }
        });
        map.setOnInfoWindowClickListener(this);
    }

    private int getHeightOfImage(byte[] data, View image) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        int height = (int) (((double) bitmap.getHeight() / bitmap.getWidth()) * image.getWidth());
        Log.d(TAG, "bit map height : " + bitmap.getHeight());
        Log.d(TAG, "bit map width : " + bitmap.getWidth());
        return height;
    }

    private LatLng getLocationWithOffset(Marker marker, LatLngBounds bounds) {
        double latitude = marker.getPosition().latitude + (bounds.northeast.latitude - bounds.southwest.latitude) * offsetOfPic;
        double longitude = marker.getPosition().longitude;
        return new LatLng(latitude, longitude);
    }

    private void fetchDataAfterZoom(LatLngBounds bounds, int k) {
        double southLatitude = bounds.southwest.latitude - (bounds.northeast.latitude - bounds.southwest.latitude) * offsetOfLat > -90.0 ? bounds.southwest.latitude - (bounds.northeast.latitude - bounds.southwest.latitude) * offsetOfLat : -90.0;
        ParseGeoPoint northEast = new ParseGeoPoint(bounds.northeast.latitude, bounds.northeast.longitude);
        ParseGeoPoint southWest = new ParseGeoPoint(southLatitude, bounds.southwest.longitude);
        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        query.whereWithinGeoBox("location", southWest, northEast);
        if(filterFood) {
            query.whereEqualTo("foodFilter", "true");
        }
        if(filterSight) {
            query.whereEqualTo("foodFilter", "false");
        }
        if (k != -1) {
            query.setLimit(k);
        }
        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(getActivity(), "server error", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (final PicturePost post : objects) {
                    if (pinList.contains(post)) {
                        continue;
                    }
                    addPostToListIfNotAvailable(post);
                }
                removeInvisibleMarker(pinList, objects);
            }
        });
    }

    private void removeInvisibleMarker(Set<PicturePost> pinList, List<PicturePost> objects) {
        Set<PicturePost> temp = new HashSet<>(objects);
        Iterator<PicturePost> iterator = pinList.iterator();
        while (iterator.hasNext()) {
            PicturePost post = iterator.next();
            if (!temp.contains(post)) {
                iterator.remove();
                if (postToMarkers.containsKey(post)) {
                    postToMarkers.get(post).remove();
                    postToMarkers.remove(post);
                }
            }
        }
    }

    private void addPostToListIfNotAvailable(final PicturePost post) {
        pinList.add(post);
        post.getUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Log.d(TAG, "fetch user successfully");
                if(e != null) {
                    Toast.makeText(getActivity(), "server error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, object.getString("profilePictureUrl"));
                if(object.getString("profilePictureUrl") != null) {
//                    Picasso.with(getActivity()).load(object.getString("profilePictureUrl")).fit().centerCrop().into(imageForMarker, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            Log.d(TAG, "fetch image");
//                            Bitmap icon = iconGenerator.makeIcon();
//                            Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//                            MapUtils.dropPinEffect(marker);
//                            postToMarkers.put(post, marker);
//                        }
//
//                        @Override
//                        public void onError() {
//                            Log.d(TAG, "load error");
//                        }
//                    });
                    Picasso.with(getActivity()).load(object.getString("profilePictureUrl")).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "success");
                            imageForMarker.setImageBitmap(bitmap);
                            Bitmap icon = iconGenerator.makeIcon();
                            Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
                            MapUtils.dropPinEffect(marker);
                            postToMarkers.put(post, marker);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            Log.d(TAG, "fail");
                            imageForMarker.setImageResource(R.drawable.ic_profile_image);
                            Bitmap icon = iconGenerator.makeIcon();
                            Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
                            MapUtils.dropPinEffect(marker);
                            postToMarkers.put(post, marker);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d(TAG, "prepare");
//                            imageForMarker.setImageResource(R.drawable.ic_profile_image);
//                            Bitmap icon = iconGenerator.makeIcon();
//                            Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//                            MapUtils.dropPinEffect(marker);
//                            postToMarkers.put(post, marker);
                        }
                    });
                }
            }
        });
//        try {
//            if (post.getUser().fetchIfNeeded().getString("profilePictureUrl") != null) {
//                Picasso.with(getActivity()).load(post.getUser().getString("profilePictureUrl")).fit().centerCrop().into(imageForMarker, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        Bitmap icon = iconGenerator.makeIcon();
//                        Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//                        MapUtils.dropPinEffect(marker);
//                        postToMarkers.put(post, marker);
//                    }
//
//                    @Override
//                    public void onError() {
//                        Log.d(TAG, "load error");
//                    }
//                });
//            } else {
//                imageForMarker.setImageResource(R.drawable.ic_profile_image);
//                Bitmap icon = iconGenerator.makeIcon();
//                Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//                MapUtils.dropPinEffect(marker);
//                postToMarkers.put(post, marker);
//            }
//        } catch (ParseException e1) {
//            e1.printStackTrace();
//            imageForMarker.setImageResource(R.drawable.ic_profile_image);
//            Bitmap icon = iconGenerator.makeIcon();
//            Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//            MapUtils.dropPinEffect(marker);
//            postToMarkers.put(post, marker);
//        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                clicked.clear();
                pinList.addAll(objects);
                Log.d(TAG, objects.size() + "");
                if (map != null) {
                    //map.clear();
                    for (PicturePost post : pinList) {
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
        String lat = getArguments().getString("locationLat");
        String longi = getArguments().getString("locationLong");
        if (lat != null && longi != null){
            double locationLat = Double.parseDouble(lat);
            double locationLong = Double.parseDouble(longi);
            Location myLocation = new Location("aaa");
            myLocation.setLatitude(locationLat);
            myLocation.setLongitude(locationLong);
            moveCamera(myLocation);

        }
        else {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastLocation != null) {
                moveCamera(lastLocation);
            } else {
                Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
            }

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

    private void updateCameraLocation(LatLng latlng) {
//        map.clear();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, 15);
        map.animateCamera(update, this);
//        getKNearestPins(latlng.latitude, latlng.longitude, 5);
    }

    private void updateCameraLocation(LatLng latlng, int zoomLevel) {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel);
        map.animateCamera(update, this);
    }

    private void updateCameraLocationWithOffset(LatLng latlng) {
        CameraUpdate update = CameraUpdateFactory.newLatLng(latlng);
        map.animateCamera(update, this);
    }

    private void moveCamera(Location location) {
        if (location == null) {
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
                updateCameraLocation(latlng, 10);
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
//        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
        String url = "http://maps.google.com/maps?saddr=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + "&daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
//        Uri gmmIntentUri = Uri.parse(url);
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);
        LeavingFragment leavingDialog = new LeavingFragment();
        leavingDialog.setUrl(url);
        leavingDialog.show(getFragmentManager(), "leaving dialog");
    }

    private PicturePost findMatchedPin(double latitude, double longitude) {
        for (PicturePost post : pinList) {
            if (post.getLocation().getLatitude() == latitude && post.getLocation().getLongitude() == longitude) {
                return post;
            }
        }
        return null;
    }

    public Set<PicturePost> getPinList() {
        return pinList;
    }

    public void addMarker(PicturePost post) {
        if (lastLocation == null) {
            return;
        }
        pinList.add(post);
        moveCamera(lastLocation);

        addPostToListIfNotAvailable(post);

//        BitmapDescriptor icon = MapUtils.createBubble(finderAppApplication.getApplication().getApplicationContext(), IconGenerator.STYLE_BLUE, "title");
//        Marker marker = MapUtils.addMarker(map, new LatLng(post.getLocation().getLatitude(), post.getLocation().getLongitude()), "", "", icon);
//        MapUtils.dropPinEffect(marker);
//        postToMarkers.put(post, marker);
//        moveCamera(lastLocation);

        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        ParseGeoPoint currentLocation = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
        query.whereWithinMiles("location", currentLocation, maxRadius);
        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                Log.d(TAG, objects.size() + " nearby");
                if (objects.size() == 0) {
                    builder.setTitle("Congratulation")
                            .setMessage("You are the first person post picture within this area")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            }
        });
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

    @OnClick(R.id.home_current_loc)
    public void onCurrentLocClick() {
        moveCamera(lastLocation);
    }

    @OnClick(R.id.home_filter)
    public void onFilterClick() {
        Log.d(TAG, filterChooser.getPivotX() + " pivotX");
        Log.d(TAG, filterChooser.getWidth() + " width");
        filterChooser.setPivotX(filterChooser.getWidth());
        if (filterExpand) {
            ViewCompat.animate(filterChooser)
                    .alpha(0)
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {

                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            filterChooser.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    });
        } else {
            ViewCompat.animate(filterChooser)
                    .alpha(1)
                    .setDuration(300)
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {

                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            filterChooser.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    });

        }
        filterExpand = !filterExpand;
    }

    @OnClick(R.id.home_map_food_filter_btn)
    public void onFoodFilterClick() {
        if(!filterFood) {
            foodFilter.setColorFilter(Color.parseColor("#3FBE37"));
            sightFilter.setColorFilter(Color.parseColor("#777777"));
            filterSight = false;
            fetchDataForFilter(true, false);
        }
        else {
            foodFilter.setColorFilter(Color.parseColor("#777777"));
            fetchDataForFilter(false, false);
        }
        filterFood = !filterFood;
    }

    @OnClick(R.id.home_map_view_filter_btn)
    public void onSightFilterClick() {
        if(!filterSight) {
            sightFilter.setColorFilter(Color.parseColor("#3FBE37"));
            foodFilter.setColorFilter(Color.parseColor("#777777"));
            filterFood = false;
            fetchDataForFilter(false, true);
        }
        else {
            sightFilter.setColorFilter(Color.parseColor("#777777"));
            fetchDataForFilter(false, false);
        }
        filterSight = !filterSight;
    }

    private void fetchDataForFilter(boolean filterFood, boolean filterSight) {
        ParseQuery<PicturePost> query = ParseQuery.getQuery("Posts");
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        Log.d(TAG, bounds.southwest.latitude + ", " + bounds.southwest.longitude);
        ParseGeoPoint northEast = new ParseGeoPoint(bounds.northeast.latitude, bounds.northeast.longitude);
        ParseGeoPoint southWest = new ParseGeoPoint(bounds.southwest.latitude, bounds.southwest.longitude);
        query.whereWithinGeoBox("location", southWest, northEast);
        if(filterFood) {
            query.whereEqualTo("foodFilter", "true");
        }
        if(filterSight) {
            query.whereEqualTo("foodFilter", "false");
        }

        query.findInBackground(new FindCallback<PicturePost>() {
            @Override
            public void done(List<PicturePost> objects, ParseException e) {
                if(e != null) {
                    Toast.makeText(getActivity(), "server error", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(PicturePost post : objects) {
                    if(pinList.contains(post)) {
                        continue;
                    }
                    addPostToListIfNotAvailable(post);
                }
                removeInvisibleMarker(pinList, objects);
            }
        });
    }
}
