package com.mp3player.app.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import com.mp3player.app.data.local.entity.Song;

@Singleton
public class MediaStoreScanner {

    private final Context context;

    @Inject
    public MediaStoreScanner(@ApplicationContext Context context) {
        this.context = context;
    }

    /** スキャンの進み具合を受け取る（バックグラウンドスレッドから呼ばれる） */
    public interface ProgressListener {
        void onProgress(int processed, int total);
    }

    public List<Song> scan() {
        return scan((processed, total) -> { });
    }

    public List<Song> scan(ProgressListener listener) {
        List<Song> result = new ArrayList<>();
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED
        };
        String selection = MediaStore.Audio.Media.MIME_TYPE + "=?";
        String[] selectionArgs = {"audio/mpeg"};

        try (Cursor cursor = context.getContentResolver()
                .query(collection, projection, selection, selectionArgs, null)) {
            if (cursor == null) {
                return result;
            }
            int idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

            int total = cursor.getCount();
            int processed = 0;
            while (cursor.moveToNext()) {
                processed++;
                if (ScanProgress.shouldReport(processed, total)) {
                    listener.onProgress(processed, total);
                }
                String data = cursor.getString(dataCol);
                String folderPath = "";
                if (data != null && data.contains("/")) {
                    folderPath = data.substring(0, data.lastIndexOf('/'));
                }

                Song song = new Song();
                song.id = cursor.getLong(idCol);
                String title = cursor.getString(titleCol);
                String artist = cursor.getString(artistCol);
                String album = cursor.getString(albumCol);
                song.title = title != null ? title : "Unknown";
                song.artist = artist != null ? artist : "Unknown Artist";
                song.album = album != null ? album : "Unknown Album";
                song.albumId = cursor.getLong(albumIdCol);
                song.folderPath = folderPath;
                song.duration = cursor.getLong(durationCol);
                song.data = data != null ? data : "";
                song.dateAdded = cursor.getLong(dateAddedCol);
                result.add(song);
            }
        }
        return result;
    }
}
