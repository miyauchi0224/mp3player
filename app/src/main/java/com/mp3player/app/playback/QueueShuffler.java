package com.mp3player.app.playback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 再生中にシャッフルを切り替えるときのキューの並べ替え。
 * プレーヤーのシャッフル機能は、シャッフル順で再生中の曲より前に来た曲を再生しないため使わず、
 * 再生中の曲を先頭に、残りをランダムに並べたキューに置き換える。
 */
public final class QueueShuffler {

    private QueueShuffler() {
    }

    public static <T> List<T> shuffleAroundCurrent(List<T> queue, int currentIndex, Random random) {
        if (queue.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> rest = new ArrayList<>(queue);
        T current = rest.remove(Math.max(0, Math.min(currentIndex, queue.size() - 1)));
        Collections.shuffle(rest, random);
        List<T> result = new ArrayList<>(queue.size());
        result.add(current);
        result.addAll(rest);
        return result;
    }

    /** シャッフル前の並びに戻す。再生中の曲が元のキューに無ければ先頭から */
    public static <T> Restored<T> restoreOriginal(List<T> original, T current) {
        return new Restored<>(original, Math.max(0, original.indexOf(current)));
    }

    public static final class Restored<T> {
        public final List<T> queue;
        public final int currentIndex;

        Restored(List<T> queue, int currentIndex) {
            this.queue = Collections.unmodifiableList(new ArrayList<>(queue));
            this.currentIndex = currentIndex;
        }
    }
}
