package com.mp3player.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import com.mp3player.app.ui.common.SongViewMode;

@Singleton
public class UserPreferences {

    public static final String DARK_MODE_SYSTEM = "system";
    public static final String DARK_MODE_LIGHT = "light";
    public static final String DARK_MODE_DARK = "dark";

    public static final String LANGUAGE_SYSTEM = "system";
    public static final String LANGUAGE_JA = "ja";
    public static final String LANGUAGE_EN = "en";

    private static final String PREFS_NAME = "user_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_TARGET_FOLDERS = "target_folders";
    private static final String KEY_SONG_VIEW_MODE = "song_view_mode";
    private static final String KEY_LYRICS_FOLDER = "lyrics_folder";
    private static final String KEY_BATTERY_PROMPT_SHOWN = "battery_prompt_shown";

    private final SharedPreferences prefs;
    private final List<Runnable> targetFoldersListeners = new CopyOnWriteArrayList<>();
    private final MutableLiveData<SongViewMode> songViewMode;

    @Inject
    public UserPreferences(@ApplicationContext Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // 初期値はコンストラクタで渡す（setValueはメインスレッド専用で、ここは別スレッドから呼ばれ得るため）
        songViewMode = new MutableLiveData<>(SongViewMode.fromKey(prefs.getString(KEY_SONG_VIEW_MODE, null)));
    }

    public void addTargetFoldersListener(Runnable listener) {
        targetFoldersListeners.add(listener);
    }

    public void removeTargetFoldersListener(Runnable listener) {
        targetFoldersListeners.remove(listener);
    }

    public String getDarkMode() {
        return prefs.getString(KEY_DARK_MODE, DARK_MODE_SYSTEM);
    }

    public void setDarkMode(String mode) {
        prefs.edit().putString(KEY_DARK_MODE, mode).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, LANGUAGE_SYSTEM);
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public Set<String> getTargetFolders() {
        return new HashSet<>(prefs.getStringSet(KEY_TARGET_FOLDERS, Collections.emptySet()));
    }

    public void setTargetFolders(Set<String> folders) {
        prefs.edit().putStringSet(KEY_TARGET_FOLDERS, folders).apply();
        for (Runnable listener : targetFoldersListeners) {
            listener.run();
        }
    }

    /** 曲一覧の表示方法（リスト／アイコン）。全ての曲一覧画面で共有する */
    public LiveData<SongViewMode> getSongViewMode() {
        return songViewMode;
    }

    public void setSongViewMode(SongViewMode mode) {
        prefs.edit().putString(KEY_SONG_VIEW_MODE, mode.key()).apply();
        songViewMode.setValue(mode);
    }

    /** 歌詞（.lrc）を探すフォルダ（SAFのツリーURI）。未設定なら null */
    public String getLyricsFolder() {
        return prefs.getString(KEY_LYRICS_FOLDER, null);
    }

    public void setLyricsFolder(String treeUri) {
        prefs.edit().putString(KEY_LYRICS_FOLDER, treeUri).apply();
    }

    public boolean isBatteryPromptShown() {
        return prefs.getBoolean(KEY_BATTERY_PROMPT_SHOWN, false);
    }

    public void setBatteryPromptShown() {
        prefs.edit().putBoolean(KEY_BATTERY_PROMPT_SHOWN, true).apply();
    }
}
