package com.mp3player.app.ui.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class LyricsParserTest {

    @Test
    public void parsesTimeTagsIntoMilliseconds() {
        List<LyricLine> lines = LyricsParser.parse("[00:04.00]一行目\n[01:02.5]二行目\n[00:10]三行目");

        assertEquals(3, lines.size());
        assertEquals(4000, lines.get(0).timeMs);
        assertEquals("一行目", lines.get(0).text);
        assertEquals(10000, lines.get(1).timeMs);
        assertEquals(62500, lines.get(2).timeMs);
    }

    @Test
    public void lineWithSeveralTagsIsRepeated() {
        List<LyricLine> lines = LyricsParser.parse("[00:01.00][00:20.00]サビ");

        assertEquals(2, lines.size());
        assertEquals(1000, lines.get(0).timeMs);
        assertEquals(20000, lines.get(1).timeMs);
        assertEquals("サビ", lines.get(1).text);
    }

    @Test
    public void metadataTagsAreIgnored() {
        List<LyricLine> lines = LyricsParser.parse("[ti:Morning Walk]\n[ar:Sample Trio]\n[00:00.00]始まり");

        assertEquals(1, lines.size());
        assertEquals("始まり", lines.get(0).text);
    }

    @Test
    public void byteOrderMarkAndWindowsLineEndingsAreHandled() {
        List<LyricLine> lines = LyricsParser.parse("﻿[00:00.00]A\r\n[00:01.00]B\r\n");

        assertEquals(2, lines.size());
        assertEquals("A", lines.get(0).text);
        assertEquals("B", lines.get(1).text);
    }

    @Test
    public void textWithoutTimeTags_isShownAsUnsyncedLines() {
        List<LyricLine> lines = LyricsParser.parse("一行目\n\n二行目");

        assertEquals(2, lines.size());
        assertEquals("一行目", lines.get(0).text);
        assertTrue(lines.get(0).timeMs < 0);
        assertTrue(lines.get(1).timeMs < 0);
    }

    @Test
    public void emptyOrNullText_hasNoLines() {
        assertTrue(LyricsParser.parse("").isEmpty());
        assertTrue(LyricsParser.parse(null).isEmpty());
    }
}
