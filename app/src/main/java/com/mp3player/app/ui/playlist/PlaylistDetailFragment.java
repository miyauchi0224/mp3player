package com.mp3player.app.ui.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.mp3player.app.ui.common.ListGestures;
import com.mp3player.app.ui.common.SongListLayout;
import com.mp3player.app.ui.common.SongViewMode;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.FragmentSongListBinding;

@AndroidEntryPoint
public class PlaylistDetailFragment extends Fragment {

    @javax.inject.Inject
    com.mp3player.app.data.UserPreferences userPreferences;

    private FragmentSongListBinding binding;
    private PlaylistDetailViewModel viewModel;
    private PlaylistSongAdapter adapter;
    private long playlistId;
    private String playlistName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PlaylistDetailViewModel.class);

        Bundle args = getArguments();
        playlistId = args != null ? args.getLong("playlistId") : -1;
        playlistName = args != null ? args.getString("playlistName") : "";
        viewModel.init(playlistId);

        binding.header.setVisibility(View.VISIBLE);
        binding.textHeaderTitle.setText(playlistName);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        binding.buttonMore.setVisibility(View.VISIBLE);
        binding.buttonMore.setOnClickListener(this::showOverflowMenu);
        binding.fabPlayAll.setVisibility(View.VISIBLE);

        adapter = new PlaylistSongAdapter((song, position) -> viewModel.playFrom(adapter.getCurrentList(), position));
        binding.recyclerView.setAdapter(adapter);
        binding.buttonViewMode.setVisibility(View.VISIBLE);
        binding.buttonViewMode.setOnClickListener(v ->
                userPreferences.setSongViewMode(userPreferences.getSongViewMode().getValue().toggle()));
        userPreferences.getSongViewMode().observe(getViewLifecycleOwner(), mode ->
                SongListLayout.apply(binding.recyclerView, adapter, mode, binding.buttonViewMode));

        binding.fabPlayAll.setOnClickListener(v -> {
            List<Song> songs = adapter.getCurrentList();
            if (!songs.isEmpty()) {
                viewModel.playFrom(songs, 0);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, 0) {
            // 表示方法によって並べ替え・削除の方向を変える（グリッドではスワイプ削除なし）
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                SongViewMode mode = userPreferences.getSongViewMode().getValue();
                return makeMovementFlags(ListGestures.dragDirections(mode), ListGestures.swipeDirections(mode));
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                   @NonNull RecyclerView.ViewHolder target) {
                adapter.moveItem(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Song song = adapter.getSongAt(position);
                adapter.removeAt(position);
                viewModel.removeSong(song.id);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.reorder(adapter.getCurrentList());
            }
        }).attachToRecyclerView(binding.recyclerView);

        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            List<Song> list = songs != null ? songs : new ArrayList<>();
            adapter.submitList(list);
            binding.textEmpty.setText(R.string.empty_songs);
            binding.textEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void showOverflowMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.inflate(R.menu.menu_playlist_detail);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_rename) {
                showRenameDialog();
                return true;
            } else if (id == R.id.action_delete) {
                showDeleteConfirmDialog();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showRenameDialog() {
        EditText input = new EditText(requireContext());
        input.setText(playlistName);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.menu_rename)
                .setView(input)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        playlistName = name;
                        binding.textHeaderTitle.setText(name);
                        viewModel.rename(name);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.menu_delete)
                .setMessage(playlistName)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    viewModel.deletePlaylist();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
