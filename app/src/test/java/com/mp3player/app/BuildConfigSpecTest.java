package com.mp3player.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Google Play の公開条件など、ビルド設定・マニフェストで守るべき仕様を確認する。
 * 単体テストは app モジュールを作業ディレクトリとして実行される。
 */
public class BuildConfigSpecTest {

    /** 2026-08-31 以降、新規アプリと更新は API 36 以上を対象にする必要がある */
    private static final int REQUIRED_TARGET_SDK = 36;

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int intSetting(String gradle, String name) {
        Matcher m = Pattern.compile(name + "\\s+(\\d+)").matcher(gradle);
        assertTrue(name + " が build.gradle に無い", m.find());
        return Integer.parseInt(m.group(1));
    }

    @Test
    public void targetSdk_meetsGooglePlayRequirement() throws IOException {
        int targetSdk = intSetting(read("build.gradle"), "targetSdk");

        assertTrue("targetSdk " + targetSdk + " < " + REQUIRED_TARGET_SDK, targetSdk >= REQUIRED_TARGET_SDK);
    }

    @Test
    public void compileSdk_isAtLeastTargetSdk() throws IOException {
        String gradle = read("build.gradle");

        assertTrue(intSetting(gradle, "compileSdk") >= intSetting(gradle, "targetSdk"));
    }

    // --- リリースビルド（No.34） ---

    /** release ブロック（次の閉じ括弧まで）を取り出す */
    private static String releaseBlock(String gradle) {
        int start = gradle.indexOf("buildTypes");
        assertTrue("buildTypes が無い", start >= 0);
        int release = gradle.indexOf("release {", start);
        assertTrue("buildTypes に release が無い", release >= 0);
        return gradle.substring(release, gradle.indexOf("}", release));
    }

    @Test
    public void release_isShrunkWithR8() throws IOException {
        String release = releaseBlock(read("build.gradle"));

        assertTrue(release.contains("minifyEnabled true"));
        assertTrue(release.contains("shrinkResources true"));
        assertTrue(release.contains("signingConfig signingConfigs.release"));
    }

    @Test
    public void signing_readsSecretsFromUntrackedPropertiesFile() throws IOException {
        String gradle = read("build.gradle");

        assertTrue(gradle.contains("keystore.properties"));
        // パスワードを build.gradle に直接書かない
        assertFalse(Pattern.compile("storePassword\\s+['\"]").matcher(gradle).find());
        assertFalse(Pattern.compile("keyPassword\\s+['\"]").matcher(gradle).find());
    }

    @Test
    public void signingSecrets_areIgnoredByGit() throws IOException {
        String gitignore = read("../.gitignore");

        assertTrue(gitignore.contains("keystore.properties"));
        assertTrue(gitignore.contains("*.jks"));
        assertTrue(gitignore.contains("*.keystore"));
    }

    /** versionName は「メジャー.マイナー.パッチ」、versionCode はそこから一意に決める */
    @Test
    public void versionCode_followsVersionName() throws IOException {
        String gradle = read("build.gradle");
        Matcher name = Pattern.compile("versionName\\s+\"(\\d+)\\.(\\d+)\\.(\\d+)\"").matcher(gradle);
        assertTrue("versionName は x.y.z 形式にする", name.find());
        int expected = Integer.parseInt(name.group(1)) * 10000
                + Integer.parseInt(name.group(2)) * 100
                + Integer.parseInt(name.group(3));

        assertEquals(expected, intSetting(gradle, "versionCode"));
    }

    @Test
    public void manifest_optsIntoPredictiveBack() throws IOException {
        String manifest = read("src/main/AndroidManifest.xml");

        assertTrue("application に android:enableOnBackInvokedCallback=\"true\" が必要",
                manifest.contains("android:enableOnBackInvokedCallback=\"true\""));
    }
}
