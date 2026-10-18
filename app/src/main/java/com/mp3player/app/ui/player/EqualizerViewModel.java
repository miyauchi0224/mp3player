package com.mp3player.app.ui.player;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import com.mp3player.app.playback.PlayerController;

@HiltViewModel
public class EqualizerViewModel extends ViewModel {

    private final PlayerController playerController;

    @Inject
    public EqualizerViewModel(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void loadState(Consumer<Bundle> callback) {
        playerController.getEqualizerState(callback);
    }

    public void setEnabled(boolean enabled) {
        playerController.setEqualizerEnabled(enabled);
    }

    public void setBandLevel(int band, int level) {
        playerController.setEqualizerBandLevel(band, level);
    }

    public void setPreset(int preset, Consumer<Bundle> callback) {
        playerController.setEqualizerPreset(preset, callback);
    }
}
