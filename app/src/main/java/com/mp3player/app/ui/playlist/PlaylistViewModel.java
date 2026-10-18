package com.mp3player.app.ui.playlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.PlaylistRepository;
import com.mp3player.app.data.local.entity.Playlist;

@HiltViewModel
public class PlaylistViewModel extends ViewModel {

    private final PlaylistRepository repository;

    @Inject
    public PlaylistViewModel(PlaylistRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Playlist>> getPlaylists() {
        return repository.getAllPlaylists();
    }

    public void createPlaylist(String name) {
        repository.createPlaylist(name);
    }
}
