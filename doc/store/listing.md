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
端末のMP3をすぐ再生。アルバム単位のシャッフル・リピート、歌詞表示、イコライザー、再生速度0.5〜4倍に対応。
```

**詳しい説明**（4,000文字以内）

```
端末に保存したMP3を、シンプルな操作で気持ちよく再生できる音楽プレーヤーです。インターネットに接続しないので、聴いている曲の情報が外部に送られることはありません。

■ ライブラリ
・端末内のMP3を自動で読み込み、曲・アルバム・アーティスト・フォルダ別に表示
・リスト表示とアイコン表示（ジャケットを大きく表示）を切り替え
・MP3に埋め込まれたジャケット画像を表示
・曲名・アーティストで検索、曲名・アーティスト・追加日で並び替え
・表示するフォルダを選べます

■ 再生
・アルバム・フォルダ単位でシャッフル再生、1曲リピート、全曲リピート
・再生速度を0.5〜4倍で調整（語学学習や作業用BGMに）
・イコライザー、音量調整、スリープタイマー
・歌詞表示（MP3に埋め込まれた歌詞、またはLRCファイル。再生位置に合わせて強調表示）
・前回の曲・再生位置・速度を次回起動時に復元
・通知・ロック画面・Bluetoothイヤホン・ホーム画面ウィジェットから操作
・Android Auto に対応

■ プレイリストほか
・プレイリストの作成・並べ替え、M3U形式でのエクスポート／インポート
・お気に入り、最近再生した曲、再生回数ランキング
・音声録音、クラウドストレージ（Googleドライブ等）からのMP3取り込み
・ダークモード、日本語／英語表示
```

## English（en-US）

**App name** (30 characters max)

```
mp3player: Simple Music Player
```

**Short description** (80 characters max)

```
Play your MP3s. Album shuffle & repeat, lyrics, equalizer, and 0.5x-4x speed.
```

**Full description** (4,000 characters max)

```
A simple, comfortable music player for the MP3 files on your device. The app never connects to the internet, so what you listen to stays on your device.

LIBRARY
- Automatically finds MP3s on your device and organizes them by song, album, artist, and folder
- Switch between list view and icon view with large cover art
- Shows cover art embedded in your MP3 files
- Search by title or artist; sort by title, artist, or date added
- Choose which folders to show

PLAYBACK
- Shuffle, repeat one, or repeat a whole album or folder
- Playback speed from 0.5x to 4x (great for language learning)
- Equalizer, volume, and sleep timer
- Lyrics (embedded in the MP3 or from LRC files), highlighted in sync with playback
- Resumes the last song, position, and speed the next time you open the app
- Control from notifications, the lock screen, Bluetooth headsets, and a home screen widget
- Works with Android Auto

PLAYLISTS AND MORE
- Create and reorder playlists; export and import them as M3U files
- Favorites, recently played, and most played songs
- Voice recording, and import MP3s from cloud storage such as Google Drive
- Dark mode; Japanese and English
```

## そのほかの登録項目

| 項目 | 内容 |
| --- | --- |
| カテゴリ | 音楽＆オーディオ |
| プライバシーポリシー | [privacy-policy.md](../privacy-policy.md) を GitHub Pages などで公開した URL |
| 連絡先メールアドレス | 公開用のアドレスを用意する（必須） |
