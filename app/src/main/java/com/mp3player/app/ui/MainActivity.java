package com.mp3player.app.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.databinding.ActivityMainBinding;
import com.mp3player.app.ui.library.LibraryViewModel;
import com.mp3player.app.ui.player.PlayerViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PlayerViewModel playerViewModel;
    private NavController navController;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    new ViewModelProvider(this).get(LibraryViewModel.class).rescan();
                }
            });

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                // 通知が拒否されてもバックグラウンド再生自体は継続可能なため、追加処理は不要
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        setupMiniPlayer();
        requestAudioPermissionIfNeeded();
        requestNotificationPermissionIfNeeded();
    }

    private void setupMiniPlayer() {
        binding.miniPlayerRoot.getRoot().setOnClickListener(v ->
                navController.navigate(R.id.playerFragment));
        binding.miniPlayerRoot.buttonPlayPause.setOnClickListener(v -> playerViewModel.togglePlayPause());
        binding.miniPlayerRoot.buttonNext.setOnClickListener(v -> playerViewModel.skipToNext());

        playerViewModel.getState().observe(this, state -> {
            if (state == null || !state.hasMedia) {
                binding.miniPlayerRoot.getRoot().setVisibility(View.GONE);
                return;
            }
            binding.miniPlayerRoot.getRoot().setVisibility(View.VISIBLE);
            binding.miniPlayerRoot.textTitle.setText(state.title);
            binding.miniPlayerRoot.textArtist.setText(state.artist);
            binding.miniPlayerRoot.buttonPlayPause.setImageResource(
                    state.isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            Glide.with(binding.miniPlayerRoot.imageArt)
                    .load(state.artworkUri)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .into(binding.miniPlayerRoot.imageArt);
        });
    }

    private void requestAudioPermissionIfNeeded() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission);
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }
}
