package com.ltst.core.net.exceptions;

import com.ltst.core.net.response.ServerDBExceptionResponse;

import java.io.IOException;

public class ServerDataBaseException extends IOException {

    public static final String TAKEN_EXCEPTION = "taken";

    public final ServerDBExceptionResponse response;

    public ServerDBExceptionResponse getResponse() {
        return response;
    }

    public ServerDataBaseException(ServerDBExceptionResponse response) {
        this.response = response;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
