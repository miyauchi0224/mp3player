package com.mp3player.app.ui.common;

/** アイコン表示（グリッド）の列数を画面幅から決める */
public final class GridSpan {

    private static final int MIN_COLUMNS = 2;

    private GridSpan() {
    }

    public static int count(int screenWidthDp, int minCellWidthDp) {
        if (minCellWidthDp <= 0) {
            throw new IllegalArgumentException("minCellWidthDp must be positive: " + minCellWidthDp);
        }
        return Math.max(MIN_COLUMNS, screenWidthDp / minCellWidthDp);
    }
}
