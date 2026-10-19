package com.mp3player.app.ui.common;

import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3player.app.R;

/** 曲一覧に表示方法（リスト／アイコン）を反映する */
public final class SongListLayout {

    /** アイコン表示のセルの最小幅 */
    private static final int MIN_CELL_WIDTH_DP = 120;

    private SongListLayout() {
    }

    public static void apply(RecyclerView recyclerView, ViewModeAdapter adapter, SongViewMode mode,
                             @Nullable ImageButton toggleButton) {
        apply(recyclerView, new ViewModeAdapter[]{adapter}, mode, toggleButton, null);
    }

    /**
     * @param isHeader 見出しの位置なら true（グリッドで1行全体を使う）。見出しが無ければ null
     */
    public static void apply(RecyclerView recyclerView, ViewModeAdapter[] adapters, SongViewMode mode,
                             @Nullable ImageButton toggleButton, @Nullable java.util.function.IntPredicate isHeader) {
        if (mode == SongViewMode.GRID) {
            int widthDp = recyclerView.getResources().getConfiguration().screenWidthDp;
            int spanCount = GridSpan.count(widthDp, MIN_CELL_WIDTH_DP);
            GridLayoutManager grid = new GridLayoutManager(recyclerView.getContext(), spanCount);
            if (isHeader != null) {
                grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ListGestures.spanSize(isHeader.test(position), spanCount);
                    }
                });
            }
            recyclerView.setLayoutManager(grid);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        }
        for (ViewModeAdapter adapter : adapters) {
            adapter.setViewMode(mode);
        }
        if (toggleButton != null) {
            updateToggle(toggleButton, mode);
        }
    }

    /** 切り替えボタンには「押すと切り替わる先」の表示方法を示す */
    public static void updateToggle(ImageButton toggleButton, SongViewMode mode) {
        boolean grid = mode == SongViewMode.GRID;
        toggleButton.setImageResource(grid ? R.drawable.ic_view_list : R.drawable.ic_grid_view);
        toggleButton.setContentDescription(toggleButton.getContext().getString(
                grid ? R.string.action_view_mode_list : R.string.action_view_mode_grid));
    }
}
