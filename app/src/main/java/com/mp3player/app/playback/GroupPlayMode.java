package com.mp3player.app.playback;

/** アルバム／フォルダ単位で再生するときの再生モード */
public enum GroupPlayMode {
    /** グループ内をランダムな順番で1周再生する */
    SHUFFLE,
    /** 先頭の曲を繰り返し再生する */
    REPEAT_ONE,
    /** グループ全体を繰り返し再生する */
    REPEAT_ALL
}
