package com.ltst.core.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.BuildConfig;
import com.ltst.core.CoreScope;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.NeedShowLogoutPopup;
import com.ltst.core.data.preferences.qualifiers.ScreenDensity;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.util.DensityCalculator;

import dagger.Module;
import dagger.Provides;

@Module
public class PreferencesModule {

    @Provides
    @CoreScope
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    private static final String SERVER_TOKEN_KEY = "TeacherApp.ServerToken";

    @Provides
    @ServerToken
    @CoreScope
    StringPreference provideServerTokenPreference(SharedPreferences sharedPreferences) {
        return new StringPreference(sharedPreferences, SERVER_TOKEN_KEY, null);
    }

    @Provides
    @CoreScope
    @ScreenDensity
    String provideScreenDensity(Context context) {
        WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int densityDpi = displayMetrics.densityDpi;
        String density = DensityCalculator.calculate(densityDpi);
        return density;
    }

    private static final String NEED_SHOW_LOGOUT_POPUP = "SchoolApp.NeedShowLogoutPopup";

    @Provides
    @CoreScope
    @NeedShowLogoutPopup
    BooleanPreference provideNeedShowLogoutPopup(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, NEED_SHOW_LOGOUT_POPUP, false);
    }


    private static final String FIREBASE_DEVICE_TOKEN = "SchoolApp.FirebaseDeviceToken";

    @Provides
    @CoreScope
    @FirebaseDeviceToken
    StringPreference provideFireBaseToken(SharedPreferences sharedPreferences) {
        StringPreference stringPreference = new StringPreference(sharedPreferences, FIREBASE_DEVICE_TOKEN);
        String token = FirebaseInstanceId.getInstance().getToken();
        stringPreference.set(token);
        return stringPreference;
    }


}
