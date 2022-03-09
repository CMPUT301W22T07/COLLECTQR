package com.example.collectqr.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class MapViewFragment extends Fragment {

    // TODO: Test cases for the map bounds

    private MapView mMapView;

    private MapViewViewModel mViewModel;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Context context = this.getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        // MapView Setup
        mMapView = new MapView(inflater.getContext());
        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView");

        mMapView.setTileSource(TileSourceFactory.MAPNIK);

        return mMapView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        // TODO: Use the ViewModel
    }

}