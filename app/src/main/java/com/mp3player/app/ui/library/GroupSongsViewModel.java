package com.mp3player.app.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class GroupSongsViewModel extends ViewModel {

    private final MusicRepository repository;
    private final PlayerController playerController;
    private LiveData<List<Song>> songs = new MutableLiveData<>();
    private boolean loaded = false;

    @Inject
    public GroupSongsViewModel(MusicRepository repository, PlayerController playerController) {
        this.repository = repository;
        this.playerController = playerController;
    }

    public void loadAlbum(long albumId) {
        if (loaded) return;
        loaded = true;
        songs = repository.getSongsByAlbum(albumId);
    }

    public void loadArtist(String artist) {
        if (loaded) return;
        loaded = true;
        songs = repository.getSongsByArtist(artist);
    }

    public void loadFolder(String folderPath) {
        if (loaded) return;
        loaded = true;
        songs = repository.getSongsByFolder(folderPath);
    }

    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public void playFrom(List<Song> queue, int index) {
        playerController.playQueue(queue, index);
    }

    public void toggleFavorite(Song song) {
        repository.toggleFavorite(song);
    }
}
