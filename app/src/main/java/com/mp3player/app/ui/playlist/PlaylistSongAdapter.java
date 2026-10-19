package com.mp3player.app.ui.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.ui.common.SongViewMode;
import com.mp3player.app.ui.common.ViewModeAdapter;
import com.mp3player.app.ui.common.ArtworkLoader;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.ViewHolder> implements ViewModeAdapter {

    public interface Listener {
        void onSongClick(Song song, int position);
    }

    private final List<Song> songs = new ArrayList<>();
    private final Listener listener;
    private SongViewMode viewMode = SongViewMode.LIST;

    public PlaylistSongAdapter(Listener listener) {
        this.listener = listener;
    }

    // 表示方法が変わると全項目のレイアウト（ViewType）が変わるため、一覧全体を更新する
    @android.annotation.SuppressLint("NotifyDataSetChanged")
    @Override
    public void setViewMode(SongViewMode mode) {
        if (viewMode == mode) return;
        viewMode = mode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewMode.ordinal();
    }

    public void submitList(List<Song> newSongs) {
        songs.clear();
        songs.addAll(newSongs);
        notifyDataSetChanged();
    }

    public List<Song> getCurrentList() {
        return new ArrayList<>(songs);
    }

    public Song getSongAt(int position) {
        return songs.get(position);
    }

    public void moveItem(int from, int to) {
        Song song = songs.remove(from);
        songs.add(to, song);
        notifyItemMoved(from, to);
    }

    public void removeAt(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == SongViewMode.GRID.ordinal() ? R.layout.item_song_grid : R.layout.item_song;
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    /** item_song.xml と item_song_grid.xml は同じIDを持つため、1つのViewHolderで両方を扱う */
    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageArt;
        private final TextView textTitle;
        private final TextView textArtist;
        private final ImageView iconFavorite;

        ViewHolder(View itemView) {
            super(itemView);
            imageArt = itemView.findViewById(R.id.image_art);
            textTitle = itemView.findViewById(R.id.text_title);
            textArtist = itemView.findViewById(R.id.text_artist);
            iconFavorite = itemView.findViewById(R.id.icon_favorite);
        }

        void bind(Song song) {
            textTitle.setText(song.title);
            textArtist.setText(song.artist);
            iconFavorite.setVisibility(song.favorite ? View.VISIBLE : View.GONE);
            ArtworkLoader.into(imageArt, song.id, song.albumId);
            itemView.setOnClickListener(v -> listener.onSongClick(song, getBindingAdapterPosition()));
        }
    }
}
