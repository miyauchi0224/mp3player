package com.mp3player.app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.data.UserPreferences;
import com.mp3player.app.databinding.FragmentFolderFilterBinding;

@AndroidEntryPoint
public class FolderFilterFragment extends Fragment {

    @Inject
    MusicRepository musicRepository;

    @Inject
    UserPreferences userPreferences;

    private FragmentFolderFilterBinding binding;
    private FolderCheckAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentFolderFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        adapter = new FolderCheckAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        musicRepository.getFolders().observe(getViewLifecycleOwner(), folders ->
                adapter.submitList(folders != null ? folders : new ArrayList<>(), userPreferences.getTargetFolders()));

        binding.buttonSave.setOnClickListener(v -> {
            userPreferences.setTargetFolders(adapter.getSelected());
            Navigation.findNavController(view).popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
