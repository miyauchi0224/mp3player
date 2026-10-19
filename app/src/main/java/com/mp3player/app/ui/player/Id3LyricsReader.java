package com.mp3player.app.ui.player;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * MP3 の ID3v2.3 / v2.4 タグから、埋め込み歌詞（USLT フレーム）を取り出す。
 * 音声ファイル自体は読める権限があるため、Android 10 以降でも追加の許可なしに歌詞を表示できる。
 */
public final class Id3LyricsReader {

    /** ファイル先頭からこの長さまで読めば、ほとんどのタグ（ジャケット画像を含む）が収まる */
    public static final int MAX_TAG_BYTES = 4 * 1024 * 1024;

    private static final int HEADER_SIZE = 10;

    private Id3LyricsReader() {
    }

    /** @param data ファイル先頭のバイト列 @return 歌詞。無い・読めない場合は null */
    public static String read(byte[] data) {
        if (data == null || data.length < HEADER_SIZE || data[0] != 'I' || data[1] != 'D' || data[2] != '3') {
            return null;
        }
        int version = data[3];
        if (version != 3 && version != 4) {
            return null;
        }
        int flags = data[5] & 0xFF;
        int end = Math.min(data.length, HEADER_SIZE + syncsafe(data, 6));
        int pos = HEADER_SIZE;
        if ((flags & 0x40) != 0 && pos + 4 <= end) {
            // 拡張ヘッダーを読み飛ばす（v2.4 はサイズに自身を含む、v2.3 は含まない）
            pos += version == 4 ? syncsafe(data, pos) : int32(data, pos) + 4;
        }
        while (pos + HEADER_SIZE <= end) {
            if (data[pos] == 0) {
                return null; // パディングに到達
            }
            String id = new String(data, pos, 4, StandardCharsets.ISO_8859_1);
            int size = version == 4 ? syncsafe(data, pos + 4) : int32(data, pos + 4);
            int bodyStart = pos + HEADER_SIZE;
            if (size <= 0 || bodyStart + size > end) {
                return null; // 壊れたタグ
            }
            if ("USLT".equals(id)) {
                return decodeUslt(data, bodyStart, size);
            }
            pos = bodyStart + size;
        }
        return null;
    }

    /** USLT: エンコーディング(1) 言語(3) 説明(終端付き) 歌詞 */
    private static String decodeUslt(byte[] data, int start, int size) {
        if (size < 5) {
            return null;
        }
        int encoding = data[start];
        int end = start + size;
        boolean wide = encoding == 1 || encoding == 2;
        int textStart = -1;
        for (int i = start + 4; i < end; i += wide ? 2 : 1) {
            if (wide ? i + 1 < end && data[i] == 0 && data[i + 1] == 0 : data[i] == 0) {
                textStart = i + (wide ? 2 : 1);
                break;
            }
        }
        if (textStart < 0 || textStart > end) {
            return null;
        }
        String text = new String(data, textStart, end - textStart, charset(encoding));
        int trimmed = text.length();
        while (trimmed > 0 && text.charAt(trimmed - 1) == '\u0000') {
            trimmed--;
        }
        text = text.substring(0, trimmed).trim();
        return text.isEmpty() ? null : text;
    }

    private static Charset charset(int encoding) {
        switch (encoding) {
            case 1:
                return StandardCharsets.UTF_16; // BOM付き
            case 2:
                return StandardCharsets.UTF_16BE;
            case 3:
                return StandardCharsets.UTF_8;
            default:
                return StandardCharsets.ISO_8859_1;
        }
    }

    private static int syncsafe(byte[] b, int i) {
        return ((b[i] & 0x7F) << 21) | ((b[i + 1] & 0x7F) << 14) | ((b[i + 2] & 0x7F) << 7) | (b[i + 3] & 0x7F);
    }

    private static int int32(byte[] b, int i) {
        return ((b[i] & 0xFF) << 24) | ((b[i + 1] & 0xFF) << 16) | ((b[i + 2] & 0xFF) << 8) | (b[i + 3] & 0xFF);
    }
}
