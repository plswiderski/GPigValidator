package org.bitbucket.pablo127.gpigvalidator.constraint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Locale;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {TranslationConfig.class})
public class TranslationConfigTest {

    private static final String ENGLISH = "English";

    @Before
    public void setUp() {
        TranslationConfig.locale = null;
    }

    @Test
    public void languageMessageForPolish() {
        Locale.setDefault(new Locale("pl", "PL"));
        assertLanguage("polski");
    }

    @Test
    public void languageMessageForEnglish() {
        Locale.setDefault(new Locale("en", "US"));
        assertLanguage(ENGLISH);
    }

    @Test
    public void messageInUnsupportedLanguage() {
        Locale.setDefault(new Locale("fr", "FR"));
        assertLanguage(ENGLISH);
    }

    @Test
    public void changeLocale() {
        Locale.setDefault(new Locale("pl", "PL"));
        TranslationConfig.changeLocale(new Locale("en", "US"));
        assertLanguage(ENGLISH);
    }

    private void assertLanguage(String expectedLanguage) {
        assertEquals(expectedLanguage, TranslationConfig.getTranslation(Message.LANGUAGE));
    }
}