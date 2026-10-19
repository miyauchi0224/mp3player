package com.mp3player.app.ui.common;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

/** edge-to-edge 表示で、システムバーに重ならないよう余白を足す計算 */
public class SystemBarPaddingTest {

    // 余白は {left, top, right, bottom}
    private static final int[] BASE = {4, 8, 12, 16};
    private static final int[] BARS = {10, 50, 20, 30};

    @Test
    public void top_addsOnlyStatusBarHeightAndSides() {
        int[] result = SystemBarPadding.apply(BASE, BARS, SystemBarPadding.TOP);

        assertArrayEquals(new int[]{14, 58, 32, 16}, result);
    }

    @Test
    public void bottom_addsOnlyNavigationBarHeightAndSides() {
        int[] result = SystemBarPadding.apply(BASE, BARS, SystemBarPadding.BOTTOM);

        assertArrayEquals(new int[]{14, 8, 32, 46}, result);
    }

    @Test
    public void topAndBottom_canBeCombined() {
        int[] result = SystemBarPadding.apply(BASE, BARS, SystemBarPadding.TOP | SystemBarPadding.BOTTOM);

        assertArrayEquals(new int[]{14, 58, 32, 46}, result);
    }

    @Test
    public void noSides_keepsBasePadding() {
        int[] result = SystemBarPadding.apply(BASE, BARS, 0);

        assertArrayEquals(BASE, result);
    }

    @Test
    public void doesNotModifyGivenArrays() {
        int[] base = BASE.clone();
        int[] bars = BARS.clone();

        SystemBarPadding.apply(base, bars, SystemBarPadding.TOP | SystemBarPadding.BOTTOM);

        assertArrayEquals(BASE, base);
        assertArrayEquals(BARS, bars);
    }
}
