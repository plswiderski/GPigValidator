package org.bitbucket.pablo127.gpigvalidator.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringBuilderUtilTest {

    private static final String TEST = "test";
    private static final String TEST2 = "test2";

    @Test
    public void appendWithSpace() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilderUtil.appendWithSpaceIfNeeded(stringBuilder, TEST);

        assertEquals(TEST, stringBuilder.toString());
    }

    @Test
    public void appendWithoutSpace() {
        StringBuilder stringBuilder = new StringBuilder();

        StringBuilderUtil.appendWithSpaceIfNeeded(stringBuilder, TEST);
        StringBuilderUtil.appendWithSpaceIfNeeded(stringBuilder, TEST2);

        assertEquals(TEST + " " + TEST2, stringBuilder.toString());
    }

    @Test
    public void appendEmptyStringWithoutSpace() {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilderUtil.appendWithSpaceIfNeeded(stringBuilder, "");
        StringBuilderUtil.appendWithSpaceIfNeeded(stringBuilder, TEST2);

        assertEquals(TEST2, stringBuilder.toString());
    }
}