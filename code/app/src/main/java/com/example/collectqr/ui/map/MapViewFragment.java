package com.example.collectqr.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class MapViewFragment extends Fragment {

    // TODO: Test cases for the map bounds
    private MapViewViewModel mViewModel;
    private MapView mMapView;
    private FragmentMapViewBinding binding;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mMapView = binding.mapView;
        mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        // TODO: Use the ViewModel
        binding.fabLaunchQRScanner.setOnClickListener(view -> {
            Intent intent = new Intent(this.getActivity(), ScanQRCodeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}