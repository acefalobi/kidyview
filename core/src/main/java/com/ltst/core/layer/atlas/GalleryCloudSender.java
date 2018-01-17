package com.ltst.core.layer.atlas;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.layer.atlas.messagetypes.threepartimage.GallerySender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageUtils;
import com.layer.atlas.util.LayerUtils;
import com.layer.atlas.util.Log;
import com.layer.sdk.messaging.Identity;
import com.layer.sdk.messaging.Message;
import com.layer.sdk.messaging.PushNotificationPayload;
import com.ltst.core.util.gallerypictureloader.GalleryPictureLoader;

import java.io.IOException;

/*//////////////////////////Fixed version of atlas file//////////////*/

public class GalleryCloudSender extends GallerySender {

    private final GalleryPictureLoader galleryPictureLoader;

    public GalleryCloudSender(int titleResId, Integer iconResId, Activity activity) {
        super(titleResId, iconResId, activity);
        this.galleryPictureLoader = new GalleryPictureLoader(activity);
    }

    public GalleryCloudSender(String title, Integer iconResId, Activity activity) {
        super(title, iconResId, activity);
        this.galleryPictureLoader = new GalleryPictureLoader(activity);
    }

    @Override public boolean onActivityResult(Activity activity, int requestCode, int resultCode,
                                              Intent data) {
        if (requestCode != ACTIVITY_REQUEST_CODE) return false;
        if (resultCode != Activity.RESULT_OK) {
            if (Log.isLoggable(Log.ERROR)) Log.e("Result: " + requestCode + ", data: " + data);
            return true;
        }
        if (Log.isLoggable(Log.VERBOSE)) Log.v("Received gallery response");
        try {
            if (Log.isPerfLoggable()) {
                Log.perf("GallerySender is attempting to send a message");
            }
            Identity me = getLayerClient().getAuthenticatedUser();
            String myName = me == null ? "" : LayerUtils.getDisplayName(me);
            Uri uri = data.getData();
            Message message = FixedThreePartImageUtils.newThreePartImageMessage(activity, getLayerClient(), data, galleryPictureLoader);
//            Message message = ThreePartImageUtils.newThreePartImageMessage(activity, getLayerClient(), data.getData());

            PushNotificationPayload payload = new PushNotificationPayload.Builder()
                    .text(getContext().getString(com.layer.atlas.R.string.atlas_notification_image, myName))
                    .build();
            message.getOptions().defaultPushNotificationPayload(payload);
            send(message);
        } catch (IOException e) {
            if (Log.isLoggable(Log.ERROR)) Log.e(e.getMessage(), e);
        }
        return true;
    }
}
