package org.bitbucket.pablo127.gpigvalidator.constraint;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;

public interface ConstraintValidator {

    Class getAnnotationType();

    boolean isCorrect(Object objectValue, Annotation annotation);

    Optional<String> getErrorMessage(Object objectValue, Annotation annotation);
}
