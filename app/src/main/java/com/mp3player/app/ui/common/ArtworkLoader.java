package com.mp3player.app.ui.common;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import com.mp3player.app.R;
import com.mp3player.app.playback.PlayerController;

/** 曲のサムネイルを表示する。MP3埋め込み画像 → MediaStoreのアルバムアート → 音符アイコンの順に試す */
public final class ArtworkLoader {

    private ArtworkLoader() {
    }

    public static void into(ImageView view, long songId, long albumId) {
        into(view, songId, PlayerController.getAlbumArtUri(albumId));
    }

    public static void into(ImageView view, long songId, @Nullable Uri fallbackArtworkUri) {
        Glide.with(view)
                .load(new EmbeddedArtwork(songId))
                .placeholder(R.drawable.ic_music_note)
                .error(Glide.with(view)
                        .load(fallbackArtworkUri)
                        .error(R.drawable.ic_music_note))
                .into(view);
    }
}
