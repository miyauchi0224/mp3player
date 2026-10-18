package com.mp3player.app.ui.common;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;

public class SongMenuHelper {

    public interface Callback {
        void onToggleFavorite();
        void onAddToPlaylist();
        void onEditTags();
    }

    public static void show(Context context, Song song, Callback callback) {
        String favoriteLabel = song.favorite ? "お気に入りから削除" : "お気に入りに追加";
        String[] items = {
                favoriteLabel,
                context.getString(R.string.action_add_to_playlist),
                context.getString(R.string.action_edit_tags)
        };
        new AlertDialog.Builder(context)
                .setTitle(song.title)
                .setItems(items, (dialog, which) -> {
                    if (which == 0) {
                        callback.onToggleFavorite();
                    } else if (which == 1) {
                        callback.onAddToPlaylist();
                    } else {
                        callback.onEditTags();
                    }
                })
                .show();
    }
}
