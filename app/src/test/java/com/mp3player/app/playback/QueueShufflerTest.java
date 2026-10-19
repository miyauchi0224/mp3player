package com.mp3player.app.playback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** プレイヤー画面のシャッフル切り替え（No.45）。再生中の曲を止めずにキューを並べ替える */
public class QueueShufflerTest {

    private static final List<String> QUEUE = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");

    @Test
    public void shuffle_putsCurrentSongFirst() {
        List<String> shuffled = QueueShuffler.shuffleAroundCurrent(QUEUE, 3, new Random(1));

        assertEquals("d", shuffled.get(0));
    }

    @Test
    public void shuffle_keepsEverySongExactlyOnce() {
        for (int seed = 0; seed < 100; seed++) {
            List<String> shuffled = new ArrayList<>(QueueShuffler.shuffleAroundCurrent(QUEUE, 5, new Random(seed)));
            Collections.sort(shuffled);
            assertEquals(QUEUE, shuffled);
        }
    }

    @Test
    public void shuffle_actuallyChangesOrderOfTheRest() {
        List<String> shuffled = QueueShuffler.shuffleAroundCurrent(QUEUE, 0, new Random(42));

        assertNotEquals(QUEUE, shuffled);
    }

    @Test
    public void unshuffle_returnsOriginalOrderAndPositionOfCurrentSong() {
        QueueShuffler.Restored<String> restored = QueueShuffler.restoreOriginal(QUEUE, "f");

        assertEquals(QUEUE, restored.queue);
        assertEquals(5, restored.currentIndex);
    }

    @Test
    public void unshuffle_whenCurrentSongIsUnknown_startsFromTop() {
        QueueShuffler.Restored<String> restored = QueueShuffler.restoreOriginal(QUEUE, "zzz");

        assertEquals(0, restored.currentIndex);
    }

    @Test
    public void emptyQueueStaysEmpty() {
        assertEquals(Collections.emptyList(), QueueShuffler.shuffleAroundCurrent(Collections.emptyList(), 0, new Random(1)));
    }
}
