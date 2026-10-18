package com.mp3player.app.di;

import android.content.Context;

import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import com.mp3player.app.data.local.AppDatabase;
import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.dao.SongDao;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "mp3player.db").build();
    }

    @Provides
    public SongDao provideSongDao(AppDatabase database) {
        return database.songDao();
    }

    @Provides
    public PlaylistDao providePlaylistDao(AppDatabase database) {
        return database.playlistDao();
    }

    @Provides
    @Singleton
    public ExecutorService provideExecutorService() {
        return Executors.newFixedThreadPool(4);
    }
}
