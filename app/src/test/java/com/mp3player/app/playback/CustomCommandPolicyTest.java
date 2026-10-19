package com.mp3player.app.playback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** イコライザー等の独自コマンドを、自アプリからの接続にだけ許可する（No.43） */
public class CustomCommandPolicyTest {

    private static final String APP = "com.mp3player.app";

    @Test
    public void ownAppIsAllowed() {
        assertTrue(CustomCommandPolicy.isAllowed(APP, APP));
    }

    @Test
    public void otherAppsAreRejected() {
        assertFalse(CustomCommandPolicy.isAllowed("com.example.other", APP));
    }

    @Test
    public void systemControllersAreRejected() {
        // 通知・Android Auto などは通常の再生操作だけ使えればよい
        assertFalse(CustomCommandPolicy.isAllowed("com.android.systemui", APP));
        assertFalse(CustomCommandPolicy.isAllowed("com.google.android.projection.gearhead", APP));
    }

    @Test
    public void unknownCallerIsRejected() {
        assertFalse(CustomCommandPolicy.isAllowed(null, APP));
        assertFalse(CustomCommandPolicy.isAllowed("", APP));
    }
}
