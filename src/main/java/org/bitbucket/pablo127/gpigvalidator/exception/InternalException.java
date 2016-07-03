package org.bitbucket.pablo127.gpigvalidator.exception;

public class InternalException extends RuntimeException {

    public InternalException(Throwable cause) {
        super(cause);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
