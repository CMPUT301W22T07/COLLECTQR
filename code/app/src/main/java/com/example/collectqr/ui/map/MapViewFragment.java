package com.example.collectqr.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

import com.example.collectqr.R;
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

        setupMap();
        return view;
    }

    private void setupMap() {
        mMapView = binding.mapView;

        // https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#java
        // https://stackoverflow.com/a/41451143 by harshithdwivedi
        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentTheme) {
            case Configuration.UI_MODE_NIGHT_NO:
                mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                mMapView.getMapboxMap().loadStyleUri(Style.DARK);
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        // TODO: Use the ViewModel
        setButtonsActions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
                binding.fabGpsLockLocation.setImageResource(R.drawable.ic_baseline_gps_off);
            }
        }
    }

    private void setButtonsActions() {
        binding.fabLaunchQRScanner.setOnClickListener(view -> startScanner());

        binding.fabGpsLockLocation.setOnClickListener(view -> setMapToCurrentLocation());
    }

    private void setMapToCurrentLocation() {
        /* Permission requesting based off example; uses an older method:
           https://developer.android.com/training/permissions/requesting

           Requesting all permissions (location)
           https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Java)
           By: IpswichMapper
        */
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location perm granted", Toast.LENGTH_SHORT).show();
            // binding.fabGpsLockLocation.setSize(FloatingActionButton.SIZE_MINI);
            // https://stackoverflow.com/a/42001431 by EckoTan
            binding.fabGpsLockLocation.setImageResource(R.drawable.ic_baseline_gps_fixed);
        } else {
            Snackbar.make(requireView(), "Cannot access location", Snackbar.LENGTH_LONG)
                    .setAction("Enable Location", view -> requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_REQUEST_CODE
                    )).show();
        }
    }

    private void startScanner() {
        Intent intent = new Intent(this.getActivity(), ScanQRCodeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupMap();
    }
}