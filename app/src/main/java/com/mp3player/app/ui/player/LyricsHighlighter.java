package com.mp3player.app.ui.player;

import java.util.List;

/** 再生位置に合わせて強調表示する歌詞の行を決める */
public final class LyricsHighlighter {

    private LyricsHighlighter() {
    }

    /** @return 強調する行の位置。まだどの行も始まっていない・時刻の無い歌詞なら -1 */
    public static int activeIndex(List<LyricLine> lines, long positionMs) {
        int active = -1;
        for (int i = 0; i < lines.size(); i++) {
            long time = lines.get(i).timeMs;
            if (time < 0 || time > positionMs) {
                break;
            }
            active = i;
        }
        return active;
    }
}
