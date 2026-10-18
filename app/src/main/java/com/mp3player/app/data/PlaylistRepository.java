package com.mp3player.app.data;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.PlaylistSongCrossRef;
import com.mp3player.app.data.local.entity.Song;

@Singleton
public class PlaylistRepository {

    private final PlaylistDao playlistDao;
    private final ExecutorService executor;

    @Inject
    public PlaylistRepository(PlaylistDao playlistDao, ExecutorService executor) {
        this.playlistDao = playlistDao;
        this.executor = executor;
    }

    public LiveData<List<Playlist>> getAllPlaylists() {
        return playlistDao.getAllPlaylists();
    }

    public LiveData<List<Song>> getSongsForPlaylist(long playlistId) {
        return playlistDao.getSongsForPlaylist(playlistId);
    }

    public void createPlaylist(String name) {
        executor.execute(() -> {
            Playlist playlist = new Playlist();
            playlist.name = name;
            playlist.createdAt = System.currentTimeMillis();
            playlistDao.insertPlaylist(playlist);
        });
    }

    public void renamePlaylist(long playlistId, String name) {
        executor.execute(() -> playlistDao.renamePlaylist(playlistId, name));
    }

    public void deletePlaylist(long playlistId) {
        executor.execute(() -> playlistDao.deletePlaylist(playlistId));
    }

    public void addSongToPlaylist(long playlistId, long songId) {
        executor.execute(() -> {
            int position = playlistDao.getNextPosition(playlistId);
            PlaylistSongCrossRef ref = new PlaylistSongCrossRef();
            ref.playlistId = playlistId;
            ref.songId = songId;
            ref.position = position;
            playlistDao.addSongToPlaylist(ref);
        });
    }

    public void removeSongFromPlaylist(long playlistId, long songId) {
        executor.execute(() -> playlistDao.removeSongFromPlaylist(playlistId, songId));
    }

    public void reorderSongs(long playlistId, List<Song> orderedSongs) {
        executor.execute(() -> {
            for (int i = 0; i < orderedSongs.size(); i++) {
                playlistDao.updatePosition(playlistId, orderedSongs.get(i).id, i);
            }
        });
    }
}
