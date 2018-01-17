package com.ltst.core;

import android.content.SharedPreferences;

import com.layer.sdk.LayerClient;
import com.livetyping.utils.preferences.BooleanPreference;
import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.data.NetworkModule;
import com.ltst.core.data.preferences.PreferencesModule;
import com.ltst.core.data.preferences.qualifiers.FirebaseDeviceToken;
import com.ltst.core.data.preferences.qualifiers.NeedShowLogoutPopup;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.layer.LayerModule;
import com.ltst.core.layer.LayerNotificationsHelper;
import com.ltst.core.util.TokenExceptionHandler;
import com.squareup.moshi.Moshi;

import dagger.Component;
import retrofit2.Retrofit;


@CoreScope
@Component(modules = {CoreAppModule.class, PreferencesModule.class, NetworkModule.class, LayerModule.class})
public interface CoreComponent {

    Retrofit.Builder retrofitBuilder();

    SharedPreferences sharedPreferences();

    @ServerToken
    StringPreference serverToken();

    Moshi moshi();

    TokenExceptionHandler tokeExceptionHandler();

    @NeedShowLogoutPopup BooleanPreference needShowLogoutPopup();

    LayerClient layerClient();

    LayerNotificationsHelper layerNotificationHelper();

    @FirebaseDeviceToken StringPreference fireBaseDeviceToken();
}
