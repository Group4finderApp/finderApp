package com.codepath.finderapp.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.codepath.finderapp.R;
import com.codepath.finderapp.fragments.CameraFragment;
import com.codepath.finderapp.models.PicturePost;

public class CameraPostActivity extends AppCompatActivity {

    private PicturePost picturePost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        picturePost = new PicturePost();

        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.container);

        if (fragment == null) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.container, new CameraFragment());
            transaction.commit();

            /*getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CameraFragment())
                    .commit();*/

        }

    }

    public PicturePost getCurrentPicturePost() {
        return picturePost;
    }
}
