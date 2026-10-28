# ストア掲載情報（案）

Google Play の「メインのストアの掲載情報」に入力する文面と素材。登録手順は [play-store-release.md](../play-store-release.md) の手順6。

## 素材

| 種類 | ファイル | 備考 |
| --- | --- | --- |
| アプリアイコン（512×512） | [icon-512.png](icon-512.png) | アプリのアイコン（ベクター）から書き出したもの |
| フィーチャー グラフィック（1024×500） | [feature-graphic-ja.jpg](feature-graphic-ja.jpg) / [feature-graphic-en.jpg](feature-graphic-en.jpg) | 透過なしの JPEG |
| スクリーンショット（スマートフォン） | [screenshots/](screenshots/) | 日本語・英語各5枚。1388×2776（縦横比 2:1）。システムバーを除き、左右に背景色の余白を付けている |

スクリーンショットの曲・ジャケットはすべて自作のサンプル（合成音源）。

| 順番 | ファイル（ja / en） | 内容 |
| --- | --- | --- |
| 1 | ja-1-library-list / en-1-library-list | ライブラリ（リスト表示、サムネイル付き） |
| 2 | ja-2-library-grid / en-2-library-grid | アイコン表示 |
| 3 | ja-3-album / en-3-album | アルバム単位のシャッフル・リピート |
| 4 | ja-4-player / en-4-player | プレイヤー（再生速度・シャッフル） |
| 5 | ja-5-lyrics / en-5-lyrics | 歌詞表示（MP3に埋め込まれた同期歌詞） |

## 日本語（ja-JP）

**アプリ名**（30文字以内）

```
mp3player - シンプルな音楽プレーヤー
```

**簡単な説明**（80文字以内）

```
無料・広告なしのMP3プレーヤー。再生速度0.5〜4倍、アプリを閉じても再生を続けるバックグラウンド再生に対応。
```

**詳しい説明**（4,000文字以内）

```
端末に保存したMP3を再生する、無料のシンプルな音楽プレーヤーです。
広告は一切表示しません。アプリ内課金もありません。

■ 主な機能
・音声の再生
  端末内のMP3を自動で読み込み、曲・アルバム・アーティスト・フォルダ別にすぐ再生できます。
・スピードコントロール
  再生速度を0.5〜4倍で調整できます。−／＋ボタンで0.1倍ずつ、または0.75倍・1.5倍・2倍などの候補からワンタップで選べます。語学学習、講義やオーディオブックの聞き流しにも便利です。
・バックグラウンド再生
  アプリを閉じても、画面を消しても再生を続けます。通知やロック画面、Bluetoothイヤホンのボタンから操作できます。

■ そのほかの機能
・アルバム・フォルダ単位のシャッフル再生、1曲リピート、全曲リピート
・リスト表示とアイコン表示（ジャケットを大きく表示）の切り替え、MP3に埋め込まれたジャケット画像の表示
・歌詞表示（MP3に埋め込まれた歌詞、またはLRCファイル。再生位置に合わせて強調表示）
・イコライザー、音量調整、スリープタイマー
・前回の曲・再生位置・速度を次回起動時に復元
・プレイリスト（作成・並べ替え・M3U形式でのエクスポート／インポート）、お気に入り、再生履歴
・ホーム画面ウィジェット、Android Auto
・ダークモード、日本語／英語表示

■ プライバシー
インターネットに接続しないアプリです。再生履歴などの情報はすべて端末内にだけ保存し、外部に送信しません。

■ 動作確認端末
Google Pixel 8 Pro
```

## English（en-US）

**App name** (30 characters max)

```
mp3player: Simple Music Player
```

**Short description** (80 characters max)

```
Free, ad-free MP3 player with 0.5x-4x speed control and background playback.
```

**Full description** (4,000 characters max)

```
A free, simple music player for the MP3 files on your device.
No ads, ever. No in-app purchases.

KEY FEATURES
- Audio playback
  Finds the MP3s on your device automatically and lets you play them by song, album, artist, or folder.
- Speed control
  Adjust playback speed from 0.5x to 4x. Step by 0.1x with the -/+ buttons, or pick 0.75x, 1.5x, 2x and more with one tap. Great for language learning, lectures, and audiobooks.
- Background playback
  Keeps playing when you leave the app or turn off the screen. Control playback from notifications, the lock screen, or your Bluetooth headset.

MORE FEATURES
- Shuffle, repeat one, or repeat a whole album or folder
- List view or icon view with large cover art; shows cover art embedded in your MP3s
- Lyrics (embedded in the MP3 or from LRC files), highlighted in sync with playback
- Equalizer, volume, and sleep timer
- Resumes the last song, position, and speed the next time you open the app
- Playlists (create, reorder, export and import as M3U), favorites, and playback history
- Home screen widget and Android Auto
- Dark mode; Japanese and English

PRIVACY
The app never connects to the internet. Your playback history and settings stay on your device and are never sent anywhere.

TESTED ON
Google Pixel 8 Pro
```

## そのほかの登録項目

| 項目 | 内容 |
| --- | --- |
| カテゴリ | 音楽＆オーディオ |
| プライバシーポリシー | [privacy-policy.md](../privacy-policy.md) を GitHub Pages などで公開した URL |
| 連絡先メールアドレス | 公開用のアドレスを用意する（必須） |
