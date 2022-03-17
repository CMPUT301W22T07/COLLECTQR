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
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.EnterQrInfoActivity;
import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.viewmodels.MapViewViewModel;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
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

import java.util.List;


/**
 * A class for displaying the main map, specifically with configuring it, and
 * setting it to be centered around the users location.
 * See {@link Fragment}.
 */
public class MapViewFragment extends Fragment {

    private final String TAG = "MapViewFragment";
    private FragmentMapViewBinding binding;

    // Map Variables
    private MapView mapView;
    // Store reference and override the position listener
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
    // Store reference and override the on-move listener
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
    private GesturesPlugin gesturesPlugin;
    private LocationComponentPlugin locationComponentPlugin;
    private MapViewViewModel mViewModel;
    private PermissionsManager permManager;
    private String username;

    /**
     * Constructor for a MapViewFragment.
     *
     * @return A map-view fragment
     */
    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }


    /**
     * Setting up the map to be displayed in the fragment.
     *
     * @param inflater           Reference to the layout inflater of the fragment
     * @param container          Reference to the view group of the fragment
     * @param savedInstanceState Reference to state to restore on fragment creation
     * @return An inflated view with its corresponding layout
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        username = Preferences.loadPreferences(requireContext());
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapView = binding.mapView;
        checkPermissions();
        return view;

    }


    /**
     * On fragment creation, check that requisite location permissions have been granted.
     * If location permissions have been granted then prepare the map's settings.
     */
    private void checkPermissions() {
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            onMapReady();
        } else {
            permManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> list) {
                    Toast.makeText(requireContext(),
                            "Location is used to move the map to where you are",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        onMapReady();
                    } else {
                        requireActivity().finish();
                    }
                }
            });
            permManager.requestLocationPermissions(requireActivity());
        }
    }

    /**
     * Setup the map's style, camera, and location/gesture listeners.
     */
    private void onMapReady() {
        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder().zoom(14.0).build()
        );
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                style -> {
                    initLocationComponent();
                    setupGesturesListener();
                });
    }


    /**
     * Add a onMoveListener for when the player manually moves the map camera.
     */
    private void setupGesturesListener() {
        gesturesPlugin = mapView.getPlugin(Plugin.Mapbox.MAPBOX_GESTURES_PLUGIN_ID);
        assert gesturesPlugin != null;
        gesturesPlugin.addOnMoveListener(onMoveListener);
    }


    /**
     * Setup the location indicator on the map and add a listener for changes to the player's
     * position.
     */
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
        binding.fabGpsLockLocation.setOnClickListener(view -> onCameraTrackingRequested());
    }

    /**
     * Start the QR code scanner activity.
     */
    private void startScanner() {
        Intent intent = new Intent(this.getActivity(), ScanQRCodeActivity.class);
        startActivityForResult(intent, 1);
    }

    // https://www.tutorialspoint.com/how-to-send-data-to-previous-activity-in-android

    /**
     * Handles returning from the scanner activity and send to enter qr info activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    Intent intent = new Intent(this.getActivity(), EnterQrInfoActivity.class);
                    intent.putExtra("sha", data.getStringExtra("sha"));
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        }
    }


    /**
     * Move the map's camera to the player's current location.
     */
    private void onCameraTrackingRequested() {
        Toast.makeText(requireContext(), "Moving to your location", Toast.LENGTH_SHORT).show();

        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert gesturesPlugin != null;
        assert locationComponentPlugin != null;

        locationComponentPlugin.addOnIndicatorPositionChangedListener(posChangedListener);
        gesturesPlugin.addOnMoveListener(onMoveListener);
    }


    /**
     * When the map camera moves, stop the camera from tracking the player's movement.
     */
    private void onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show();

        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert gesturesPlugin != null;
        assert locationComponentPlugin != null;

        locationComponentPlugin.removeOnIndicatorPositionChangedListener(posChangedListener);
        gesturesPlugin.removeOnMoveListener(onMoveListener);
    }


    private void drawMarkers() {

    }


    /**
     * Setup the interactive elements of the fragment, like buttons.
     *
     * @param view               The view to set actions on
     * @param savedInstanceState Reference to a state to restore
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MapViewViewModel.class);
        mViewModel.getGeoLocations().observe(getViewLifecycleOwner(), geoLocations -> {
            addMapMarkers();
        });
        // TODO: Use the ViewModel
        setButtonsActions();
    }

    private void addMapMarkers() {
    }


    /**
     * Stop all defined listeners from listening, such as location and gestures.
     */
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


    /**
     * Responds to the player's granting or rejecting the requested permission. Namely
     * move the map's camera when the user accepts the permissions.
     *
     * @param requestCode  The code assigned to set of permissions requested
     * @param permissions  String array of all permissions requested
     * @param grantResults Integer array of the status of the requested permissions
     * @deprecated
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}