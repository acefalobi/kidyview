package com.ltst.core.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.livetyping.utils.utils.StringUtils;

import java.util.List;

public class SharingService extends ActivityConnector<Activity> {

    private Application application;

    public SharingService(Application application) {
        this.application = application;
    }

    public void sharePost(String text, List<String> imageUrls, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        StringBuilder sharingText = new StringBuilder();
        for (String url : imageUrls) {
            sharingText.append(url);
            sharingText.append(StringUtils.CARRET);
        }
        if (!StringUtils.isBlank(text)) {
            sharingText.append(text);
            sharingText.append(StringUtils.CARRET);
        }
        sharingText.append(title);
        intent.putExtra(Intent.EXTRA_TEXT, sharingText.toString());
        application.startActivity(Intent.createChooser(intent, "").setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void shareCode(String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        application.startActivity(Intent.createChooser(intent, "").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

    }

}
