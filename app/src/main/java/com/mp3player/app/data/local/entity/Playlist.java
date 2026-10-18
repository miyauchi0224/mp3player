package com.mp3player.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class Playlist {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name = "";

    public long createdAt;
}
