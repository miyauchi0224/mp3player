package com.mp3player.app.ui.player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 歌詞（LRC形式）を解析する。時刻タグの無いテキストは、時刻なし（timeMs = -1）の行として扱う。
 */
public class LyricsParser {

    /** 時刻タグの無い行の timeMs */
    public static final long UNSYNCED = -1;

    private static final Pattern TIME_TAG = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{1,3}))?]");
    /** [ti:曲名] などのメタデータ行 */
    private static final Pattern METADATA = Pattern.compile("^\\[[a-zA-Z]+:.*]$");

    /** 端末内のファイルパスから同名の .lrc を探す（Android 9 以前、またはアクセスできる場所のみ） */
    public static File findLrcFile(String songDataPath) {
        if (songDataPath == null || songDataPath.isEmpty()) {
            return null;
        }
        File songFile = new File(songDataPath);
        File lrcFile = new File(songFile.getParentFile(), LrcNames.forSongFile(songFile.getName()));
        return lrcFile.canRead() ? lrcFile : null;
    }

    public static List<LyricLine> parseFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            return parseStream(in);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static List<LyricLine> parseStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int n;
        while ((n = in.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }
        return parse(new String(out.toByteArray(), StandardCharsets.UTF_8));
    }

    public static List<LyricLine> parse(String text) {
        List<LyricLine> synced = new ArrayList<>();
        List<LyricLine> unsynced = new ArrayList<>();
        if (text == null) {
            return synced;
        }
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }
        for (String raw : text.split("\\r?\\n")) {
            String line = raw.trim();
            if (line.isEmpty() || METADATA.matcher(line).matches()) {
                continue;
            }
            Matcher matcher = TIME_TAG.matcher(line);
            List<Long> timestamps = new ArrayList<>();
            int lastEnd = 0;
            while (matcher.find()) {
                long minutes = Long.parseLong(matcher.group(1));
                long seconds = Long.parseLong(matcher.group(2));
                String fraction = matcher.group(3);
                long millis = fraction != null ? Long.parseLong((fraction + "00").substring(0, 3)) : 0;
                timestamps.add(minutes * 60_000 + seconds * 1000 + millis);
                lastEnd = matcher.end();
            }
            if (timestamps.isEmpty()) {
                unsynced.add(new LyricLine(UNSYNCED, line));
                continue;
            }
            String lyric = line.substring(lastEnd).trim();
            for (Long timestamp : timestamps) {
                synced.add(new LyricLine(timestamp, lyric));
            }
        }
        if (synced.isEmpty()) {
            return unsynced;
        }
        Collections.sort(synced, (a, b) -> Long.compare(a.timeMs, b.timeMs));
        return synced;
    }
}
