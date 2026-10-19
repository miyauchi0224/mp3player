package com.mp3player.app.playback;

/**
 * 再生サービスの独自コマンド（イコライザー操作など）を許可する接続元の判定。
 * 再生サービスは Android Auto や通知から使うため他アプリに公開しているが、
 * 独自コマンドは自アプリの画面からだけ使えればよい。
 */
public final class CustomCommandPolicy {

    private CustomCommandPolicy() {
    }

    public static boolean isAllowed(String controllerPackage, String appPackage) {
        return controllerPackage != null && !controllerPackage.isEmpty() && controllerPackage.equals(appPackage);
    }
}
