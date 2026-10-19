package com.mp3player.app.playback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 再生状態（キュー・再生中の位置・速度・リピート）。次回起動時に復元するため文字列にして保存する。
 * 形式: "v1|曲ID,曲ID,...|再生中の位置(キュー内)|再生位置(ms)|速度|リピート"
 */
public final class PlaybackSnapshot {

    private static final String VERSION = "v1";
    private static final String SEPARATOR = "\\|";

    public final List<Long> songIds;
    public final int index;
    public final long positionMs;
    public final float speed;
    /** androidx.media3.common.Player の REPEAT_MODE_* */
    public final int repeatMode;

    public PlaybackSnapshot(List<Long> songIds, int index, long positionMs, float speed, int repeatMode) {
        this.songIds = Collections.unmodifiableList(new ArrayList<>(songIds));
        this.index = index;
        this.positionMs = positionMs;
        this.speed = speed;
        this.repeatMode = repeatMode;
    }

    public String encode() {
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < songIds.size(); i++) {
            if (i > 0) ids.append(',');
            ids.append(songIds.get(i));
        }
        return VERSION + "|" + ids + "|" + index + "|" + positionMs + "|" + speed + "|" + repeatMode;
    }

    /** 保存が無い・壊れている・形式が違う場合は null */
    public static PlaybackSnapshot decode(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String[] parts = text.split(SEPARATOR, -1);
        if (parts.length != 6 || !VERSION.equals(parts[0])) {
            return null;
        }
        try {
            List<Long> ids = new ArrayList<>();
            if (!parts[1].isEmpty()) {
                for (String id : parts[1].split(",")) {
                    ids.add(Long.parseLong(id));
                }
            }
            return new PlaybackSnapshot(ids, Integer.parseInt(parts[2]), Long.parseLong(parts[3]),
                    Float.parseFloat(parts[4]), Integer.parseInt(parts[5]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 今ある曲に合わせて補正した、復元する内容 */
    public static final class Restored {
        public final List<Long> songIds;
        public final int index;
        public final long positionMs;
        public final float speed;
        public final int repeatMode;

        Restored(List<Long> songIds, int index, long positionMs, float speed, int repeatMode) {
            this.songIds = Collections.unmodifiableList(new ArrayList<>(songIds));
            this.index = index;
            this.positionMs = positionMs;
            this.speed = speed;
            this.repeatMode = repeatMode;
        }
    }
}
