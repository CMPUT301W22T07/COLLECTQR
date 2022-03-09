package com.example.collectqr.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

public class MapViewFragment extends Fragment {

    // TODO: Test cases for the map bounds
    private MapViewViewModel mViewModel;

    private MapView mMapView;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Context context = this.getActivity();
        mMapView = new MapView(inflater.getContext());
        mMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        return mMapView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}