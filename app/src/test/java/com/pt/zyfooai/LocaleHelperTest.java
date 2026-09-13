package com.pt.zyfooai;

import static org.junit.Assert.assertEquals;

import com.pt.zyfooai.utils.LocaleHelper;

import org.junit.Test;

public class LocaleHelperTest {

    @Test
    public void localeCodeFromLanguageTitle_detectsHindi() {
        assertEquals("hi", LocaleHelper.localeCodeFromLanguageTitle("Hindi"));
        assertEquals("hi", LocaleHelper.localeCodeFromLanguageTitle("हिंदी"));
    }

    @Test
    public void localeCodeFromLanguageTitle_defaultsEnglish() {
        assertEquals("en", LocaleHelper.localeCodeFromLanguageTitle("English"));
        assertEquals("en", LocaleHelper.localeCodeFromLanguageTitle(null));
    }

    @Test
    public void localeCodeFromLanguageTitle_detectsMarathi() {
        assertEquals("mr", LocaleHelper.localeCodeFromLanguageTitle("Marathi"));
    }
}
