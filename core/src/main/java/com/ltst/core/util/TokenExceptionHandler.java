package com.ltst.core.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.base.CoreActivity;
import com.ltst.core.data.DataBaseService;

import java.util.List;

import rx.Observable;

public class TokenExceptionHandler extends ActivityConnector<CoreActivity> {

    public static final String TOKEN_EXCEPTION_BROADCAST = "Schoolapp.android.action.broadcast";

    private DataBaseService dataBaseService;
    private final StringPreference tokenPreference;
    private final Context context;
    private final BooleanPreference needShowLogoutPopup;
    private boolean isEnabled = false;

    public TokenExceptionHandler(Context context, StringPreference tokenPreference, BooleanPreference needShowLogoutPopup) {
        this.tokenPreference = tokenPreference;
        this.context = context;
        this.needShowLogoutPopup = needShowLogoutPopup;
    }

    private Observable<Boolean> clearData() {
        return Observable.just(dataBaseService.dropData())
                .doOnNext(aBoolean -> tokenPreference.delete());
    }

    public void setDataBaseService(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    public void process() {
        if (isEnabled) {
            clearData()
                    .flatMap(aBoolean -> isAppInForeground())
                    .subscribe(inForeground -> {
                        needShowLogoutPopup.set(true);
                        if (inForeground) {
                            PackageManager pm = context.getPackageManager();
                            Intent launchIntentForPackage = pm.getLaunchIntentForPackage(context.getPackageName());
                            launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(launchIntentForPackage);
                            isEnabled = false;
                        } else {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
        }
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    private Observable<Boolean> isAppInForeground() {
        ActivityManager activityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return Observable.just(false);
        } else {
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcesses) {
                if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        appProcessInfo.processName.equals(packageName)) {
                    return Observable.just(true);
                }
            }
        }
        return Observable.just(false);
    }
}
