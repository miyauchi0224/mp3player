package com.mp3player.app.ui.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlaybackUiState;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class PlayerViewModel extends ViewModel {

    private final PlayerController playerController;
    private final MusicRepository musicRepository;

    private final MediatorLiveData<Song> currentSong = new MediatorLiveData<>();
    private LiveData<Song> currentSongSource;
    private long lastSongId = -1;

    @Inject
    public PlayerViewModel(PlayerController playerController, MusicRepository musicRepository) {
        this.playerController = playerController;
        this.musicRepository = musicRepository;

        currentSong.addSource(playerController.getState(), state -> {
            if (state == null || !state.hasMedia || state.songId < 0 || state.songId == lastSongId) {
                return;
            }
            lastSongId = state.songId;
            if (currentSongSource != null) {
                currentSong.removeSource(currentSongSource);
            }
            currentSongSource = musicRepository.getSongById(state.songId);
            currentSong.addSource(currentSongSource, currentSong::setValue);
        });
    }

    public LiveData<PlaybackUiState> getState() {
        return playerController.getState();
    }

    public LiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void togglePlayPause() {
        playerController.togglePlayPause();
    }

    public void skipToNext() {
        playerController.skipToNext();
    }

    public void skipToPrevious() {
        playerController.skipToPrevious();
    }

    public void seekTo(long positionMs) {
        playerController.seekTo(positionMs);
    }

    public void toggleShuffle() {
        playerController.toggleShuffle();
    }

    public void cycleRepeatMode() {
        playerController.cycleRepeatMode();
    }

    public long getCurrentPosition() {
        return playerController.getCurrentPosition();
    }

    public void toggleFavorite(Song song) {
        musicRepository.toggleFavorite(song);
    }

    public void setVolume(float volume) {
        playerController.setVolume(volume);
    }

    public void setPlaybackSpeed(float speed) {
        playerController.setPlaybackSpeed(speed);
    }

    public void startSleepTimer(int minutes) {
        playerController.startSleepTimer(minutes);
    }

    public void cancelSleepTimer() {
        playerController.cancelSleepTimer();
    }
}
