package com.mp3player.app.ui.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.databinding.DialogEqualizerBinding;

@AndroidEntryPoint
public class EqualizerFragment extends BottomSheetDialogFragment {

    private DialogEqualizerBinding binding;
    private EqualizerViewModel viewModel;
    private boolean updatingFromState = false;
    private int minLevel = -1500;
    private int maxLevel = 1500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = DialogEqualizerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EqualizerViewModel.class);

        binding.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!updatingFromState) {
                viewModel.setEnabled(isChecked);
            }
        });

        viewModel.loadState(this::renderState);
    }

    private void renderState(Bundle state) {
        if (binding == null || state == null) {
            return;
        }
        boolean available = state.getBoolean("available", false);
        if (!available) {
            binding.textUnavailable.setVisibility(View.VISIBLE);
            binding.textUnavailable.setText(R.string.lyrics_empty);
            binding.switchEnabled.setVisibility(View.GONE);
            return;
        }

        updatingFromState = true;
        binding.switchEnabled.setChecked(state.getBoolean("enabled", false));
        updatingFromState = false;

        minLevel = state.getInt("minLevel", -1500);
        maxLevel = state.getInt("maxLevel", 1500);
        int numBands = state.getInt("numBands", 0);
        int[] levels = state.getIntArray("levels");
        int[] centerFreqs = state.getIntArray("centerFreqs");
        String[] presetNames = state.getStringArray("presetNames");
        int currentPreset = state.getInt("currentPreset", -1);

        buildPresetChips(presetNames, currentPreset);
        buildBandRows(numBands, levels, centerFreqs);
    }

    private void buildPresetChips(@Nullable String[] presetNames, int currentPreset) {
        binding.chipGroupPresets.removeAllViews();
        if (presetNames == null) {
            return;
        }
        for (int i = 0; i < presetNames.length; i++) {
            Chip chip = new Chip(requireContext());
            chip.setText(presetNames[i]);
            chip.setCheckable(true);
            chip.setChecked(i == currentPreset);
            int presetIndex = i;
            chip.setOnClickListener(v -> viewModel.setPreset(presetIndex, this::renderState));
            binding.chipGroupPresets.addView(chip);
        }
    }

    private void buildBandRows(int numBands, @Nullable int[] levels, @Nullable int[] centerFreqs) {
        binding.bandsContainer.removeAllViews();
        if (levels == null || centerFreqs == null) {
            return;
        }
        for (int band = 0; band < numBands; band++) {
            android.widget.LinearLayout rowLayout = new android.widget.LinearLayout(requireContext());
            rowLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            rowLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);

            TextView freqLabel = new TextView(requireContext());
            freqLabel.setText(centerFreqs[band] + "Hz");
            freqLabel.setWidth(dpToPx(64));
            freqLabel.setTextColor(getResources().getColor(R.color.on_surface_variant, null));

            SeekBar seekBar = new SeekBar(requireContext());
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            seekBar.setLayoutParams(params);
            seekBar.setMax(maxLevel - minLevel);
            seekBar.setProgress(levels[band] - minLevel);
            int bandIndex = band;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        viewModel.setBandLevel(bandIndex, progress + minLevel);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            rowLayout.addView(freqLabel);
            rowLayout.addView(seekBar);
            binding.bandsContainer.addView(rowLayout);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
