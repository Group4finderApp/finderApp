package com.codepath.finderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.finderapp.DispatchActivity;
import com.codepath.finderapp.R;
import com.codepath.finderapp.adapters.HomeViewPagerAdapter;
import com.codepath.finderapp.fragments.HomeMapView;
import com.codepath.finderapp.fragments.SaveCaptionFragment;
import com.codepath.finderapp.models.PicturePost;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements
        SaveCaptionFragment.SaveCaptionFragmentDialogListener{

    @BindView(R.id.activity_main_view_pager)
    ViewPager viewPager;
    @BindView(R.id.activity_main_tab)
    TabLayout tab;

    private HomeViewPagerAdapter adapter;
    private PicturePost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(this, new Crashlytics());
        ButterKnife.bind(this);

        post = new PicturePost();

        adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                onLogout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogout() {
        // Log the user out
        ParseUser.logOut();
        // close this user's session
        LoginManager.getInstance().logOut();
        // Go to the login view
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public PicturePost getCurrentPicturePost() {
        return post;
    }

    public void onSaveCaption() {

        HomeMapView mapView = (HomeMapView) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPager + ":" +
                        "0");
        Fragment currentView = getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + viewPager + ":" +
                        viewPager.getCurrentItem());

        //if (currentView != null) {
            //if (!(currentView instanceof HomeMapView)) {
                viewPager.setCurrentItem(0);
            //}
        //}
    }
}

