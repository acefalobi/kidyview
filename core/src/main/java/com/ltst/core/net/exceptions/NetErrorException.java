package com.ltst.core.net.exceptions;

import com.ltst.core.net.ErrorCode;

import java.io.IOException;

public class NetErrorException extends IOException {

    private final long code;
    private final String message;
    private final String localizedMessage;


    public NetErrorException(String message) {
        this.code = ErrorCode.UNSPECIFIED;
        this.message = message;
        this.localizedMessage = message;
    }

    public NetErrorException(long code, String message) {
        this.code = code;
        this.message = message;
        this.localizedMessage = message;
    }

    public NetErrorException(long code, String message, String localizedMessage) {
        this.code = code;
        this.message = message;
        this.localizedMessage = localizedMessage;
    }

    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return localizedMessage;
    }
}
