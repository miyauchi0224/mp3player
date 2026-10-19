package com.mp3player.app.ui.common;

/** 曲一覧の表示方法 */
public enum SongViewMode {
    LIST("list"),
    GRID("grid");

    private final String key;

    SongViewMode(String key) {
        this.key = key;
    }

    /** 保存用のキー */
    public String key() {
        return key;
    }

    public SongViewMode toggle() {
        return this == LIST ? GRID : LIST;
    }

    /** 保存されたキーから復元する。未保存・不明な値はリスト表示とする */
    public static SongViewMode fromKey(String key) {
        for (SongViewMode mode : values()) {
            if (mode.key.equals(key)) {
                return mode;
            }
        }
        return LIST;
    }
}
