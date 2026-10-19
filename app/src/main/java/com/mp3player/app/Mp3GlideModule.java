package com.mp3player.app;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import com.mp3player.app.ui.common.EmbeddedArtwork;
import com.mp3player.app.ui.common.EmbeddedArtworkLoader;

/** MP3埋め込みサムネイルを読めるよう、Glideに EmbeddedArtwork の読み込み方法を登録する */
@GlideModule
public class Mp3GlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.prepend(EmbeddedArtwork.class, InputStream.class, new EmbeddedArtworkLoader.Factory(context));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
