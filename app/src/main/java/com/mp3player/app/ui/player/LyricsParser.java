package com.mp3player.app.ui.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricsParser {

    private static final Pattern TIME_TAG = Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{1,3}))?]");

    public static File findLrcFile(String songDataPath) {
        if (songDataPath == null || songDataPath.isEmpty()) {
            return null;
        }
        int dot = songDataPath.lastIndexOf('.');
        String base = dot > 0 ? songDataPath.substring(0, dot) : songDataPath;
        File lrcFile = new File(base + ".lrc");
        return lrcFile.exists() ? lrcFile : null;
    }

    public static List<LyricLine> parse(File file) {
        List<LyricLine> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = TIME_TAG.matcher(line);
                List<Long> timestamps = new ArrayList<>();
                int lastEnd = 0;
                while (matcher.find()) {
                    long minutes = Long.parseLong(matcher.group(1));
                    long seconds = Long.parseLong(matcher.group(2));
                    String fraction = matcher.group(3);
                    long millis = fraction != null
                            ? Long.parseLong((fraction + "00").substring(0, 3))
                            : 0;
                    timestamps.add(minutes * 60_000 + seconds * 1000 + millis);
                    lastEnd = matcher.end();
                }
                if (timestamps.isEmpty()) {
                    continue;
                }
                String text = line.substring(lastEnd).trim();
                for (Long timestamp : timestamps) {
                    lines.add(new LyricLine(timestamp, text));
                }
            }
        } catch (IOException e) {
            return new ArrayList<>();
        }
        Collections.sort(lines, (a, b) -> Long.compare(a.timeMs, b.timeMs));
        return lines;
    }
}
