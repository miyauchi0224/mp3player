package com.mp3player.app.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class FavoritesViewModel extends ViewModel {

    private final MusicRepository repository;
    private final PlayerController playerController;

    @Inject
    public FavoritesViewModel(MusicRepository repository, PlayerController playerController) {
        this.repository = repository;
        this.playerController = playerController;
    }

    public LiveData<List<Song>> getFavorites() {
        return repository.getFavorites();
    }

    public void toggleFavorite(Song song) {
        repository.toggleFavorite(song);
    }

    public void playFrom(List<Song> queue, int index) {
        playerController.playQueue(queue, index);
    }
}
