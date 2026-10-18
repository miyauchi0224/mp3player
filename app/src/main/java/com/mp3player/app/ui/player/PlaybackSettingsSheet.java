package com.mp3player.app.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.databinding.DialogPlaybackSettingsBinding;
import com.mp3player.app.playback.PlaybackUiState;

@AndroidEntryPoint
public class PlaybackSettingsSheet extends BottomSheetDialogFragment {

    private DialogPlaybackSettingsBinding binding;
    private PlayerViewModel viewModel;
    private boolean updatingFromState = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = DialogPlaybackSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(PlayerViewModel.class);

        binding.seekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && !updatingFromState) {
                    viewModel.setVolume(progress / 100f);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float speed = 0.5f + progress * 0.1f;
                binding.textSpeedValue.setText(String.format(Locale.getDefault(), "%.1fx", speed));
                if (fromUser && !updatingFromState) {
                    viewModel.setPlaybackSpeed(speed);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.chip15.setOnClickListener(v -> viewModel.startSleepTimer(15));
        binding.chip30.setOnClickListener(v -> viewModel.startSleepTimer(30));
        binding.chip60.setOnClickListener(v -> viewModel.startSleepTimer(60));
        binding.chipCancel.setOnClickListener(v -> viewModel.cancelSleepTimer());

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(@Nullable PlaybackUiState state) {
        if (state == null || binding == null) {
            return;
        }
        updatingFromState = true;
        binding.seekVolume.setProgress(Math.round(state.volume * 100));
        int speedProgress = Math.round((state.speed - 0.5f) / 0.1f);
        binding.seekSpeed.setProgress(speedProgress);
        binding.textSpeedValue.setText(String.format(Locale.getDefault(), "%.1fx", state.speed));
        updatingFromState = false;

        if (state.sleepTimerActive) {
            long minutesLeft = Math.max(1, (state.sleepTimerEndAt - System.currentTimeMillis()) / 60_000);
            binding.textSleepTimerStatus.setText(getString(R.string.sleep_timer_active, minutesLeft));
        } else {
            binding.textSleepTimerStatus.setText(R.string.sleep_timer_off);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
