package com.mp3player.app.ui.common;

/**
 * MP3ファイルに埋め込まれたサムネイル（ID3のAPICフレーム）を表すGlide用のモデル。
 * Glideはモデルのequals/hashCodeをキャッシュキーに使うため、曲IDで同一性を決める。
 */
public final class EmbeddedArtwork {
    public final long songId;

    public EmbeddedArtwork(long songId) {
        this.songId = songId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmbeddedArtwork)) return false;
        return songId == ((EmbeddedArtwork) o).songId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(songId);
    }

    @Override
    public String toString() {
        return "EmbeddedArtwork{songId=" + songId + "}";
    }
}
