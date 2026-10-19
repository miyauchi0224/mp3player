package com.mp3player.app.ui.player;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.Player;
import androidx.navigation.Navigation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.databinding.FragmentPlayerBinding;
import com.mp3player.app.playback.PlaybackUiState;
import com.mp3player.app.ui.common.ArtworkLoader;
import com.mp3player.app.ui.playlist.AddToPlaylistDialogFragment;

@AndroidEntryPoint
public class PlayerFragment extends Fragment {

    private FragmentPlayerBinding binding;
    private PlayerViewModel viewModel;
    private Song currentSong;
    private long lyricsLoadedForSongId = -1;
    private List<LyricLine> currentLyrics = new ArrayList<>();
    private boolean lyricsVisible = false;
    private static final float MIN_SPEED = 0.5f;
    private static final float MAX_SPEED = 4.0f;
    private static final float SPEED_STEP = 0.1f;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isTrackingTouch = false;
    private float currentSpeed = 1f;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (binding == null) return;
            long position = viewModel.getCurrentPosition();
            binding.seekBar.setProgress((int) position);
            binding.textPosition.setText(formatDuration(position));
            if (lyricsVisible) {
                updateLyricsHighlight(position);
            }
            handler.postDelayed(this, 500);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        binding.buttonPlayPause.setOnClickListener(v -> viewModel.togglePlayPause());
        binding.buttonNext.setOnClickListener(v -> viewModel.skipToNext());
        binding.buttonPrevious.setOnClickListener(v -> viewModel.skipToPrevious());
        binding.buttonRepeat.setOnClickListener(v -> viewModel.cycleRepeatMode());
        binding.buttonFavorite.setOnClickListener(v -> {
            if (currentSong != null) {
                viewModel.toggleFavorite(currentSong);
            }
        });
        binding.buttonAddToPlaylist.setOnClickListener(v -> {
            if (currentSong != null) {
                AddToPlaylistDialogFragment.newInstance(currentSong.id)
                        .show(getChildFragmentManager(), "add_to_playlist");
            }
        });
        binding.buttonPlaybackSettings.setOnClickListener(v ->
                new PlaybackSettingsSheet().show(getChildFragmentManager(), "playback_settings"));
        binding.buttonEqualizer.setOnClickListener(v ->
                new EqualizerFragment().show(getChildFragmentManager(), "equalizer"));
        binding.buttonLyrics.setOnClickListener(v -> toggleLyrics());
        binding.buttonSpeedDown.setOnClickListener(v -> changeSpeed(currentSpeed - SPEED_STEP));
        binding.buttonSpeedUp.setOnClickListener(v -> changeSpeed(currentSpeed + SPEED_STEP));
        binding.textSpeed.setOnClickListener(v -> changeSpeed(1f));
        renderSpeed(currentSpeed);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.textPosition.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = false;
                viewModel.seekTo(seekBar.getProgress());
            }
        });

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
        viewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            currentSong = song;
            if (song != null) {
                binding.buttonFavorite.setImageResource(
                        song.favorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                loadLyricsIfNeeded(song);
            }
        });
    }

    private void changeSpeed(float speed) {
        // 0.1刻みに丸めて浮動小数点の誤差を吸収する
        float rounded = Math.round(speed * 10f) / 10f;
        float clamped = Math.max(MIN_SPEED, Math.min(MAX_SPEED, rounded));
        renderSpeed(clamped);
        viewModel.setPlaybackSpeed(clamped);
    }

    private void renderSpeed(float speed) {
        currentSpeed = Math.round(speed * 10f) / 10f;
        binding.textSpeed.setText(String.format(Locale.getDefault(), "%.1fx", currentSpeed));
        binding.buttonSpeedDown.setEnabled(currentSpeed > MIN_SPEED);
        binding.buttonSpeedUp.setEnabled(currentSpeed < MAX_SPEED);
    }

    private void toggleLyrics() {
        lyricsVisible = !lyricsVisible;
        binding.lyricsContainer.setVisibility(lyricsVisible ? View.VISIBLE : View.GONE);
        binding.imageArt.setVisibility(lyricsVisible ? View.GONE : View.VISIBLE);
        if (lyricsVisible) {
            updateLyricsHighlight(viewModel.getCurrentPosition());
        }
    }

    private void loadLyricsIfNeeded(Song song) {
        if (song.id == lyricsLoadedForSongId) {
            return;
        }
        lyricsLoadedForSongId = song.id;
        currentLyrics = new ArrayList<>();
        binding.textLyrics.setText(R.string.lyrics_empty);

        new Thread(() -> {
            File lrcFile = LyricsParser.findLrcFile(song.data);
            List<LyricLine> parsed = lrcFile != null ? LyricsParser.parse(lrcFile) : new ArrayList<>();
            handler.post(() -> {
                if (binding == null || song.id != lyricsLoadedForSongId) {
                    return;
                }
                currentLyrics = parsed;
                if (parsed.isEmpty()) {
                    binding.textLyrics.setText(R.string.lyrics_empty);
                } else if (lyricsVisible) {
                    updateLyricsHighlight(viewModel.getCurrentPosition());
                }
            });
        }).start();
    }

    private void updateLyricsHighlight(long position) {
        if (binding == null || currentLyrics.isEmpty()) {
            return;
        }
        int activeIndex = -1;
        for (int i = 0; i < currentLyrics.size(); i++) {
            if (currentLyrics.get(i).timeMs <= position) {
                activeIndex = i;
            } else {
                break;
            }
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0; i < currentLyrics.size(); i++) {
            if (i > 0) {
                builder.append('\n');
            }
            int start = builder.length();
            builder.append(currentLyrics.get(i).text);
            if (i == activeIndex) {
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, builder.length(), 0);
            }
        }
        binding.textLyrics.setText(builder);
    }

    private void renderState(@Nullable PlaybackUiState state) {
        if (state == null || binding == null) return;

        binding.textTitle.setText(state.title);
        binding.textArtist.setText(state.artist);
        binding.buttonPlayPause.setImageResource(state.isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        binding.seekBar.setMax((int) state.duration);
        binding.textDuration.setText(formatDuration(state.duration));
        renderSpeed(state.speed);

        ArtworkLoader.into(binding.imageArt, state.songId, state.artworkUri);

        if (state.repeatMode == Player.REPEAT_MODE_ONE) {
            binding.buttonRepeat.setImageResource(R.drawable.ic_repeat_one);
            binding.buttonRepeat.setAlpha(1f);
        } else if (state.repeatMode == Player.REPEAT_MODE_ALL) {
            binding.buttonRepeat.setImageResource(R.drawable.ic_repeat);
            binding.buttonRepeat.setAlpha(1f);
        } else {
            binding.buttonRepeat.setImageResource(R.drawable.ic_repeat);
            binding.buttonRepeat.setAlpha(0.4f);
        }

        if (state.isPlaying) {
            handler.removeCallbacks(ticker);
            handler.post(ticker);
        } else {
            handler.removeCallbacks(ticker);
            if (!isTrackingTouch) {
                binding.seekBar.setProgress((int) viewModel.getCurrentPosition());
                binding.textPosition.setText(formatDuration(viewModel.getCurrentPosition()));
            }
        }
    }

    private static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(ticker);
        binding = null;
    }
}
