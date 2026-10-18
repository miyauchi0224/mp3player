package com.mp3player.app.data;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.dao.SongDao;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.PlaylistSongCrossRef;
import com.mp3player.app.data.local.entity.Song;

@Singleton
public class PlaylistImportExportManager {

    public interface Callback {
        void onSuccess(int songCount);
        void onError();
    }

    private final PlaylistDao playlistDao;
    private final SongDao songDao;
    private final java.util.concurrent.ExecutorService executor;

    @Inject
    public PlaylistImportExportManager(PlaylistDao playlistDao, SongDao songDao,
                                        java.util.concurrent.ExecutorService executor) {
        this.playlistDao = playlistDao;
        this.songDao = songDao;
        this.executor = executor;
    }

    public void export(long playlistId, Uri targetUri, ContentResolver resolver, Callback callback) {
        executor.execute(() -> {
            try (OutputStream out = resolver.openOutputStream(targetUri)) {
                if (out == null) {
                    callback.onError();
                    return;
                }
                List<Song> songs = playlistDao.getSongsForPlaylistSync(playlistId);
                StringBuilder sb = new StringBuilder();
                sb.append("#EXTM3U\n");
                for (Song song : songs) {
                    sb.append("#EXTINF:").append(song.duration / 1000).append(',')
                            .append(song.artist).append(" - ").append(song.title).append('\n');
                    sb.append(song.data).append('\n');
                }
                out.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                callback.onSuccess(songs.size());
            } catch (IOException e) {
                callback.onError();
            }
        });
    }

    public void importPlaylist(Uri sourceUri, ContentResolver resolver, String playlistName, Callback callback) {
        executor.execute(() -> {
            try (InputStream in = resolver.openInputStream(sourceUri)) {
                if (in == null) {
                    callback.onError();
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    lines.add(line);
                }

                List<Song> allSongs = songDao.getAllSongsSync();
                Playlist playlist = new Playlist();
                playlist.name = playlistName;
                playlist.createdAt = System.currentTimeMillis();
                long playlistId = playlistDao.insertPlaylist(playlist);

                int position = 0;
                int matched = 0;
                for (String path : lines) {
                    String fileName = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
                    for (Song song : allSongs) {
                        if (song.data.equals(path) || song.data.endsWith("/" + fileName)) {
                            PlaylistSongCrossRef ref = new PlaylistSongCrossRef();
                            ref.playlistId = playlistId;
                            ref.songId = song.id;
                            ref.position = position++;
                            playlistDao.addSongToPlaylist(ref);
                            matched++;
                            break;
                        }
                    }
                }
                callback.onSuccess(matched);
            } catch (IOException e) {
                callback.onError();
            }
        });
    }
}
