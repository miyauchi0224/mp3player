package com.mp3player.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;

import com.mp3player.app.playback.PlaybackService;

@OptIn(markerClass = UnstableApi.class)
public class MusicWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_TOGGLE_PLAY_PAUSE = "com.mp3player.app.widget.ACTION_TOGGLE_PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.mp3player.app.widget.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.mp3player.app.widget.ACTION_PREVIOUS";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 初期表示はPlaybackServiceが再生開始時にプッシュ更新するため、ここでは何もしない
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (!ACTION_TOGGLE_PLAY_PAUSE.equals(action) && !ACTION_NEXT.equals(action) && !ACTION_PREVIOUS.equals(action)) {
            return;
        }

        PendingResult pendingResult = goAsync();
        SessionToken token = new SessionToken(context, new ComponentName(context, PlaybackService.class));
        ListenableFuture<MediaController> future = new MediaController.Builder(context, token).buildAsync();
        future.addListener(() -> {
            try {
                MediaController controller = future.get();
                if (ACTION_TOGGLE_PLAY_PAUSE.equals(action)) {
                    if (controller.isPlaying()) {
                        controller.pause();
                    } else {
                        controller.play();
                    }
                } else if (ACTION_NEXT.equals(action)) {
                    controller.seekToNext();
                } else {
                    controller.seekToPrevious();
                }
                controller.release();
            } catch (Exception ignored) {
                // ウィジェットからの操作失敗は無視する（次回操作で再試行される）
            } finally {
                pendingResult.finish();
            }
        }, ContextCompat.getMainExecutor(context));
    }
}
