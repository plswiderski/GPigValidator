package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongFieldTypeException;
import org.bitbucket.pablo127.gpigvalidator.util.StringBuilderUtil;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

public class SizeValidator implements ConstraintValidator {

    @Override
    public Class getAnnotationType() {
        return Size.class;
    }

    @Override
    public boolean isCorrect(Object objectValue, Annotation annotation) {
        if (areWrongPreconditions(objectValue, annotation))
            return false;

        Size sizeAnnotation = (Size) annotation;

        int size = getSize(objectValue);
        return sizeAnnotation.min() <= size && size <= sizeAnnotation.max();
    }

    @Override
    public Optional<String> getErrorMessage(Object objectValue, Annotation annotation) {
        if (isCorrect(objectValue, annotation))
            return Optional.absent();

        Size sizeAnnotation = (Size) annotation;

        if (objectValue instanceof String) {
            return Optional.of(createErrorMessageForString(sizeAnnotation));
        } else {
            return Optional.of(createErrorMessageForCollection(sizeAnnotation));
        }
    }

    private String createErrorMessageForString(Size sizeAnnotation) {
        return createErrorMessage(
                sizeAnnotation,
                Message.SIZE_ERROR_FOR_STRING_TOO_SHORT_FORMATTED_MESSAGE,
                Message.SIZE_ERROR_FOR_STRING_TOO_LONG_FORMATTED_MESSAGE);
    }

    private String createErrorMessageForCollection(Size sizeAnnotation) {
        return createErrorMessage(
                sizeAnnotation,
                Message.SIZE_ERROR_FOR_COLLECTIONS_TOO_SHORT_FORMATTED_MESSAGE,
                Message.SIZE_ERROR_FOR_COLLECTIONS_TOO_LONG_FORMATTED_MESSAGE);
    }

    private String createErrorMessage(Size sizeAnnotation, Message errorForLowerBound, Message errorForUpperBound) {
        StringBuilder errorMessageBuilder = new StringBuilder();

        appendNotProperSizeErrorMessage(errorMessageBuilder);
        if (sizeAnnotation.min() != 0) {
            appendFormattedErrorMessage(
                    errorMessageBuilder,
                    errorForLowerBound,
                    sizeAnnotation.min());
        }
        if (sizeAnnotation.max() != Integer.MAX_VALUE) {
            appendFormattedErrorMessage(
                    errorMessageBuilder,
                    errorForUpperBound,
                    sizeAnnotation.max());
        }

        return errorMessageBuilder.toString();
    }

    private void appendFormattedErrorMessage(StringBuilder errorMessageBuilder, Message message, int value) {
        StringBuilderUtil.appendWithSpaceIfNeeded(
                errorMessageBuilder,
                String.format(
                        TranslationConfig.getTranslation(message),
                        value));
    }

    private void appendNotProperSizeErrorMessage(StringBuilder errorMessageBuilder) {
        StringBuilderUtil.appendWithSpaceIfNeeded(
                errorMessageBuilder,
                TranslationConfig.getTranslation(Message.SIZE_ERROR_NOT_PROPER_SIZE));
    }

    private boolean areWrongPreconditions(Object objectValue, Annotation annotation) {
        if (objectValue == null)
            return true;
        if (!(annotation instanceof Size))
            throw new WrongAnnotationTypeException();

        return false;
    }

    private int getSize(Object objectValue) {
        if (objectValue instanceof String)
            return ((String) objectValue).length();
        else if (objectValue instanceof Object[])
            return ((Object[]) objectValue).length;
        else if (objectValue instanceof Collection)
            return ((Collection) objectValue).size();
        else if (objectValue instanceof Map)
            return ((Map) objectValue).size();
        else
            throw new WrongFieldTypeException();
    }
}
