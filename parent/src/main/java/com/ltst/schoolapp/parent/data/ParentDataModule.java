package com.ltst.schoolapp.parent.data;

import android.content.SharedPreferences;

import com.livetyping.utils.preferences.BooleanPreference;
import com.ltst.core.data.DataBaseService;
import com.ltst.core.data.NetworkModule;
import com.ltst.core.data.preferences.qualifiers.IsFirstStart;
import com.ltst.core.data.realm.AppRealmModule;
import com.ltst.core.util.TokenExceptionHandler;
import com.ltst.schoolapp.parent.ParentApplication;
import com.ltst.schoolapp.parent.ParentScope;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;

@Module
public class ParentDataModule {


    @Provides
    @ParentScope
    DataBaseService provideDataBaseService(Realm realm, TokenExceptionHandler handler) {
        DataBaseService dataBaseService = new DataBaseService(realm);
        handler.setDataBaseService(dataBaseService);
        return dataBaseService;
    }

    @Provides
    @ParentScope
    Realm provideRealm(ParentApplication application) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(application)
                .modules(new AppRealmModule())
                .build();
        Realm.setDefaultConfiguration(configuration);
        return Realm.getInstance(configuration);
    }

//    @Provides
//    @FirebaseDeviceToken
//    String provideAppInstanceId(ParentApplication application) {
//        // TODO: 30.11.16 (alexeenkoff) need firebase token
////        String id = Settings.Secure.getString(application.getContentResolver(),
////                Settings.Secure.ANDROID_ID);
////        if (id.isEmpty()) id = InstanceID.getInstance(application).getId();
////        return id + "_parent_app";
//        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
//        return firebaseToken;
//    }

    @Provides
    @ParentScope
    ApiService provideApiService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(NetworkModule.API_URL).build();
        return retrofit.create(ApiService.class);
    }

    private static final String IS_FIRST_START_KEY = "CoreApp.IsFirstStart";

    @Provides
    @ParentScope
    @IsFirstStart
    BooleanPreference provideIsFirstStart(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, IS_FIRST_START_KEY, true);
    }
}
