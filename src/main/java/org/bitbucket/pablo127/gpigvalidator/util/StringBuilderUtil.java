package org.bitbucket.pablo127.gpigvalidator.util;

public final class StringBuilderUtil {

    private StringBuilderUtil() {
    }

    public static void appendWithSpaceIfNeeded(StringBuilder stringBuilder, String text) {
        if (stringBuilder.length() > 0)
            stringBuilder.append(" ");
        stringBuilder.append(text);
    }
}
