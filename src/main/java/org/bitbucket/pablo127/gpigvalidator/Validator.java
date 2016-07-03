package org.bitbucket.pablo127.gpigvalidator;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bitbucket.pablo127.gpigvalidator.constraint.ConstraintValidator;
import org.bitbucket.pablo127.gpigvalidator.constraint.EmailValidator;
import org.bitbucket.pablo127.gpigvalidator.constraint.NotNullValidator;
import org.bitbucket.pablo127.gpigvalidator.constraint.SizeValidator;
import org.bitbucket.pablo127.gpigvalidator.exception.*;
import org.bitbucket.pablo127.gpigvalidator.util.StringBuilderUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Validator {

    private Validator() {
    }

    private static List<ConstraintValidator> getValidators() {
        return ImmutableList.of(
                new NotNullValidator(),
                new EmailValidator(),
                new SizeValidator()
        );
    }

    /**
     * Check if object is correct in case of constraints.
     * @param objectToValidate
     * @return true if object is correct; false otherwise
     * @throws InternalException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     */
    public static boolean isCorrectObject(Object objectToValidate) {
        Class clazz = objectToValidate.getClass();
        boolean result = true;
        for (Field field : clazz.getDeclaredFields())
            result &= isCorrectFieldForAnnotations(objectToValidate, field);

        return result;
    }

    /**
     * Validate object. If it is incorrect throw specific ValidationException.
     * @param objectToValidate
     * @param exceptionToThrow - exception specific type to throw after incorrect object
     * @throws InternalException
     * @throws ValidationException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     */
    public static void validateObject(Object objectToValidate, Class<? extends ValidationException> exceptionToThrow) {
        Class clazz = objectToValidate.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Optional<String> errorsOpt = checkFieldForErrors(objectToValidate, field);
            if (errorsOpt.isPresent())
                throwValidationException(exceptionToThrow, errorsOpt.get());
        }
    }

    /**
     * Validate object. If it is incorrect throw ValidationException.
     * @param objectToValidate
     * @throws InternalException
     * @throws ValidationException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     */
    public static void validateObject(Object objectToValidate) {
        validateObject(objectToValidate, ValidationException.class);
    }

    /**
     * Validate specific field in object. If it is incorrect throw ValidationException.
     * @param objectToValidate
     * @param fieldName - name of field to validate
     * @throws InternalException
     * @throws ValidationException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     * @throws NotExistingField
     */
    public static void validateField(Object objectToValidate, String fieldName) {
        validateField(objectToValidate, fieldName, ValidationException.class);
    }

    /**
     * Validate specific field in object. If it is incorrect throw specific ValidationException.
     * @param objectToValidate
     * @param fieldName - name of field to validate.
     * @param exceptionToThrow - exception specific type to throw after incorrect field in object.
     * @throws InternalException
     * @throws ValidationException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     * @throws NotExistingField
     */
    public static void validateField(Object objectToValidate, String fieldName,
                                     Class<? extends ValidationException> exceptionToThrow) {
        Class clazz = objectToValidate.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                Optional<String> errorsOpt = checkFieldForErrors(objectToValidate, field);
                if (errorsOpt.isPresent())
                    throwValidationException(exceptionToThrow, errorsOpt.get());
                else
                    return;
            }
        }
        throw new NotExistingField();
    }

    /**
     * Validate object and return map with incorrect messages.
     * @param objectToValidate
     * @return immutable map with: key - name of field; value - error message
     * @throws InternalException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     */
    public static Map<String, String> validateWithErrorMessages(Object objectToValidate) {
        final Map<String, String> errorsMap = new HashMap<>();

        validate(objectToValidate, new ErrorCollectStrategy() {
            @Override
            public void collectError(String fieldName, String errorMessage) {
                errorsMap.put(fieldName, errorMessage);
            }
        });

        return ImmutableMap.copyOf(errorsMap);
    }

    /**
     * Validate object and return string with incorrect messages.
     * @param objectToValidate
     * @return String with all error messages.
     * @throws InternalException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     */
    public static Optional<String> validateWithErrorMessage(Object objectToValidate) {
        final StringBuilder messageBuilder = new StringBuilder();

        validate(objectToValidate, new ErrorCollectStrategy() {
            @Override
            public void collectError(String fieldName, String errorMessage) {
                StringBuilderUtil.appendWithSpaceIfNeeded(messageBuilder, errorMessage);
            }
        });

        return messageBuilder.length() > 0
                ? Optional.of(messageBuilder.toString())
                : Optional.<String>absent();
    }

    /**
     * Method checks if specific field from object is correct.
     * @param objectToValidate
     * @param fieldName
     * @return true if field is correct; false otherwise
     * @throws InternalException
     * @throws WrongAnnotationTypeException
     * @throws WrongFieldTypeException
     * @throws NotExistingField
     */
    public static boolean isCorrectField(Object objectToValidate, String fieldName) {
        Class clazz = objectToValidate.getClass();
        Optional<Field> fieldOpt = getField(clazz.getDeclaredFields(), fieldName);
        if (fieldOpt.isPresent())
            return isCorrectFieldForAnnotations(objectToValidate, fieldOpt.get());

        throw new NotExistingField();
    }

    protected static Optional<String> checkAnnotationConstraintError(Object objectToValidate, Field field,
                                                                     Annotation annotation) {
        try {
            field.setAccessible(true);

            Object fieldValue = field.get(objectToValidate);

            Optional<String> errorOpt = checkErrorInFieldForAnnotation(annotation, fieldValue);
            if (errorOpt.isPresent())
                return Optional.of(String.format("Field '%s' %s", field.getName(), errorOpt.get()));
            return errorOpt;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new InternalException(e);
        }
    }

    private static void validate(Object objectToValidate, ErrorCollectStrategy errorCollectStrategy) {
        Class clazz = objectToValidate.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            Optional<String> errorsOpt = checkFieldForErrors(objectToValidate, field);
            if (errorsOpt.isPresent())
                errorCollectStrategy.collectError(field.getName(), errorsOpt.get());
        }
    }

    private static Optional<String> checkFieldForErrors(Object objectToValidate, Field field) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        for (Annotation annotation : field.getAnnotations()) {
            Optional<String> annotationErrorOpt = checkAnnotationConstraintError(objectToValidate, field, annotation);
            if (annotationErrorOpt.isPresent())
                StringBuilderUtil.appendWithSpaceIfNeeded(errorMessageBuilder, annotationErrorOpt.get());
        }

        return errorMessageBuilder.length() > 0
                ? Optional.of(errorMessageBuilder.toString())
                : Optional.<String>absent();
    }

    private static boolean isAnnotationConstraintCorrect(Object objectToValidate, Field field, Annotation annotation) {
        return !checkAnnotationConstraintError(objectToValidate, field, annotation)
                .isPresent();
    }

    private static void throwValidationException(Class<? extends ValidationException> exceptionToThrow,
                                                 String messageToThrow) {
        try {
            throw exceptionToThrow.getDeclaredConstructor(String.class)
                    .newInstance(messageToThrow);
        } catch (ReflectiveOperationException e) {
            throw new InternalException("ValidationException class is incorrect. It has to be public outer class.", e);
        }
    }

    private static Optional<Field> getField(Field[] fields, String fieldName) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName))
                return Optional.of(field);
        }

        return Optional.absent();
    }

    private static Optional<String> checkErrorInFieldForAnnotation(Annotation annotation, Object fieldValue) {
        Optional<ConstraintValidator> validatorOpt = getConstraintValidatorOptional(annotation.annotationType());

        if (validatorOpt.isPresent())
            return validatorOpt.get().getErrorMessage(fieldValue, annotation);

        throw new WrongAnnotationTypeException("Currently no such an annotation is supported.");
    }

    private static boolean isCorrectFieldForAnnotations(Object objectToValidate, Field field) {
        boolean result = true;
        for (Annotation annotation : field.getAnnotations())
            result &= isAnnotationConstraintCorrect(objectToValidate, field, annotation);

        return result;
    }

    private static Optional<ConstraintValidator> getConstraintValidatorOptional(
            final Class<? extends Annotation> annotationType) {
        return FluentIterable.from(getValidators())
                .firstMatch(new Predicate<ConstraintValidator>() {
                    @Override
                    public boolean apply(ConstraintValidator validator) {
                        return annotationType.equals(validator.getAnnotationType());
                    }
                });
    }

    private interface ErrorCollectStrategy {
        void collectError(String fieldName, String errorMessage);
    }
}
