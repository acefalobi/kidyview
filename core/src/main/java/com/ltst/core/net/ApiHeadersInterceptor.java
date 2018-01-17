package com.ltst.core.net;

import com.livetyping.utils.preferences.StringPreference;
import com.ltst.core.data.preferences.qualifiers.ScreenDensity;
import com.ltst.core.data.preferences.qualifiers.ServerToken;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public final class ApiHeadersInterceptor implements Interceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String SCREEN_DENSITY = "Screen-Density";

    private final StringPreference accessToken;
    private final String screenDensity;

    public ApiHeadersInterceptor(@ServerToken StringPreference serverToken,
                                 @ScreenDensity String screenDensity) {
        this.accessToken = serverToken;
        this.screenDensity = screenDensity;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (accessToken.isSet()) {
            Request request = chain.request();
            Request newRequest;
            newRequest = request.newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, "Token token=" + accessToken.get())
                    .addHeader(SCREEN_DENSITY, screenDensity)
                    .build();
//            request.newBuilder().headers
            return chain.proceed(newRequest);
        } else return chain.proceed(chain.request());

    }
}
