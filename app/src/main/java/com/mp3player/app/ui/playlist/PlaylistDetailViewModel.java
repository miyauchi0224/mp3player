package com.mp3player.app.ui.playlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.PlaylistRepository;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class PlaylistDetailViewModel extends ViewModel {

    private final PlaylistRepository playlistRepository;
    private final PlayerController playerController;
    private long playlistId = -1;
    private LiveData<List<Song>> songs = new MutableLiveData<>();

    @Inject
    public PlaylistDetailViewModel(PlaylistRepository playlistRepository, PlayerController playerController) {
        this.playlistRepository = playlistRepository;
        this.playerController = playerController;
    }

    public void init(long playlistId) {
        if (this.playlistId == playlistId) {
            return;
        }
        this.playlistId = playlistId;
        songs = playlistRepository.getSongsForPlaylist(playlistId);
    }

    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public void removeSong(long songId) {
        playlistRepository.removeSongFromPlaylist(playlistId, songId);
    }

    public void reorder(List<Song> ordered) {
        playlistRepository.reorderSongs(playlistId, ordered);
    }

    public void rename(String name) {
        playlistRepository.renamePlaylist(playlistId, name);
    }

    public void deletePlaylist() {
        playlistRepository.deletePlaylist(playlistId);
    }

    public void playFrom(List<Song> queue, int index) {
        playerController.playQueue(queue, index);
    }
}
