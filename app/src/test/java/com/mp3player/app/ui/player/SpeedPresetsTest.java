package com.mp3player.app.ui.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mp3player.app.playback.PlaybackSpeed;

/** 再生速度の候補（No.44） */
public class SpeedPresetsTest {

    @Test
    public void presetsAreAscendingAndWithinAllowedRange() {
        float[] presets = SpeedPresets.VALUES;
        for (int i = 0; i < presets.length; i++) {
            assertTrue(presets[i] >= PlaybackSpeed.MIN && presets[i] <= PlaybackSpeed.MAX);
            if (i > 0) assertTrue(presets[i] > presets[i - 1]);
        }
    }

    @Test
    public void presetsCoverSlowestNormalAndFastest() {
        float[] presets = SpeedPresets.VALUES;
        assertEquals(PlaybackSpeed.MIN, presets[0], 0.0001f);
        assertEquals(PlaybackSpeed.MAX, presets[presets.length - 1], 0.0001f);
        assertTrue(SpeedPresets.indexOf(1.0f) >= 0);
    }

    @Test
    public void indexOf_findsCurrentSpeedEvenWithRoundingError() {
        assertEquals(SpeedPresets.indexOf(1.5f), SpeedPresets.indexOf(1.5000001f));
        assertTrue(SpeedPresets.indexOf(1.5f) >= 0);
    }

    @Test
    public void indexOf_isMinusOneForSpeedNotInList() {
        assertEquals(-1, SpeedPresets.indexOf(1.3f));
    }

    @Test
    public void labelsShowMultiplier() {
        assertEquals("1.0x", SpeedPresets.label(1.0f));
        assertEquals("0.75x", SpeedPresets.label(0.75f));
        assertEquals("2.0x", SpeedPresets.label(2.0f));
    }
}
