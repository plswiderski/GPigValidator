package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;

import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

public class NotNullValidator implements ConstraintValidator {

    @Override
    public Class getAnnotationType() {
        return NotNull.class;
    }

    @Override
    public boolean isCorrect(Object objectValue, Annotation annotation) {
        checkPreconditions(annotation);

        return objectValue != null;
    }

    @Override
    public Optional<String> getErrorMessage(Object objectValue, Annotation annotation) {
        if (isCorrect(objectValue, annotation))
            return Optional.absent();

        return Optional.of(TranslationConfig.getTranslation(Message.NOT_NULL_ERROR_MESSAGE));
    }

    private void checkPreconditions(Annotation annotation) {
        if (!(annotation instanceof NotNull))
            throw new WrongAnnotationTypeException();
    }
}
