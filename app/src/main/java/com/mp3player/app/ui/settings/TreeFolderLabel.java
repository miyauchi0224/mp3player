package com.mp3player.app.ui.settings;

/** SAF で選んだフォルダの表示名。ドキュメントID「ストレージ:パス」のパス部分を見せる */
public final class TreeFolderLabel {

    private TreeFolderLabel() {
    }

    public static String of(String treeDocumentId) {
        if (treeDocumentId == null) {
            return "";
        }
        int colon = treeDocumentId.indexOf(':');
        String path = colon >= 0 ? treeDocumentId.substring(colon + 1) : treeDocumentId;
        return path.isEmpty() ? "/" : path;
    }
}
