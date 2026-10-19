package com.mp3player.app.data.local;

import static org.junit.Assert.assertTrue;

import androidx.room.migration.Migration;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * アップデートでお気に入り・プレイリスト・履歴を失わないための決まりごとを確認する。
 * - 全バージョンのスキーマJSONがリポジトリにあること（移行テストに必要）
 * - バージョンを上げたら、1つ前からの Migration が必ずあること
 */
public class DatabaseSchemaSpecTest {

    private static final String SCHEMA_DIR = "schemas/com.mp3player.app.data.local.AppDatabase/";

    @Test
    public void everyVersionHasExportedSchema() {
        for (int version = 1; version <= AppDatabase.VERSION; version++) {
            assertTrue("スキーマが無い: " + SCHEMA_DIR + version + ".json",
                    Files.exists(Paths.get(SCHEMA_DIR + version + ".json")));
        }
    }

    @Test
    public void everyVersionUpgradeHasMigration() {
        for (int from = 1; from < AppDatabase.VERSION; from++) {
            boolean found = false;
            for (Migration migration : AppDatabase.MIGRATIONS) {
                if (migration.startVersion == from && migration.endVersion == from + 1) {
                    found = true;
                }
            }
            assertTrue("Migration " + from + "→" + (from + 1) + " が無い", found);
        }
    }
}
