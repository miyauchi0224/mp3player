package com.mp3player.app.playback;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.ContentUris;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.LibraryResult;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionResult;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.dao.SongDao;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.widget.MusicWidgetProvider;

@UnstableApi
@AndroidEntryPoint
public class PlaybackService extends MediaLibraryService {

    public static final String CMD_EQ_GET_STATE = "com.mp3player.app.EQ_GET_STATE";
    public static final String CMD_EQ_SET_ENABLED = "com.mp3player.app.EQ_SET_ENABLED";
    public static final String CMD_EQ_SET_BAND_LEVEL = "com.mp3player.app.EQ_SET_BAND_LEVEL";
    public static final String CMD_EQ_SET_PRESET = "com.mp3player.app.EQ_SET_PRESET";

    private static final String ROOT_ID = "root";
    private static final String NODE_SONGS = "songs";
    private static final String NODE_PLAYLISTS = "playlists";
    private static final String NODE_FAVORITES = "favorites";
    private static final String PLAYLIST_PREFIX = "playlist:";

    @Inject
    SongDao songDao;

    @Inject
    PlaylistDao playlistDao;

    @Inject
    ExecutorService executorService;

    private ListeningExecutorService listeningExecutor;
    private ExoPlayer player;
    private MediaLibrarySession mediaSession;
    private Equalizer equalizer;

    @Override
    public void onCreate() {
        super.onCreate();
        listeningExecutor = MoreExecutors.listeningDecorator(executorService);
        player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaLibrarySession.Builder(this, player, new LibrarySessionCallback())
                .build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updateWidget();
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                updateWidget();
            }
        });
    }

    private void updateWidget() {
        Context context = this;
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, MusicWidgetProvider.class));
        if (ids.length == 0) {
            return;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_player);
        MediaItem item = player.getCurrentMediaItem();
        if (item != null) {
            views.setTextViewText(R.id.widget_title, item.mediaMetadata.title);
            views.setTextViewText(R.id.widget_artist, item.mediaMetadata.artist);
        }
        views.setImageViewResource(R.id.widget_button_play_pause,
                player.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        views.setOnClickPendingIntent(R.id.widget_button_play_pause,
                widgetActionPendingIntent(MusicWidgetProvider.ACTION_TOGGLE_PLAY_PAUSE, 1));
        views.setOnClickPendingIntent(R.id.widget_button_next,
                widgetActionPendingIntent(MusicWidgetProvider.ACTION_NEXT, 2));
        views.setOnClickPendingIntent(R.id.widget_button_previous,
                widgetActionPendingIntent(MusicWidgetProvider.ACTION_PREVIOUS, 3));

        for (int id : ids) {
            manager.updateAppWidget(id, views);
        }
    }

    private PendingIntent widgetActionPendingIntent(String action, int requestCode) {
        Intent intent = new Intent(this, MusicWidgetProvider.class).setAction(action);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        return PendingIntent.getBroadcast(this, requestCode, intent, flags);
    }

    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
        super.onDestroy();
    }

    @Nullable
    private Equalizer getOrCreateEqualizer() {
        if (equalizer == null) {
            try {
                equalizer = new Equalizer(0, player.getAudioSessionId());
            } catch (RuntimeException e) {
                return null;
            }
        }
        return equalizer;
    }

    private Bundle buildEqStateBundle() {
        Bundle bundle = new Bundle();
        Equalizer eq = getOrCreateEqualizer();
        if (eq == null) {
            bundle.putBoolean("available", false);
            return bundle;
        }
        bundle.putBoolean("available", true);
        bundle.putBoolean("enabled", eq.getEnabled());
        short numBands = eq.getNumberOfBands();
        bundle.putInt("numBands", numBands);
        short[] levelRange = eq.getBandLevelRange();
        bundle.putInt("minLevel", levelRange[0]);
        bundle.putInt("maxLevel", levelRange[1]);
        int[] levels = new int[numBands];
        int[] centerFreqs = new int[numBands];
        for (short i = 0; i < numBands; i++) {
            levels[i] = eq.getBandLevel(i);
            centerFreqs[i] = eq.getCenterFreq(i) / 1000;
        }
        bundle.putIntArray("levels", levels);
        bundle.putIntArray("centerFreqs", centerFreqs);
        short numPresets = eq.getNumberOfPresets();
        String[] presetNames = new String[numPresets];
        for (short i = 0; i < numPresets; i++) {
            presetNames[i] = eq.getPresetName(i);
        }
        bundle.putStringArray("presetNames", presetNames);
        int currentPreset;
        try {
            currentPreset = eq.getCurrentPreset();
        } catch (IllegalStateException e) {
            currentPreset = -1;
        }
        bundle.putInt("currentPreset", currentPreset);
        return bundle;
    }

    private static Uri songArtworkUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    private static Uri songContentUri(long songId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/media"), songId);
    }

    private static MediaItem songToMediaItem(Song song) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(song.title)
                .setArtist(song.artist)
                .setAlbumTitle(song.album)
                .setArtworkUri(songArtworkUri(song.albumId))
                .setIsBrowsable(false)
                .setIsPlayable(true)
                .build();
        return new MediaItem.Builder()
                .setMediaId(String.valueOf(song.id))
                .setUri(songContentUri(song.id))
                .setMediaMetadata(metadata)
                .build();
    }

    private static MediaItem browsableItem(String id, String title) {
        MediaMetadata metadata = new MediaMetadata.Builder()
                .setTitle(title)
                .setIsBrowsable(true)
                .setIsPlayable(false)
                .build();
        return new MediaItem.Builder().setMediaId(id).setMediaMetadata(metadata).build();
    }

    private class LibrarySessionCallback implements MediaLibrarySession.Callback {

        @NonNull
        @Override
        public MediaSession.ConnectionResult onConnect(@NonNull MediaSession session,
                                                         @NonNull MediaSession.ControllerInfo controller) {
            MediaSession.ConnectionResult defaultResult =
                    MediaLibrarySession.Callback.super.onConnect(session, controller);
            SessionCommands sessionCommands = defaultResult.availableSessionCommands.buildUpon()
                    .add(new SessionCommand(CMD_EQ_GET_STATE, Bundle.EMPTY))
                    .add(new SessionCommand(CMD_EQ_SET_ENABLED, Bundle.EMPTY))
                    .add(new SessionCommand(CMD_EQ_SET_BAND_LEVEL, Bundle.EMPTY))
                    .add(new SessionCommand(CMD_EQ_SET_PRESET, Bundle.EMPTY))
                    .build();
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .setAvailablePlayerCommands(defaultResult.availablePlayerCommands)
                    .build();
        }

        @NonNull
        @Override
        public ListenableFuture<SessionResult> onCustomCommand(@NonNull MediaSession session,
                                                                 @NonNull MediaSession.ControllerInfo controller,
                                                                 @NonNull SessionCommand customCommand,
                                                                 @NonNull Bundle args) {
            switch (customCommand.customAction) {
                case CMD_EQ_GET_STATE:
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS, buildEqStateBundle()));
                case CMD_EQ_SET_ENABLED: {
                    Equalizer eq = getOrCreateEqualizer();
                    if (eq != null) {
                        eq.setEnabled(args.getBoolean("enabled"));
                    }
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS, buildEqStateBundle()));
                }
                case CMD_EQ_SET_BAND_LEVEL: {
                    Equalizer eq = getOrCreateEqualizer();
                    if (eq != null) {
                        eq.setBandLevel((short) args.getInt("band"), (short) args.getInt("level"));
                    }
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
                }
                case CMD_EQ_SET_PRESET: {
                    Equalizer eq = getOrCreateEqualizer();
                    if (eq != null) {
                        eq.usePreset((short) args.getInt("preset"));
                    }
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS, buildEqStateBundle()));
                }
                default:
                    return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED));
            }
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<MediaItem>> onGetLibraryRoot(
                @NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser,
                @Nullable LibraryParams params) {
            return Futures.immediateFuture(LibraryResult.ofItem(browsableItem(ROOT_ID, getString(R.string.app_name)), params));
        }

        @NonNull
        @Override
        public ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> onGetChildren(
                @NonNull MediaLibrarySession session, @NonNull MediaSession.ControllerInfo browser,
                @NonNull String parentId, int page, int pageSize, @Nullable LibraryParams params) {
            return listeningExecutor.submit(() -> {
                List<MediaItem> children = buildChildren(parentId);
                return LibraryResult.ofItemList(ImmutableList.copyOf(children), params);
            });
        }

        @NonNull
        @Override
        public ListenableFuture<List<MediaItem>> onAddMediaItems(@NonNull MediaSession mediaSession,
                                                                   @NonNull MediaSession.ControllerInfo controller,
                                                                   @NonNull List<MediaItem> mediaItems) {
            return listeningExecutor.submit(() -> {
                List<MediaItem> resolved = new ArrayList<>();
                for (MediaItem item : mediaItems) {
                    try {
                        Song song = songDao.getSongByIdSync(Long.parseLong(item.mediaId));
                        if (song != null) {
                            resolved.add(songToMediaItem(song));
                        }
                    } catch (NumberFormatException ignored) {
                        // ブラウズ用のフォルダIDなど、曲でないIDは無視する
                    }
                }
                return resolved;
            });
        }

        private List<MediaItem> buildChildren(String parentId) {
            switch (parentId) {
                case ROOT_ID:
                    List<MediaItem> roots = new ArrayList<>();
                    roots.add(browsableItem(NODE_SONGS, getString(R.string.tab_songs)));
                    roots.add(browsableItem(NODE_PLAYLISTS, getString(R.string.tab_playlists)));
                    roots.add(browsableItem(NODE_FAVORITES, getString(R.string.tab_favorites)));
                    return roots;
                case NODE_SONGS: {
                    List<MediaItem> items = new ArrayList<>();
                    for (Song song : songDao.getAllSongsSync()) {
                        items.add(songToMediaItem(song));
                    }
                    return items;
                }
                case NODE_FAVORITES: {
                    List<MediaItem> items = new ArrayList<>();
                    for (Song song : songDao.getFavoritesSync()) {
                        items.add(songToMediaItem(song));
                    }
                    return items;
                }
                case NODE_PLAYLISTS: {
                    List<MediaItem> items = new ArrayList<>();
                    for (Playlist playlist : playlistDao.getAllPlaylistsSync()) {
                        items.add(browsableItem(PLAYLIST_PREFIX + playlist.id, playlist.name));
                    }
                    return items;
                }
                default:
                    if (parentId.startsWith(PLAYLIST_PREFIX)) {
                        long playlistId = Long.parseLong(parentId.substring(PLAYLIST_PREFIX.length()));
                        List<MediaItem> items = new ArrayList<>();
                        for (Song song : playlistDao.getSongsForPlaylistSync(playlistId)) {
                            items.add(songToMediaItem(song));
                        }
                        return items;
                    }
                    return Collections.emptyList();
            }
        }
    }
}
