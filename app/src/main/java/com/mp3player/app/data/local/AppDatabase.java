package com.mp3player.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.dao.SongDao;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.PlaylistSongCrossRef;
import com.mp3player.app.data.local.entity.Song;

@Database(
        entities = {Song.class, Playlist.class, PlaylistSongCrossRef.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
}
