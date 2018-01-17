package com.ltst.core.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;

import com.ltst.core.R;
import com.ltst.core.net.exceptions.AuthException;
import com.ltst.core.net.exceptions.LoginException;
import com.ltst.core.net.exceptions.NetErrorException;
import com.ltst.core.net.exceptions.NotFoundException;
import com.ltst.core.net.exceptions.ServerDataBaseException;
import com.ltst.core.net.response.ServerDBExceptionResponse;
import com.ltst.core.util.TokenExceptionHandler;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

public class ServerErrorInterceptor implements Interceptor {

    private static final int OK_200 = 200;

    private final ConnectivityManager connectivityManager;
    private final LocalizedErrorHelper localizedErrorHelper;
    private final TokenExceptionHandler tokenExceptionHandler;
    private final Moshi moshi;

    public ServerErrorInterceptor(Context context, Moshi moshi, TokenExceptionHandler exceptionHandler) {
        this.moshi = moshi;
        this.connectivityManager = ((ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        localizedErrorHelper = new LocalizedErrorHelper(context);
        this.tokenExceptionHandler = exceptionHandler;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!hasInternetConnetction()) {
            throw new NetErrorException(ErrorCode.NO_INTERNET_CONNECTION, localizedErrorHelper.getMessage(ErrorCode.NO_INTERNET_CONNECTION));
        }
        Response originalResponse = chain.proceed(request);
        if (originalResponse.code() == OK_200) {
            return originalResponse;
        }
        if (originalResponse.code() == ErrorCode.SERVER_DB_EXCEPTION) {
            BufferedSource json = originalResponse.body().source();
            JsonAdapter<ServerDBExceptionResponse> adapter
                    = moshi.adapter(ServerDBExceptionResponse.class);
            ServerDBExceptionResponse response = adapter.fromJson(json);

            throw new ServerDataBaseException(response);
        } else if (originalResponse.code() == ErrorCode.SERVER_LOGIN_EXCEPTION) {
            BufferedSource json = originalResponse.body().source();
            JsonAdapter<ServerDBExceptionResponse> adapter
                    = moshi.adapter(ServerDBExceptionResponse.class);
            ServerDBExceptionResponse response = adapter.fromJson(json);
            tokenExceptionHandler.process();
            throw new LoginException(response);
        } else if (originalResponse.code() == ErrorCode.NOT_FOUND) {
            throw new NotFoundException();
        } else if (originalResponse.code() == ErrorCode.NOT_AUTHORIZED_USER) {
            throw new AuthException();
        }
        originalResponse.body().close(); //close connection for any error
        return originalResponse;
    }

    private boolean hasInternetConnetction() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static class LocalizedErrorHelper {

        private final Context context;

        public LocalizedErrorHelper(Context context) {
            this.context = context;
        }

        public String getMessage(int code) {
            @StringRes int resource = R.string.error_unknown;
            switch (code) {
                case ErrorCode.NO_INTERNET_CONNECTION:
                    resource = R.string.error_no_network_connection;
                    break;
                default:
            }
            return context.getString(resource);
        }
    }
}
