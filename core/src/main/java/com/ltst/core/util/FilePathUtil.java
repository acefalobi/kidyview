package com.ltst.core.util;

import android.content.Context;
import android.os.Environment;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.BuildConfig;

import java.io.File;

public class FilePathUtil {
    public static File getFileForPhoto() {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(Environment.getExternalStorageDirectory().getPath())
                .append("/")
                .append(BuildConfig.APPLICATION_ID + "/avatars");
        File directory = new File(pathBuilder.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        pathBuilder.append("/pic")
                .append(System.currentTimeMillis())
                .append(".png");
        return new File(pathBuilder.toString());
    }

    public static File getFileForDrive(Context context, String fileName) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(context.getExternalCacheDir().getPath())
                .append(StringUtils.SLASH)
                .append("documents");
        File directory = new File(pathBuilder.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }
//        int lastSlash = mimeType.lastIndexOf(StringUtils.SLASH);
        pathBuilder.append(StringUtils.SLASH);
        pathBuilder.append(fileName);
//                .append(StringUtils.DOT)
//                .append(mimeType.substring(lastSlash + 1));
        return new File(pathBuilder.toString());
    }

    public static File getCacheDir(Context context) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(context.getExternalCacheDir().getPath())
                .append(StringUtils.SLASH)
                .append(System.currentTimeMillis())
                .append(StringUtils.PNG_FORMAT);
        return new File(pathBuilder.toString());
    }
}
