package com.codepath.finderapp.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.codepath.finderapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hison7463 on 12/4/16.
 */

public class LeavingFragment extends DialogFragment {

    private String url;

    @BindView(R.id.leaving_dialog_yes)
    Button btnYes;
    @BindView(R.id.leaving_dialog_cancel)
    Button btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaving_dialog, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.leaving_dialog_yes)
    public void onClickYes() {
        Uri gmmIntentUri = Uri.parse(url);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        dismiss();
    }

    @OnClick(R.id.leaving_dialog_cancel)
    public void onClickCancel() {
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
