package com.example.collectqr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.databinding.FragmentMapInfoSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Based off example from: https://betterprogramming.pub/bottom-sheet-android-340703e114d2
 * Creates a bottom sheet dialog on the map screen to display more information about the
 * Point-of-interest.
 */
public class MapInfoBottomSheet extends BottomSheetDialogFragment {

    BottomSheetBehavior bottomSheetBehavior;
    FragmentMapInfoSheetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentMapInfoSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

}
