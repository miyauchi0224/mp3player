# 実装タスク一覧

stories.mdの各ストーリーを実装単位に分解したタスク一覧。No.はstories.mdのストーリーNoに対応。
すべてAndroid Studioプロジェクトとして実装済み（実機・エミュレータでの動作確認は未実施）。

## 基盤・共通

- [x] プロジェクト作成（Java, minSdk/targetSdk設定）
- [x] Hiltセットアップ（Application/Module定義）
- [x] Room DBセットアップ（Database, 基本Entity/DAO雛形）
- [x] Media3(ExoPlayer)基盤導入、PlaybackServiceの土台実装
- [x] Navigation Component導入、画面遷移の土台構築
- [x] 共通UIテーマ・スタイル定義
- [x] GitHub Actions CI設定（build, lint, test）※gradle-wrapper.jar未同梱のためGradle本体をセットアップして実行
- [x] 実行時パーミッション共通ハンドリング（READ_MEDIA_AUDIO等）

## 再生

### No.1 音量調整
- [x] PlayerViewModelに音量制御メソッド追加
- [x] ExoPlayerのsetVolumeと連携
- [x] 音量スライダーUI実装（SeekBar、プレイヤー設定シート内）
- [x] 音量変更のリアルタイム反映

### No.2 速度調整
- [x] PlaybackParametersで再生速度変更を実装
- [x] 速度選択UI（ボトムシート）作成
- [x] 速度設定はセッション状態として保持（アプリ再起動をまたぐ永続化は未実装）

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
- [ ] バッテリー最適化除外案内ダイアログ（未実装）

### No.8 ロック画面・通知欄操作
- [x] MediaStyle通知（Media3標準機能で自動対応）
- [x] 通知からの再生/一時停止/次へ/前へ操作連携
- [x] ロック画面へのアートワーク表示

### No.9 Bluetooth／イヤホンボタン操作
- [x] MediaSessionが標準でメディアボタンイベントを処理（追加実装不要）
- [ ] 実機（Bluetoothデバイス）での動作確認（未実施）

### No.10 スリープタイマー
- [x] タイマー時間選択UI（プレイヤー設定シート内チップ）作成
- [x] Handler.postDelayedによる再生停止処理実装
- [x] タイマー動作中の残り時間表示

## ライブラリ操作

### No.11 端末全体MP3検索
- [x] MediaStore.Audioクエリ実装
- [x] Android 13以降のREAD_MEDIA_AUDIO対応
- [x] スキャン結果のRoom DB保存処理
- [ ] スキャン中のプログレス表示UI（未実装、バックグラウンド実行のみ）

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
- [ ] エミュレータ／実機（Android Auto）での動作確認（未実施、要検証）

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
