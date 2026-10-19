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
import com.mp3player.app.data.local.entity.FolderSummary;
import com.mp3player.app.databinding.FragmentSongListBinding;
import com.mp3player.app.ui.common.GroupAdapter;
import com.mp3player.app.ui.common.GroupItem;

@AndroidEntryPoint
public class FoldersFragment extends Fragment {

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
            FolderSummary folder = (FolderSummary) item.payload;
            Bundle args = new Bundle();
            args.putString("groupType", "folder");
            args.putString("groupKey", folder.folderPath);
            args.putString("groupTitle", folder.getFolderName());
            Navigation.findNavController(view).navigate(R.id.groupSongsFragment, args);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel.getFolders().observe(getViewLifecycleOwner(), folders -> {
            List<GroupItem> items = new ArrayList<>();
            if (folders != null) {
                for (FolderSummary folder : folders) {
                    items.add(new GroupItem("folder:" + folder.folderPath, folder.getFolderName(),
                            getResources().getQuantityString(R.plurals.song_count, folder.songCount, folder.songCount), folder));
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
