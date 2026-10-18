package com.mp3player.app.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class CloudImportManager {

    public interface Callback {
        void onSuccess(int fileCount);
        void onError();
    }

    private final Context context;
    private final ExecutorService executor;

    @Inject
    public CloudImportManager(@ApplicationContext Context context, ExecutorService executor) {
        this.context = context;
        this.executor = executor;
    }

    public void importFiles(List<Uri> sources, Callback callback) {
        executor.execute(() -> {
            int success = 0;
            for (Uri source : sources) {
                if (copyToMusicLibrary(source)) {
                    success++;
                }
            }
            if (success > 0) {
                callback.onSuccess(success);
            } else {
                callback.onError();
            }
        });
    }

    private boolean copyToMusicLibrary(Uri source) {
        ContentResolver resolver = context.getContentResolver();
        String displayName = queryDisplayName(resolver, source);
        if (displayName == null) {
            displayName = "imported_" + System.currentTimeMillis() + ".mp3";
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, displayName);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/mp3player");
                values.put(MediaStore.Audio.Media.IS_PENDING, 1);

                Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri targetUri = resolver.insert(collection, values);
                if (targetUri == null) {
                    return false;
                }
                try (InputStream in = resolver.openInputStream(source);
                     OutputStream out = resolver.openOutputStream(targetUri)) {
                    if (in == null || out == null) {
                        return false;
                    }
                    copyStream(in, out);
                }
                values.clear();
                values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                resolver.update(targetUri, values, null, null);
                return true;
            } else {
                File musicDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC), "mp3player");
                if (!musicDir.exists()) {
                    musicDir.mkdirs();
                }
                File targetFile = new File(musicDir, displayName);
                try (InputStream in = resolver.openInputStream(source);
                     OutputStream out = new FileOutputStream(targetFile)) {
                    if (in == null) {
                        return false;
                    }
                    copyStream(in, out);
                }
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, displayName);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
                values.put(MediaStore.Audio.Media.DATA, targetFile.getAbsolutePath());
                resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private String queryDisplayName(ContentResolver resolver, Uri uri) {
        try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception ignored) {
            // 表示名が取得できない場合はデフォルト名にフォールバックする
        }
        return null;
    }
}
