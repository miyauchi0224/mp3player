package com.mp3player.app.playback;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import android.content.ContentUris;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionResult;
import androidx.media3.session.SessionToken;

import java.util.function.Consumer;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import com.mp3player.app.data.local.entity.Song;

@Singleton
public class PlayerController {

    private static final String TAG = "PlayerController";

    private final Context context;
    private MediaController mediaController;
    private final MutableLiveData<PlaybackUiState> state = new MutableLiveData<>(new PlaybackUiState());
    private final List<Runnable> pendingActions = new ArrayList<>();
    private TrackStartedListener trackStartedListener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable sleepTimerRunnable;
    private long sleepTimerEndAt = -1;

    @Inject
    public PlayerController(@ApplicationContext Context context) {
        this.context = context;
        connect();
    }

    public interface TrackStartedListener {
        void onTrackStarted(long songId);
    }

    public void setTrackStartedListener(TrackStartedListener listener) {
        this.trackStartedListener = listener;
    }

    private void connect() {
        SessionToken token = new SessionToken(context, new ComponentName(context, PlaybackService.class));
        ListenableFuture<MediaController> future = new MediaController.Builder(context, token).buildAsync();
        future.addListener(() -> {
            try {
                mediaController = future.get();
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        updateState();
                    }

                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        updateState();
                        if (mediaItem != null && mediaItem.mediaId != null && !mediaItem.mediaId.isEmpty()) {
                            long songId = Long.parseLong(mediaItem.mediaId);
                            if (trackStartedListener != null) {
                                trackStartedListener.onTrackStarted(songId);
                            }
                        }
                    }

                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        updateState();
                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {
                        updateState();
                    }

                    @Override
                    public void onVolumeChanged(float volume) {
                        updateState();
                    }

                    @Override
                    public void onPlaybackParametersChanged(@androidx.annotation.NonNull PlaybackParameters playbackParameters) {
                        updateState();
                    }
                });
                for (Runnable action : pendingActions) {
                    action.run();
                }
                pendingActions.clear();
                updateState();
            } catch (Exception e) {
                Log.e(TAG, "MediaControllerへの接続に失敗しました", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void runOrQueue(Runnable action) {
        if (mediaController != null) {
            action.run();
        } else {
            pendingActions.add(action);
        }
    }

    public void playQueue(List<Song> songs, int startIndex) {
        runOrQueue(() -> {
            List<MediaItem> items = new ArrayList<>();
            for (Song song : songs) {
                items.add(toMediaItem(song));
            }
            mediaController.setMediaItems(items, startIndex, 0);
            mediaController.prepare();
            mediaController.play();
        });
    }

    private MediaItem toMediaItem(Song song) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(getAlbumArtUri(song.albumId))
                .build();
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(song.id))
                .setUri(getSongContentUri(song.id))
                .setMediaMetadata(metadata)
                .build();
    }

    public static Uri getSongContentUri(long songId) {
        return ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/media"), songId);
    }

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public void togglePlayPause() {
        runOrQueue(() -> {
            if (mediaController.isPlaying()) {
                mediaController.pause();
            } else {
                mediaController.play();
            }
        });
    }

    public void skipToNext() {
        runOrQueue(() -> mediaController.seekToNext());
    }

    public void skipToPrevious() {
        runOrQueue(() -> mediaController.seekToPrevious());
    }

    public void seekTo(long positionMs) {
        runOrQueue(() -> mediaController.seekTo(positionMs));
    }

    public void cycleRepeatMode() {
        runOrQueue(() -> {
            int current = mediaController.getRepeatMode();
            int next;
            if (current == Player.REPEAT_MODE_OFF) {
                next = Player.REPEAT_MODE_ALL;
            } else if (current == Player.REPEAT_MODE_ALL) {
                next = Player.REPEAT_MODE_ONE;
            } else {
                next = Player.REPEAT_MODE_OFF;
            }
            mediaController.setRepeatMode(next);
        });
    }

    public void setVolume(float volume) {
        runOrQueue(() -> mediaController.setVolume(volume));
    }

    public void setPlaybackSpeed(float speed) {
        runOrQueue(() -> mediaController.setPlaybackParameters(new PlaybackParameters(speed)));
    }

    public void startSleepTimer(int minutes) {
        cancelSleepTimer();
        sleepTimerEndAt = System.currentTimeMillis() + minutes * 60_000L;
        sleepTimerRunnable = () -> {
            runOrQueue(() -> mediaController.pause());
            sleepTimerEndAt = -1;
            updateState();
        };
        handler.postDelayed(sleepTimerRunnable, minutes * 60_000L);
        updateState();
    }

    public void cancelSleepTimer() {
        if (sleepTimerRunnable != null) {
            handler.removeCallbacks(sleepTimerRunnable);
            sleepTimerRunnable = null;
        }
        sleepTimerEndAt = -1;
        updateState();
    }

    public boolean isSleepTimerActive() {
        return sleepTimerEndAt > 0;
    }

    public void getEqualizerState(Consumer<Bundle> callback) {
        sendCustomCommand(com.mp3player.app.playback.PlaybackService.CMD_EQ_GET_STATE, Bundle.EMPTY, callback);
    }

    public void setEqualizerEnabled(boolean enabled) {
        Bundle args = new Bundle();
        args.putBoolean("enabled", enabled);
        sendCustomCommand(com.mp3player.app.playback.PlaybackService.CMD_EQ_SET_ENABLED, args, null);
    }

    public void setEqualizerBandLevel(int band, int level) {
        Bundle args = new Bundle();
        args.putInt("band", band);
        args.putInt("level", level);
        sendCustomCommand(com.mp3player.app.playback.PlaybackService.CMD_EQ_SET_BAND_LEVEL, args, null);
    }

    public void setEqualizerPreset(int preset, Consumer<Bundle> callback) {
        Bundle args = new Bundle();
        args.putInt("preset", preset);
        sendCustomCommand(com.mp3player.app.playback.PlaybackService.CMD_EQ_SET_PRESET, args, callback);
    }

    private void sendCustomCommand(String action, Bundle args, @Nullable Consumer<Bundle> callback) {
        runOrQueue(() -> {
            SessionCommand command = new SessionCommand(action, Bundle.EMPTY);
            com.google.common.util.concurrent.ListenableFuture<SessionResult> future =
                    mediaController.sendCustomCommand(command, args);
            if (callback != null) {
                future.addListener(() -> {
                    try {
                        callback.accept(future.get().extras);
                    } catch (Exception e) {
                        callback.accept(Bundle.EMPTY);
                    }
                }, ContextCompat.getMainExecutor(context));
            }
        });
    }

    public LiveData<PlaybackUiState> getState() {
        return state;
    }

    public long getCurrentPosition() {
        return mediaController != null ? mediaController.getCurrentPosition() : 0;
    }

    private void updateState() {
        if (mediaController == null) {
            return;
        }
        PlaybackUiState s = new PlaybackUiState();
        MediaItem current = mediaController.getCurrentMediaItem();
        if (current != null) {
            s.songId = current.mediaId != null && !current.mediaId.isEmpty() ? Long.parseLong(current.mediaId) : -1;
            s.title = current.mediaMetadata.title != null ? current.mediaMetadata.title.toString() : "";
            s.artist = current.mediaMetadata.artist != null ? current.mediaMetadata.artist.toString() : "";
            s.artworkUri = current.mediaMetadata.artworkUri;
        }
        s.hasMedia = current != null;
        s.isPlaying = mediaController.isPlaying();
        s.duration = Math.max(mediaController.getDuration(), 0);
        s.repeatMode = mediaController.getRepeatMode();
        s.volume = mediaController.getVolume();
        s.speed = mediaController.getPlaybackParameters().speed;
        s.sleepTimerActive = isSleepTimerActive();
        s.sleepTimerEndAt = sleepTimerEndAt;
        state.postValue(s);
    }
}
