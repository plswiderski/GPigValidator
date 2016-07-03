package org.bitbucket.pablo127.gpigvalidator;

import org.bitbucket.pablo127.gpigvalidator.exception.ValidationException;

public class TestValidationException extends ValidationException {
    public TestValidationException(String message) {
        super(message);
    }
}
