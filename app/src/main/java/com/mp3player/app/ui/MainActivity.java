package com.mp3player.app.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;


import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.databinding.ActivityMainBinding;
import com.mp3player.app.ui.common.ArtworkLoader;
import com.mp3player.app.ui.common.SystemBarPadding;
import com.mp3player.app.ui.library.LibraryViewModel;
import com.mp3player.app.ui.player.PlayerViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @javax.inject.Inject
    com.mp3player.app.data.UserPreferences userPreferences;

    private ActivityMainBinding binding;
    private PlayerViewModel playerViewModel;
    private NavController navController;
    private boolean hasMedia = false;
    private boolean onPlayerScreen = false;

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
        // targetSdk 35以降は edge-to-edge が強制されるため、全OSで同じ表示になるよう明示的に有効にする
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        applySystemBarInsets();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        setupMiniPlayer();
        requestAudioPermissionIfNeeded();
        requestNotificationPermissionIfNeeded();
    }

    /** 画面上部をステータスバーの下から、ボトムナビをナビゲーションバーの上から始める */
    private void applySystemBarInsets() {
        View content = binding.navHostFragment;
        View bottomNav = binding.bottomNavigation;
        int[] contentBase = paddingOf(content);
        int[] bottomNavBase = paddingOf(bottomNav);
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (root, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            int[] barSizes = {bars.left, bars.top, bars.right, bars.bottom};
            setPadding(content, SystemBarPadding.apply(contentBase, barSizes, SystemBarPadding.TOP));
            setPadding(bottomNav, SystemBarPadding.apply(bottomNavBase, barSizes, SystemBarPadding.BOTTOM));
            // 子のビュー（ボトムナビ等）が同じ余白を重ねて足さないよう、ここで消費する
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private static int[] paddingOf(View view) {
        return new int[]{view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom()};
    }

    private static void setPadding(View view, int[] padding) {
        view.setPadding(padding[0], padding[1], padding[2], padding[3]);
    }

    /** 初めて再生が始まったときに一度だけ、省電力機能による停止について案内する */
    private void maybeShowBatteryPrompt() {
        android.os.PowerManager power = (android.os.PowerManager) getSystemService(POWER_SERVICE);
        boolean ignoring = power != null && power.isIgnoringBatteryOptimizations(getPackageName());
        if (!BatteryOptimizationPrompt.shouldShow(Build.VERSION.SDK_INT, ignoring, userPreferences.isBatteryPromptShown())) {
            return;
        }
        userPreferences.setBatteryPromptShown();
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.battery_prompt_title)
                .setMessage(R.string.battery_prompt_message)
                .setPositiveButton(R.string.battery_prompt_open_settings, (dialog, which) -> {
                    try {
                        startActivity(new android.content.Intent(
                                android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
                    } catch (android.content.ActivityNotFoundException ignored) {
                        // 設定画面が無い機種では何もしない
                    }
                })
                .setNegativeButton(R.string.battery_prompt_later, null)
                .show();
    }

    private void updateMiniPlayerVisibility() {
        binding.miniPlayerRoot.getRoot().setVisibility(
                MiniPlayerVisibility.shouldShow(hasMedia, onPlayerScreen) ? View.VISIBLE : View.GONE);
    }

    private void setupMiniPlayer() {
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            onPlayerScreen = destination.getId() == R.id.playerFragment;
            updateMiniPlayerVisibility();
        });
        binding.miniPlayerRoot.getRoot().setOnClickListener(v ->
                navController.navigate(R.id.playerFragment));
        binding.miniPlayerRoot.buttonPlayPause.setOnClickListener(v -> playerViewModel.togglePlayPause());
        binding.miniPlayerRoot.buttonNext.setOnClickListener(v -> playerViewModel.skipToNext());

        playerViewModel.getState().observe(this, state -> {
            hasMedia = state != null && state.hasMedia;
            if (state != null && state.isPlaying) {
                maybeShowBatteryPrompt();
            }
            updateMiniPlayerVisibility();
            if (!hasMedia) {
                return;
            }
            binding.miniPlayerRoot.textTitle.setText(state.title);
            binding.miniPlayerRoot.textArtist.setText(state.artist);
            binding.miniPlayerRoot.buttonPlayPause.setImageResource(
                    state.isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
            ArtworkLoader.into(binding.miniPlayerRoot.imageArt, state.songId, state.artworkUri);
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
