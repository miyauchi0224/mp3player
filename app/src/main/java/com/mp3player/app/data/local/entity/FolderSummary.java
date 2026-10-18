package com.mp3player.app.data.local.entity;

public class FolderSummary {
    public String folderPath;
    public int songCount;

    public String getFolderName() {
        if (folderPath == null || folderPath.isEmpty()) {
            return folderPath;
        }
        int index = folderPath.lastIndexOf('/');
        return index >= 0 && index < folderPath.length() - 1 ? folderPath.substring(index + 1) : folderPath;
    }
}
