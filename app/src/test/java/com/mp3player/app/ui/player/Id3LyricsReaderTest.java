package com.mp3player.app.ui.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** MP3 の ID3v2 タグから、埋め込み歌詞（USLT フレーム）を取り出す */
public class Id3LyricsReaderTest {

    private static byte[] syncsafe(int n) {
        return new byte[]{(byte) ((n >> 21) & 0x7F), (byte) ((n >> 14) & 0x7F), (byte) ((n >> 7) & 0x7F), (byte) (n & 0x7F)};
    }

    private static byte[] int32(int n) {
        return new byte[]{(byte) (n >> 24), (byte) (n >> 16), (byte) (n >> 8), (byte) n};
    }

    private static byte[] frame(int version, String id, byte[] body) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(id.getBytes(StandardCharsets.ISO_8859_1));
        out.writeBytes(version == 4 ? syncsafe(body.length) : int32(body.length));
        out.writeBytes(new byte[]{0, 0});
        out.writeBytes(body);
        return out.toByteArray();
    }

    private static byte[] tag(int version, byte[]... frames) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (byte[] f : frames) body.writeBytes(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.writeBytes(new byte[]{'I', 'D', '3', (byte) version, 0, 0});
        out.writeBytes(syncsafe(body.size()));
        out.writeBytes(body.toByteArray());
        out.writeBytes(new byte[]{(byte) 0xFF, (byte) 0xFB, 0x10, 0x00}); // 続く音声データ
        return out.toByteArray();
    }

    /** USLT: エンコーディング(1) 言語(3) 説明(終端付き) 歌詞 */
    private static byte[] uslt(int encoding, Charset charset, byte[] terminator, String description, String lyrics) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(encoding);
        out.writeBytes("jpn".getBytes(StandardCharsets.ISO_8859_1));
        out.writeBytes(description.getBytes(charset));
        out.writeBytes(terminator);
        out.writeBytes(lyrics.getBytes(charset));
        return out.toByteArray();
    }

    private static byte[] textFrameBody(String text) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(3);
        out.writeBytes(text.getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }

    @Test
    public void readsUtf8LyricsFromId3v24() {
        byte[] data = tag(4,
                frame(4, "TIT2", textFrameBody("さくら道")),
                frame(4, "USLT", uslt(3, StandardCharsets.UTF_8, new byte[]{0}, "", "[00:00.00]桜の道\n[00:04.00]歩こう")));

        assertEquals("[00:00.00]桜の道\n[00:04.00]歩こう", Id3LyricsReader.read(data));
    }

    @Test
    public void readsUtf16LyricsWithBomFromId3v23() {
        byte[] data = tag(3,
                frame(3, "USLT", uslt(1, StandardCharsets.UTF_16, new byte[]{0, 0}, "desc", "夕焼けの歌")));

        assertEquals("夕焼けの歌", Id3LyricsReader.read(data));
    }

    @Test
    public void readsLatin1Lyrics() {
        byte[] data = tag(3,
                frame(3, "USLT", uslt(0, StandardCharsets.ISO_8859_1, new byte[]{0}, "", "Hello lyrics")));

        assertEquals("Hello lyrics", Id3LyricsReader.read(data));
    }

    @Test
    public void returnsNullWhenThereIsNoLyricsFrame() {
        byte[] data = tag(4, frame(4, "TIT2", textFrameBody("Drive")));

        assertNull(Id3LyricsReader.read(data));
    }

    @Test
    public void returnsNullWhenThereIsNoId3Tag() {
        assertNull(Id3LyricsReader.read(new byte[]{(byte) 0xFF, (byte) 0xFB, 0x10, 0x00}));
        assertNull(Id3LyricsReader.read(new byte[0]));
        assertNull(Id3LyricsReader.read(null));
    }

    @Test
    public void brokenFrameSizeDoesNotCrash() {
        byte[] data = tag(4, frame(4, "USLT", uslt(3, StandardCharsets.UTF_8, new byte[]{0}, "", "abc")));
        // フレームサイズをタグ全体より大きく書き換える
        data[10 + 4] = 0x7F;

        assertNull(Id3LyricsReader.read(data));
    }
}
