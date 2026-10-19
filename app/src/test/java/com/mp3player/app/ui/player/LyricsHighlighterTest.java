package com.mp3player.app.ui.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LyricsHighlighterTest {

    private static final List<LyricLine> SYNCED = Arrays.asList(
            new LyricLine(0, "A"), new LyricLine(4000, "B"), new LyricLine(8000, "C"));

    @Test
    public void highlightsLastLineThatHasStarted() {
        assertEquals(0, LyricsHighlighter.activeIndex(SYNCED, 0));
        assertEquals(1, LyricsHighlighter.activeIndex(SYNCED, 4000));
        assertEquals(1, LyricsHighlighter.activeIndex(SYNCED, 7999));
        assertEquals(2, LyricsHighlighter.activeIndex(SYNCED, 60000));
    }

    @Test
    public void nothingHighlightedBeforeFirstLine() {
        List<LyricLine> lines = Arrays.asList(new LyricLine(3000, "A"));

        assertEquals(-1, LyricsHighlighter.activeIndex(lines, 1000));
    }

    @Test
    public void unsyncedLyricsAreNeverHighlighted() {
        List<LyricLine> lines = Arrays.asList(new LyricLine(-1, "A"), new LyricLine(-1, "B"));

        assertEquals(-1, LyricsHighlighter.activeIndex(lines, 5000));
    }

    @Test
    public void noLyrics_nothingHighlighted() {
        assertEquals(-1, LyricsHighlighter.activeIndex(Collections.emptyList(), 5000));
    }
}
