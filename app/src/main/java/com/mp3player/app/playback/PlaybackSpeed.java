package com.mp3player.app.playback;

/** 再生速度の範囲。プレイヤー画面と再生状態の復元で共通に使う */
public final class PlaybackSpeed {

    public static final float MIN = 0.5f;
    public static final float MAX = 4.0f;

    private PlaybackSpeed() {
    }

    public static float clamp(float speed) {
        return Math.max(MIN, Math.min(MAX, speed));
    }
}
