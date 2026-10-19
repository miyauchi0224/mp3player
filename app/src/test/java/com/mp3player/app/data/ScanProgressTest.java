package com.mp3player.app.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** 曲のスキャン状況の表示（No.46） */
public class ScanProgressTest {

    @Test
    public void idle_isNotScanning() {
        assertFalse(ScanProgress.idle().scanning);
    }

    @Test
    public void startOfScan_hasUnknownTotal() {
        ScanProgress p = ScanProgress.scanning(0, 0);

        assertTrue(p.scanning);
        assertTrue(p.isIndeterminate());
    }

    @Test
    public void percent_isProcessedOverTotal() {
        assertEquals(50, ScanProgress.scanning(5, 10).percent());
        assertEquals(100, ScanProgress.scanning(10, 10).percent());
        assertFalse(ScanProgress.scanning(5, 10).isIndeterminate());
    }

    @Test
    public void percent_staysWithinRange() {
        assertEquals(100, ScanProgress.scanning(12, 10).percent());
        assertEquals(0, ScanProgress.scanning(3, 0).percent());
    }

    @Test
    public void reportsFirstEveryNthAndLastRowOnly() {
        // 1行ごとに画面を更新すると重いので、間引いて知らせる
        assertTrue(ScanProgress.shouldReport(1, 1000));
        assertFalse(ScanProgress.shouldReport(2, 1000));
        assertTrue(ScanProgress.shouldReport(ScanProgress.REPORT_INTERVAL, 1000));
        assertTrue(ScanProgress.shouldReport(1000, 1000));
        assertFalse(ScanProgress.shouldReport(999, 1000));
    }
}
