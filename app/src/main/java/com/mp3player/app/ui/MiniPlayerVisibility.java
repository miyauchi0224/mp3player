package com.mp3player.app.ui;

/** ミニプレイヤーを表示するかどうか。プレイヤー画面では同じ曲を表示しているので隠す */
public final class MiniPlayerVisibility {

    private MiniPlayerVisibility() {
    }

    public static boolean shouldShow(boolean hasMedia, boolean onPlayerScreen) {
        return hasMedia && !onPlayerScreen;
    }
}
