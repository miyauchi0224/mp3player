package com.mp3player.app.ui.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3player.app.databinding.ItemHeaderBinding;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    private final String title;

    public HeaderAdapter(String title) {
        this.title = title;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHeaderBinding binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HeaderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.binding.textHeader.setText(title);
    }

    @Override
    public long getItemId(int position) {
        return title.hashCode();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final ItemHeaderBinding binding;

        HeaderViewHolder(ItemHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
