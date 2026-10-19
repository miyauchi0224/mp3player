package com.mp3player.app.ui.playlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.ItemSongBinding;
import com.mp3player.app.ui.common.ArtworkLoader;

public class PlaylistSongAdapter extends RecyclerView.Adapter<PlaylistSongAdapter.ViewHolder> {

    public interface Listener {
        void onSongClick(Song song, int position);
    }

    private final List<Song> songs = new ArrayList<>();
    private final Listener listener;

    public PlaylistSongAdapter(Listener listener) {
        this.listener = listener;
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
        ItemSongBinding binding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        ViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Song song) {
            binding.textTitle.setText(song.title);
            binding.textArtist.setText(song.artist);
            binding.iconFavorite.setVisibility(song.favorite ? View.VISIBLE : View.GONE);
            ArtworkLoader.into(binding.imageArt, song.id, song.albumId);
            binding.getRoot().setOnClickListener(v -> listener.onSongClick(song, getBindingAdapterPosition()));
        }
    }
}
