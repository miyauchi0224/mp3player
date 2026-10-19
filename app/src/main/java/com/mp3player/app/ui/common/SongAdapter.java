package com.mp3player.app.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;

public class SongAdapter extends ListAdapter<Song, SongAdapter.SongViewHolder> implements ViewModeAdapter {

    public interface Listener {
        void onSongClick(Song song, int position);
        void onSongLongClick(Song song);
    }

    private final Listener listener;
    private SongViewMode viewMode = SongViewMode.LIST;

    public SongAdapter(Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    /** リスト表示／アイコン表示を切り替える。LayoutManagerの切り替えは SongListLayout が行う */
    // 表示方法が変わると全項目のレイアウト（ViewType）が変わるため、一覧全体を更新する
    @android.annotation.SuppressLint("NotifyDataSetChanged")
    @Override
    public void setViewMode(SongViewMode mode) {
        if (viewMode == mode) return;
        viewMode = mode;
        notifyDataSetChanged();
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

    @Override
    public int getItemViewType(int position) {
        return viewMode.ordinal();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == SongViewMode.GRID.ordinal() ? R.layout.item_song_grid : R.layout.item_song;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /** item_song.xml と item_song_grid.xml は同じIDを持つため、1つのViewHolderで両方を扱う */
    class SongViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageArt;
        private final TextView textTitle;
        private final TextView textArtist;
        private final ImageView iconFavorite;

        SongViewHolder(View itemView) {
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
            itemView.setOnLongClickListener(v -> {
                listener.onSongLongClick(song);
                return true;
            });
        }
    }
}
