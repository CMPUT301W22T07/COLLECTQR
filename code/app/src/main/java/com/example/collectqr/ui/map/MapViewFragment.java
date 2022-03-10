package com.example.collectqr.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.GenerateQRCodeActivity;
import com.example.collectqr.QRCodeHomeActivity;
import com.example.collectqr.R;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class MapViewFragment extends Fragment {

    // TODO: Test cases for the map bounds
    private MapViewViewModel mViewModel;

    private MapView mMapView;

    // BRUHHHHHHHHHHHHHHHHHHH
    // BRUHHHHHHHHHHHHHHHHHHH
    private FragmentMapViewBinding binding;
    // BRUHHHHHHHHHHHHHHHHHHH
    // BRUHHHHHHHHHHHHHHHHHHH

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        mMapView = new MapView(inflater.getContext());
        mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        // TODO: Use the ViewModel
        binding.fabGpsLockLocation.setOnClickListener(view1 -> {
            Toast toast = Toast.makeText(view1.getContext(), "BRUH", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(this.getActivity(), GenerateQRCodeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}