package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongFieldTypeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {SizeValidator.class, TranslationConfig.class})
public class SizeValidatorTest {

    private static final String SIZE_ERROR_NOT_PROPER_SIZE = "has not proper size.";

    private static final String SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE = "It should be at least %d char long.";
    private static final String SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE = "It should be no longer than %d chars.";

    private static final String SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE =
            "It should contain at least %d elements.";
    private static final String SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE =
            "It should have no more than %d elements.";

    private static final List<Integer> CORRECT_SIZES = ImmutableList.of(1, 2, 3);

    private SizeValidator sizeValidator;

    private Size sizeWithMinMax;
    private Size sizeWithMax;
    private Size sizeWithMin;

    @Before
    public void setUp() {
        sizeValidator = new SizeValidator();
        createSizeWithMinMaxMock();
        createSizeWithMinMock();
        createSizeWithMaxMock();

        mockTranslationConfig();
    }

    private void mockTranslationConfig() {
        mockStatic(TranslationConfig.class);

        when(TranslationConfig.getTranslation(eq(Message.SIZE_ERROR_NOT_PROPER_SIZE)))
                .thenReturn(SIZE_ERROR_NOT_PROPER_SIZE);
        when(TranslationConfig.getTranslation(eq(Message.SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE)))
                .thenReturn(SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE);
        when(TranslationConfig.getTranslation(eq(Message.SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE)))
                .thenReturn(SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE);
        when(TranslationConfig.getTranslation(eq(Message.SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE)))
                .thenReturn(SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE);
        when(TranslationConfig.getTranslation(eq(Message.SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE)))
                .thenReturn(SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE);
    }

    @Test
    public void getAnnotationType() {
        assertEquals(Size.class, sizeValidator.getAnnotationType());
    }

    @Test
    public void correctStringSize() {
        assertTrue(sizeValidator.isCorrect("a", sizeWithMinMax));
        assertTrue(sizeValidator.isCorrect("ab", sizeWithMinMax));
        assertTrue(sizeValidator.isCorrect("abc", sizeWithMinMax));

        assertTrue(sizeValidator.isCorrect("a", sizeWithMin));
        assertTrue(sizeValidator.isCorrect("ab", sizeWithMin));
        assertTrue(sizeValidator.isCorrect("abcasadsfdsfsdfsdfwqrweqrewrew", sizeWithMin));

        assertTrue(sizeValidator.isCorrect("", sizeWithMax));
        assertTrue(sizeValidator.isCorrect("ab", sizeWithMax));
        assertTrue(sizeValidator.isCorrect("abc", sizeWithMax));
    }

    @Test
    public void wrongTooSmallStringSize() {
        assertFalse(sizeValidator.isCorrect("", sizeWithMinMax));

        assertFalse(sizeValidator.isCorrect("", sizeWithMin));
    }

    @Test
    public void wrongTooBigStringSize() {
        assertFalse(sizeValidator.isCorrect("abcd", sizeWithMinMax));

        assertFalse(sizeValidator.isCorrect("abcd", sizeWithMax));
    }

    @Test
    public void correctListSize() {
        List list = mock(ArrayList.class);
        checkCorrectCollectionSize(list);
    }

    @Test
    public void wrongListSize() {
        LinkedList list = mock(LinkedList.class);
        checkWrongCollectionSize(list);
    }

    @Test
    public void correctMapSize() {
        Map<Integer, String> map = mock(HashMap.class);
        checkCorrectMapSize(map);
    }

    @Test
    public void wrongMapSize() {
        Map<Integer, String> map = mock(HashMap.class);
        checkWrongMapSize(map);
    }

    @Test
    public void correctSetSize() {
        Set set = mock(Set.class);
        checkCorrectCollectionSize(set);
    }

    @Test
    public void wrongSetSize() {
        Set set = mock(Set.class);
        checkWrongCollectionSize(set);
    }

    @Test
    public void correctQueueSize() {
        Queue queue = mock(Queue.class);
        checkCorrectCollectionSize(queue);
    }

    @Test
    public void wrongQueueSize() {
        Queue queue = mock(Queue.class);
        checkWrongCollectionSize(queue);
    }

    @Test
    public void correctVectorSize() {
        Vector vector = mock(Vector.class);
        checkCorrectCollectionSize(vector);
    }

    @Test
    public void wrongVectorSize() {
        Vector vector = mock(Vector.class);
        checkWrongCollectionSize(vector);
    }

    @Test
    public void correctObjectArraySize() {
        for (Integer correctLength : CORRECT_SIZES) {
            Object[] array = new Object[correctLength];
            assertCorrectSize(array);
        }
    }

    @Test
    public void wrongObjectArraySize() {
        checkWrongSizeInLowerBoundary(new Object[Collections.min(CORRECT_SIZES) - 1]);

        checkWrongSizeInUpperBoundary(new Object[Collections.max(CORRECT_SIZES) + 1]);
    }

    @Test
    public void correctStringArraySize() {
        for (Integer correctLength : CORRECT_SIZES) {
            String[] array = new String[correctLength];
            assertCorrectSize(array);
        }
    }

    @Test
    public void wrongStringArraySize() {
        checkWrongSizeInLowerBoundary(new String[Collections.min(CORRECT_SIZES) - 1]);

        checkWrongSizeInUpperBoundary(new String[Collections.max(CORRECT_SIZES) + 1]);
    }

    @Test(expected = WrongFieldTypeException.class)
    public void wrongFieldType() {
        sizeValidator.isCorrect(2, sizeWithMinMax);
    }

    @Test
    public void nullObject() {
        List<Size> annotations = Lists.newArrayList(sizeWithMinMax, sizeWithMin, sizeWithMax);
        for (Annotation annotation : annotations)
            assertFalse(sizeValidator.isCorrect(null, annotation));
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void wrongAnnotationType() {
        sizeValidator.isCorrect("abd", mock(NotNull.class));
    }

    @Test
    public void noErrorMessages() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage("asd", sizeWithMinMax);
        assertFalse(errorsOpt.isPresent());
    }

    @Test
    public void errorMessageForStringWithLowerLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage("", sizeWithMin);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE,
                        Collections.min(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test
    public void errorMessageForStringWithUpperLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage("abcd", sizeWithMax);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE,
                        Collections.max(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test
    public void errorMessageForStringWithLowerAndUpperLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage("abcd", sizeWithMinMax);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE
                            + " " + SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE,
                        Collections.min(CORRECT_SIZES),
                        Collections.max(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test
    public void errorMessageForCollectionWithLowerLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage(Lists.newArrayList(), sizeWithMin);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE,
                        Collections.min(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test
    public void errorMessageForCollectionWithUpperLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage(Lists.newArrayList("", "", "", ""), sizeWithMax);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE,
                        Collections.max(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test
    public void errorMessageForCollectionWithLowerAndUpperLimit() {
        Optional<String> errorsOpt = sizeValidator.getErrorMessage(Lists.newArrayList(), sizeWithMinMax);
        assertTrue(errorsOpt.isPresent());
        assertEquals(
                String.format(
                        SIZE_ERROR_NOT_PROPER_SIZE + " " + SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE
                                + " " + SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE,
                        Collections.min(CORRECT_SIZES),
                        Collections.max(CORRECT_SIZES)),
                errorsOpt.get());
    }

    @Test(expected = WrongAnnotationTypeException.class)
    public void errorMessageWithWrongAnnotation() {
        sizeValidator.getErrorMessage(Lists.newArrayList(), mock(NotNull.class));
    }

    @Test(expected = WrongFieldTypeException.class)
    public void errorMessageWithWrongFieldType() {
        sizeValidator.getErrorMessage(12L, sizeWithMinMax);
    }

    private void checkCorrectMapSize(Map<Integer, String> map) {
        for (Integer correctSize : CORRECT_SIZES) {
            when(map.size()).thenReturn(correctSize);
            assertMapCorrectSizes(map);
        }
    }

    private void checkWrongMapSize(Map<Integer, String> map) {
        when(map.size()).thenReturn(Collections.min(CORRECT_SIZES) - 1);
        checkWrongSizeInLowerBoundary(map);

        when(map.size()).thenReturn(Collections.max(CORRECT_SIZES) + 1);
        checkWrongSizeInUpperBoundary(map);
    }

    private void checkCorrectCollectionSize(Collection collection) {
        for (Integer correctSize : CORRECT_SIZES) {
            when(collection.size()).thenReturn(correctSize);
            assertCollectionCorrectSizes(collection);
        }
    }

    private void checkWrongCollectionSize(Collection collection) {
        when(collection.size()).thenReturn(Collections.min(CORRECT_SIZES) - 1);
        checkWrongSizeInLowerBoundary(collection);

        when(collection.size()).thenReturn(Collections.max(CORRECT_SIZES) + 1);
        checkWrongSizeInUpperBoundary(collection);
    }

    private void checkWrongSizeInLowerBoundary(Object object) {
        assertFalse(sizeValidator.isCorrect(object, sizeWithMinMax));
        assertFalse(sizeValidator.isCorrect(object, sizeWithMin));
    }

    private void checkWrongSizeInUpperBoundary(Object object) {
        assertFalse(sizeValidator.isCorrect(object, sizeWithMinMax));
        assertFalse(sizeValidator.isCorrect(object, sizeWithMax));
    }

    private void assertCorrectSize(Object object) {
        assertTrue(sizeValidator.isCorrect(object, sizeWithMinMax));
        assertTrue(sizeValidator.isCorrect(object, sizeWithMin));
        assertTrue(sizeValidator.isCorrect(object, sizeWithMax));
    }

    private void assertMapCorrectSizes(Map<Integer, String> map) {
        assertTrue(sizeValidator.isCorrect(map, sizeWithMinMax));
        assertTrue(sizeValidator.isCorrect(map, sizeWithMin));
        assertTrue(sizeValidator.isCorrect(map, sizeWithMax));
    }

    private void assertCollectionCorrectSizes(Collection collection) {
        assertTrue(sizeValidator.isCorrect(collection, sizeWithMinMax));
        assertTrue(sizeValidator.isCorrect(collection, sizeWithMin));
        assertTrue(sizeValidator.isCorrect(collection, sizeWithMax));
    }

    private void createSizeWithMinMaxMock() {
        sizeWithMinMax = mock(Size.class);
        when(sizeWithMinMax.min()).thenReturn(Collections.min(CORRECT_SIZES));
        when(sizeWithMinMax.max()).thenReturn(Collections.max(CORRECT_SIZES));
    }

    private void createSizeWithMaxMock() {
        sizeWithMax = mock(Size.class);
        when(sizeWithMax.max()).thenReturn(Collections.max(CORRECT_SIZES));
        when(sizeWithMax.min()).thenReturn(0);
    }

    private void createSizeWithMinMock() {
        sizeWithMin = mock(Size.class);
        when(sizeWithMin.min()).thenReturn(Collections.min(CORRECT_SIZES));
        when(sizeWithMin.max()).thenReturn(Integer.MAX_VALUE);
    }
}