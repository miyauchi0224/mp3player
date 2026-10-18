package com.mp3player.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

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

    private final SharedPreferences prefs;
    private final List<Runnable> targetFoldersListeners = new CopyOnWriteArrayList<>();

    @Inject
    public UserPreferences(@ApplicationContext Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
}
