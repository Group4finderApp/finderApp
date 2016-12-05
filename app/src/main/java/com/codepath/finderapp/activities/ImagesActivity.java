package com.codepath.finderapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.AlbumFragment;
import com.codepath.finderapp.fragments.PhotoScrollFragment;
import com.codepath.finderapp.fragments.PhotosFragment;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.models.PicturePostCollection;

import java.util.List;

/**
 * Created by phoen on 11/17/2016.
 */

public class ImagesActivity extends AppCompatActivity
        implements PhotosFragment.ImagesListener,
        AlbumFragment.AlbumListener{
    public static int PHOTOS_VIEW = 0;
    public static int ALBUM_VIEW = 1;
    Toolbar toolbar;
    FragmentTransaction ft;
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
            ft = getSupportFragmentManager().beginTransaction();
            if (getIntent().getIntExtra("type", PHOTOS_VIEW) == PHOTOS_VIEW) {
                Fragment photosFragment = PhotosFragment.newInstance(null);
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
/**
    //Listener for Images Fragments
    @Override
    public void onImageClick(PicturePost picPost, int type) {
        FragmentManager fm = getSupportFragmentManager();
        NewAlbumDialogFragment frag = NewAlbumDialogFragment
                .newInstance(picPost);
        frag.show(fm, "singlephotofragment");
    }
 **/

    //Listener for Images Fragments
    @Override
    public void onImageClick(PicturePostCollection picPosts, int position) {
        ft = getSupportFragmentManager().beginTransaction();
        Fragment photoScrollFragment = PhotoScrollFragment.newInstance(picPosts, position);
        ft.replace(R.id.flContainer, photoScrollFragment);
        ft.commit();
    }

    @Override
    public void onAlbumClick(List<PicturePost> posts) {
        ft = getSupportFragmentManager().beginTransaction();
        Fragment photosFragment = PhotosFragment
                .newInstance(new PicturePostCollection(posts));
        ft.replace(R.id.flContainer, photosFragment);
        ft.commit();
    }
}
