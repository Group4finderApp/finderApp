package com.codepath.finderapp.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.codepath.finderapp.R;
import com.codepath.finderapp.models.PicturePost;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

/**
 * Created by phoen on 11/24/2016.
 */

public class NewAlbumDialogFragment extends DialogFragment {

    public interface CreateAlbumDialogListener {
        void onFinishDialog(String inputText);
    }

    ImageView singleImage;
    EditText etAlbumName;
    Button btCreateAlbum;
    public static NewAlbumDialogFragment newInstance(PicturePost picPost) {
        NewAlbumDialogFragment frag = new NewAlbumDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("picPost", Parcels.wrap(picPost));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return  inflater.inflate(R.layout.new_album_dialog_fragment_view, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PicturePost picPost = Parcels.unwrap(getArguments().getParcelable("picPost"));
        singleImage = (ImageView) view.findViewById(R.id.selectPhoto);
        etAlbumName = (EditText) view.findViewById(R.id.etAlbumName);
        btCreateAlbum = (Button) view.findViewById(R.id.btCreateAlbum);
        btCreateAlbum.setOnClickListener(saveAlbumlistener);

        try {
            Picasso.with(getActivity()).load(picPost.getImage().getFile()).into(singleImage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener saveAlbumlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String albumName = etAlbumName.getText().toString().trim();
            if (!TextUtils.isEmpty(albumName)) {
                CreateAlbumDialogListener cl = (CreateAlbumDialogListener) getTargetFragment();
                cl.onFinishDialog(albumName);
                dismiss();
            }
        }
    };
}
