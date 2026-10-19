package com.mp3player.app.data.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mp3player.app.data.local.entity.Song;

/**
 * 最も古いスキーマ（v1）で作ったDBが、全 Migration を経て現在のスキーマになり、データが残ることを確認する。
 * 新しいバージョンを追加しても、このテストはそのまま使える。
 */
@RunWith(AndroidJUnit4.class)
public class MigrationTest {

    private static final String TEST_DB = "migration-test.db";

    @Rule
    public MigrationTestHelper helper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(), AppDatabase.class);

    @After
    public void tearDown() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(TEST_DB);
    }

    @Test
    public void oldestSchema_migratesToCurrentAndKeepsUserData() throws Exception {
        try (SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1)) {
            db.execSQL("INSERT INTO songs (id, title, artist, album, albumId, folderPath, duration, data, "
                    + "dateAdded, playCount, favorite, lastPlayedAt) "
                    + "VALUES (1, 'Morning Walk', 'Sample Trio', 'First Steps', 10, '/Music', 16000, "
                    + "'/Music/morning_walk.mp3', 100, 3, 1, 200)");
        }

        // 全 Migration を適用し、現在のスキーマと一致するか検証する
        helper.runMigrationsAndValidate(TEST_DB, AppDatabase.VERSION, true, AppDatabase.MIGRATIONS).close();

        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, TEST_DB)
                .addMigrations(AppDatabase.MIGRATIONS)
                .build();
        try {
            Song song = database.songDao().getSongByIdSync(1);
            assertNotNull(song);
            assertEquals("Morning Walk", song.title);
            assertEquals(3, song.playCount);
            assertTrue(song.favorite);
            assertEquals(200, song.lastPlayedAt);
        } finally {
            database.close();
        }
    }
}
