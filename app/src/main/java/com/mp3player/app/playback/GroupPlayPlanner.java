package com.mp3player.app.playback;

import androidx.media3.common.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 再生モードから、グループ（アルバム／フォルダ）の再生方法を決める。
 * シャッフルはプレーヤーのシャッフル機能を使わず、ここで再生順を並べ替えて決める
 * （プレーヤー側に任せると、開始位置より前にシャッフルされた曲が再生されないため）。
 */
public final class GroupPlayPlanner {

    private GroupPlayPlanner() {
    }

    public static PlayPlan plan(GroupPlayMode mode, int songCount, Random random) {
        if (songCount <= 0) {
            throw new IllegalArgumentException("songCount must be positive: " + songCount);
        }
        List<Integer> order = new ArrayList<>(songCount);
        for (int i = 0; i < songCount; i++) {
            order.add(i);
        }
        switch (mode) {
            case SHUFFLE:
                Collections.shuffle(order, random);
                return new PlayPlan(order, Player.REPEAT_MODE_OFF);
            case REPEAT_ONE:
                return new PlayPlan(order, Player.REPEAT_MODE_ONE);
            case REPEAT_ALL:
            default:
                return new PlayPlan(order, Player.REPEAT_MODE_ALL);
        }
    }
}
