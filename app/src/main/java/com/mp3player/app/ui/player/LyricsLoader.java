package com.mp3player.app.ui.player;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mp3player.app.data.local.entity.Song;
import com.mp3player.app.playback.PlayerController;

/**
 * 曲の歌詞を探して読み込む。次の順に試す（バックグラウンドスレッドから呼ぶ）:
 * 1. MP3に埋め込まれた歌詞（ID3 の USLT）… 追加の許可なしで読める
 * 2. 設定で選んだ歌詞フォルダ（SAF）にある同名の .lrc
 * 3. 曲と同じ場所にある同名の .lrc（Android 9 以前など、直接読める場合のみ）
 */
public final class LyricsLoader {

    private static final int MAX_FOLDER_DEPTH = 8;

    /** 歌詞フォルダ内の .lrc の一覧（小文字のファイル名 → URI）。フォルダごとに1回だけ作る */
    private static String indexedTree;
    private static Map<String, Uri> lrcIndex = new HashMap<>();

    private LyricsLoader() {
    }

    public static List<LyricLine> load(Context context, Song song, @Nullable String lyricsFolderUri) {
        ContentResolver resolver = context.getContentResolver();

        String embedded = readEmbeddedLyrics(resolver, PlayerController.getSongContentUri(song.id));
        if (embedded != null) {
            List<LyricLine> lines = LyricsParser.parse(embedded);
            if (!lines.isEmpty()) {
                return lines;
            }
        }

        String lrcName = LrcNames.forSongFile(song.data);
        if (lyricsFolderUri != null && lrcName != null) {
            Uri lrc = findInFolder(resolver, Uri.parse(lyricsFolderUri), lrcName);
            if (lrc != null) {
                try (InputStream in = resolver.openInputStream(lrc)) {
                    if (in != null) {
                        return LyricsParser.parseStream(in);
                    }
                } catch (IOException | SecurityException ignored) {
                    // 許可が取り消された場合などは次の方法を試す
                }
            }
        }

        File file = LyricsParser.findLrcFile(song.data);
        return file != null ? LyricsParser.parseFile(file) : new ArrayList<>();
    }

    /** 歌詞フォルダを変えたときに一覧を作り直す */
    public static synchronized void invalidateFolderIndex() {
        indexedTree = null;
        lrcIndex = new HashMap<>();
    }

    @Nullable
    private static String readEmbeddedLyrics(ContentResolver resolver, Uri songUri) {
        try (InputStream in = resolver.openInputStream(songUri)) {
            if (in == null) {
                return null;
            }
            byte[] header = readFully(in, 10);
            if (header.length < 10 || header[0] != 'I' || header[1] != 'D' || header[2] != '3') {
                return null;
            }
            int tagSize = ((header[6] & 0x7F) << 21) | ((header[7] & 0x7F) << 14)
                    | ((header[8] & 0x7F) << 7) | (header[9] & 0x7F);
            byte[] body = readFully(in, Math.min(tagSize, Id3LyricsReader.MAX_TAG_BYTES));
            byte[] tag = new byte[header.length + body.length];
            System.arraycopy(header, 0, tag, 0, header.length);
            System.arraycopy(body, 0, tag, header.length, body.length);
            return Id3LyricsReader.read(tag);
        } catch (IOException | SecurityException e) {
            return null;
        }
    }

    private static byte[] readFully(InputStream in, int length) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(0, length));
        byte[] buffer = new byte[8192];
        int remaining = length;
        while (remaining > 0) {
            int n = in.read(buffer, 0, Math.min(buffer.length, remaining));
            if (n < 0) {
                break;
            }
            out.write(buffer, 0, n);
            remaining -= n;
        }
        return out.toByteArray();
    }

    @Nullable
    private static synchronized Uri findInFolder(ContentResolver resolver, Uri tree, String lrcName) {
        if (!tree.toString().equals(indexedTree)) {
            Map<String, Uri> index = new HashMap<>();
            try {
                indexFolder(resolver, tree, DocumentsContract.getTreeDocumentId(tree), index, 0);
            } catch (RuntimeException e) {
                // 許可の取り消し・フォルダの削除など
            }
            lrcIndex = index;
            indexedTree = tree.toString();
        }
        return lrcIndex.get(lrcName.toLowerCase(Locale.ROOT));
    }

    private static void indexFolder(ContentResolver resolver, Uri tree, String documentId,
                                    Map<String, Uri> index, int depth) {
        if (depth > MAX_FOLDER_DEPTH) {
            return;
        }
        Uri children = DocumentsContract.buildChildDocumentsUriUsingTree(tree, documentId);
        String[] projection = {
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE
        };
        try (Cursor cursor = resolver.query(children, projection, null, null, null)) {
            if (cursor == null) {
                return;
            }
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String mime = cursor.getString(2);
                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                    indexFolder(resolver, tree, id, index, depth + 1);
                } else if (name != null && name.toLowerCase(Locale.ROOT).endsWith(".lrc")) {
                    index.put(name.toLowerCase(Locale.ROOT), DocumentsContract.buildDocumentUriUsingTree(tree, id));
                }
            }
        }
    }
}
