package com.mp3player.app.ui.common;

/**
 * edge-to-edge 表示で、システムバー（ステータスバー・ナビゲーションバー）に重ならないよう余白を足す計算。
 * 余白・バーの大きさはどちらも {left, top, right, bottom} の配列で扱う。
 * 左右（画面の切り欠きや横向きのナビゲーションバー）は、上下どちらかを指定したときに足す。
 */
public final class SystemBarPadding {

    /** ステータスバーの高さを上に足す */
    public static final int TOP = 1;
    /** ナビゲーションバーの高さを下に足す */
    public static final int BOTTOM = 2;

    private SystemBarPadding() {
    }

    public static int[] apply(int[] basePadding, int[] bars, int sides) {
        int[] result = basePadding.clone();
        if (sides == 0) {
            return result;
        }
        result[0] += bars[0];
        result[2] += bars[2];
        if ((sides & TOP) != 0) {
            result[1] += bars[1];
        }
        if ((sides & BOTTOM) != 0) {
            result[3] += bars[3];
        }
        return result;
    }
}
