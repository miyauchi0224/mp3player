package com.mp3player.app.playback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.media3.common.Player;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/** 再生状態（キュー・位置・速度・リピート）を保存用の文字列にする（No.39） */
public class PlaybackSnapshotTest {

    @Test
    public void encodeThenDecode_restoresAllFields() {
        PlaybackSnapshot original = new PlaybackSnapshot(
                Arrays.asList(19L, 23L, 25L), 1, 4200L, 1.5f, Player.REPEAT_MODE_ALL);

        PlaybackSnapshot decoded = PlaybackSnapshot.decode(original.encode());

        assertEquals(Arrays.asList(19L, 23L, 25L), decoded.songIds);
        assertEquals(1, decoded.index);
        assertEquals(4200L, decoded.positionMs);
        assertEquals(1.5f, decoded.speed, 0.0001f);
        assertEquals(Player.REPEAT_MODE_ALL, decoded.repeatMode);
    }

    @Test
    public void emptyQueue_canBeSavedToKeepSettings() {
        PlaybackSnapshot original = new PlaybackSnapshot(Collections.emptyList(), 0, 0L, 2.0f, Player.REPEAT_MODE_ONE);

        PlaybackSnapshot decoded = PlaybackSnapshot.decode(original.encode());

        assertEquals(Collections.emptyList(), decoded.songIds);
        assertEquals(2.0f, decoded.speed, 0.0001f);
        assertEquals(Player.REPEAT_MODE_ONE, decoded.repeatMode);
    }

    @Test
    public void decode_returnsNullWhenNothingSaved() {
        assertNull(PlaybackSnapshot.decode(null));
        assertNull(PlaybackSnapshot.decode(""));
    }

    @Test
    public void decode_returnsNullForBrokenData() {
        assertNull(PlaybackSnapshot.decode("garbage"));
        assertNull(PlaybackSnapshot.decode("v1|1,x,3|0|0|1.0|0"));
        assertNull(PlaybackSnapshot.decode("v1|1,2|0|0|1.0"));
    }

    @Test
    public void decode_returnsNullForUnknownFormatVersion() {
        assertNull(PlaybackSnapshot.decode("v99|1,2|0|0|1.0|0"));
    }
}
