package com.mp3player.app.ui.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.databinding.FragmentPlaylistListBinding;
import com.mp3player.app.ui.common.GroupAdapter;
import com.mp3player.app.ui.common.GroupItem;

@AndroidEntryPoint
public class PlaylistListFragment extends Fragment {

    private FragmentPlaylistListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PlaylistViewModel viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        GroupAdapter adapter = new GroupAdapter(item -> {
            Playlist playlist = (Playlist) item.payload;
            Bundle args = new Bundle();
            args.putLong("playlistId", playlist.id);
            args.putString("playlistName", playlist.name);
            Navigation.findNavController(view).navigate(R.id.playlistDetailFragment, args);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            List<GroupItem> items = new ArrayList<>();
            if (playlists != null) {
                for (Playlist playlist : playlists) {
                    items.add(new GroupItem("playlist:" + playlist.id, playlist.name, "", playlist));
                }
            }
            adapter.submitList(items);
            binding.textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.fabCreate.setOnClickListener(v -> {
            EditText input = new EditText(requireContext());
            input.setHint(R.string.playlist_name_hint);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.playlist_create)
                    .setView(input)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        String name = input.getText().toString().trim();
                        if (!name.isEmpty()) {
                            viewModel.createPlaylist(name);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
