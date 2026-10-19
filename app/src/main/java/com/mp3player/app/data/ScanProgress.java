package com.mp3player.app.data;

/** 曲のスキャン状況。ライブラリ画面に進み具合を表示する */
public final class ScanProgress {

    /** 画面に知らせる間隔（行数）。1行ごとに更新すると重いため間引く */
    public static final int REPORT_INTERVAL = 25;

    public final boolean scanning;
    public final int processed;
    /** 全体の曲数。まだ分からない間は 0 */
    public final int total;

    private ScanProgress(boolean scanning, int processed, int total) {
        this.scanning = scanning;
        this.processed = processed;
        this.total = total;
    }

    public static ScanProgress idle() {
        return new ScanProgress(false, 0, 0);
    }

    public static ScanProgress scanning(int processed, int total) {
        return new ScanProgress(true, processed, total);
    }

    /** 全体の曲数が分からず、進み具合を割合で示せない */
    public boolean isIndeterminate() {
        return total <= 0;
    }

    public int percent() {
        if (total <= 0) {
            return 0;
        }
        return Math.min(100, processed * 100 / total);
    }

    public static boolean shouldReport(int processed, int total) {
        return processed == 1 || processed == total || processed % REPORT_INTERVAL == 0;
    }
}
