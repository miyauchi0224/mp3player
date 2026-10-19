package com.mp3player.app;

import static org.junit.Assert.assertTrue;

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

/**
 * 読み上げ（TalkBack）で全てのボタンを操作できること（No.40）。
 * 文字を持たないアイコンだけのボタンには、文字列リソースの説明が必要。
 */
public class AccessibilitySpecTest {

    private static final Pattern ICON_BUTTON = Pattern.compile(
            "<(ImageButton|com\\.google\\.android\\.material\\.floatingactionbutton\\.FloatingActionButton)\\b[^>]*>");

    @Test
    public void iconButtonsHaveSpokenDescription() throws IOException {
        List<Path> layouts;
        try (Stream<Path> stream = Files.walk(Paths.get("src/main/res/layout"))) {
            layouts = stream.filter(p -> p.toString().endsWith(".xml")).collect(Collectors.toList());
        }
        List<String> missing = new ArrayList<>();
        for (Path layout : layouts) {
            Matcher m = ICON_BUTTON.matcher(new String(Files.readAllBytes(layout), StandardCharsets.UTF_8));
            while (m.find()) {
                if (!m.group().matches("(?s).*android:contentDescription=\"@string/[^\"]+\".*")) {
                    Matcher id = Pattern.compile("android:id=\"@\\+id/([^\"]+)\"").matcher(m.group());
                    missing.add(layout.getFileName() + "#" + (id.find() ? id.group(1) : "(idなし)"));
                }
            }
        }
        assertTrue("contentDescription を設定する: " + missing, missing.isEmpty());
    }
}
