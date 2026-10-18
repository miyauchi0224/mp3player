package com.mp3player.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.UserPreferences;
import com.mp3player.app.playback.PlayerController;

@HiltAndroidApp
public class MainApplication extends Application {

    @Inject
    PlayerController playerController;

    @Inject
    MusicRepository musicRepository;

    @Inject
    UserPreferences userPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        playerController.setTrackStartedListener(musicRepository::recordPlay);
        applyDarkMode();
        applyLanguage();
    }

    private void applyDarkMode() {
        switch (userPreferences.getDarkMode()) {
            case UserPreferences.DARK_MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case UserPreferences.DARK_MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void applyLanguage() {
        String language = userPreferences.getLanguage();
        if (UserPreferences.LANGUAGE_SYSTEM.equals(language)) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language));
        }
    }
}
