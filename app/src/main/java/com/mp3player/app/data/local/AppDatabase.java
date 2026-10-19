package com.mp3player.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;

import com.mp3player.app.data.local.dao.PlaylistDao;
import com.mp3player.app.data.local.dao.SongDao;
import com.mp3player.app.data.local.entity.Playlist;
import com.mp3player.app.data.local.entity.PlaylistSongCrossRef;
import com.mp3player.app.data.local.entity.Song;

/**
 * スキーマを変えるときの手順（データを消さないため、fallbackToDestructiveMigration は使わない）:
 * 1. エンティティを変更し、VERSION を1つ上げる
 * 2. 1つ前のバージョンからの Migration を書き、MIGRATIONS に追加する
 * 3. ビルドすると app/schemas/ に新しいスキーマJSONが出力されるので、コミットする
 * 4. MigrationTest（androidTest）を実行して、移行後もデータが残ることを確認する
 */
@Database(
        entities = {Song.class, Playlist.class, PlaylistSongCrossRef.class},
        version = AppDatabase.VERSION,
        exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {

    public static final int VERSION = 1;

    /** 全バージョン間の移行。アプリ本体と MigrationTest の両方で使う */
    public static final Migration[] MIGRATIONS = {};

    public abstract SongDao songDao();
    public abstract PlaylistDao playlistDao();
}
