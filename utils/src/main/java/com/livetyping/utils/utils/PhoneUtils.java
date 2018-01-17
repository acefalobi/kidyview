package com.livetyping.utils.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by ognyov on 13/04/16.
 */
public class PhoneUtils {
    public static final String PHONE_SCHEME = "tel:";

    public static void dialNumber(Context context, String phoneNumber) {
        try {
            if (phoneNumber == null) return;
            Uri number = Uri.parse(PHONE_SCHEME + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            context.startActivity(callIntent);
        } catch (Exception e) {
            // no activity to handle intent. show error dialog/toast whatever
        }
    }
}
