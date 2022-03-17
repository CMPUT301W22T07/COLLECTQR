package com.example.collectqr.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.example.collectqr.utilities.LocationPermissionHelper;
import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import java.lang.ref.WeakReference;


/**
 * A class for displaying the main map, specifically with configuring it, and
 * setting it to be centered around the users location
 */
public class MapViewFragment extends Fragment {

    private final String TAG = "MapViewFragment";
    private FragmentMapViewBinding binding;

    /*
        Mapping Variables
     */
    private MapView mapView;
    private GesturesPlugin gesturesPlugin;
    private LocationComponentPlugin locationComponentPlugin;
    private LocationPermissionHelper locationPermissionHelper;

    private final OnIndicatorPositionChangedListener posChangedListener =
            new OnIndicatorPositionChangedListener() {
                @Override
                public void onIndicatorPositionChanged(@NonNull Point point) {
                    mapView.getMapboxMap().setCamera(new CameraOptions.Builder()
                            .center(point).build());
                    GesturesPlugin gestures = mapView.getPlugin(Plugin.Mapbox.MAPBOX_GESTURES_PLUGIN_ID);
                    assert gestures != null;
                    gestures.setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
                }
            };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            onCameraTrackingDismissed();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
        }
    };

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        /*
         * Fused Location based on:
         * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
         * https://developer.android.com/training/location/retrieve-current#java
         */
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapView = binding.mapView;

        locationPermissionHelper = new LocationPermissionHelper(new WeakReference<>(requireActivity()));
        locationPermissionHelper.checkPermissions(() -> {
            mapView.getMapboxMap().setCamera(
                    new CameraOptions.Builder().zoom(14.0).build()
            );
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                    style -> {
                        initLocationComponent();
                        setupGesturesListener();

                    });
            return null;
        });

        return view;
    }

    private void setupGesturesListener() {
        gesturesPlugin = mapView.getPlugin(Plugin.Mapbox.MAPBOX_GESTURES_PLUGIN_ID);
        assert gesturesPlugin != null;
        gesturesPlugin.addOnMoveListener(onMoveListener);
    }

    private void initLocationComponent() {
        locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert locationComponentPlugin != null;
        locationComponentPlugin.updateSettings(
                locationComponentSettings -> {
                    locationComponentSettings.setEnabled(true);
                    locationComponentSettings.setLocationPuck(
                            new LocationPuck2D(
                                    null,
                                    AppCompatResources.getDrawable(
                                            MapViewFragment.this.requireContext(),
                                            com.mapbox.maps.R.drawable.mapbox_user_icon
                                    ),
                                    AppCompatResources.getDrawable(
                                            MapViewFragment.this.requireContext(),
                                            com.mapbox.maps.R.drawable.mapbox_user_icon_shadow
                                    ),
                                    null
                            )
                    );
                    return null;
                }
        );
        locationComponentPlugin.addOnIndicatorPositionChangedListener(posChangedListener);
    }

    /**
     * Sets actions on buttons making use of view binding.
     */
    private void setButtonsActions() {
        binding.fabLaunchQRScanner.setOnClickListener(view -> startScanner());

        //binding.fabGpsLockLocation.setOnClickListener(view -> setMapToCurrentLocation());
    }

    /**
     * Start the QR code scanner activity.
     */
    private void startScanner() {
        Intent intent = new Intent(this.getActivity(), ScanQRCodeActivity.class);
        startActivity(intent);
    }


    /**
     * When the map camera moves, stop the camera from tracking the player's movement.
     */
    private void onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();
        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin = mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert gesturesPlugin != null;
        assert locationComponentPlugin != null;

        locationComponentPlugin.removeOnIndicatorPositionChangedListener(posChangedListener);
        gesturesPlugin.removeOnMoveListener(onMoveListener);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Use the ViewModel
        setButtonsActions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin = mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert gesturesPlugin != null;
        assert locationComponentPlugin != null;

        locationComponentPlugin.removeOnIndicatorPositionChangedListener(posChangedListener);
        gesturesPlugin.removeOnMoveListener(onMoveListener);
    }

}