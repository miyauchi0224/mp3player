package com.mp3player.app.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.ItemSongBinding;
import com.mp3player.app.playback.PlayerController;

public class SongAdapter extends ListAdapter<Song, SongAdapter.SongViewHolder> {

    public interface Listener {
        void onSongClick(Song song, int position);
        void onSongLongClick(Song song);
    }

    private final Listener listener;

    public SongAdapter(Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Song> DIFF_CALLBACK = new DiffUtil.ItemCallback<Song>() {
        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.title.equals(newItem.title)
                    && oldItem.artist.equals(newItem.artist)
                    && oldItem.favorite == newItem.favorite;
        }
    };

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding binding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private final ItemSongBinding binding;

        SongViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Song song) {
            binding.textTitle.setText(song.title);
            binding.textArtist.setText(song.artist);
            binding.iconFavorite.setVisibility(song.favorite ? View.VISIBLE : View.GONE);
            Glide.with(binding.imageArt)
                    .load(PlayerController.getAlbumArtUri(song.albumId))
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(binding.imageArt);
            binding.getRoot().setOnClickListener(v -> listener.onSongClick(song, getBindingAdapterPosition()));
            binding.getRoot().setOnLongClickListener(v -> {
                listener.onSongLongClick(song);
                return true;
            });
        }
    }
}
