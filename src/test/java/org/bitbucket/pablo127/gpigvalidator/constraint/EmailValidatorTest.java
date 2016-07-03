package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongFieldTypeException;
import org.hibernate.validator.constraints.Email;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {EmailValidator.class, TranslationConfig.class})
public class EmailValidatorTest {

    private static final String EMAIL_ERROR_FORMATTED_MESSAGE = "with value '%s' should be email but it is not.";

    private static final String CORRECT_EMAIL = "a@as.pl";

    private EmailValidator emailValidator;
    private Email emailAnnotation;

    @Before
    public void setUp() throws Exception {
        emailValidator = new EmailValidator();
        emailAnnotation = mock(Email.class);
        mockTranslationConfig();
    }

    @Test
    public void getAnnotationType() {
        assertEquals(Email.class, emailValidator.getAnnotationType());
    }

    @Test
    public void wrongEmail() {
        assertFalse(
                emailValidator.isCorrect("a@aspl", emailAnnotation));
    }

    @Test
    public void correctEmail() {
        assertTrue(
                emailValidator.isCorrect(CORRECT_EMAIL, emailAnnotation));
    }

    @Test(expected = WrongFieldTypeException.class)
    public void wrongFieldType() {
        emailValidator.isCorrect(2, emailAnnotation);
    }

    @Test
    public void nullObject() {
        assertFalse(emailValidator.isCorrect(null, emailAnnotation));
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationType() {
        emailValidator.isCorrect(CORRECT_EMAIL, mock(NotNull.class));
    }

    @Test
    public void correctErrorMessageForNotNullValue() {
        String value = "aas@.pl";
        Optional<String> errorOpt = emailValidator.getErrorMessage(value, emailAnnotation);
        assertTrue(errorOpt.isPresent());
        assertEquals(String.format(EMAIL_ERROR_FORMATTED_MESSAGE, value), errorOpt.get());
    }

    @Test
    public void correctErrorMessageForNullValue() {
        Optional<String> errorOpt = emailValidator.getErrorMessage(null, emailAnnotation);
        assertTrue(errorOpt.isPresent());
        assertEquals(String.format(EMAIL_ERROR_FORMATTED_MESSAGE, "null"), errorOpt.get());
    }

    @Test
    public void emptyErrorMessageForCorrectValidation() {
        Optional<String> errorOpt = emailValidator.getErrorMessage(CORRECT_EMAIL, emailAnnotation);
        assertFalse(errorOpt.isPresent());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void errorMessageWithWrongAnnotation() {
        emailValidator.getErrorMessage(CORRECT_EMAIL, mock(Size.class));
    }

    @Test(expected = WrongFieldTypeException.class)
    public void errorMessageWithWrongFieldType() {
        emailValidator.getErrorMessage(2, emailAnnotation);
    }

    private void mockTranslationConfig() {
        mockStatic(TranslationConfig.class);

        when(TranslationConfig.getTranslation(eq(Message.EMAIL_ERROR_MESSAGE)))
                .thenReturn(EMAIL_ERROR_FORMATTED_MESSAGE);
    }
}