package com.codepath.finderapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.codepath.finderapp.MyCustomReceiver;
import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.HomeViewPagerAdapter;
import com.codepath.finderapp.common.Constants;
import com.codepath.finderapp.fragments.CameraFragment;
import com.codepath.finderapp.fragments.HomeMapFragment;
import com.codepath.finderapp.fragments.SaveCaptionFragment;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.utils.AppUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.geeksters.googleplaceautocomplete.lib.CustomAutoCompleteTextView;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements
        CameraFragment.CameraFragmentDialogListener,
        SaveCaptionFragment.SaveCaptionFragmentDialogListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks{

    @BindView(R.id.activity_main_view_pager)
    ViewPager viewPager;
    @BindView(R.id.activity_main_tab)
    TabLayout tab;
    @BindView(R.id.bottom_bar_map)
    ImageView bottomBarMap;
    @BindView(R.id.bottom_bar_camera)
    ImageView bottomBarCam;


    private HomeViewPagerAdapter adapter;
    private PicturePost post;
    private ParseFile photoFile;

    private Location lastLocation;
    private Location currentLocation;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private CardView toolbarContainer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView ivsearchButton;
    public CustomAutoCompleteTextView customAutoCompleteTextView;

    //Menu Layouts
    LinearLayout menuPhotos;
    LinearLayout menuAlbums;
    LinearLayout menuaddFriends;
    LinearLayout menuExplore;
    LinearLayout menuLogout;

    /*
    * Constants for location update parameters
    */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String locationLat = getIntent().getStringExtra("locationLat");
        String locationLong = getIntent().getStringExtra("locationLong");

        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        ButterKnife.bind(this);
        //container for toolbar
        toolbarContainer = (CardView) findViewById(R.id.map_toolbar_container);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //change background color of toolbar
        toolbar.setBackgroundColor(Color.WHITE);

        //autocomplete searchview
        customAutoCompleteTextView = (CustomAutoCompleteTextView)toolbar.findViewById(R.id.atv_places);
        customAutoCompleteTextView.setOnClickListener(editTextListener);
        //customAutoCompleteTextView.setOnFocusChangeListener(etfocusListener);
        //search icon
        ivsearchButton = (ImageView) toolbar.findViewById(R.id.search_place);
        ivsearchButton.setOnClickListener(searchplaceListener);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our navigation view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        post = new PicturePost();

        adapter = new HomeViewPagerAdapter(getSupportFragmentManager(), this, locationLat, locationLong);
        //viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        //show and hide toolbar
        viewPager.addOnPageChangeListener(toolbarListener);
        tab.setupWithViewPager(viewPager);

        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

    }
/**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //customAutoCompleteTextView.setVisibility(View.INVISIBLE);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //when user clicks the view
        searchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                //customAutoCompleteTextView.setVisibility(View.VISIBLE);
            }
        });

        //when user presses search
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            String userQuery = customAutoCompleteTextView.googlePlace.getCity();
            // perform query here

            // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
            // see https://code.google.com/p/android/issues/detail?id=24599
            if (!TextUtils.isEmpty(userQuery)) {
                HomeMapFragment mapFragment = (HomeMapFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + R.id.activity_main_view_pager + ":" +
                                viewPager.getCurrentItem());
                try {
                    mapFragment.moveToSearchLocation(userQuery);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                searchView.clearFocus();
            }
            //customAutoCompleteTextView.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    });
    return super.onCreateOptionsMenu(menu);
}

**/

//when user presses search
View.OnClickListener editTextListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        customAutoCompleteTextView.setCursorVisible(true);
    }
};

    /**
    View.OnFocusChangeListener etfocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    };
     **/

View.OnClickListener searchplaceListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String userQuery = "";
        if (customAutoCompleteTextView.googlePlace.getCity() != null) {
            userQuery = customAutoCompleteTextView.googlePlace.getCity();
        }
        if (!TextUtils.isEmpty(userQuery)) {
            HomeMapFragment mapFragment = (HomeMapFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.activity_main_view_pager + ":" +
                            viewPager.getCurrentItem());
            try {
                mapFragment.moveToSearchLocation(userQuery);
            } catch (IOException e) {
                e.printStackTrace();
            }
            customAutoCompleteTextView.setText("");
            customAutoCompleteTextView.setCursorVisible(false);
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
};

public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_main_menu, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        //set profile pic
        ImageView profilePic = (ImageView) navigationView.getHeaderView(0)
                .findViewById(R.id.image_profile);
        if (ParseUser.getCurrentUser().getString("profilePictureUrl") != null) {
            Picasso.with(this).load(ParseUser.getCurrentUser().getString("profilePictureUrl"))
                    //.transform(new CropCircleTransformation())
                    .into(profilePic);
        }
        //set profile name
        TextView profileName = (TextView) navigationView.getHeaderView(0)
                .findViewById(R.id.name_profile);
        profileName.setText(ParseUser.getCurrentUser().getUsername());
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();
        View x = navigationView.getHeaderView(0);
        x.setMinimumHeight(height);

        //navigationView.findViewById(R.id.menu_main).setVisibility(View.GONE);

        menuPhotos = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.menu_photos);
        menuAlbums = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.menu_albums);
        menuExplore = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.menu_explore);
        menuLogout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.menu_logout);
        menuaddFriends = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.menu_add_friends);

        //Set on click listeners
        menuPhotos.setOnClickListener(menuphotosListener);
        menuAlbums.setOnClickListener(menualbumsListener);
        menuExplore.setOnClickListener(menuexploreListener);
        menuLogout.setOnClickListener(menulogoutListener);
        menuaddFriends.setOnClickListener(menuaddfriendsListener);
    }

    public void selectDrawerItem(MenuItem menuItem) {

        /**
        // Intent for photos/album
        Intent i = null;
        switch (menuItem.getItemId()) {
            case R.id.photos:
                i = new Intent(this, ImagesActivity.class);
                i.putExtra("type", ImagesActivity.PHOTOS_VIEW);
                break;
            case R.id.albums:
                i = new Intent(this, ImagesActivity.class);
                i.putExtra("type", ImagesActivity.ALBUM_VIEW);
                break;
            case R.id.invite_button:
                AppUtils.SendInvite(this);
                break;
            case R.id.logout:
                onLogout();
                break;
            default:
                break;
        }

        if (i != null) {
            startActivity(i);
        }
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
             **/
    }

    View.OnClickListener menuphotosListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Intent i = new Intent(v.getContext(), ImagesActivity.class);
            i.putExtra("type", ImagesActivity.PHOTOS_VIEW);
            startActivity(i);
            mDrawer.closeDrawers();
        }
    };

    View.OnClickListener menualbumsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(v.getContext(), ImagesActivity.class);
            i.putExtra("type", ImagesActivity.ALBUM_VIEW);
            startActivity(i);
            mDrawer.closeDrawers();
        }
    };

    View.OnClickListener menuaddfriendsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AppUtils.SendInvite(v.getContext());
        }
    };

    View.OnClickListener menuexploreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener menulogoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onLogout();
        }
    };

    private void onLogout() {
        // Log the user out
        ParseUser.logOut();
        // close this user's session
        LoginManager.getInstance().logOut();
        // Go to the login view
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Listener for highlighting selected thumbnail
    ViewPager.OnPageChangeListener toolbarListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float[] fromHSV = new float[3];
            float[] toHSV = new float[3];
            float[] from = new float[3];
            float[] to = new float[3];
            Color.colorToHSV(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_selected), from);
            Color.colorToHSV(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_unselected), to);
            fromHSV[0] = from[0] + (to[0] - from[0]) * positionOffset;
            fromHSV[1] = from[1] + (to[1] - from[1]) * positionOffset;
            fromHSV[2] = from[2] + (to[2] - from[2]) * positionOffset;
            toHSV[0] = to[0] + (from[0] - to[0]) * positionOffset;
            toHSV[1] = to[1] + (from[1] - to[1]) * positionOffset;
            toHSV[2] = to[2] + (from[2] - to[2]) * positionOffset;
            switch (position) {
                case 0 :{
                    bottomBarMap.setColorFilter(Color.HSVToColor(fromHSV));
                    bottomBarCam.setColorFilter(Color.HSVToColor(toHSV));
                    break;
                }
                case 1 : {
                    bottomBarCam.setColorFilter(Color.HSVToColor(fromHSV));
                    bottomBarMap.setColorFilter(Color.HSVToColor(toHSV));
                    break;
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            CameraFragment cameraFragment = (CameraFragment) getSupportFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.activity_main_view_pager + ":" +
                            "1");
            switch (position) {
                case 0:
                    toolbarContainer.setVisibility(View.VISIBLE);
                    cameraFragment.onPause();
                    bottomBarMap.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_selected));
                    bottomBarCam.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_unselected));
                    break;
                default:
                    toolbarContainer.setVisibility(View.INVISIBLE);
                    cameraFragment.onResume();
                    bottomBarCam.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_selected));
                    bottomBarMap.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.bottom_bar_unselected));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    /*
 * Called when the Activity is no longer visible at all. Stop updates and disconnect.
 */
    @Override
    public void onStop() {
        // If the client is connected
        if (locationClient.isConnected()) {
            stopPeriodicUpdates();
        }
        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();

        super.onStop();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Connect to the location services client
        locationClient.connect();

    }

    /*
     * Called by Location Services when the request to connect the client finishes successfully. At
     * this point, you can request the current location or start periodic updates
     */
    public void onConnected(Bundle bundle) {
        Log.d("MainActivity", "Connected to location services");
        currentLocation = getLocation();
        startPeriodicUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MainActivity", "GoogleApiClient connection has been suspend");
    }

    /*
  * Report location updates to the UI.
  */
    public void onLocationChanged(Location location) {
        currentLocation = location;
        lastLocation = location;
    }

    public Location getCurrentLocation(){
        Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
        if (myLoc == null) {
//            Toast.makeText(MainActivity.this,
//                    "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();

        }
        return myLoc;
    }

    public PicturePost getCurrentPicturePost() {
        return post;
    }


    public void createParseFile(byte[] scaledData){
        photoFile = new ParseFile("post_photo.jpg", scaledData);
    }

    public void onSaveCaption() {

        photoFile.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
//                    Toast.makeText(getApplicationContext(),
//                            "Error saving picture: " + e.getMessage(),
//                            Toast.LENGTH_LONG).show();

                } else {
                    post.setImage(photoFile);
                    post.saveInBackground(new SaveCallback() {

                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Push notifications
                                HashMap<String, String> test = new HashMap<>();
                                test.put("message", "testing");
                                test.put("customData", post.getUser().getUsername());
                                test.put("locationLat", String.valueOf(post.getLocation().getLatitude()));
                                test.put("locationLong", String.valueOf(post.getLocation().getLongitude()));

                                ParseCloud.callFunctionInBackground("pushChannelTest", test);

                            } else {

//                                Toast.makeText(
//                                        getApplicationContext(),
//                                        "Error saving post: " + e.getMessage(),
//                                        Toast.LENGTH_SHORT).show();

                            }
                        }

                    });


                }
            }
        });



        viewPager.setCurrentItem(0);
        HomeMapFragment mapView = (HomeMapFragment) adapter.getItem(0);
        mapView.addMarker(post);
    }

    /*
 * Get the current location
 */
    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            checkPermission();
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    /*
  * In response to a request to start updates, send a request to Location Services
  */
    private void startPeriodicUpdates() {
        checkPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    /*
     * In response to a request to stop updates, send a request to Location Services
     */
    private void stopPeriodicUpdates() {
        locationClient.disconnect();
    }

    /*
 * Verify that Google Play services is available before making a request.
 *
 * @return true if Google Play services is available, otherwise false
 */
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
                Log.d("MainActivity", "Google play services available");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.d("MainActivity", "Google play services not available");
            return false;
        }
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(perms, Constants.locationPermission);
            }
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
        }
    };

    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));
    }

    @OnClick(R.id.bottom_bar_map)
    public void onClickMap() {
        int pos = viewPager.getCurrentItem();
        if(pos == 0) {
            return;
        }
        viewPager.setCurrentItem(0, true);
        bottomBarMap.setColorFilter(ContextCompat.getColor(this, R.color.bottom_bar_selected));
        bottomBarCam.setColorFilter(ContextCompat.getColor(this, R.color.bottom_bar_unselected));
    }

    @OnClick(R.id.bottom_bar_camera)
    public void onClickCam() {
        int pos = viewPager.getCurrentItem();
        if(pos == 1) {
            return;
        }
        viewPager.setCurrentItem(1, true);
        bottomBarCam.setColorFilter(ContextCompat.getColor(this, R.color.bottom_bar_selected));
        bottomBarMap.setColorFilter(ContextCompat.getColor(this, R.color.bottom_bar_unselected));
    }
}

