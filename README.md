# mp3player

Android向けMP3プレイヤーアプリ

## 主な機能

### 再生

- 単一再生
  - 音量調整
  - 速度調整
  - イコライザー／音響エフェクト
  - 歌詞表示（LRC対応）
- 複数再生
  - 再生単位、アルバム単位、フォルダ単位、アーティスト単位
  - ループ再生機能
- バックグラウンド再生
  - ロック画面・通知欄でのメディアコントロール
  - Bluetooth／有線イヤホンのボタン操作対応
- スリープタイマー（指定時間後に自動停止）

### ライブラリ操作

- 端末全体検索によるMP3検索
- フォルダ読み取り
- タグ編集（曲名・アーティスト・アルバムアート）
- ソート／絞り込み（曲名・アーティスト・追加日など）

### プレイリスト

- 作成、編集
- エクスポート／インポート

### その他

- 音声録音機能
- クラウド読み込み
- ホーム画面ウィジェット
- Android Auto対応

### アプリ設定

- プライバシーポリシー
- ライセンス
- ダークモード対応
- 多言語対応（日本語・英語など）

## データベース

再生履歴を保持し、以下を表示する。

- 最近再生した曲
- 再生回数が多い曲
- お気に入り

## 技術スタック

- 言語: Java
- UI: XML Layout + View Binding
- アーキテクチャ: MVVM
- 非同期処理: LiveData + ExecutorService
- DI: Hilt
- ローカルDB: Room
- メディア再生・バックグラウンド再生: Media3 (ExoPlayer + MediaLibraryService)
- 画像読み込み: Glide
- ナビゲーション: Navigation Component (XML)
- クラウド連携: Storage Access Framework（ドキュメントピッカー）
- テスト: JUnit, Espresso
- CI/CD: GitHub Actions

## 実装状況

Android Studioで開けるプロジェクトとして、README記載の機能を一通り実装済み（詳細はtasks.md参照）。実機・エミュレータでの動作確認は未実施のため、初回ビルドで細部の修正が必要になる可能性がある。

- [x] プロジェクト基盤（Hilt / Room / Media3 / MVVM構成）
- [x] 端末全体のMP3検索・ライブラリ表示（曲／アルバム／アーティスト／フォルダ／履歴）
- [x] アルバム・アーティスト・フォルダ単位の連続再生、ループ再生
- [x] バックグラウンド再生・ロック画面/通知欄からの操作
- [x] 音量・速度調整、イコライザー、歌詞表示（LRC）、スリープタイマー
- [x] タグ編集、ソート／絞り込み、対象フォルダ管理
- [x] プレイリストの作成・編集・エクスポート／インポート（M3U）
- [x] お気に入り、最近再生した曲・再生回数ランキング
- [x] 音声録音、クラウド（SAF経由）読み込み
- [x] ホーム画面ウィジェット、Android Auto対応（MediaLibraryService）
- [x] ダークモード切替、多言語対応（日本語／英語）、プライバシーポリシー・ライセンス画面
- [x] アルバム／フォルダ単位のシャッフル・リピート、リスト／アイコン表示の切り替え、MP3埋め込みサムネイル・歌詞の表示
- [x] 再生速度0.5〜4倍（候補から選択）、プレイヤー画面のシャッフル、再生状態・設定の次回起動時の復元
- [x] Android 16（API 36）対応（edge-to-edge）、署名付きリリースビルド（R8）、DBの移行テスト、TalkBack対応

### 実装上の既知の制約

- **録音ファイルはMP3ではなくAAC(.m4a)**: AndroidのMediaRecorderはMP3エンコード出力に対応していないため。
- **タグ編集はMediaStoreのメタデータ更新のみ**: 曲ファイル内のID3バイナリタグ自体は書き換えない。
- **クラウド読み込みはSAFドキュメントピッカー経由**: Google Drive専用SDK/OAuth連携ではなく、端末にインストールされたクラウドアプリ（Drive等）をシステム標準の選択画面から選ぶ方式。
- **歌詞の .lrc ファイルは、Android 10以降では設定の「歌詞フォルダ」で選んだフォルダから読む**: MP3に埋め込まれた歌詞（ID3 の USLT）は許可なしで表示できる。
- **Android Auto・イコライザーはMedia3の比較的新しいAPI（MediaLibraryService／カスタムセッションコマンド）を使用**しており、実機・実行環境での検証ができていないため、Android Studioでのビルド時に細部の修正が必要になる可能性が他の画面より高い。

## ビルド方法

コマンドラインでは Gradle Wrapper を使う（Gradle 8.13 / Android Gradle Plugin 8.13）。

```sh
./gradlew test assembleDebug
```

## 開発ルール

### テスト

- 開発スタイルはTDD（機能単位で、失敗するテスト → 実装 → テスト成功）。
- 単体テスト: `./gradlew testDebugUnitTest`（`app/src/test`）
- 端末・エミュレータでのテスト: `./gradlew connectedDebugAndroidTest`（`app/src/androidTest`）。結果の受け取りに失敗する場合は、`./gradlew installDebug installDebugAndroidTest` のあと `adb shell am instrument -w io.github.miyauchi0224.mp3player.test/androidx.test.runner.AndroidJUnitRunner` で直接実行する。

### DBのスキーマを変えるとき

公開後のアップデートでお気に入り・プレイリスト・履歴を失わないため、次の手順を守る。`fallbackToDestructiveMigration` は使わない。

1. エンティティを変更し、`AppDatabase.VERSION` を1つ上げる
2. 1つ前のバージョンからの `Migration` を書き、`AppDatabase.MIGRATIONS` に追加する
3. ビルドすると `app/schemas/` に新しいスキーマJSONが出力されるので、コミットする
4. `MigrationTest`（androidTest）を実行し、移行後もデータが残ることを確認する

`DatabaseSchemaSpecTest`（単体テスト）が、スキーマJSONと Migration の書き忘れを検出する。

## Android Studioで開く

1. Android Studio（Hedgehog以降推奨）で本フォルダを開く
2. ビルドには **JDK 17** を使う。`gradle/gradle-daemon-jvm.properties` で JDK 17 を指定しているため、Android Studio の Gradle JDK 設定に関わらず JDK 17 で動く（JDK 17 がインストールされている必要がある）
3. Gradle Sync完了後、実機またはエミュレータ（API 24以上）でRun
4. 初回起動時に音楽ファイル・通知へのアクセス許可を求められるので許可すると、端末内のMP3が自動スキャンされる


## memo
アルバム単位でランダム再生、リピート再生（１曲）、リピート再生（アルバム）
フォルダ単位でランダム再生、リピート再生（１曲）、リピート再生（アルバム）


リスト表示に加えて、アイコン表示可能。
mp3にサムネイルがある場合、サムネイルも表示



無料公開アプリ、
広告なし
主な機能：音声再生、スピードコントロール
バックグランド再生
テスト環境：Pixel8Pro




方法	Google Play の規約	このアプリへの影響
A. アプリ外で受け付ける（GitHub Sponsors、Ko-fi などを GitHub やウェブサイトに置く）	アプリ内の決済ルールの対象外	アプリの変更は不要。「インターネット通信なし」「データ収集なし」のままにできる




