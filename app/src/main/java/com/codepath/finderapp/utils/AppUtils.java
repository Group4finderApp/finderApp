package com.codepath.finderapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.parse.ParseACL;


/**
 * Created by hison7463 on 11/12/16.
 */

public class AppUtils {

    public static void disPlayFragment(FragmentManager fragmentManager, Fragment fragment, int layout) {
        fragmentManager.beginTransaction().replace(layout, fragment).commit();
    }

    public static int getPixelsFromDp(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return (int) px;
    }

    public static void SendInvite(Context context) {
        String appLinkUrl = "https://fb.me/161679120966718";
        String previewImageUrl = "https://i.imgur.com/XgxWfyF.png";
        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show((AppCompatActivity) context, content);
        }
    }
    public static int dpToPixels(int dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public static ParseACL getObjectReadWritePermissions() {
        ParseACL acl = new ParseACL();
        // Give public read access
        // TODO: Update ACL
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        return acl;
    }
}
