package com.mp3player.app.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MiniPlayerVisibilityTest {

    @Test
    public void shownWhileSomethingIsLoadedOnOtherScreens() {
        assertTrue(MiniPlayerVisibility.shouldShow(true, false));
    }

    @Test
    public void hiddenOnPlayerScreenBecauseItShowsTheSameSong() {
        assertFalse(MiniPlayerVisibility.shouldShow(true, true));
    }

    @Test
    public void hiddenWhenNothingIsLoaded() {
        assertFalse(MiniPlayerVisibility.shouldShow(false, false));
        assertFalse(MiniPlayerVisibility.shouldShow(false, true));
    }
}
