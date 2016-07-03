package org.bitbucket.pablo127.gpigvalidator.exception;

/**
 * Exception with details about validation's errors.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
