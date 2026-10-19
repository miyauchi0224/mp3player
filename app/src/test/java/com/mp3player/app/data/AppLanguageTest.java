package com.mp3player.app.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** 表示言語の設定値を、AppCompat に渡す言語タグにする（空文字は「端末の設定に従う」） */
public class AppLanguageTest {

    @Test
    public void followSystem_isEmptyTag() {
        assertEquals("", AppLanguage.languageTags(UserPreferences.LANGUAGE_SYSTEM));
    }

    @Test
    public void japaneseAndEnglish_areTheirTags() {
        assertEquals("ja", AppLanguage.languageTags(UserPreferences.LANGUAGE_JA));
        assertEquals("en", AppLanguage.languageTags(UserPreferences.LANGUAGE_EN));
    }

    @Test
    public void unknownOrMissingSetting_followsSystem() {
        assertEquals("", AppLanguage.languageTags("fr-XX-broken"));
        assertEquals("", AppLanguage.languageTags(null));
    }
}
