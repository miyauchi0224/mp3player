package com.mp3player.app.ui.settings;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** SAF で選んだフォルダの表示名（ドキュメントID「ストレージ:パス」からパス部分を取り出す） */
public class TreeFolderLabelTest {

    @Test
    public void showsPathWithinStorage() {
        assertEquals("Music", TreeFolderLabel.of("primary:Music"));
        assertEquals("Music/Lyrics", TreeFolderLabel.of("primary:Music/Lyrics"));
    }

    @Test
    public void storageRootIsShownAsSlash() {
        assertEquals("/", TreeFolderLabel.of("primary:"));
    }

    @Test
    public void idWithoutStoragePrefixIsShownAsIs() {
        assertEquals("Lyrics", TreeFolderLabel.of("Lyrics"));
    }

    @Test
    public void missingIdIsEmpty() {
        assertEquals("", TreeFolderLabel.of(null));
    }
}
