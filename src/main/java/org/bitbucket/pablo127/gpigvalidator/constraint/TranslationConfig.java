package org.bitbucket.pablo127.gpigvalidator.constraint;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class TranslationConfig {

    protected static Locale locale;

    private TranslationConfig() {
    }

    public static void changeLocale(Locale locale) {
        TranslationConfig.locale = locale;
    }

    protected static String getTranslation(Message message) {
        return getResourceBundle().getString(message.getPropertyName());
    }

    private static ResourceBundle getResourceBundle() {
        try {
            return ResourceBundle.getBundle(
                    "gpigvalidator",
                    locale == null
                            ? Locale.getDefault()
                            : locale);
        } catch (MissingResourceException e) {
            return ResourceBundle.getBundle("gpigvalidator", new Locale("en", "US"));
        }
    }
}
