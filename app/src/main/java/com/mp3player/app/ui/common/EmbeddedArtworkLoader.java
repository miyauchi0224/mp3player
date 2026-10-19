package com.mp3player.app.ui.common;

import android.content.Context;
import android.media.MediaMetadataRetriever;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.mp3player.app.playback.PlayerController;

/** MP3に埋め込まれたサムネイルを MediaMetadataRetriever で取り出すGlideのModelLoader */
public class EmbeddedArtworkLoader implements ModelLoader<EmbeddedArtwork, InputStream> {

    private final Context context;

    EmbeddedArtworkLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull EmbeddedArtwork model, int width, int height,
                                               @NonNull Options options) {
        return new LoadData<>(new ObjectKey(model), new Fetcher(context, model));
    }

    @Override
    public boolean handles(@NonNull EmbeddedArtwork model) {
        return true;
    }

    private static class Fetcher implements DataFetcher<InputStream> {
        private final Context context;
        private final EmbeddedArtwork model;

        Fetcher(Context context, EmbeddedArtwork model) {
            this.context = context;
            this.model = model;
        }

        @Override
        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            try {
                retriever.setDataSource(context, PlayerController.getSongContentUri(model.songId));
                byte[] picture = retriever.getEmbeddedPicture();
                if (picture == null) {
                    // 埋め込み画像が無い場合は失敗として扱い、呼び出し側の代替画像に任せる
                    callback.onLoadFailed(new FileNotFoundException("No embedded picture: " + model));
                } else {
                    callback.onDataReady(new ByteArrayInputStream(picture));
                }
            } catch (RuntimeException e) {
                callback.onLoadFailed(e);
            } finally {
                try {
                    retriever.release();
                } catch (Exception ignored) {
                    // 解放に失敗しても読み込み結果には影響しない
                }
            }
        }

        @Override
        public void cleanup() {
        }

        @Override
        public void cancel() {
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class Factory implements ModelLoaderFactory<EmbeddedArtwork, InputStream> {
        private final Context context;

        public Factory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public ModelLoader<EmbeddedArtwork, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new EmbeddedArtworkLoader(context);
        }

        @Override
        public void teardown() {
        }
    }
}
