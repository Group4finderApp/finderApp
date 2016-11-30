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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.activities.MainActivity;
import com.codepath.finderapp.models.PicturePost;
import com.parse.ParseACL;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * Created by chmanish on 11/13/16.
 */
public class SaveCaptionFragment extends DialogFragment {

    private Button saveButton;
    private ImageView bgImage;
    private ImageButton thumbsUpButton;
    private boolean isThumbsUp = false;
    static private Bitmap bmBackground;

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

        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        View v = inflater.inflate(R.layout.fragment_save_caption, parent, false);

        bgImage = (ImageView) v.findViewById(R.id.bgImage);
        bgImage.setImageBitmap(bmBackground);
        bgImage.setAlpha(0.8f);

        caption = ((EditText) v.findViewById(R.id.etCaption));

        saveButton = ((Button) v.findViewById(R.id.btnSave));
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicturePost post = ((MainActivity) getActivity()).getCurrentPicturePost();
                // When the user clicks "Save," upload the post to Parse
                // Add data to the post object:
                post.setText(caption.getText().toString());
                post.setThumbsUp(String.valueOf(isThumbsUp));
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
                    thumbsUpButton.setImageResource(R.drawable.thumb_up_outline_white);
                }
                else {
                    isThumbsUp = true;
                    thumbsUpButton.setImageResource(R.drawable.thumb_up);
                }


            }
        });
        return v;
    }

    @Override
    public void onResume() {
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
