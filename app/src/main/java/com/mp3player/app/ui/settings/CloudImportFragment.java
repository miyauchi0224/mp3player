package com.mp3player.app.ui.settings;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.data.CloudImportManager;
import com.mp3player.app.data.MusicRepository;
import com.mp3player.app.databinding.FragmentCloudImportBinding;

@AndroidEntryPoint
public class CloudImportFragment extends Fragment {

    @Inject
    CloudImportManager cloudImportManager;

    @Inject
    MusicRepository musicRepository;

    private FragmentCloudImportBinding binding;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ActivityResultLauncher<String[]> pickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null || uris.isEmpty()) {
                    return;
                }
                importFiles(uris);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentCloudImportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        binding.buttonSelect.setOnClickListener(v -> pickerLauncher.launch(new String[]{"audio/mpeg", "audio/*"}));
    }

    private void importFiles(List<Uri> uris) {
        binding.textResult.setText("");
        cloudImportManager.importFiles(uris, new CloudImportManager.Callback() {
            @Override
            public void onSuccess(int fileCount) {
                musicRepository.scanLibrary();
                mainHandler.post(() -> {
                    if (binding != null) {
                        binding.textResult.setText(getResources().getQuantityString(R.plurals.cloud_import_success, fileCount, fileCount));
                    }
                });
            }

            @Override
            public void onError() {
                mainHandler.post(() -> {
                    if (binding != null) {
                        binding.textResult.setText(R.string.import_failure);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
