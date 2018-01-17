package com.ltst.core.data;

import android.content.Context;

import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.CoreScope;
import com.ltst.core.data.preferences.qualifiers.ScreenDensity;
import com.ltst.core.data.preferences.qualifiers.ServerToken;
import com.ltst.core.net.ApiHeadersInterceptor;
import com.ltst.core.net.CurlLoggingInterceptor;
import com.ltst.core.net.ServerErrorInterceptor;
import com.ltst.core.util.TokenExceptionHandler;
import com.squareup.moshi.Moshi;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

@Module
public class NetworkModule {

//    private static final String SERVER_URL = "http://95.213.203.129"; //test server
    private static final String SERVER_URL = "https://kidyview.com"; //release server
    public static final String API_URL = SERVER_URL + "/api/mobile/v1/";
    public static final String ACTICITY_ICONS_PATH = SERVER_URL + "/icons/";

    @Provides
    @CoreScope
    Moshi provideMoshi() {
        return new Moshi.Builder().build();
    }

    @Provides
    @CoreScope
    Retrofit.Builder provideRetrofitBuilder(Context context,
                                            Moshi moshi,
                                            CurlLoggingInterceptor curlLoggingInterceptor,
                                            @ServerToken StringPreference serverToken,
                                            @ScreenDensity String density,
                                            TokenExceptionHandler exceptionHandler) {
        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new ServerErrorInterceptor(context, moshi, exceptionHandler))
                .addInterceptor(new ApiHeadersInterceptor(serverToken, density))
                .addInterceptor(curlLoggingInterceptor)
                .build();

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient);
    }

    @Provides
    @CoreScope
    CurlLoggingInterceptor provideCurlLoggingInterceptor() {
        return new CurlLoggingInterceptor(message -> Timber.tag("Curl").v(message));
    }

    @Provides
    @CoreScope
    GetUserByLayerIdentityService provideLayerIdentityService(Retrofit.Builder builder) {
        Retrofit retrofit = builder.baseUrl(NetworkModule.API_URL).build();
        return retrofit.create(GetUserByLayerIdentityService.class);
    }
}
