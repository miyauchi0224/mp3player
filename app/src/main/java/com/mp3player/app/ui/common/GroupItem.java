package com.mp3player.app.ui.common;

public class GroupItem {
    public final String key;
    public final String title;
    public final String subtitle;
    public final Object payload;

    public GroupItem(String key, String title, String subtitle, Object payload) {
        this.key = key;
        this.title = title;
        this.subtitle = subtitle;
        this.payload = payload;
    }
}
