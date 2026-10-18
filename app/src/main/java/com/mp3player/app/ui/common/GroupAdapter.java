package com.mp3player.app.ui.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3player.app.databinding.ItemGroupBinding;

public class GroupAdapter extends ListAdapter<GroupItem, GroupAdapter.GroupViewHolder> {

    public interface Listener {
        void onGroupClick(GroupItem item);
    }

    private final Listener listener;

    public GroupAdapter(Listener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<GroupItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<GroupItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull GroupItem oldItem, @NonNull GroupItem newItem) {
            return oldItem.key.equals(newItem.key);
        }

        @Override
        public boolean areContentsTheSame(@NonNull GroupItem oldItem, @NonNull GroupItem newItem) {
            return oldItem.title.equals(newItem.title) && oldItem.subtitle.equals(newItem.subtitle);
        }
    };

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GroupViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private final ItemGroupBinding binding;

        GroupViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GroupItem item) {
            binding.textTitle.setText(item.title);
            binding.textSubtitle.setText(item.subtitle);
            binding.getRoot().setOnClickListener(v -> listener.onGroupClick(item));
        }
    }
}
