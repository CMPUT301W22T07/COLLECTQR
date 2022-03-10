package com.example.collectqr.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class MapViewFragment extends Fragment {

    final private int LOCATION_REQUEST_CODE = 1;
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
        setButtonsActions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setButtonsActions() {
        binding.fabLaunchQRScanner.setOnClickListener(view -> startScanner());

        binding.fabGpsLockLocation.setOnClickListener(view -> setMapToCurrentLocation());
    }

    private void setMapToCurrentLocation() {
        /* Permission requesting based off example; uses an older method:
           https://developer.android.com/training/permissions/requesting */
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location perm granted", Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(requireView(), "Cannot access location", Snackbar.LENGTH_LONG)
                    .setAction("Enable Location", view -> requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_REQUEST_CODE
                    )).show();
        }
    }

    public void startScanner() {
        Intent intent = new Intent(this.getActivity(), ScanQRCodeActivity.class);
        startActivity(intent);
    }
}