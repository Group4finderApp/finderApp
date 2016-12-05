package com.codepath.finderapp.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.finderapp.OnSwipeTouchListener;
import com.codepath.finderapp.R;
import com.codepath.finderapp.activities.MainActivity;
import com.codepath.finderapp.models.PicturePost;
import com.codepath.finderapp.utils.AppUtils;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

/**
 * Created by chmanish on 11/13/16.
 */
public class SaveCaptionFragment extends DialogFragment {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageButton saveButton;
    private ImageView bgImage;
    private ImageView foodFilter;
    private ImageView sightFilter;
    private boolean isFoodFilter = false;
    private ImageButton thumbsUpButton;
    private ImageButton formatText;
    private boolean isThumbsUp = false;
    static private Bitmap bmBackground;
    public static int filterNum = 0;

    private TextView caption;

    public SaveCaptionFragment() {
        // Empty constructor required for DialogFragment
    }

    public static SaveCaptionFragment newInstance(Bitmap image) {
        SaveCaptionFragment frag = new SaveCaptionFragment();
        bmBackground = image;
        return frag;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface SaveCaptionFragmentDialogListener {

        void onSaveCaption();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SaveFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {


        /*View decorView = getDialog().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);*/

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View v = inflater.inflate(R.layout.fragment_save_caption, parent, false);

        bgImage = (ImageView) v.findViewById(R.id.bgImage);
        bgImage.setImageBitmap(bmBackground);
        //bgImage.setAlpha(0.8f);

        caption = ((EditText) v.findViewById(R.id.etCaption));

        saveButton = ((ImageButton) v.findViewById(R.id.btnSave));
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicturePost post = ((MainActivity) getActivity()).getCurrentPicturePost();
                // When the user clicks "Save," upload the post to Parse
                // Add data to the post object:
                post.setText(caption.getText().toString());
                post.setThumbsUp(String.valueOf(isThumbsUp));
                post.setFoodFilter(String.valueOf(isFoodFilter));
                Location myLoc = ((MainActivity) getActivity()).getCurrentLocation();
                ParseGeoPoint geoPoint = new ParseGeoPoint(myLoc.getLatitude(), myLoc.getLongitude());
                // Set the location to the current user's location
                post.setLocation(geoPoint);

                ParseUser user = ParseUser.getCurrentUser();
                post.setUser(user);
                ParseACL acl = new ParseACL();
                // Give public read access
                // TODO: Update ACL
                acl.setPublicReadAccess(true);
                acl.setPublicWriteAccess(true);
                post.setACL(acl);

                SaveCaptionFragmentDialogListener mListener = (SaveCaptionFragmentDialogListener) getActivity();
                mListener.onSaveCaption();
                dismiss();

            }
        });

        thumbsUpButton = (ImageButton) v.findViewById(R.id.thumbs_up_button);
        thumbsUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isThumbsUp == true) {
                    isThumbsUp = false;
                    thumbsUpButton.setLayoutParams(new RelativeLayout.LayoutParams(AppUtils.dpToPixels(48, getContext().getResources()), AppUtils.dpToPixels(48, getContext().getResources())));
                    thumbsUpButton.setImageResource(R.drawable.thumb_up_outline_white);


                }
                else {
                    isThumbsUp = true;
                    thumbsUpButton.setLayoutParams(new RelativeLayout.LayoutParams(AppUtils.dpToPixels(48, getContext().getResources()), AppUtils.dpToPixels(48, getContext().getResources())));
                    thumbsUpButton.setImageResource(R.drawable.thumb_up);

                }


            }
        });

        foodFilter = (ImageView) v.findViewById(R.id.food_filter);

        foodFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFoodFilter == true) {
                    isFoodFilter = false;
                    foodFilter.setImageResource(R.drawable.ic_food_filter_grey);
                    sightFilter.setImageResource(R.drawable.ic_sight_filter);


                }
                else {
                    isFoodFilter = true;
                    foodFilter.setImageResource(R.drawable.ic_food_filter);
                    sightFilter.setImageResource(R.drawable.ic_sight_filter_grey);

                }


            }
        });

        sightFilter = (ImageView) v.findViewById(R.id.sight_filter);

        sightFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFoodFilter == true) {
                    isFoodFilter = false;
                    sightFilter.setImageResource(R.drawable.ic_sight_filter);
                    foodFilter.setImageResource(R.drawable.ic_food_filter_grey);


                }
                else {
                    isFoodFilter = true;
                    sightFilter.setImageResource(R.drawable.ic_sight_filter_grey);
                    foodFilter.setImageResource(R.drawable.ic_food_filter);

                }


            }
        });


        formatText = (ImageButton) v.findViewById(R.id.imageFormatText);
        formatText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                caption.setVisibility(View.VISIBLE);
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);



            }
        });


        v.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeRight() {

                Bitmap temp = Bitmap.createBitmap(bmBackground);
                Filter myFilter;

                switch (filterNum) {
                    case 0:
                        myFilter = SampleFilters.getBlueMessFilter();
                        break;
                    case 1:
                        myFilter = SampleFilters.getLimeStutterFilter();
                        break;
                    case 2:
                        myFilter = SampleFilters.getNightWhisperFilter();
                        break;
                    case 3:
                        myFilter = SampleFilters.getStarLitFilter();
                        break;
                    case 4:
                        myFilter = SampleFilters.getAweStruckVibeFilter();
                        break;
                    case 5:
                        myFilter = null;
                        break;
                    default:
                        myFilter = null;
                        break;



                }
                filterNum++;
                filterNum = filterNum % 6;

                if (myFilter != null) {
                    Bitmap outputImage = myFilter.processFilter(temp);
                    bgImage.setImageBitmap(outputImage);

                }
                else {
                    bgImage.setImageBitmap(bmBackground);
                }

            }
        });
        return v;
    }

    @Override
    public void onResume() {
        View decorView = getDialog().getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 1), (int) (size.y * 1));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }



}
