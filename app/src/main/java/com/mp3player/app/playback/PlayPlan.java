package com.mp3player.app.playback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** キューをどの順番で、どのリピート設定で再生するかをまとめた値 */
public final class PlayPlan {
    /** 再生する順番。元のリストでの位置（0始まり）を並べたもの */
    public final List<Integer> order;
    /** androidx.media3.common.Player の REPEAT_MODE_* */
    public final int repeatMode;

    public PlayPlan(List<Integer> order, int repeatMode) {
        this.order = Collections.unmodifiableList(new ArrayList<>(order));
        this.repeatMode = repeatMode;
    }

    /** 元のリストを再生順に並べ替えたリストを返す */
    public <T> List<T> applyTo(List<T> items) {
        List<T> result = new ArrayList<>(order.size());
        for (int index : order) {
            result.add(items.get(index));
        }
        return result;
    }
}
