package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {NotNullValidator.class, TranslationConfig.class})
public class NotNullValidatorTest {

    private static final String NOT_NULL_FORMATTED_MESSAGE = "is null but should be not null.";

    private NotNullValidator notNullValidator;
    private NotNull notNullAnnotation;

    @Before
    public void setUp() {
        notNullValidator = new NotNullValidator();
        notNullAnnotation = mock(NotNull.class);
        mockTranslationConfig();
    }

    @Test
    public void getAnnotationType() {
        assertEquals(NotNull.class, notNullValidator.getAnnotationType());
    }

    @Test
    public void correctNotNullString() {
        assertTrue(
                notNullValidator.isCorrect("asd", notNullAnnotation));
    }

    @Test
    public void correctNotNullBigDecimal() {
        assertTrue(
                notNullValidator.isCorrect(new BigDecimal("2.12"), notNullAnnotation));
    }

    @Test
    public void wrongNullString() {
        assertFalse(
                notNullValidator.isCorrect(null, notNullAnnotation));
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationType() {
        notNullValidator.isCorrect(null, mock(Size.class));
    }

    @Test
    public void errorMessageForNullValue() {
        Optional<String> errorsOpt = notNullValidator.getErrorMessage(null, notNullAnnotation);
        assertTrue(errorsOpt.isPresent());
        assertEquals(NOT_NULL_FORMATTED_MESSAGE, errorsOpt.get());
    }

    @Test
    public void noErrorMessageForNotNullValue() {
        Optional<String> errorsOpt = notNullValidator.getErrorMessage(2, notNullAnnotation);
        assertFalse(errorsOpt.isPresent());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void errorMessageOnNullWithWrongAnnotation() {
        notNullValidator.getErrorMessage(null, mock(Size.class));
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void errorMessageOnNotNullWithWrongAnnotation() {
        notNullValidator.getErrorMessage("happy", mock(Size.class));
    }

    private void mockTranslationConfig() {
        mockStatic(TranslationConfig.class);

        when(TranslationConfig.getTranslation(eq(Message.NOT_NULL_ERROR_MESSAGE)))
                .thenReturn(NOT_NULL_FORMATTED_MESSAGE);
    }
}