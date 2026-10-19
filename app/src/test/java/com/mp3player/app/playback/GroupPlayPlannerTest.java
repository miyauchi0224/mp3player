package com.mp3player.app.playback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import androidx.media3.common.Player;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GroupPlayPlannerTest {

    private static List<Integer> identity(int n) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) list.add(i);
        return list;
    }

    @Test
    public void shuffle_playsEverySongExactlyOnce() {
        // 不具合の再発防止: プレーヤー側のシャッフルに任せると開始位置より前の曲が再生されなかった
        for (int seed = 0; seed < 200; seed++) {
            PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.SHUFFLE, 8, new Random(seed));

            List<Integer> sorted = new ArrayList<>(plan.order);
            Collections.sort(sorted);
            assertEquals(identity(8), sorted);
        }
    }

    @Test
    public void shuffle_orderIsDecidedByGivenRandom() {
        List<Integer> expected = identity(8);
        Collections.shuffle(expected, new Random(42));

        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.SHUFFLE, 8, new Random(42));

        assertEquals(expected, plan.order);
    }

    @Test
    public void shuffle_actuallyChangesTheOrder() {
        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.SHUFFLE, 8, new Random(42));

        assertNotEquals(identity(8), plan.order);
    }

    @Test
    public void shuffle_playsThroughOnceWithoutRepeat() {
        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.SHUFFLE, 5, new Random(42));

        assertEquals(Player.REPEAT_MODE_OFF, plan.repeatMode);
    }

    @Test
    public void repeatOne_keepsListOrderAndRepeatsFirstSong() {
        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.REPEAT_ONE, 5, new Random(42));

        assertEquals(identity(5), plan.order);
        assertEquals(Player.REPEAT_MODE_ONE, plan.repeatMode);
    }

    @Test
    public void repeatAll_keepsListOrderAndRepeatsWholeGroup() {
        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.REPEAT_ALL, 5, new Random(42));

        assertEquals(identity(5), plan.order);
        assertEquals(Player.REPEAT_MODE_ALL, plan.repeatMode);
    }

    @Test
    public void singleSong_shuffleJustPlaysIt() {
        PlayPlan plan = GroupPlayPlanner.plan(GroupPlayMode.SHUFFLE, 1, new Random(7));

        assertEquals(Arrays.asList(0), plan.order);
    }

    @Test
    public void applyTo_reordersSongsByPlan() {
        PlayPlan plan = new PlayPlan(Arrays.asList(2, 0, 1), Player.REPEAT_MODE_OFF);

        assertEquals(Arrays.asList("c", "a", "b"), plan.applyTo(Arrays.asList("a", "b", "c")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyGroup_isRejected() {
        GroupPlayPlanner.plan(GroupPlayMode.REPEAT_ALL, 0, new Random(42));
    }
}
