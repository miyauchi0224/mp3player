package com.mp3player.app.ui.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GridSpanTest {

    @Test
    public void fitsAsManyCellsAsTheWidthAllows() {
        assertEquals(3, GridSpan.count(411, 120));
        assertEquals(7, GridSpan.count(900, 120));
    }

    @Test
    public void neverFewerThanTwoColumns() {
        assertEquals(2, GridSpan.count(200, 120));
        assertEquals(2, GridSpan.count(0, 120));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNonPositiveCellWidth() {
        GridSpan.count(411, 0);
    }
}
