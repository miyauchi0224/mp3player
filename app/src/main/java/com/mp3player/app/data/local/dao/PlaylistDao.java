package com.mp3player.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.PlaylistSongCrossRef;
import com.mp3player.app.data.local.entity.Song;

@Dao
public interface PlaylistDao {

    @Insert
    long insertPlaylist(Playlist playlist);

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    LiveData<List<Playlist>> getAllPlaylists();

    @Query("UPDATE playlists SET name = :name WHERE id = :playlistId")
    void renamePlaylist(long playlistId, String name);

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    void deletePlaylist(long playlistId);

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    void clearPlaylistSongs(long playlistId);

    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM playlist_song_cross_ref WHERE playlistId = :playlistId")
    int getNextPosition(long playlistId);

    @Insert
    void addSongToPlaylist(PlaylistSongCrossRef crossRef);

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId = :playlistId AND songId = :songId")
    void removeSongFromPlaylist(long playlistId, long songId);

    @Query("UPDATE playlist_song_cross_ref SET position = :position WHERE playlistId = :playlistId AND songId = :songId")
    void updatePosition(long playlistId, long songId, int position);

    @Query("SELECT songs.* FROM songs INNER JOIN playlist_song_cross_ref " +
            "ON songs.id = playlist_song_cross_ref.songId " +
            "WHERE playlist_song_cross_ref.playlistId = :playlistId ORDER BY playlist_song_cross_ref.position ASC")
    LiveData<List<Song>> getSongsForPlaylist(long playlistId);

    @Query("SELECT * FROM playlists ORDER BY createdAt DESC")
    List<Playlist> getAllPlaylistsSync();

    @Query("SELECT songs.* FROM songs INNER JOIN playlist_song_cross_ref " +
            "ON songs.id = playlist_song_cross_ref.songId " +
            "WHERE playlist_song_cross_ref.playlistId = :playlistId ORDER BY playlist_song_cross_ref.position ASC")
    List<Song> getSongsForPlaylistSync(long playlistId);
}
