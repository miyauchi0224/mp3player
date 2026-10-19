package com.mp3player.app.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.FragmentSongListBinding;
import com.mp3player.app.ui.common.HeaderAdapter;
import com.mp3player.app.ui.common.SongListLayout;
import com.mp3player.app.ui.common.ViewModeAdapter;
import com.mp3player.app.ui.common.SongAdapter;
import com.mp3player.app.ui.common.SongMenuHelper;
import com.mp3player.app.ui.playlist.AddToPlaylistDialogFragment;

@AndroidEntryPoint
public class HistoryFragment extends Fragment {

    @javax.inject.Inject
    com.mp3player.app.data.UserPreferences userPreferences;

    private FragmentSongListBinding binding;
    private HistoryViewModel viewModel;
    private SongAdapter recentAdapter;
    private SongAdapter mostPlayedAdapter;
    private List<Song> recentSongs = new ArrayList<>();
    private List<Song> mostPlayedSongs = new ArrayList<>();

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
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        binding.header.setVisibility(View.VISIBLE);
        binding.textHeaderTitle.setText(R.string.tab_history);
        binding.buttonBack.setVisibility(View.GONE);
        binding.buttonMore.setVisibility(View.VISIBLE);
        binding.buttonMore.setImageResource(R.drawable.ic_delete);
        binding.buttonMore.setContentDescription(getString(R.string.action_clear_history));
        binding.buttonMore.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setMessage(R.string.history_clear_confirm)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> viewModel.clearHistory())
                .setNegativeButton(R.string.dialog_cancel, null)
                .show());

        SongAdapter.Listener recentListener = new SongAdapter.Listener() {
            @Override
            public void onSongClick(Song song, int position) {
                viewModel.playFrom(recentSongs, position);
            }

            @Override
            public void onSongLongClick(Song song) {
                showMenu(song);
            }
        };
        SongAdapter.Listener mostPlayedListener = new SongAdapter.Listener() {
            @Override
            public void onSongClick(Song song, int position) {
                viewModel.playFrom(mostPlayedSongs, position);
            }

            @Override
            public void onSongLongClick(Song song) {
                showMenu(song);
            }
        };

        recentAdapter = new SongAdapter(recentListener);
        mostPlayedAdapter = new SongAdapter(mostPlayedListener);

        ConcatAdapter concatAdapter = new ConcatAdapter(
                new HeaderAdapter(getString(R.string.history_recent)), recentAdapter,
                new HeaderAdapter(getString(R.string.history_most_played)), mostPlayedAdapter);
        // 「最近再生した曲」「再生回数が多い曲」の見出しは、グリッドでも1行全体を使う。
        // 切り替えボタンは親の LibraryFragment のヘッダーにある
        userPreferences.getSongViewMode().observe(getViewLifecycleOwner(), mode ->
                SongListLayout.apply(binding.recyclerView, new ViewModeAdapter[]{recentAdapter, mostPlayedAdapter},
                        mode, null,
                        position -> concatAdapter.getWrappedAdapterAndPosition(position).first instanceof HeaderAdapter));
        binding.recyclerView.setAdapter(concatAdapter);

        viewModel.getRecentlyPlayed().observe(getViewLifecycleOwner(), songs -> {
            recentSongs = songs != null ? songs : new ArrayList<>();
            recentAdapter.submitList(new ArrayList<>(recentSongs));
            updateEmptyState();
        });
        viewModel.getMostPlayed().observe(getViewLifecycleOwner(), songs -> {
            mostPlayedSongs = songs != null ? songs : new ArrayList<>();
            mostPlayedAdapter.submitList(new ArrayList<>(mostPlayedSongs));
            updateEmptyState();
        });
    }

    private void showMenu(Song song) {
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
                com.mp3player.app.ui.library.TagEditDialogFragment
                        .newInstance(song.id, song.title, song.artist, song.album)
                        .show(getChildFragmentManager(), "edit_tags");
            }
        });
    }

    private void updateEmptyState() {
        boolean empty = recentSongs.isEmpty() && mostPlayedSongs.isEmpty();
        binding.textEmpty.setText(R.string.empty_songs);
        binding.textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
