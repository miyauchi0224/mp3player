package com.mp3player.app.ui.common;

import androidx.recyclerview.widget.ItemTouchHelper;

/** 表示方法（リスト／アイコン）ごとの操作の決まり */
public final class ListGestures {

    private ListGestures() {
    }

    /** ドラッグで並べ替えられる方向 */
    public static int dragDirections(SongViewMode mode) {
        int vertical = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return mode == SongViewMode.GRID ? vertical | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT : vertical;
    }

    /** スワイプで削除できる方向。グリッドでは横方向も並べ替えに使うので削除しない */
    public static int swipeDirections(SongViewMode mode) {
        return mode == SongViewMode.GRID ? 0 : ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    /** グリッドでの1項目の幅（列数）。見出しは1行全体を使う */
    public static int spanSize(boolean header, int spanCount) {
        return header ? spanCount : 1;
    }
}
