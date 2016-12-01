package com.codepath.finderapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.AlbumFragment;
import com.codepath.finderapp.fragments.PhotosFragment;
import com.codepath.finderapp.fragments.SinglePhotoDialogFragment;
import com.codepath.finderapp.models.PicturePost;

/**
 * Created by phoen on 11/17/2016.
 */

public class ImagesActivity extends AppCompatActivity
        implements PhotosFragment.ImagesListener{
    public static int PHOTOS_VIEW = 0;
    public static int ALBUM_VIEW = 1;
    Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_return_home);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        //android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            //Display user fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (getIntent().getIntExtra("type", PHOTOS_VIEW) == PHOTOS_VIEW) {
                Fragment photosFragment = PhotosFragment.newInstance();
                ft.replace(R.id.flContainer, photosFragment);
            } else {
                Fragment albumsFragment = AlbumFragment.newInstance();
                ft.replace(R.id.flContainer, albumsFragment);
            }
            ft.commit();
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_images_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Listener for Images Fragments
    @Override
    public void onImageClick(PicturePost picPost, int type) {
        FragmentManager fm = getSupportFragmentManager();
        SinglePhotoDialogFragment frag = SinglePhotoDialogFragment
                .newInstance(picPost);
        frag.show(fm, "singlephotofragment");
    }
}
