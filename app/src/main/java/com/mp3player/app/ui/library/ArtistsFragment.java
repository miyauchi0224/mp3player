package com.mp3player.app.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.local.entity.ArtistSummary;
import com.mp3player.app.databinding.FragmentSongListBinding;
import com.mp3player.app.ui.common.GroupAdapter;
import com.mp3player.app.ui.common.GroupItem;

@AndroidEntryPoint
public class ArtistsFragment extends Fragment {

    private FragmentSongListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LibraryViewModel viewModel = new ViewModelProvider(requireActivity()).get(LibraryViewModel.class);

        GroupAdapter adapter = new GroupAdapter(item -> {
            ArtistSummary artist = (ArtistSummary) item.payload;
            Bundle args = new Bundle();
            args.putString("groupType", "artist");
            args.putString("groupKey", artist.artist);
            args.putString("groupTitle", artist.artist);
            Navigation.findNavController(view).navigate(R.id.groupSongsFragment, args);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getArtists().observe(getViewLifecycleOwner(), artists -> {
            List<GroupItem> items = new ArrayList<>();
            if (artists != null) {
                for (ArtistSummary artist : artists) {
                    items.add(new GroupItem("artist:" + artist.artist, artist.artist,
                            getResources().getQuantityString(R.plurals.song_count, artist.songCount, artist.songCount), artist));
                }
            }
            adapter.submitList(items);
            binding.textEmpty.setText(R.string.empty_songs);
            binding.textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
