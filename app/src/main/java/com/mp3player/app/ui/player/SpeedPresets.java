package com.mp3player.app.ui.player;

import java.util.Locale;

/** プレイヤー画面で速度の数値をタップしたときに出す候補 */
public final class SpeedPresets {

    public static final float[] VALUES = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f, 4.0f};

    private static final float TOLERANCE = 0.001f;

    private SpeedPresets() {
    }

    /** @return 候補での位置。候補に無い速度なら -1 */
    public static int indexOf(float speed) {
        for (int i = 0; i < VALUES.length; i++) {
            if (Math.abs(VALUES[i] - speed) < TOLERANCE) {
                return i;
            }
        }
        return -1;
    }

    /** 1.0x / 0.75x のように、必要な桁数だけ表示する */
    public static String label(float speed) {
        float rounded = Math.round(speed * 100f) / 100f;
        boolean oneDecimal = Math.abs(rounded * 10f - Math.round(rounded * 10f)) < TOLERANCE;
        return String.format(Locale.ROOT, oneDecimal ? "%.1fx" : "%.2fx", rounded);
    }
}
