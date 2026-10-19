package com.mp3player.app.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mp3player.app.data.local.dao.SongDao;
import com.mp3player.app.data.local.entity.AlbumSummary;
import com.mp3player.app.data.local.entity.ArtistSummary;
import com.mp3player.app.data.local.entity.FolderSummary;
import com.mp3player.app.data.local.entity.Song;

@Singleton
public class MusicRepository {

    private final SongDao songDao;
    private final MediaStoreScanner scanner;
    private final ExecutorService executor;

    @Inject
    public MusicRepository(SongDao songDao, MediaStoreScanner scanner, ExecutorService executor) {
        this.songDao = songDao;
        this.scanner = scanner;
        this.executor = executor;
    }

    private final MutableLiveData<ScanProgress> scanProgress = new MutableLiveData<>(ScanProgress.idle());

    public LiveData<ScanProgress> getScanProgress() {
        return scanProgress;
    }

    public void scanLibrary() {
        scanProgress.postValue(ScanProgress.scanning(0, 0));
        executor.execute(() -> {
            try {
                syncLibrary();
            } finally {
                scanProgress.postValue(ScanProgress.idle());
            }
        });
    }

    private void syncLibrary() {
        {
            List<Song> scanned = scanner.scan((processed, total) ->
                    scanProgress.postValue(ScanProgress.scanning(processed, total)));
            List<Long> existingIds = songDao.getAllIds();
            Set<Long> existingSet = new HashSet<>(existingIds);
            Set<Long> scannedIds = new HashSet<>();
            List<Song> newSongs = new ArrayList<>();

            for (Song song : scanned) {
                scannedIds.add(song.id);
                if (existingSet.contains(song.id)) {
                    songDao.updateMetadata(song.id, song.title, song.artist, song.album, song.albumId,
                            song.folderPath, song.duration, song.data, song.dateAdded);
                } else {
                    newSongs.add(song);
                }
            }
            if (!newSongs.isEmpty()) {
                songDao.insertNew(newSongs);
            }

            List<Long> removedIds = new ArrayList<>();
            for (Long existingId : existingIds) {
                if (!scannedIds.contains(existingId)) {
                    removedIds.add(existingId);
                }
            }
            if (!removedIds.isEmpty()) {
                songDao.deleteByIds(removedIds);
            }
        }
    }

    public LiveData<List<Song>> getAllSongs() {
        return songDao.getAllSongs();
    }

    public LiveData<Song> getSongById(long id) {
        return songDao.getSongById(id);
    }

    public LiveData<List<Song>> getSongsByAlbum(long albumId) {
        return songDao.getSongsByAlbum(albumId);
    }

    public LiveData<List<Song>> getSongsByArtist(String artist) {
        return songDao.getSongsByArtist(artist);
    }

    public LiveData<List<Song>> getSongsByFolder(String folderPath) {
        return songDao.getSongsByFolder(folderPath);
    }

    public LiveData<List<Song>> getFavorites() {
        return songDao.getFavorites();
    }

    public LiveData<List<Song>> getRecentlyPlayed(int limit) {
        return songDao.getRecentlyPlayed(limit);
    }

    public LiveData<List<Song>> getMostPlayed(int limit) {
        return songDao.getMostPlayed(limit);
    }

    public void clearHistory() {
        executor.execute(songDao::clearHistory);
    }

    public LiveData<List<AlbumSummary>> getAlbums() {
        return songDao.getAlbums();
    }

    public LiveData<List<ArtistSummary>> getArtists() {
        return songDao.getArtists();
    }

    public LiveData<List<FolderSummary>> getFolders() {
        return songDao.getFolders();
    }

    public void toggleFavorite(Song song) {
        executor.execute(() -> songDao.setFavorite(song.id, !song.favorite));
    }

    public void updateTags(long songId, String title, String artist, String album) {
        executor.execute(() -> songDao.updateTags(songId, title, artist, album));
    }

    public void recordPlay(long songId) {
        executor.execute(() -> songDao.recordPlay(songId, System.currentTimeMillis()));
    }
}
