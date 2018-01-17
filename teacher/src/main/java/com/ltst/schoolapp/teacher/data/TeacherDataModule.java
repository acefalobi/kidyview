package com.ltst.schoolapp.teacher.data;

import android.content.SharedPreferences;

import com.livetyping.utils.preferences.BooleanPreference;
import com.ltst.core.data.DataBaseService;
import com.ltst.core.data.NetworkModule;
import com.ltst.core.data.preferences.qualifiers.IsAdmin;
import com.ltst.core.data.realm.AppRealmModule;
import com.ltst.core.util.TokenExceptionHandler;
import com.ltst.schoolapp.TeacherApplication;
import com.ltst.schoolapp.TeacherScope;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;

@Module
public class TeacherDataModule {



    @Provides
    @TeacherScope
    DataBaseService provideDataBaseService(Realm realm, TokenExceptionHandler handler) {
        DataBaseService dataBaseService = new DataBaseService(realm);
        handler.setDataBaseService(dataBaseService);
        return dataBaseService;
    }

    @Provides
    @TeacherScope
    Realm provideRealm(TeacherApplication application) {
        RealmConfiguration configuration = new RealmConfiguration.Builder(application)
                .modules(new AppRealmModule())
                .build();
        Realm.setDefaultConfiguration(configuration);
        return Realm.getInstance(configuration);
    }

    @Provides
    @TeacherScope
    ApiService provideApiService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(NetworkModule.API_URL).build();
        return retrofit.create(ApiService.class);
    }

//    @Provides
//    @FirebaseDeviceToken
//    String provideAppInstanceId(TeacherApplication application) {
//        //TODO need device_token for pushes
//        String id = Settings.Secure.getString(application.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//        if (id.isEmpty()) id = InstanceID.getInstance(application).getId();
//        return id + "_teacher_app";
//    }

    private static final String IS_TEACHER_ADMIN = "SchoolApp.IsTeacherAdmin";

    @Provides
    @TeacherScope
    @IsAdmin
    BooleanPreference provideIsAdmin(SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, IS_TEACHER_ADMIN, true);
    }

}
