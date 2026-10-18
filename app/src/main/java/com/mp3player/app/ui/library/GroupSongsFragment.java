package com.mp3player.app.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.FragmentSongListBinding;
import com.mp3player.app.ui.common.SongAdapter;
import com.mp3player.app.ui.common.SongMenuHelper;
import com.mp3player.app.ui.playlist.AddToPlaylistDialogFragment;

@AndroidEntryPoint
public class GroupSongsFragment extends Fragment {

    private FragmentSongListBinding binding;
    private GroupSongsViewModel viewModel;
    private SongAdapter adapter;
    private List<Song> currentSongs = new ArrayList<>();

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
        viewModel = new ViewModelProvider(this).get(GroupSongsViewModel.class);

        Bundle args = getArguments();
        String groupType = args != null ? args.getString("groupType") : "";
        String groupKey = args != null ? args.getString("groupKey") : "";
        String groupTitle = args != null ? args.getString("groupTitle") : "";

        if ("album".equals(groupType)) {
            viewModel.loadAlbum(Long.parseLong(groupKey));
        } else if ("artist".equals(groupType)) {
            viewModel.loadArtist(groupKey);
        } else if ("folder".equals(groupType)) {
            viewModel.loadFolder(groupKey);
        }

        binding.header.setVisibility(View.VISIBLE);
        binding.textHeaderTitle.setText(groupTitle);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        binding.fabPlayAll.setVisibility(View.VISIBLE);
        binding.fabPlayAll.setOnClickListener(v -> {
            if (!currentSongs.isEmpty()) {
                viewModel.playFrom(currentSongs, 0);
            }
        });

        adapter = new SongAdapter(new SongAdapter.Listener() {
            @Override
            public void onSongClick(Song song, int position) {
                viewModel.playFrom(currentSongs, position);
            }

            @Override
            public void onSongLongClick(Song song) {
                SongMenuHelper.show(requireContext(), song, new SongMenuHelper.Callback() {
                    @Override
                    public void onToggleFavorite() {
                        viewModel.toggleFavorite(song);
                    }

                    @Override
                    public void onAddToPlaylist() {
                        AddToPlaylistDialogFragment.newInstance(song.id)
                                .show(getChildFragmentManager(), "add_to_playlist");
                    }

                    @Override
                    public void onEditTags() {
                        TagEditDialogFragment.newInstance(song.id, song.title, song.artist, song.album)
                                .show(getChildFragmentManager(), "edit_tags");
                    }
                });
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getSongs().observe(getViewLifecycleOwner(), songs -> {
            currentSongs = songs != null ? songs : new ArrayList<>();
            adapter.submitList(new ArrayList<>(currentSongs));
            binding.textEmpty.setText(R.string.empty_songs);
            binding.textEmpty.setVisibility(currentSongs.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
