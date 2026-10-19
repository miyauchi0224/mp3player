package com.mp3player.app.playback;

import androidx.media3.common.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 保存した再生状態を、今ある曲に合わせて復元する内容を決める */
public final class PlaybackRestorer {

    private PlaybackRestorer() {
    }

    /**
     * @param existingSongIds 現在ライブラリにある曲のID。保存後に削除された曲はキューから外す
     * @return 復元する内容。保存が無ければ null。曲が全て無くなっても速度・リピート設定は返す
     */
    public static PlaybackSnapshot.Restored restore(PlaybackSnapshot saved, Set<Long> existingSongIds) {
        if (saved == null) {
            return null;
        }
        int savedIndex = Math.max(0, Math.min(saved.index, saved.songIds.size() - 1));
        List<Long> kept = new ArrayList<>();
        int index = -1;
        long position = Math.max(0L, saved.positionMs);
        for (int i = 0; i < saved.songIds.size(); i++) {
            long id = saved.songIds.get(i);
            if (!existingSongIds.contains(id)) {
                continue;
            }
            if (i == savedIndex) {
                index = kept.size();
            } else if (i > savedIndex && index < 0) {
                // 再生中だった曲が消えていたら、次の曲の先頭から
                index = kept.size();
                position = 0L;
            }
            kept.add(id);
        }
        if (index < 0) {
            // 後ろの曲も無ければ、残っている最後の曲の先頭から
            index = Math.max(0, kept.size() - 1);
            position = 0L;
        }
        return new PlaybackSnapshot.Restored(kept, index, position,
                PlaybackSpeed.clamp(saved.speed), validRepeatMode(saved.repeatMode));
    }

    private static int validRepeatMode(int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_ONE:
            case Player.REPEAT_MODE_ALL:
                return repeatMode;
            default:
                return Player.REPEAT_MODE_OFF;
        }
    }
}
