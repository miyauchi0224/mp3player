package com.mp3player.app.ui.library;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class LibraryPagerAdapter extends FragmentStateAdapter {

    public LibraryPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AlbumsFragment();
            case 2:
                return new ArtistsFragment();
            case 3:
                return new FoldersFragment();
            case 4:
                return new com.mp3player.app.ui.history.HistoryFragment();
            case 0:
            default:
                return new SongsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
