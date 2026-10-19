package com.mp3player.app.ui.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/** Glideはモデルのequals/hashCodeをキャッシュキーに使うため、曲IDで同一性が決まることを確認する */
public class EmbeddedArtworkTest {

    @Test
    public void sameSong_isSameArtwork() {
        EmbeddedArtwork a = new EmbeddedArtwork(10L);
        EmbeddedArtwork b = new EmbeddedArtwork(10L);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void differentSongs_areDifferentArtwork() {
        assertNotEquals(new EmbeddedArtwork(10L), new EmbeddedArtwork(11L));
    }

    @Test
    public void exposesSongId() {
        assertEquals(42L, new EmbeddedArtwork(42L).songId);
    }
}
