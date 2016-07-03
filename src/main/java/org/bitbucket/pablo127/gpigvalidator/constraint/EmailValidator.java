package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongFieldTypeException;
import org.hibernate.validator.constraints.Email;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public Class getAnnotationType() {
        return Email.class;
    }

    @Override
    public boolean isCorrect(Object objectValue, Annotation annotation) {
        if (areWrongPreconditions(objectValue, annotation))
            return false;

        if (objectValue instanceof String) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            return pattern.matcher((String) objectValue)
                    .matches();
        }
        throw new WrongFieldTypeException();
    }

    @Override
    public Optional<String> getErrorMessage(Object objectValue, Annotation annotation) {
        if (isCorrect(objectValue, annotation))
            return Optional.absent();

        return Optional.of(
                String.format(TranslationConfig.getTranslation(Message.EMAIL_ERROR_MESSAGE), objectValue));
    }

    private boolean areWrongPreconditions(Object objectValue, Annotation annotation) {
        if (objectValue == null)
            return true;
        if (!(annotation instanceof Email))
            throw new WrongAnnotationTypeException();

        return false;
    }
}
