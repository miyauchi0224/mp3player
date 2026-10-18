package com.mp3player.app.ui.record;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.mp3player.app.R;
import com.mp3player.app.databinding.ItemRecordingBinding;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.ViewHolder> {

    public interface Listener {
        void onRecordingClick(File file, int position);
    }

    private final List<File> files = new ArrayList<>();
    private final Listener listener;
    private int playingPosition = -1;

    public RecordingAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<File> newFiles) {
        files.clear();
        files.addAll(newFiles);
        playingPosition = -1;
        notifyDataSetChanged();
    }

    public void setPlayingPosition(int position) {
        int previous = playingPosition;
        playingPosition = position;
        if (previous >= 0) notifyItemChanged(previous);
        if (playingPosition >= 0) notifyItemChanged(playingPosition);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecordingBinding binding = ItemRecordingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(files.get(position), position == playingPosition);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecordingBinding binding;
        private final SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());

        ViewHolder(ItemRecordingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(File file, boolean isPlaying) {
            binding.textTitle.setText(file.getName());
            binding.textSubtitle.setText(dateFormat.format(new Date(file.lastModified())));
            binding.iconPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            binding.getRoot().setOnClickListener(v -> listener.onRecordingClick(file, getBindingAdapterPosition()));
        }
    }
}
