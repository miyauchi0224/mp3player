package com.mp3player.app.data;

/** 表示言語の設定値と、AppCompat に渡す言語タグの対応 */
public final class AppLanguage {

    private AppLanguage() {
    }

    /** @return 言語タグ。「端末の設定に従う」や不明な値は空文字 */
    public static String languageTags(String setting) {
        if (UserPreferences.LANGUAGE_JA.equals(setting)) {
            return "ja";
        }
        if (UserPreferences.LANGUAGE_EN.equals(setting)) {
            return "en";
        }
        return "";
    }
}
