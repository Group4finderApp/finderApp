package com.codepath.finderapp.fragments;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.finderapp.R;
import com.codepath.finderapp.activities.CameraPostActivity;
import com.codepath.finderapp.models.PicturePost;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

/**
 * Created by chmanish on 11/13/16.
 */
public class SaveCaptionFragment extends Fragment {

    private Button saveButton;

    private TextView caption;

    //private ParseImageView mealPreview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle SavedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_save_caption, parent, false);

        caption = ((EditText) v.findViewById(R.id.etCaption));

        saveButton = ((Button) v.findViewById(R.id.btnSave));
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PicturePost post = ((CameraPostActivity) getActivity()).getCurrentPicturePost();

                // When the user clicks "Save," upload the post to Parse
                // Add data to the post object:
                post.setText(caption.getText().toString());

                // TODO: Update location
                ParseGeoPoint geoPoint = new ParseGeoPoint(50, -150);
                // Set the location to the current user's location
                post.setLocation(geoPoint);

                //ParseUser user = ParseUser.getCurrentUser();
                //post.setUser(user);
                ParseACL acl = new ParseACL();
                // Give public read access
                acl.setPublicReadAccess(true);
                post.setACL(acl);

                // Save the post and return
                post.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                            fm.popBackStack("CameraFragment",
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        } else {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });
        return v;
    }



}
