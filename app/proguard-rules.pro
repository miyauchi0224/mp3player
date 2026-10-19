# R8（リリースビルドの縮小・難読化）のルール。
# Glide・Room・Hilt・Media3 は各ライブラリが必要なルールを同梱しているため、ここではアプリ固有のものだけ書く。

# Glide: アプリ独自の AppGlideModule（Mp3GlideModule）は生成コードからリフレクションで生成される
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

# クラッシュ時のスタックトレースで行番号を読めるようにする（Play Console に mapping.txt が自動で送られる）
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
