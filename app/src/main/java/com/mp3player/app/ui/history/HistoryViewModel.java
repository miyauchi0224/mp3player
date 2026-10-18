package com.mp3player.app.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class HistoryViewModel extends ViewModel {

    private static final int LIMIT = 30;

    private final MusicRepository repository;
    private final PlayerController playerController;

    @Inject
    public HistoryViewModel(MusicRepository repository, PlayerController playerController) {
        this.repository = repository;
        this.playerController = playerController;
    }

    public LiveData<List<Song>> getRecentlyPlayed() {
        return repository.getRecentlyPlayed(LIMIT);
    }

    public LiveData<List<Song>> getMostPlayed() {
        return repository.getMostPlayed(LIMIT);
    }

    public void playFrom(List<Song> queue, int index) {
        playerController.playQueue(queue, index);
    }

    public void toggleFavorite(Song song) {
        repository.toggleFavorite(song);
    }

    public void clearHistory() {
        repository.clearHistory();
    }
}
