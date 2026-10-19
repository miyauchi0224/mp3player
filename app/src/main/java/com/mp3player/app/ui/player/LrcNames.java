package com.mp3player.app.ui.player;

import java.util.Locale;

/** 曲ファイルに対応する .lrc ファイル名（同じ名前で拡張子だけ .lrc） */
public final class LrcNames {

    private LrcNames() {
    }

    /** @param songFile ファイル名またはパス */
    public static String forSongFile(String songFile) {
        if (songFile == null || songFile.isEmpty()) {
            return null;
        }
        String name = songFile.substring(Math.max(songFile.lastIndexOf('/'), songFile.lastIndexOf('\\')) + 1);
        int dot = name.lastIndexOf('.');
        return (dot > 0 ? name.substring(0, dot) : name) + ".lrc";
    }

    /** 大文字・小文字の違いは同じ名前とみなす */
    public static boolean matches(String candidate, String lrcName) {
        return candidate != null && lrcName != null
                && candidate.toLowerCase(Locale.ROOT).equals(lrcName.toLowerCase(Locale.ROOT));
    }
}
