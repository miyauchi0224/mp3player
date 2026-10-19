package com.mp3player.app;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 「戻る」ボタンに「前の曲」のアイコンを使わない（No.38） */
public class NavigationIconSpecTest {

    /** id が button_back の要素（開始タグ全体） */
    private static final Pattern BACK_BUTTON = Pattern.compile("<ImageButton[^>]*android:id=\"@\\+id/button_back\"[^>]*>");

    private static List<String> backButtons() throws IOException {
        List<String> tags = new ArrayList<>();
        List<Path> layouts;
        try (Stream<Path> stream = Files.walk(Paths.get("src/main/res/layout"))) {
            layouts = stream.filter(p -> p.toString().endsWith(".xml")).collect(Collectors.toList());
        }
        for (Path layout : layouts) {
            Matcher m = BACK_BUTTON.matcher(new String(Files.readAllBytes(layout), StandardCharsets.UTF_8));
            while (m.find()) {
                tags.add(layout.getFileName() + ": " + m.group());
            }
        }
        return tags;
    }

    @Test
    public void backButtonsExist() throws IOException {
        assertFalse(backButtons().isEmpty());
    }

    @Test
    public void backButtonsUseBackArrowIcon() throws IOException {
        for (String tag : backButtons()) {
            assertTrue("戻る矢印のアイコンにする: " + tag, tag.contains("android:src=\"@drawable/ic_arrow_back\""));
        }
    }
}
