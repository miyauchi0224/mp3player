package com.mp3player.app.ui.library;

import android.app.Dialog;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.databinding.DialogTagEditBinding;

@AndroidEntryPoint
public class TagEditDialogFragment extends DialogFragment {

    private static final String ARG_SONG_ID = "songId";
    private static final String ARG_TITLE = "title";
    private static final String ARG_ARTIST = "artist";
    private static final String ARG_ALBUM = "album";

    @Inject
    MusicRepository musicRepository;

    private DialogTagEditBinding binding;
    private ContentValues pendingValues;
    private Uri pendingUri;

    private final ActivityResultLauncher<IntentSenderRequest> recoverableLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && pendingUri != null && pendingValues != null) {
                    applyMediaStoreUpdate(pendingUri, pendingValues);
                }
            });

    public static TagEditDialogFragment newInstance(long songId, String title, String artist, String album) {
        TagEditDialogFragment fragment = new TagEditDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SONG_ID, songId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_ARTIST, artist);
        args.putString(ARG_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogTagEditBinding.inflate(getLayoutInflater());
        Bundle args = requireArguments();
        long songId = args.getLong(ARG_SONG_ID);
        binding.editTitle.setText(args.getString(ARG_TITLE));
        binding.editArtist.setText(args.getString(ARG_ARTIST));
        binding.editAlbum.setText(args.getString(ARG_ALBUM));

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.tag_edit_title)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> save(songId))
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();
    }

    private void save(long songId) {
        String title = binding.editTitle.getText().toString().trim();
        String artist = binding.editArtist.getText().toString().trim();
        String album = binding.editAlbum.getText().toString().trim();
        if (title.isEmpty()) {
            return;
        }

        musicRepository.updateTags(songId, title, artist, album);

        Uri songUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.TITLE, title);
        values.put(MediaStore.Audio.Media.ARTIST, artist);
        values.put(MediaStore.Audio.Media.ALBUM, album);
        applyMediaStoreUpdate(songUri, values);
    }

    private void applyMediaStoreUpdate(Uri uri, ContentValues values) {
        try {
            requireContext().getContentResolver().update(uri, values, null, null);
        } catch (SecurityException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e instanceof RecoverableSecurityException) {
                pendingUri = uri;
                pendingValues = values;
                IntentSender sender =
                        ((RecoverableSecurityException) e).getUserAction().getActionIntent().getIntentSender();
                recoverableLauncher.launch(new IntentSenderRequest.Builder(sender).build());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
