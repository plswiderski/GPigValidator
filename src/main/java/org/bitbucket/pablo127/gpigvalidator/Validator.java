package org.bitbucket.pablo127.gpigvalidator;

import com.google.common.collect.ImmutableMap;
import org.bitbucket.pablo127.gpigvalidator.exception.ValidationException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongAnnotationTypeException;
import org.bitbucket.pablo127.gpigvalidator.exception.WrongFieldTypeException;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class Validator {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    // TODO maybe customized annotations later
    // TODO annotations with documentations
    // TODO Size for collections

    private Validator() {
    }

    /**
     * Check if object is correct in case of constraints.
     * @param objectToValidate
     * @return
     * @throws IllegalAccessException
     */
    public static boolean isCorrectObject(Object objectToValidate) throws IllegalAccessException {
        // TODO
        Class clazz = objectToValidate.getClass();
        System.out.println("XXX "+ clazz.getName());

        System.out.println("class fields number "+clazz.getDeclaredFields().length);
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                field.setAccessible(true);
                System.out.println("Field "+field.get(objectToValidate) + " annotation "
                        + annotation.annotationType().toString());

                Object objectValue = field.get(objectToValidate);

                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (!analyzeCorrectness(field, annotation, objectValue, annotationType))
                    return false;
            }
        }
        return true;
    }

    public static boolean isCorrectField() {
        // TODO
        return false;
    }

    /**
     *
     * @param objectToValidate
     * @param exceptionToThrow
     */
    public static void validateObject(Object objectToValidate, Class<? extends ValidationException> exceptionToThrow) {
        // TODO

    }

    public static void validateField() {
        // TODO
    }

    /**
     *
     * @return key - name of field; value - error message
     */
    public static Map<String, String> getValidationErrorMessages() {
        // TODO
        return ImmutableMap.of();
    }

    /**
     *
     * @return error message for all fields with bugs
     */
    public static String getValidationErrorMessage() {
        // TODO
        return "";
    }

    private static boolean analyzeCorrectness(Field field, Annotation annotation, Object objectValue,
                                              Class<? extends Annotation> annotationType) {
        if (annotationType.equals(NotNull.class)) {
            return isNotNull(objectValue);
        } else if (annotationType.equals(Size.class)) {
            Size sizeAnnotation = (Size) annotation;
            return hasCorrectSize(objectValue, field.getType(), sizeAnnotation);
        } else if (annotationType.equals(Email.class)) {
            Email emailAnnotation = (Email) annotation;
            return isCorrectEmailField(objectValue, field.getType(), emailAnnotation);
        }
        // TODO more constraints
        throw new WrongAnnotationTypeException();
    }

    private static boolean isCorrectEmailField(Object objectValue, Class<?> type, Email emailAnnotation) {
        if (type.equals(String.class)) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            return pattern.matcher((String) objectValue)
                    .matches();
        }
        throw new WrongFieldTypeException();
    }

    private static boolean hasCorrectSize(Object objectValue, Class<?> type, Size sizeAnnotation) {
        int size = -1;
        if (type.equals(String.class))
            size = ((String) objectValue).length();
        else if (type.equals(Map.class))
            size = ((Map) objectValue).size();
        else if (type.equals(List.class))
            size = ((List) objectValue).size();

        if (size == -1)
            throw new WrongFieldTypeException();
        return sizeAnnotation.min() <= size && size <= sizeAnnotation.max();
    }

    private static boolean isNotNull(Object o) {
        return o != null;
    }
}
