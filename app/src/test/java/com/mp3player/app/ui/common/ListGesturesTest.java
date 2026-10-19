package com.mp3player.app.ui.common;

import static org.junit.Assert.assertEquals;

import androidx.recyclerview.widget.ItemTouchHelper;

import org.junit.Test;

/** 表示方法ごとの、ドラッグ（並べ替え）とスワイプ（削除）の方向（No.48） */
public class ListGesturesTest {

    @Test
    public void list_dragsVerticallyAndSwipesSideways() {
        assertEquals(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ListGestures.dragDirections(SongViewMode.LIST));
        assertEquals(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ListGestures.swipeDirections(SongViewMode.LIST));
    }

    @Test
    public void grid_dragsInAllDirectionsAndDoesNotSwipe() {
        // グリッドでは左右にも並べ替えるため、横スワイプでの削除はしない（削除はリスト表示で行う）
        assertEquals(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                ListGestures.dragDirections(SongViewMode.GRID));
        assertEquals(0, ListGestures.swipeDirections(SongViewMode.GRID));
    }

    @Test
    public void headerSpansWholeRowAndSongsTakeOneCell() {
        assertEquals(3, ListGestures.spanSize(true, 3));
        assertEquals(1, ListGestures.spanSize(false, 3));
    }
}
