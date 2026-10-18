package com.mp3player.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "songs")
public class Song {

    @PrimaryKey
    public long id;

    @NonNull
    public String title = "";

    @NonNull
    public String artist = "";

    @NonNull
    public String album = "";

    public long albumId;

    @NonNull
    public String folderPath = "";

    public long duration;

    @NonNull
    public String data = "";

    public long dateAdded;

    public int playCount = 0;

    public boolean favorite = false;

    public long lastPlayedAt = 0;
}
