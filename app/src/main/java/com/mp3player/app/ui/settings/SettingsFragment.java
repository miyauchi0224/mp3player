package com.mp3player.app.ui.settings;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.PlaylistImportExportManager;
import com.mp3player.app.data.PlaylistRepository;
import com.mp3player.app.data.UserPreferences;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.databinding.FragmentSettingsBinding;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    @Inject
    UserPreferences userPreferences;

    @Inject
    PlaylistRepository playlistRepository;

    @Inject
    PlaylistImportExportManager importExportManager;

    private FragmentSettingsBinding binding;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long pendingExportPlaylistId = -1;
    private List<Playlist> currentPlaylists = new java.util.ArrayList<>();

    private final ActivityResultLauncher<String> exportLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("audio/x-mpegurl"), uri -> {
                if (uri != null && pendingExportPlaylistId >= 0) {
                    doExport(pendingExportPlaylistId, uri);
                }
            });

    private final ActivityResultLauncher<String[]> importLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    promptImportName(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRow(binding.rowDarkMode, R.string.settings_dark_mode, currentDarkModeLabel(), this::showDarkModeDialog);
        setupRow(binding.rowLanguage, R.string.settings_language, currentLanguageLabel(), this::showLanguageDialog);
        setupRow(binding.rowTargetFolders, R.string.settings_target_folders, "",
                () -> Navigation.findNavController(view).navigate(R.id.folderFilterFragment));
        setupRow(binding.rowExportPlaylist, R.string.settings_export_playlist, "", this::showExportPlaylistDialog);
        setupRow(binding.rowImportPlaylist, R.string.settings_import_playlist, "",
                () -> importLauncher.launch(new String[]{"audio/x-mpegurl", "audio/mpegurl", "application/octet-stream"}));
        setupRow(binding.rowRecord, R.string.settings_record, "",
                () -> Navigation.findNavController(view).navigate(R.id.recordFragment));
        setupRow(binding.rowCloudImport, R.string.settings_cloud_import, "",
                () -> Navigation.findNavController(view).navigate(R.id.cloudImportFragment));
        setupRow(binding.rowPrivacyPolicy, R.string.settings_privacy_policy, "", () ->
                navigateToStaticText(view, R.string.settings_privacy_policy, R.string.privacy_policy_body));
        setupRow(binding.rowLicense, R.string.settings_license, "", () ->
                navigateToStaticText(view, R.string.settings_license, R.string.license_body));

        playlistRepository.getAllPlaylists().observe(getViewLifecycleOwner(), playlists ->
                currentPlaylists = playlists != null ? playlists : new java.util.ArrayList<>());
    }

    private void setupRow(com.mp3player.app.databinding.ItemSettingRowBinding row, int titleRes,
                           String subtitle, Runnable onClick) {
        row.textTitle.setText(titleRes);
        row.textSubtitle.setText(subtitle);
        row.textSubtitle.setVisibility(subtitle.isEmpty() ? View.GONE : View.VISIBLE);
        row.getRoot().setOnClickListener(v -> onClick.run());
    }

    private void navigateToStaticText(View view, int titleRes, int bodyRes) {
        Bundle args = new Bundle();
        args.putString("title", getString(titleRes));
        args.putString("body", getString(bodyRes));
        Navigation.findNavController(view).navigate(R.id.staticTextFragment, args);
    }

    private String currentDarkModeLabel() {
        switch (userPreferences.getDarkMode()) {
            case UserPreferences.DARK_MODE_LIGHT:
                return getString(R.string.settings_dark_mode_light);
            case UserPreferences.DARK_MODE_DARK:
                return getString(R.string.settings_dark_mode_dark);
            default:
                return getString(R.string.settings_dark_mode_system);
        }
    }

    private String currentLanguageLabel() {
        switch (userPreferences.getLanguage()) {
            case UserPreferences.LANGUAGE_JA:
                return "日本語";
            case UserPreferences.LANGUAGE_EN:
                return "English";
            default:
                return getString(R.string.settings_language_system);
        }
    }

    private void showDarkModeDialog() {
        String[] labels = {
                getString(R.string.settings_dark_mode_system),
                getString(R.string.settings_dark_mode_light),
                getString(R.string.settings_dark_mode_dark)
        };
        String[] values = {
                UserPreferences.DARK_MODE_SYSTEM,
                UserPreferences.DARK_MODE_LIGHT,
                UserPreferences.DARK_MODE_DARK
        };
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_dark_mode)
                .setItems(labels, (dialog, which) -> {
                    userPreferences.setDarkMode(values[which]);
                    applyDarkMode(values[which]);
                })
                .show();
    }

    private void applyDarkMode(String mode) {
        int nightMode;
        switch (mode) {
            case UserPreferences.DARK_MODE_LIGHT:
                nightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case UserPreferences.DARK_MODE_DARK:
                nightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
                break;
            default:
                nightMode = androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                break;
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(nightMode);
    }

    private void showLanguageDialog() {
        String[] labels = {getString(R.string.settings_language_system), "日本語", "English"};
        String[] values = {UserPreferences.LANGUAGE_SYSTEM, UserPreferences.LANGUAGE_JA, UserPreferences.LANGUAGE_EN};
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_language)
                .setItems(labels, (dialog, which) -> userPreferences.setLanguage(values[which]))
                .show();
    }

    private void showExportPlaylistDialog() {
        List<Playlist> playlists = currentPlaylists;
        if (playlists.isEmpty()) {
            Toast.makeText(requireContext(), R.string.empty_playlists, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] names = new String[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            names[i] = playlists.get(i).name;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.export_playlist_title)
                .setItems(names, (dialog, which) -> {
                    Playlist selected = playlists.get(which);
                    pendingExportPlaylistId = selected.id;
                    exportLauncher.launch(selected.name + ".m3u8");
                })
                .show();
    }

    private void doExport(long playlistId, Uri targetUri) {
        importExportManager.export(playlistId, targetUri, requireContext().getContentResolver(),
                new PlaylistImportExportManager.Callback() {
                    @Override
                    public void onSuccess(int songCount) {
                        mainHandler.post(() ->
                                Toast.makeText(requireContext(), R.string.export_success, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError() {
                        mainHandler.post(() ->
                                Toast.makeText(requireContext(), R.string.import_failure, Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void promptImportName(Uri sourceUri) {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.playlist_name_hint);
        input.setText(getString(R.string.playlist_create));
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.settings_import_playlist)
                .setView(input)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        return;
                    }
                    importExportManager.importPlaylist(sourceUri, requireContext().getContentResolver(), name,
                            new PlaylistImportExportManager.Callback() {
                                @Override
                                public void onSuccess(int songCount) {
                                    mainHandler.post(() ->
                                            Toast.makeText(requireContext(), R.string.import_success, Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onError() {
                                    mainHandler.post(() ->
                                            Toast.makeText(requireContext(), R.string.import_failure, Toast.LENGTH_SHORT).show());
                                }
                            });
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
