package com.mp3player.app.ui.settings;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mp3player.app.data.local.entity.FolderSummary;
import com.mp3player.app.databinding.ItemFolderCheckBinding;

public class FolderCheckAdapter extends RecyclerView.Adapter<FolderCheckAdapter.ViewHolder> {

    private final List<FolderSummary> folders = new ArrayList<>();
    private final Set<String> selected = new HashSet<>();

    public void submitList(List<FolderSummary> newFolders, Set<String> selectedFolders) {
        folders.clear();
        folders.addAll(newFolders);
        selected.clear();
        selected.addAll(selectedFolders);
        notifyDataSetChanged();
    }

    public Set<String> getSelected() {
        return new HashSet<>(selected);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFolderCheckBinding binding =
                ItemFolderCheckBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(folders.get(position));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFolderCheckBinding binding;

        ViewHolder(ItemFolderCheckBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FolderSummary folder) {
            binding.textTitle.setText(folder.getFolderName());
            binding.textSubtitle.setText(folder.songCount + "曲 ・ " + folder.folderPath);
            binding.checkbox.setChecked(selected.contains(folder.folderPath));
            binding.getRoot().setOnClickListener(v -> {
                if (selected.contains(folder.folderPath)) {
                    selected.remove(folder.folderPath);
                } else {
                    selected.add(folder.folderPath);
                }
                notifyItemChanged(getBindingAdapterPosition());
            });
        }
    }
}
