package com.mp3player.app.ui.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** 曲ファイルに対応する .lrc ファイル名（同じ名前で拡張子だけ違う） */
public class LrcNamesTest {

    @Test
    public void replacesExtensionWithLrc() {
        assertEquals("morning_walk.lrc", LrcNames.forSongFile("morning_walk.mp3"));
        assertEquals("my.song.v2.lrc", LrcNames.forSongFile("my.song.v2.mp3"));
    }

    @Test
    public void acceptsFullPath() {
        assertEquals("さくら道.lrc", LrcNames.forSongFile("/storage/emulated/0/Music/さくら道.mp3"));
    }

    @Test
    public void fileWithoutExtensionGetsLrcAppended() {
        assertEquals("track01.lrc", LrcNames.forSongFile("track01"));
    }

    @Test
    public void noFileName_hasNoLrc() {
        assertNull(LrcNames.forSongFile(null));
        assertNull(LrcNames.forSongFile(""));
    }

    @Test
    public void matchingIgnoresLetterCase() {
        assertTrue(LrcNames.matches("Morning_Walk.LRC", "morning_walk.lrc"));
        assertFalse(LrcNames.matches("rainy_day.lrc", "morning_walk.lrc"));
        assertFalse(LrcNames.matches(null, "morning_walk.lrc"));
    }
}
