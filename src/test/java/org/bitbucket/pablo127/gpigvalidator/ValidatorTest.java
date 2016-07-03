package org.bitbucket.pablo127.gpigvalidator;

import com.google.common.base.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.bitbucket.pablo127.gpigvalidator.constraint.EmailValidator;
import org.bitbucket.pablo127.gpigvalidator.constraint.NotNullValidator;
import org.bitbucket.pablo127.gpigvalidator.constraint.SizeValidator;
import org.bitbucket.pablo127.gpigvalidator.exception.InternalException;
import org.bitbucket.pablo127.gpigvalidator.exception.NotExistingField;
import org.bitbucket.pablo127.gpigvalidator.exception.ValidationException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.hibernate.validator.constraints.Email;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {Validator.class, NotNullValidator.class, EmailValidator.class, SizeValidator.class})
public class ValidatorTest {

    private static final String NOT_NULL_VALIDATOR_FORMATTED_MESSAGE = "is null but should be not null.";
    private static final String SIZE_VALIDATOR_FORMATTED_MESSAGE = "the size should be between %s and %s";
    private static final String EMAIL_VALIDATOR_FORMATTED_MESSAGE = "it should be an email";

    private static final String FIELD_NAME = "field";
    
    private static final String VALIDATOR_MESSAGE_WITH_FIELD_PREFIX = "Field 'field'";
    private static final String VALIDATOR_MESSAGE_WITH_STRING_FIELD_PREFIX = "Field 'stringField'";

    private NotNullValidator notNullValidator;
    private EmailValidator emailValidator;
    private SizeValidator sizeValidator;

    @Before
    public void setUp() throws Exception {
        mockNotNullValidator();
        mockEmailValidator();
        mockSizeValidator();
    }

    @Test
    public void objectWithNotNullFieldsCorrect() {
        mockValidatorsWithoutErrorMessages();

        Object field = new Object();
        String stringField = "";

        assertTrue(
                Validator.isCorrectObject(
                        NotNullFieldsObject.builder()
                                .field(field)
                                .stringField(stringField)
                                .build()));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(field), any(Annotation.class));
        verify(notNullValidator, times(1))
                .getErrorMessage(eq(stringField), any(Annotation.class));
        verify(notNullValidator, times(2));
    }

    @Test
    public void objectWithNotNullFieldCorrect() {
        mockValidatorsWithoutErrorMessages();

        Object field = new Object();

        assertTrue(
                Validator.isCorrectObject(
                        NotNullFieldObject.builder()
                                .field(field)
                                .build()));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(field), any(Annotation.class));
        verify(notNullValidator, times(1));
    }

    @Test
    public void objectWithSomeFieldsNull() {
        String stringField = "";

        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));
        when(notNullValidator.getErrorMessage(eq(stringField), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());

        assertFalse(
                Validator.isCorrectObject(
                        NotNullFieldsObject.builder()
                                .stringField(stringField)
                                .build()));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(null), any(Annotation.class));
        verify(notNullValidator, times(1));
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationSingle() {
        Validator.isCorrectObject(
                WrongAnnotationFieldObject.builder()
                        .nullable(new Object())
                        .build());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationAndCorrectConstraint() {
        Validator.isCorrectObject(
                WrongAndCorrectAnnotationFieldObject.builder()
                        .field(new Object())
                        .build());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationAndWrongConstraint() {
        Validator.isCorrectObject(
                WrongAndCorrectAnnotationFieldObject.builder()
                        .build());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void correctConstraintAndWrongAnnotation() {
        mockValidatorsWithoutErrorMessages();

        Validator.isCorrectObject(
                CorrectAndWrongAnnotationFieldObject.builder()
                        .field(new Object())
                        .build());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongConstraintAndWrongAnnotation() {
        mockValidatorsWithErrorMessages();

        Validator.isCorrectObject(
                CorrectAndWrongAnnotationFieldObject.builder()
                        .build());
    }

    @Test
    public void correctField() {
        final Object field = new Object();

        mockValidatorsWithoutErrorMessages();

        assertTrue(
                Validator.isCorrectField(
                        NotNullFieldObject.builder()
                                .field(field)
                                .build(),
                        FIELD_NAME));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(field), any(Annotation.class));
        verify(notNullValidator, times(1));
    }

    @Test
    public void correctFieldWithMultipleConstraints() {
        String field = "ab";

        mockValidatorsWithoutErrorMessages();

        FieldWithCorrectMultipleAnnotationsObject object = FieldWithCorrectMultipleAnnotationsObject.builder()
                .field(field)
                .build();

        assertTrue(
                Validator.isCorrectField(object, FIELD_NAME));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(field), any(Annotation.class));
        verify(sizeValidator, times(1))
                .getErrorMessage(eq(field), any(Annotation.class));
    }

    @Test
    public void incorrectField() {
        mockValidatorsWithErrorMessages();

        NotNullFieldObject notNullFieldObject = NotNullFieldObject.builder()
                .build();

        assertFalse(
                Validator.isCorrectField(notNullFieldObject, FIELD_NAME));

        verify(notNullValidator, times(1))
                .getErrorMessage(eq(null), any(Annotation.class));
        verify(notNullValidator, times(1));
    }

    @Test
    public void incorrectSizeFieldWithMultipleConstraints() {
        String field = "abcd";

        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
        when(sizeValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(SIZE_VALIDATOR_FORMATTED_MESSAGE));

        assertFalse(
                Validator.isCorrectField(
                        FieldWithCorrectMultipleAnnotationsObject.builder()
                                .field(field)
                                .build(),
                        FIELD_NAME));

        verify(notNullValidator, times(1))
                .getErrorMessage(any(Object.class), any(Annotation.class));
        verify(sizeValidator, times(1))
                .getErrorMessage(any(Object.class), any(Annotation.class));
    }

    @Test
    public void incorrectNullityFieldWithMultipleConstraints() {
        mockValidatorsWithErrorMessages();

        assertFalse(
                Validator.isCorrectField(
                        FieldWithCorrectMultipleAnnotationsObject.builder()
                                .build(),
                        FIELD_NAME));

        verify(notNullValidator, times(1))
            .getErrorMessage(any(Object.class), any(Annotation.class));
        verify(sizeValidator, times(0));
    }

    @Test(expected = NotExistingField.class)
    public void notExistingField() {
        Validator.isCorrectField(new Object(), FIELD_NAME);
    }

    @Test(expected = InternalException.class)
    public void fieldOfWrongObject() throws NoSuchFieldException {
        NotNullFieldObject firstObj = NotNullFieldObject.builder()
                .build();
        WrongAnnotationFieldObject secObj = WrongAnnotationFieldObject.builder()
                .build();

        Validator.checkAnnotationConstraintError(
                secObj,
                firstObj.getClass().getDeclaredField(FIELD_NAME),
                null);
    }

    @Test
    public void validateCorrectObjectWithSpecificValidationException() {
        mockValidatorsWithoutErrorMessages();

        Validator.validateObject(getCorrectNotNullFieldObject(), TestValidationException.class);
    }

    @Test
    public void validateCorrectObjectWithValidationException() {
        mockValidatorsWithoutErrorMessages();

        Validator.validateObject(getCorrectNotNullFieldObject());
    }

    @Test
    public void validateIncorrectObjectWithSpecificValidationException() {
        mockValidatorsWithErrorMessages();

        try {
            Validator.validateObject(getIncorrectNotNullFieldObject(), TestValidationException.class);
            fail();
        } catch (TestValidationException e) {
            assertNotNull(e.getMessage());
            assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, e.getMessage());
        }
    }

    @Test(expected = InternalException.class)
    public void validateIncorrectObjectWithSpecificIncorrectValidationException() {
        mockValidatorsWithErrorMessages();

        Validator.validateObject(getIncorrectNotNullFieldObject(), IncorrectTestValidationException.class);
    }

    @Test(expected = ValidationException.class)
    public void validateIncorrectObjectWithValidationException() {
        mockValidatorsWithErrorMessages();

        Validator.validateObject(getIncorrectNotNullFieldObject());
    }

    @Test
    public void validateCorrectFieldWithValidationException() {
        mockValidatorsWithoutErrorMessages();

        Validator.validateField(getCorrectNotNullFieldObject(), FIELD_NAME);
    }

    @Test
    public void validateIncorrectFieldWithValidationException() {
        mockValidatorsWithErrorMessages();

        try {
            Validator.validateField(getIncorrectNotNullFieldObject(), FIELD_NAME);
            fail();
        } catch (ValidationException e) {
            assertNotNull(e.getMessage());
            assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, e.getMessage());
        }
    }

    @Test
    public void validateIncorrectFieldWithSpecificValidationException() {
        mockValidatorsWithErrorMessages();

        try {
            Validator.validateField(getIncorrectNotNullFieldObject(), FIELD_NAME, TestValidationException.class);
            fail();
        } catch (TestValidationException e) {
            assertNotNull(e.getMessage());
            assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, e.getMessage());
        }
    }

    @Test(expected = InternalException.class)
    public void validateIncorrectFieldWithWrongSpecificValidationException() {
        mockValidatorsWithErrorMessages();

        Validator.validateField(getIncorrectNotNullFieldObject(), FIELD_NAME, IncorrectTestValidationException.class);
    }

    @Test(expected = NotExistingField.class)
    public void validateNotExistingField() {
        Validator.validateField(getIncorrectNotNullFieldObject(), "field2", IncorrectTestValidationException.class);
    }

    @Test
    public void validateWithErrorMessagesSingleWrongFieldWithSingleAnnotation() {
        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));

        Map<String, String> errors = Validator.validateWithErrorMessages(getIncorrectNotNullFieldObject());
        assertEquals(1, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, errors.get(FIELD_NAME));
    }

    @Test
    public void validateWithErrorMessagesSingleWrongFieldWithMultipleAnnotationsAllWrong() {
        mockValidatorsWithErrorMessages();

        Map<String, String> errors = Validator.validateWithErrorMessages(
                FieldWithCorrectMultipleAnnotationsObject.builder()
                        .build());
        assertEquals(1, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE + " "
                + VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + SIZE_VALIDATOR_FORMATTED_MESSAGE , errors.get(FIELD_NAME));
    }

    @Test
    public void validateWithErrorMessagesSingleWrongFieldWithMultipleAnnotations1stWrong() {
        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));
        when(sizeValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());

        Map<String, String> errors = Validator.validateWithErrorMessages(
                FieldWithCorrectMultipleAnnotationsObject.builder()
                        .field("")
                        .build());
        assertEquals(1, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, errors.get(FIELD_NAME));
    }

    @Test
    public void validateWithErrorMessagesSingleWrongFieldWithMultipleAnnotations2ndWrong() {
        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
        when(sizeValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(SIZE_VALIDATOR_FORMATTED_MESSAGE));

        Map<String, String> errors = Validator.validateWithErrorMessages(
                FieldWithCorrectMultipleAnnotationsObject.builder()
                        .field("")
                        .build());
        assertEquals(1, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + SIZE_VALIDATOR_FORMATTED_MESSAGE , errors.get(FIELD_NAME));
    }

    @Test
    public void validateWithOneErrorMessageMultipleWrongFields() {
        when(notNullValidator.getErrorMessage(eq(""), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
        when(notNullValidator.getErrorMessage(eq(null), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));

        Map<String, String> errors = Validator.validateWithErrorMessages(
                NotNullFieldsObject.builder()
                        .stringField("")
                        .build());

        assertEquals(1, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE , errors.get(FIELD_NAME));
    }

    @Test
    public void validateWithErrorMessagesMultipleWrongFields() {
        when(notNullValidator.getErrorMessage(eq(null), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));

        Map<String, String> errors = Validator.validateWithErrorMessages(
                NotNullFieldsObject.builder()
                        .build());

        assertEquals(2, errors.size());
        assertNotNull(errors.get(FIELD_NAME));
        assertNotNull(errors.get("stringField"));
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, errors.get(FIELD_NAME));
        assertEquals(
                VALIDATOR_MESSAGE_WITH_STRING_FIELD_PREFIX + " " +NOT_NULL_VALIDATOR_FORMATTED_MESSAGE,
                errors.get("stringField"));
    }

    @Test
    public void validateWithErrorMessage2Messages() {
        when(notNullValidator.getErrorMessage(eq(null), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));

        Optional<String> errorsOpt = Validator.validateWithErrorMessage(
                NotNullFieldsObject.builder()
                        .build());

        assertTrue(errorsOpt.isPresent());
        assertEquals(VALIDATOR_MESSAGE_WITH_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE + " "
                + VALIDATOR_MESSAGE_WITH_STRING_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE, errorsOpt.get());
    }

    @Test
    public void validateWithErrorMessage1Message() {
        when(notNullValidator.getErrorMessage(eq(null), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));
        when(notNullValidator.getErrorMessage(eq(""), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());

        Optional<String> errorsOpt = Validator.validateWithErrorMessage(
                NotNullFieldsObject.builder()
                        .field("")
                        .build());

        assertTrue(errorsOpt.isPresent());
        assertEquals(
                VALIDATOR_MESSAGE_WITH_STRING_FIELD_PREFIX + " " + NOT_NULL_VALIDATOR_FORMATTED_MESSAGE,
                errorsOpt.get());
    }

    @Test
    public void validateWithErrorMessageNoMessage() {
        mockValidatorsWithoutErrorMessages();

        Optional<String> errorsOpt = Validator.validateWithErrorMessage(
                NotNullFieldsObject.builder()
                        .field("")
                        .stringField("")
                        .build());

        assertFalse(errorsOpt.isPresent());
    }

    private NotNullFieldObject getIncorrectNotNullFieldObject() {
        return NotNullFieldObject.builder()
                .build();
    }

    private NotNullFieldObject getCorrectNotNullFieldObject() {
        String field = "asd";

        return NotNullFieldObject.builder()
                .field(field)
                .build();
    }

    private void mockValidatorsWithErrorMessages() {
        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(NOT_NULL_VALIDATOR_FORMATTED_MESSAGE));
        when(sizeValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(SIZE_VALIDATOR_FORMATTED_MESSAGE));
        when(emailValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.of(EMAIL_VALIDATOR_FORMATTED_MESSAGE));
    }

    private void mockValidatorsWithoutErrorMessages() {
        when(notNullValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
        when(sizeValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
        when(emailValidator.getErrorMessage(any(Object.class), any(Annotation.class)))
                .thenReturn(Optional.<String>absent());
    }

    private void mockNotNullValidator() throws Exception {
        notNullValidator = mock(NotNullValidator.class);

        whenNew(NotNullValidator.class)
                .withAnyArguments()
                .thenReturn(notNullValidator);

        when(notNullValidator.getAnnotationType())
                .thenReturn(NotNull.class);
    }

    private void mockEmailValidator() throws Exception {
        emailValidator = mock(EmailValidator.class);

        whenNew(EmailValidator.class)
                .withAnyArguments()
                .thenReturn(emailValidator);

        when(emailValidator.getAnnotationType())
                .thenReturn(Email.class);
    }

    private void mockSizeValidator() throws Exception {
        sizeValidator = mock(SizeValidator.class);

        whenNew(SizeValidator.class)
                .withAnyArguments()
                .thenReturn(sizeValidator);

        when(sizeValidator.getAnnotationType())
                .thenReturn(Size.class);
    }

    @Builder
    @EqualsAndHashCode
    private static class NotNullFieldsObject {

        @NotNull
        private Object field;

        @NotNull
        private String stringField;
    }

    @Builder
    @EqualsAndHashCode
    private static class NotNullFieldObject {

        @NotNull
        private Object field;
    }

    @Builder
    @EqualsAndHashCode
    private static class WrongAnnotationFieldObject {

        @WrongAnnotation
        private Object nullable;
    }

    @Builder
    private static class CorrectAndWrongAnnotationFieldObject {

        @NotNull
        @WrongAnnotation
        private Object field;
    }

    @Builder
    private static class WrongAndCorrectAnnotationFieldObject {

        @WrongAnnotation
        @NotNull
        private Object field;
    }

    @Builder
    private static class FieldWithCorrectMultipleAnnotationsObject {

        @NotNull
        @Size(min = 1, max = 3)
        private String field;
    }

    @Retention(RUNTIME)
    private @interface WrongAnnotation {
    }

    private class IncorrectTestValidationException extends ValidationException {
        public IncorrectTestValidationException(String message) {
            super(message);
        }
    }
}