package com.mp3player.app.ui.playlist;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.PlaylistRepository;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.databinding.DialogAddToPlaylistBinding;
import com.mp3player.app.ui.common.GroupAdapter;
import com.mp3player.app.ui.common.GroupItem;

@AndroidEntryPoint
public class AddToPlaylistDialogFragment extends DialogFragment {

    private static final String ARG_SONG_ID = "songId";

    @Inject
    PlaylistRepository playlistRepository;

    public static AddToPlaylistDialogFragment newInstance(long songId) {
        AddToPlaylistDialogFragment fragment = new AddToPlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SONG_ID, songId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogAddToPlaylistBinding binding =
                DialogAddToPlaylistBinding.inflate(LayoutInflater.from(requireContext()));
        long songId = requireArguments().getLong(ARG_SONG_ID);

        GroupAdapter adapter = new GroupAdapter(item -> {
            Playlist playlist = (Playlist) item.payload;
            playlistRepository.addSongToPlaylist(playlist.id, songId);
            dismiss();
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        playlistRepository.getAllPlaylists().observe(this, playlists -> {
            List<GroupItem> items = new ArrayList<>();
            for (Playlist p : playlists) {
                items.add(new GroupItem("playlist:" + p.id, p.name, "", p));
            }
            adapter.submitList(items);
        });

        binding.buttonCreate.setOnClickListener(v -> {
            EditText input = new EditText(requireContext());
            input.setHint(R.string.playlist_name_hint);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.playlist_create)
                    .setView(input)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        String name = input.getText().toString().trim();
                        if (!name.isEmpty()) {
                            playlistRepository.createPlaylist(name);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .show();
        });

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.action_add_to_playlist)
                .setView(binding.getRoot())
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();
    }
}
