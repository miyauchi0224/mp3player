package com.mp3player.app.ui.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SongViewModeTest {

    @Test
    public void toggle_switchesBetweenListAndGrid() {
        assertEquals(SongViewMode.GRID, SongViewMode.LIST.toggle());
        assertEquals(SongViewMode.LIST, SongViewMode.GRID.toggle());
    }

    @Test
    public void fromKey_restoresSavedMode() {
        assertEquals(SongViewMode.GRID, SongViewMode.fromKey(SongViewMode.GRID.key()));
        assertEquals(SongViewMode.LIST, SongViewMode.fromKey(SongViewMode.LIST.key()));
    }

    @Test
    public void fromKey_defaultsToListWhenNothingSaved() {
        assertEquals(SongViewMode.LIST, SongViewMode.fromKey(null));
    }

    @Test
    public void fromKey_defaultsToListForUnknownValue() {
        assertEquals(SongViewMode.LIST, SongViewMode.fromKey("tiles"));
    }
}
