package com.mp3player.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import com.mp3player.app.data.local.entity.AlbumSummary;
import com.mp3player.app.data.local.entity.ArtistSummary;
import com.mp3player.app.data.local.entity.FolderSummary;
import com.mp3player.app.data.local.entity.Song;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertNew(List<Song> songs);

    @Query("SELECT id FROM songs")
    List<Long> getAllIds();

    @Query("SELECT * FROM songs")
    List<Song> getAllSongsSync();

    @Query("DELETE FROM songs WHERE id IN (:ids)")
    void deleteByIds(List<Long> ids);

    @Query("UPDATE songs SET title = :title, artist = :artist, album = :album, albumId = :albumId, " +
            "folderPath = :folderPath, duration = :duration, data = :data, dateAdded = :dateAdded WHERE id = :id")
    void updateMetadata(long id, String title, String artist, String album, long albumId,
                         String folderPath, long duration, String data, long dateAdded);

    @Query("SELECT * FROM songs ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * FROM songs WHERE id = :id")
    LiveData<Song> getSongById(long id);

    @Query("SELECT * FROM songs WHERE albumId = :albumId ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<Song>> getSongsByAlbum(long albumId);

    @Query("SELECT * FROM songs WHERE artist = :artist ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<Song>> getSongsByArtist(String artist);

    @Query("SELECT * FROM songs WHERE folderPath = :folderPath ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<Song>> getSongsByFolder(String folderPath);

    @Query("SELECT * FROM songs WHERE favorite = 1 ORDER BY title COLLATE NOCASE ASC")
    LiveData<List<Song>> getFavorites();

    @Query("SELECT * FROM songs WHERE favorite = 1 ORDER BY title COLLATE NOCASE ASC")
    List<Song> getFavoritesSync();

    @Query("SELECT * FROM songs WHERE id = :id")
    Song getSongByIdSync(long id);

    @Query("SELECT * FROM songs WHERE lastPlayedAt > 0 ORDER BY lastPlayedAt DESC LIMIT :limit")
    LiveData<List<Song>> getRecentlyPlayed(int limit);

    @Query("SELECT * FROM songs WHERE playCount > 0 ORDER BY playCount DESC LIMIT :limit")
    LiveData<List<Song>> getMostPlayed(int limit);

    @Query("SELECT albumId, album, artist, COUNT(*) as songCount FROM songs GROUP BY albumId ORDER BY album COLLATE NOCASE ASC")
    LiveData<List<AlbumSummary>> getAlbums();

    @Query("SELECT artist, COUNT(*) as songCount FROM songs GROUP BY artist ORDER BY artist COLLATE NOCASE ASC")
    LiveData<List<ArtistSummary>> getArtists();

    @Query("SELECT folderPath, COUNT(*) as songCount FROM songs GROUP BY folderPath ORDER BY folderPath ASC")
    LiveData<List<FolderSummary>> getFolders();

    @Query("UPDATE songs SET favorite = :favorite WHERE id = :songId")
    void setFavorite(long songId, boolean favorite);

    @Query("UPDATE songs SET title = :title, artist = :artist, album = :album WHERE id = :id")
    void updateTags(long id, String title, String artist, String album);

    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayedAt = :timestamp WHERE id = :songId")
    void recordPlay(long songId, long timestamp);

    @Query("UPDATE songs SET playCount = 0, lastPlayedAt = 0")
    void clearHistory();
}
