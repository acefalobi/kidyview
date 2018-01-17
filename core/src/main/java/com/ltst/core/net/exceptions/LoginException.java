package com.ltst.core.net.exceptions;

import com.ltst.core.net.response.ServerDBExceptionResponse;

import java.io.IOException;

public class LoginException extends IOException {

    public final ServerDBExceptionResponse response;

    public LoginException(ServerDBExceptionResponse response) {
        this.response = response;
    }
}
