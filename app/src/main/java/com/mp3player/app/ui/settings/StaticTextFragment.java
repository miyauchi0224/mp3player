package com.mp3player.app.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import dagger.hilt.android.AndroidEntryPoint;

import com.mp3player.app.databinding.FragmentStaticTextBinding;

@AndroidEntryPoint
public class StaticTextFragment extends Fragment {

    private FragmentStaticTextBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        binding = FragmentStaticTextBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        String title = args != null ? args.getString("title") : "";
        String body = args != null ? args.getString("body") : "";

        binding.textTitle.setText(title);
        binding.textBody.setText(body);
        binding.buttonBack.setOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
