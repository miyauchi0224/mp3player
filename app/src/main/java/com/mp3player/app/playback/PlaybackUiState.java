package com.mp3player.app.playback;

import android.net.Uri;

import androidx.media3.common.Player;

public class PlaybackUiState {
    public boolean hasMedia = false;
    public long songId = -1;
    public String title = "";
    public String artist = "";
    public Uri artworkUri;
    public boolean isPlaying = false;
    public long duration = 0;
    public int repeatMode = Player.REPEAT_MODE_OFF;
    public float volume = 1f;
    public float speed = 1f;
    public boolean sleepTimerActive = false;
    public long sleepTimerEndAt = -1;
}
