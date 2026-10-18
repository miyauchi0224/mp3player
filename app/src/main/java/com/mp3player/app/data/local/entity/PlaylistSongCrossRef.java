package com.mp3player.app.data.local.entity;

import androidx.room.Entity;

@Entity(tableName = "playlist_song_cross_ref", primaryKeys = {"playlistId", "songId"})
public class PlaylistSongCrossRef {

    public long playlistId;

    public long songId;

    public int position;
}
