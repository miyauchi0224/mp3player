package com.mp3player.app.ui.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.R;
import com.mp3player.app.databinding.FragmentRecordBinding;

@AndroidEntryPoint
public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private RecordingAdapter adapter;
    private boolean isRecording = false;
    private File currentOutputFile;
    private int playingPosition = -1;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    startRecording();
                } else {
                    Toast.makeText(requireContext(), R.string.record_permission_required, Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        adapter = new RecordingAdapter((file, position) -> togglePreview(file, position));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        refreshList();

        binding.buttonRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                requestPermissionAndRecord();
            }
        });
    }

    private void requestPermissionAndRecord() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startRecording();
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private File getRecordingsDir() {
        File dir = new File(requireContext().getExternalFilesDir(null), "Recordings");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void startRecording() {
        String fileName = "REC_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date()) + ".m4a";
        currentOutputFile = new File(getRecordingsDir(), fileName);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(currentOutputFile.getAbsolutePath());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            binding.buttonRecord.setText(R.string.record_stop);
            binding.chronometer.setBase(android.os.SystemClock.elapsedRealtime());
            binding.chronometer.start();
        } catch (IOException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (RuntimeException ignored) {
                // 録音時間が短すぎる場合などに発生するが、ファイルは破棄すればよいので無視する
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
        isRecording = false;
        binding.buttonRecord.setText(R.string.record_start);
        binding.chronometer.stop();
        refreshList();
    }

    private void togglePreview(File file, int position) {
        if (playingPosition == position) {
            stopPreview();
            return;
        }
        stopPreview();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setOnCompletionListener(mp -> stopPreview());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playingPosition = position;
            adapter.setPlayingPosition(position);
        } catch (IOException e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPreview() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playingPosition = -1;
        if (adapter != null) {
            adapter.setPlayingPosition(-1);
        }
    }

    private void refreshList() {
        File[] fileArray = getRecordingsDir().listFiles();
        List<File> files = fileArray != null ? Arrays.asList(fileArray) : new ArrayList<>();
        List<File> sorted = new ArrayList<>(files);
        sorted.sort(Comparator.comparingLong(File::lastModified).reversed());
        adapter.submitList(sorted);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPreview();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        binding = null;
    }
}
