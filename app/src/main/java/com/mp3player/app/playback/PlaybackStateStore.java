package com.mp3player.app.playback;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/** 再生状態（PlaybackSnapshot）を端末に保存・読み込みする */
public class PlaybackStateStore {

    private static final String PREFS_NAME = "playback_state";
    private static final String KEY_SNAPSHOT = "snapshot";

    private final SharedPreferences prefs;

    public PlaybackStateStore(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(PlaybackSnapshot snapshot) {
        prefs.edit().putString(KEY_SNAPSHOT, snapshot.encode()).apply();
    }

    @Nullable
    public PlaybackSnapshot load() {
        return PlaybackSnapshot.decode(prefs.getString(KEY_SNAPSHOT, null));
    }
}
