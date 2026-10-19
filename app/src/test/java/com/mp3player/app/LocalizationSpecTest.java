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
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** 英語表示のときに日本語が混ざらないこと（No.37）を、リソースとソースを走査して確認する */
public class LocalizationSpecTest {

    private static final Pattern JAPANESE = Pattern.compile("[\\u3040-\\u30ff\\u4e00-\\u9fff]");
    /** 翻訳対象のリソース名。translatable="false" は言語によらない（例: 言語名「日本語」）ので除く */
    private static final Pattern TRANSLATABLE = Pattern.compile(
            "<(string|plurals)\\s+name=\"([^\"]+)\"(?![^>]*translatable=\"false\")");

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static Set<String> translatableNames(String file) throws IOException {
        Set<String> names = new TreeSet<>();
        Matcher m = TRANSLATABLE.matcher(read(Paths.get(file)));
        while (m.find()) {
            names.add(m.group(1) + ":" + m.group(2));
        }
        return names;
    }

    private static List<Path> files(String dir, String suffix) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir))) {
            return stream.filter(p -> p.toString().endsWith(suffix)).collect(Collectors.toList());
        }
    }

    @Test
    public void everyJapaneseStringHasEnglishTranslation() throws IOException {
        Set<String> missing = translatableNames("src/main/res/values/strings.xml");
        missing.removeAll(translatableNames("src/main/res/values-en/strings.xml"));

        assertTrue("英語訳が無い: " + missing, missing.isEmpty());
    }

    @Test
    public void everyEnglishStringHasJapaneseOriginal() throws IOException {
        Set<String> extra = translatableNames("src/main/res/values-en/strings.xml");
        extra.removeAll(translatableNames("src/main/res/values/strings.xml"));

        assertTrue("日本語の元が無い: " + extra, extra.isEmpty());
    }

    @Test
    public void javaSourceHasNoHardcodedJapanese() throws IOException {
        List<String> found = new ArrayList<>();
        for (Path file : files("src/main/java", ".java")) {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                // コメントとログは利用者に表示されないので対象外
                if (line.startsWith("//") || line.startsWith("*") || line.startsWith("/*") || line.contains("Log.")) {
                    continue;
                }
                String code = line.contains("//") ? line.substring(0, line.indexOf("//")) : line;
                Matcher literal = Pattern.compile("\"([^\"]*)\"").matcher(code);
                while (literal.find()) {
                    if (JAPANESE.matcher(literal.group(1)).find()) {
                        found.add(file.getFileName() + ":" + (i + 1) + " " + literal.group());
                    }
                }
            }
        }
        assertTrue("文字列リソースにする: " + found, found.isEmpty());
    }

    @Test
    public void layoutsHaveNoHardcodedText() throws IOException {
        // 記号だけの表示（−／＋ など）は言語に依存しないので許可する
        Pattern hardcoded = Pattern.compile("android:(text|hint|contentDescription)=\"([^@\"][^\"]*)\"");
        Pattern letter = Pattern.compile("\\p{L}");
        List<String> found = new ArrayList<>();
        for (Path file : files("src/main/res/layout", ".xml")) {
            Matcher m = hardcoded.matcher(read(file));
            while (m.find()) {
                if (letter.matcher(m.group(2)).find()) {
                    found.add(file.getFileName() + " " + m.group());
                }
            }
        }
        assertTrue("文字列リソースにする: " + found, found.isEmpty());
    }
}
