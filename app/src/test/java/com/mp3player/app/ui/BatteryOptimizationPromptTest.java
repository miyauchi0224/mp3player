package com.mp3player.app.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** バックグラウンド再生が省電力機能で止められないよう、一度だけ案内する（No.47） */
public class BatteryOptimizationPromptTest {

    private static final int M = 23;

    @Test
    public void shownOnceWhenAppIsStillOptimized() {
        assertTrue(BatteryOptimizationPrompt.shouldShow(M, false, false));
    }

    @Test
    public void notShownWhenAlreadyExcludedFromOptimization() {
        assertFalse(BatteryOptimizationPrompt.shouldShow(M, true, false));
    }

    @Test
    public void notShownAgainAfterAskingOnce() {
        assertFalse(BatteryOptimizationPrompt.shouldShow(M, false, true));
    }

    @Test
    public void notShownBeforeAndroid6WhereThereIsNoDozeSetting() {
        assertFalse(BatteryOptimizationPrompt.shouldShow(22, false, false));
    }
}
