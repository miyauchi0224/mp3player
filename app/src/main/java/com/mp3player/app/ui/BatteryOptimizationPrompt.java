package com.mp3player.app.ui;

/**
 * 省電力機能（Doze）の除外を案内するかどうか。
 * Google Play のポリシーにより除外を直接求めることはせず、案内したうえで設定画面を開く。
 */
public final class BatteryOptimizationPrompt {

    /** 省電力の除外設定は Android 6.0（API 23）から */
    private static final int MIN_SDK = 23;

    private BatteryOptimizationPrompt() {
    }

    public static boolean shouldShow(int sdkInt, boolean alreadyIgnoringOptimizations, boolean alreadyAsked) {
        return sdkInt >= MIN_SDK && !alreadyIgnoringOptimizations && !alreadyAsked;
    }
}
