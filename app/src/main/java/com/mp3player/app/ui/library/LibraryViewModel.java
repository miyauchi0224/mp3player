package com.mp3player.app.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.UserPreferences;
import com.mp3player.app.data.local.entity.AlbumSummary;
import com.mp3player.app.data.local.entity.ArtistSummary;
import com.mp3player.app.data.local.entity.FolderSummary;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class LibraryViewModel extends ViewModel {

    public enum SortOrder {
        TITLE, ARTIST, DATE_ADDED
    }

    private final MusicRepository repository;
    private final PlayerController playerController;
    private final UserPreferences userPreferences;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<Song>> filteredSongs = new MediatorLiveData<>();
    private List<Song> allSongsCache = new ArrayList<>();
    private String queryCache = "";
    private SortOrder sortOrder = SortOrder.TITLE;
    private final Runnable folderFilterListener = this::publishFiltered;

    @Inject
    public LibraryViewModel(MusicRepository repository, PlayerController playerController,
                             UserPreferences userPreferences) {
        this.repository = repository;
        this.playerController = playerController;
        this.userPreferences = userPreferences;
        repository.scanLibrary();
        userPreferences.addTargetFoldersListener(folderFilterListener);

        filteredSongs.addSource(repository.getAllSongs(), songs -> {
            allSongsCache = songs != null ? songs : new ArrayList<>();
            publishFiltered();
        });
        filteredSongs.addSource(searchQuery, query -> {
            queryCache = query != null ? query : "";
            publishFiltered();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        userPreferences.removeTargetFoldersListener(folderFilterListener);
    }

    private void publishFiltered() {
        Set<String> targetFolders = userPreferences.getTargetFolders();
        List<Song> result = new ArrayList<>();
        String lower = queryCache.toLowerCase(Locale.getDefault());
        for (Song song : allSongsCache) {
            boolean matchesFolder = targetFolders.isEmpty() || targetFolders.contains(song.folderPath);
            boolean matchesQuery = queryCache.isEmpty()
                    || song.title.toLowerCase(Locale.getDefault()).contains(lower)
                    || song.artist.toLowerCase(Locale.getDefault()).contains(lower);
            if (matchesFolder && matchesQuery) {
                result.add(song);
            }
        }
        sortSongs(result);
        filteredSongs.setValue(result);
    }

    private void sortSongs(List<Song> songs) {
        Comparator<Song> comparator;
        switch (sortOrder) {
            case ARTIST:
                comparator = (a, b) -> a.artist.compareToIgnoreCase(b.artist);
                break;
            case DATE_ADDED:
                comparator = (a, b) -> Long.compare(b.dateAdded, a.dateAdded);
                break;
            case TITLE:
            default:
                comparator = (a, b) -> a.title.compareToIgnoreCase(b.title);
                break;
        }
        Collections.sort(songs, comparator);
    }

    public void setSortOrder(SortOrder order) {
        this.sortOrder = order;
        publishFiltered();
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void rescan() {
        repository.scanLibrary();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<com.mp3player.app.data.ScanProgress> getScanProgress() {
        return repository.getScanProgress();
    }

    public LiveData<List<Song>> getFilteredSongs() {
        return filteredSongs;
    }

    public LiveData<List<AlbumSummary>> getAlbums() {
        return repository.getAlbums();
    }

    public LiveData<List<ArtistSummary>> getArtists() {
        return repository.getArtists();
    }

    public LiveData<List<FolderSummary>> getFolders() {
        return repository.getFolders();
    }

    public void playFrom(List<Song> queue, int index) {
        playerController.playQueue(queue, index);
    }

    public void toggleFavorite(Song song) {
        repository.toggleFavorite(song);
    }
}
