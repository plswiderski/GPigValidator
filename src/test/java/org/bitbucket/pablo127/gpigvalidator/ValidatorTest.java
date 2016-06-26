package org.bitbucket.pablo127.gpigvalidator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidatorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void isCorrectWithCorrectData() throws Exception {
        assertTrue(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .id(2)
                                .name("abc")
                                .text("abcde")
                                .email("a@a.pl")
                                .specialField("aj")
                                .build()));
    }

    @Test
    public void isCorrectWithWrongNullValue() throws Exception {
        assertFalse(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .name("abc")
                                .text("abcde")
                                .email("a@a.pl")
                                .specialField("aj")
                                .build()));
    }

    @Test
    public void isCorrectWithWrongMinSize() throws Exception {
        assertFalse(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .id(2)
                                .name("ab")
                                .text("abcde")
                                .email("a@a.pl")
                                .specialField("aj")
                                .build()));
    }

    @Test
    public void isCorrectWithWrongMaxSize() throws Exception {
        assertFalse(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .id(2)
                                .name("abc")
                                .text("abcdef")
                                .email("a@a.pl")
                                .specialField("aj")
                                .build()));
    }

    @Test
    public void isCorrectWithWrongEmail() throws Exception {
        assertFalse(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .id(2)
                                .name("abc")
                                .text("abcde")
                                .email("a@apl")
                                .specialField("aj")
                                .build()));
    }

    @Test
    public void isCorrectWithFewViolations() throws Exception {
        assertFalse(
                Validator.isCorrectObject(
                        ValidationTestObject.builder()
                                .id(2)
                                .name("abc")
                                .text("abcde")
                                .email("a@apl")
                                .build()));
    }

    // TODO throwing exceptions

    @Test
    public void validate() throws Exception {
        // TODO
    }

    @Test
    public void getValidationErrorMessages() throws Exception {
        // TODO
    }

    @Test
    public void getValidationErrorMessage() throws Exception {
        // TODO
    }
}