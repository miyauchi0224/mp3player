package com.mp3player.app.playback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.media3.common.Player;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 保存した再生状態を、今ある曲に合わせて復元する（No.39） */
public class PlaybackRestorerTest {

    private static Set<Long> existing(Long... ids) {
        return new HashSet<>(Arrays.asList(ids));
    }

    private static PlaybackSnapshot snapshot(List<Long> ids, int index, long position) {
        return new PlaybackSnapshot(ids, index, position, 1.0f, Player.REPEAT_MODE_OFF);
    }

    @Test
    public void nothingSaved_restoresNothing() {
        assertNull(PlaybackRestorer.restore(null, existing(1L)));
    }

    @Test
    public void allSongsStillExist_restoresAsSaved() {
        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(
                snapshot(Arrays.asList(1L, 2L, 3L), 1, 5000L), existing(1L, 2L, 3L));

        assertEquals(Arrays.asList(1L, 2L, 3L), r.songIds);
        assertEquals(1, r.index);
        assertEquals(5000L, r.positionMs);
    }

    @Test
    public void songDeletedBeforeCurrent_keepsSameSongAndPosition() {
        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(
                snapshot(Arrays.asList(1L, 2L, 3L), 2, 5000L), existing(2L, 3L));

        assertEquals(Arrays.asList(2L, 3L), r.songIds);
        assertEquals(1, r.index);
        assertEquals(5000L, r.positionMs);
    }

    @Test
    public void currentSongDeleted_movesToNextSongFromStart() {
        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(
                snapshot(Arrays.asList(1L, 2L, 3L), 1, 5000L), existing(1L, 3L));

        assertEquals(Arrays.asList(1L, 3L), r.songIds);
        assertEquals(1, r.index);
        assertEquals(0L, r.positionMs);
    }

    @Test
    public void lastSongDeleted_movesToPreviousSongFromStart() {
        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(
                snapshot(Arrays.asList(1L, 2L, 3L), 2, 5000L), existing(1L, 2L));

        assertEquals(1, r.index);
        assertEquals(0L, r.positionMs);
    }

    @Test
    public void allSongsDeleted_keepsOnlySettings() {
        PlaybackSnapshot saved = new PlaybackSnapshot(Arrays.asList(1L, 2L), 0, 100L, 1.5f, Player.REPEAT_MODE_ALL);

        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(saved, existing());

        assertTrue(r.songIds.isEmpty());
        assertEquals(1.5f, r.speed, 0.0001f);
        assertEquals(Player.REPEAT_MODE_ALL, r.repeatMode);
    }

    @Test
    public void outOfRangeValues_areCorrected() {
        PlaybackSnapshot saved = new PlaybackSnapshot(Collections.singletonList(1L), 5, -10L, 9.0f, 42);

        PlaybackSnapshot.Restored r = PlaybackRestorer.restore(saved, existing(1L));

        assertEquals(0, r.index);
        assertEquals(0L, r.positionMs);
        assertEquals(4.0f, r.speed, 0.0001f);
        assertEquals(Player.REPEAT_MODE_OFF, r.repeatMode);
    }

    @Test
    public void tooSlowSpeed_isRaisedToMinimum() {
        PlaybackSnapshot saved = new PlaybackSnapshot(Collections.singletonList(1L), 0, 0L, 0.1f, Player.REPEAT_MODE_OFF);

        assertEquals(0.5f, PlaybackRestorer.restore(saved, existing(1L)).speed, 0.0001f);
    }
}
