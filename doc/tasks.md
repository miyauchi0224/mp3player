# 実装タスク一覧

stories.mdの各ストーリーを実装単位に分解したタスク一覧。No.はstories.mdのストーリーNoに対応。
No.1〜30は実装済み。2026-10-19 時点で、エミュレータ（Android 11 / API 30、32bit版）での主要画面の動作確認と単体テスト（19件）まで実施。実機での確認は未実施（→ No.35）。
No.31以降はリリースに向けたタスク（2026-10-19 のレビューで追加）。

## 基盤・共通

- [x] プロジェクト作成（Java, minSdk/targetSdk設定）
- [x] Hiltセットアップ（Application/Module定義）
- [x] Room DBセットアップ（Database, 基本Entity/DAO雛形）
- [x] Media3(ExoPlayer)基盤導入、PlaybackServiceの土台実装
- [x] Navigation Component導入、画面遷移の土台構築
- [x] 共通UIテーマ・スタイル定義
- [x] GitHub Actions CI設定（build, lint, test）※gradle-wrapper.jar未同梱のためGradle本体をセットアップして実行
- [x] 実行時パーミッション共通ハンドリング（READ_MEDIA_AUDIO等）
- [x] CIのlintエラー修正（Media3の不安定API利用の宣言、Android Auto音声検索のintent-filter）
- [x] GitHub Actionsのアクション更新（checkout/setup-java v5、setup-gradle v5）
- [x] アプリアイコン作成（アダプティブアイコン・テーマアイコン・Android 7.x用の丸型）
- [x] 単体テストの土台（app/src/test、JUnit4）。開発スタイルはTDD

## 再生

### No.1 音量調整
- [x] PlayerViewModelに音量制御メソッド追加
- [x] ExoPlayerのsetVolumeと連携
- [x] 音量スライダーUI実装（SeekBar、プレイヤー設定シート内）
- [x] 音量変更のリアルタイム反映

### No.2 速度調整
- [x] PlaybackParametersで再生速度変更を実装
- [x] 速度選択UI（ボトムシート）作成
- [x] プレイヤー画面に速度の−／＋ボタン追加（0.1倍刻み、0.5〜4.0倍、数値タップで1.0倍に戻す）
- [x] 速度設定はセッション状態として保持（アプリ再起動をまたぐ永続化は未実装 → No.39）

### No.3 イコライザー
- [x] Android Equalizer APIとの連携実装（PlaybackServiceのカスタムセッションコマンド経由）
- [x] プリセット選択UI作成
- [x] バンドごとの調整スライダーUI作成（端末のバンド数に応じて動的生成）
- [x] イコライザー設定はOS側Equalizerインスタンスに保持

### No.4 歌詞表示
- [x] LRCファイルパーサー実装
- [x] 同名.lrcファイルの検索処理（scoped storage環境では読み取れない場合がある）
- [x] 再生位置に同期した歌詞ハイライト表示
- [x] 歌詞未検出時のフォールバックUI

### No.5 複数再生（アルバム／フォルダ／アーティスト単位）
- [x] MediaStoreクエリによるグループ化取得処理
- [x] 再生キュー生成ロジック実装
- [x] アルバム／フォルダ／アーティスト一覧画面UI作成
- [x] 一覧タップからの連続再生開始処理

### No.6 ループ再生
- [x] Repeatモード（OFF/ONE/ALL）切替ロジック実装
- [x] UIボタン・アイコン切替実装
- [x] ExoPlayerのRepeatMode連携

### No.7 バックグラウンド再生
- [x] MediaSessionService（MediaLibraryService）実装
- [x] Foreground Service化・通知（Media3標準通知）
- [x] アプリ終了後の再生継続
- [ ] バッテリー最適化除外案内ダイアログ（未実装 → No.47）

### No.8 ロック画面・通知欄操作
- [x] MediaStyle通知（Media3標準機能で自動対応）
- [x] 通知からの再生/一時停止/次へ/前へ操作連携
- [x] ロック画面へのアートワーク表示

### No.9 Bluetooth／イヤホンボタン操作
- [x] MediaSessionが標準でメディアボタンイベントを処理（追加実装不要）
- [ ] 実機（Bluetoothデバイス）での動作確認（未実施 → No.35）

### No.10 スリープタイマー
- [x] タイマー時間選択UI（プレイヤー設定シート内チップ）作成
- [x] Handler.postDelayedによる再生停止処理実装
- [x] タイマー動作中の残り時間表示

## ライブラリ操作

### No.11 端末全体MP3検索
- [x] MediaStore.Audioクエリ実装
- [x] Android 13以降のREAD_MEDIA_AUDIO対応
- [x] スキャン結果のRoom DB保存処理
- [ ] スキャン中のプログレス表示UI（未実装、バックグラウンド実行のみ → No.46）

### No.12 フォルダ読み取り
- [x] 対象フォルダ管理設定画面作成（検出済みフォルダのチェックリスト方式）
- [x] 選択フォルダによるライブラリ絞り込み処理
- 備考: SAFでの直接フォルダ選択ではなく、スキャン済みフォルダから対象を選ぶ方式で実装

### No.13 タグ編集
- [x] タグ編集UI（曲名／アーティスト／アルバム）作成
- [x] 変更内容のMediaStoreへの反映処理（RecoverableSecurityException対応込み）
- 備考: 埋め込みID3バイナリの書き換えは行わず、MediaStoreのメタデータ更新のみ

### No.14 ソート／絞り込み
- [x] ソート条件選択UI作成（曲名／アーティスト／追加日）
- [x] 曲名／アーティストによる絞り込み検索実装

## プレイリスト

### No.15 プレイリスト作成
- [x] プレイリストDB設計（Room Entity/DAO）
- [x] 新規プレイリスト作成UI
- [x] 曲追加フロー実装

### No.16 プレイリスト編集
- [x] 曲削除・並び替え（スワイプ削除・ドラッグ並び替え）UI実装
- [x] プレイリスト名編集機能
- [x] 変更内容の即時反映処理

### No.17 プレイリストのエクスポート／インポート
- [x] M3U形式でのエクスポート実装
- [x] ファイル選択によるインポート実装（ファイル名一致で曲を照合）
- [x] 読み込み失敗時のエラーハンドリング

## その他

### No.18 音声録音
- [x] MediaRecorderによる録音機能実装（AAC/.m4a、MediaRecorderはMP3出力非対応のため）
- [x] 録音UI（開始／停止／一覧／プレビュー再生）作成
- 備考: 録音ファイルはアプリ専用ディレクトリに保存し、曲ライブラリとは別リストで管理

### No.19 クラウド読み込み
- [x] SAFドキュメントピッカーによるファイル選択実装
- [x] MediaStoreへの登録によるライブラリ統合
- 備考: Google Drive専用SDK/OAuthではなく、Drive等のクラウドアプリも表示されるシステム標準ピッカーを使用

### No.20 ホーム画面ウィジェット
- [x] AppWidgetProvider実装
- [x] ウィジェットレイアウト作成
- [x] 再生状態のウィジェットへの反映処理（PlaybackServiceからプッシュ更新）

### No.21 Android Auto対応
- [x] MediaLibraryService対応実装（ブラウズツリー: 全曲/プレイリスト/お気に入り）
- [x] automotive_app_desc.xml・マニフェスト設定
- [x] 音声検索（MEDIA_PLAY_FROM_SEARCH）対応。検索語で曲名・アーティスト・アルバムを検索して再生
- [ ] エミュレータ／実機（Android Auto）での動作確認（未実施、要検証 → No.35）

## アプリ設定

### No.22 プライバシーポリシー・ライセンス
- [x] プライバシーポリシー画面実装
- [x] ライセンス一覧表示（静的テキスト。自動生成ツールは未導入）
- [x] 設定画面からの導線追加

### No.23 ダークモード対応
- [x] 設定画面でのテーマ切替UI実装（システム従う／ライト／ダーク）
- [x] AppCompatDelegateによる全画面への即時反映

### No.24 多言語対応
- [x] strings.xmlの多言語化（values-en追加）
- [x] 言語切替設定UI実装（AppCompatDelegate.setApplicationLocales）

## 履歴・統計

### No.25 最近再生した曲
- [x] 再生履歴記録処理実装（lastPlayedAt）
- [x] 最近再生一覧画面UI作成（ライブラリ内「履歴」タブ）
- [x] 履歴クリア機能実装

### No.26 再生回数が多い曲
- [x] 再生回数カウント処理実装（playCount）
- [x] ランキング一覧画面UI作成

### No.27 お気に入り
- [x] お気に入りフラグのRoom Entity追加
- [x] お気に入り登録／解除UI実装
- [x] お気に入り一覧画面作成

## 追加機能（2026-10-19）

### No.28 アルバム／フォルダ単位のシャッフル・リピート
- [x] 再生モードから再生順・リピート設定を決めるロジック（GroupPlayPlanner、単体テスト9件）
- [x] シャッフルはアプリ側で再生順を並べ替える方式（プレーヤーのシャッフル機能では開始位置より前の曲が再生されない不具合があったため）
- [x] グループ曲一覧に「シャッフル」「1曲リピート」「アルバム／フォルダ／アーティストをリピート」ボタン
- [x] 通常の再生開始時にシャッフル状態を解除

### No.29 アイコン表示
- [x] 表示方法の切り替え・保存値の復元（SongViewMode、単体テスト4件）
- [x] 画面幅からの列数計算（GridSpan、単体テスト3件）
- [x] グリッド用セルのレイアウト（item_song_grid.xml）
- [x] 曲タブ・グループ曲一覧・お気に入りに切替ボタン。設定はUserPreferencesに保存し画面間で共有

### No.30 埋め込みサムネイル表示
- [x] MP3埋め込み画像（ID3のAPIC）をMediaMetadataRetrieverで読むGlideのModelLoader
- [x] キャッシュキーのモデル（EmbeddedArtwork、単体テスト3件）
- [x] 埋め込み画像 → MediaStoreのアルバムアート → 音符アイコンの順に表示（一覧・プレイヤー・ミニプレイヤー・プレイリスト詳細）

## リリース準備：必須

### No.31 Android 16（API 36）対応
- [ ] compileSdk・targetSdk を 36 に変更
- [ ] edge-to-edge 表示への対応（Android 15以降を対象にすると強制される）。WindowInsetsで、ステータスバー・ナビゲーションバーと画面上部・ボトムナビ・ミニプレイヤーが重ならないようにする
- [ ] 予測型「戻る」ジェスチャーの動作確認（AndroidManifestの enableOnBackInvokedCallback）
- [ ] 大画面（幅600dp以上）で画面の向き・サイズ固定が無視される変更への対応確認
- [ ] Android 14〜16 の動作変更の確認（フォアグラウンドサービス、通知、メディア権限）
- [ ] API 36 環境での動作確認（このPCでは64bitエミュレータが動かないため、実機または別環境で → No.35）

### No.32 ビルド環境の整備（No.31の前提）
- [ ] Android Gradle Plugin を 8.2.2 から API 36 対応版へ更新（Android Studioの Upgrade Assistant は 8.13.2 / Gradle 8.13 を提示）
- [ ] gradle-wrapper.jar を生成してコミットし、CIを ./gradlew 実行に切り替え
- [ ] 古い依存ライブラリの更新（lint の GradleDependency 警告19件。Media3・AndroidX・Material など）
- [ ] JDKのバージョンを固定（Gradle の toolchain 設定、または README に JDK 17 を明記）

### No.33 DBの移行の仕組み
- [ ] Room の exportSchema を有効にし、スキーマJSONをリポジトリで管理
- [ ] 次回以降のスキーマ変更で Migration を書く運用を README に明記（fallbackToDestructiveMigration は使わない）
- [ ] 移行テストの土台（MigrationTestHelper を使った androidTest）

### No.34 リリースビルド
- [ ] アップロード鍵（keystore）の作成と安全な保管（リポジトリには入れない）
- [ ] signingConfig の設定（鍵情報は local.properties または CI のシークレットから読む）
- [ ] R8（minifyEnabled）とリソース縮小（shrinkResources）を有効化し、Glide・Room・Hilt・Media3 の ProGuard ルールを確認
- [ ] リリースビルドでの動作確認（縮小で壊れていないか）
- [ ] versionCode／versionName の運用ルール決め
- [ ] AAB（bundleRelease）の作成と Play App Signing の設定

### No.35 実機での動作確認（Android 13以降）
- [ ] 権限：音楽ファイルの読み取り（READ_MEDIA_AUDIO）・通知（POST_NOTIFICATIONS）・マイク（録音時）
- [ ] バックグラウンド再生・通知・ロック画面での操作
- [ ] Bluetooth／有線イヤホンのボタン操作（No.9）
- [ ] タグ編集（他アプリの曲を書き換えるときの許可ダイアログ）
- [ ] イコライザー、録音、クラウド読み込み、ホーム画面ウィジェット
- [ ] Android Auto（Desktop Head Unit または実車）でのブラウズ・音声検索（No.21）
- [ ] テーマアイコン（Android 13以降）、ダークモード、英語表示
- [ ] 確認結果をチェックリストとして doc に残す

### No.36 ストア掲載・ポリシー対応
- [ ] ストア用素材：512×512 アイコン、1024×500 フィーチャーグラフィック、スクリーンショット（日本語・英語）
- [ ] ストア掲載文（アプリ名・短い説明・詳しい説明、日本語・英語）
- [ ] プライバシーポリシーを公開URLに掲載（アプリ内の文面と一致させる）
- [ ] データセーフティの申告（データの収集・共有なし、マイクは録音機能のみで使用）
- [ ] コンテンツのレーティング、対象年齢の設定
- [ ] テスト配信（内部テスト → クローズドテスト）。個人の開発者アカウントの場合、製品版公開の前にクローズドテストの条件（テスター人数・期間）を満たす必要があるため、最新の条件を Play Console で確認

## リリース準備：推奨

### No.37 英語表示での日本語の混在をなくす
- [ ] 曲数の表示を複数形リソース（plurals）に（AlbumsFragment・ArtistsFragment・FoldersFragment・FolderCheckAdapter）
- [ ] お気に入りメニューの文言を文字列リソースに（SongMenuHelper）
- [ ] スリープタイマーの「15分／30分／60分」を文字列リソースに（dialog_playback_settings.xml）
- [ ] lint の HardcodedText 警告（5件）の解消

### No.38 プレイヤー画面の見た目の整理
- [ ] プレイヤー画面を開いている間はミニプレイヤーを隠す
- [ ] 「戻る」ボタンのアイコンを戻る矢印に変更（現在は「前の曲」アイコン ic_previous を6画面で流用）

### No.39 再生設定・再生位置の保持
- [ ] 再生速度・リピート設定を保存し、起動時に復元
- [ ] 最後に再生していた曲・再生位置・キューの復元（Media3 の再生再開の仕組みを利用）
- [ ] 保存・復元ロジックの単体テスト

### No.40 TalkBack 対応
- [ ] 操作できるボタンの contentDescription を設定（戻る・メニュー・すべて再生ボタンなど、現在 @null のもの）
- [ ] TalkBack を有効にした状態で主要な操作を一通り確認

### No.41 Android 10以降での歌詞表示
- [ ] .lrc ファイルをファイルパスではなく MediaStore またはフォルダのアクセス許可（SAF）経由で読む
- [ ] MP3に埋め込まれた歌詞（ID3のUSLT）の読み込みも検討
- [ ] LyricsParser の単体テスト追加

### No.42 クラッシュ・フリーズの把握
- [ ] Play Console の Android vitals（クラッシュ・ANR）を定期的に確認する手順を決める
- 備考: プライバシーポリシーで「外部送信なし」としているため、外部のクラッシュ収集SDKは入れない

### No.43 再生サービスの操作制限
- [ ] onConnect で、イコライザー等の独自コマンドを自アプリからの接続にだけ許可する
- [ ] lint の ExportedService 警告の確認

## リリース準備：任意

### No.44 再生速度のプリセット
- [ ] 速度の数値をタップしたときに 0.5／1.0／1.5／2.0／3.0／4.0 倍の候補を表示

### No.45 プレイヤー画面のシャッフル表示
- [ ] プレイヤー画面にシャッフルボタンを追加し、状態を表示・切り替え

### No.46 スキャン進捗の表示
- [ ] スキャン中のプログレス表示（No.11の残タスク）

### No.47 バッテリー最適化の案内
- [ ] バッテリー最適化の除外を案内するダイアログ（No.7の残タスク）

### No.48 アイコン表示の対象拡大
- [ ] プレイリスト詳細（ドラッグ並び替えとの両立）と履歴（2セクション表示との両立）にもアイコン表示を追加

### その他（lint 警告）
- [ ] Overdraw（16件）、UnusedResources（5件）、NotifyDataSetChanged（4件）などの整理
