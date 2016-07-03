package org.bitbucket.pablo127.gpigvalidator.constraint;

enum Message {
    LANGUAGE,
    CHARS,

    EMAIL_ERROR_MESSAGE,

    NOT_NULL_ERROR_MESSAGE,

    SIZE_ERROR_NOT_PROPER_SIZE,
    SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE,
    SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE,
    SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE,
    SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE;

    public String getPropertyName() {
        return name().toLowerCase()
                .replace('_', '.');
    }
}
