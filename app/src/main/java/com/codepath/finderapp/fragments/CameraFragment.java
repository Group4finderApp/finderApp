package com.codepath.finderapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.codepath.finderapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by chmanish on 11/11/16.
 */
public class CameraFragment extends Fragment {
    public static final String TAG = "CameraFragment";

    private Camera camera;
    private SurfaceView surfaceView;
    private ImageButton photoButton;
    private int rotation = 90;

    public interface CameraFragmentDialogListener {
        void createParseFile(byte[] data);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera, parent, false);
        photoButton = (ImageButton) v.findViewById(R.id.camera_photo_button);

        if (camera == null) {
            setupCamera();
        }

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (camera == null)
                    return;
                camera.takePicture(new Camera.ShutterCallback() {

                    @Override
                    public void onShutter() {
                        // nothing to do
                    }

                }, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        saveScaledPhoto(data);
                    }

                });

            }
        });

        surfaceView = (SurfaceView) v.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setDisplayOrientation(rotation);
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setting up preview", e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // nothing here
            }

        });

        return v;
    }

    private void setupCamera(){
        try {
            camera = Camera.open();
            //set camera to continually auto-focus
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);
            photoButton.setEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "No camera with exception: " + e.getMessage());
            photoButton.setEnabled(false);
            Toast.makeText(getActivity(), "No camera detected",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void startCaptionFragment(Bitmap rotatedScaledMealImage) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        SaveCaptionFragment saveCaptionFragment = SaveCaptionFragment.newInstance(rotatedScaledMealImage);
        saveCaptionFragment.show(fm, "fragment_save_caption");

    }

    /*
     * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
     * they are saved. Since we never need a full-size image in our app, we'll
     * save a scaled one right away.
     */
    private void saveScaledPhoto(byte[] data) {

        // Resize photo from camera byte array
        Bitmap mealImage = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap mealImageScaled = Bitmap.createScaledBitmap(mealImage, 960, 960
                * mealImage.getHeight() / mealImage.getWidth(), false);

        // Override Android default landscape orientation and save portrait
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap rotatedScaledMealImage = Bitmap.createBitmap(mealImageScaled, 0,
                0, mealImageScaled.getWidth(), mealImageScaled.getHeight(),
                matrix, false);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        byte[] scaledData = bos.toByteArray();

        // Save the scaled image to Parse
        CameraFragmentDialogListener mListener = (CameraFragmentDialogListener) getActivity();
        mListener.createParseFile(scaledData);

        startCaptionFragment(rotatedScaledMealImage);
        if (camera != null)
            camera.startPreview();


    }


    @Override
    public void onResume() {
        super.onResume();
        if (camera == null) {
            setupCamera();
        }
    }

    @Override
    public void onPause() {
        if (camera != null) {
            camera.stopPreview();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        camera.release();
    }
}
