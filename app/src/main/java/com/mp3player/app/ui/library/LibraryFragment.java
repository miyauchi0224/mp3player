package com.mp3player.app.ui.library;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.UserPreferences;
import com.mp3player.app.databinding.FragmentLibraryBinding;
import com.mp3player.app.ui.common.SongListLayout;

@AndroidEntryPoint
public class LibraryFragment extends Fragment {

    @Inject
    UserPreferences userPreferences;

    private FragmentLibraryBinding binding;
    private LibraryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        binding.viewPager.setAdapter(new LibraryPagerAdapter(requireActivity()));
        String[] titles = {
                getString(R.string.tab_songs),
                getString(R.string.tab_albums),
                getString(R.string.tab_artists),
                getString(R.string.tab_folders),
                getString(R.string.tab_history)
        };
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(titles[position])).attach();

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.buttonSort.setOnClickListener(this::showSortMenu);
        binding.buttonViewMode.setOnClickListener(v ->
                userPreferences.setSongViewMode(userPreferences.getSongViewMode().getValue().toggle()));
        userPreferences.getSongViewMode().observe(getViewLifecycleOwner(), mode ->
                SongListLayout.updateToggle(binding.buttonViewMode, mode));
    }

    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        popup.inflate(R.menu.menu_sort);
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_title) {
                viewModel.setSortOrder(LibraryViewModel.SortOrder.TITLE);
                return true;
            } else if (id == R.id.sort_artist) {
                viewModel.setSortOrder(LibraryViewModel.SortOrder.ARTIST);
                return true;
            } else if (id == R.id.sort_date_added) {
                viewModel.setSortOrder(LibraryViewModel.SortOrder.DATE_ADDED);
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
