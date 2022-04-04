package com.example.collectqr.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.collectqr.EnterQrInfoActivity;
import com.example.collectqr.R;
import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.example.collectqr.model.MapPOI;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.viewmodels.MapViewViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.OnCircleAnnotationClickListener;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A class for displaying the main map, specifically with configuring it, and
 * setting it to be centered around the users location.
 * See {@link Fragment}.
 */
public class MapViewFragment extends Fragment {

    private final String TAG = "MapViewFragment";
    private FragmentMapViewBinding binding;
    private BottomSheetDialogFragment infoSheet;

    // Map Variables
    private MapView mapView;
    // Store reference and override the circle annotation click listener
    private final OnCircleAnnotationClickListener poiClickListener =
            circleAnnotation -> {
                Point point = circleAnnotation.getPoint();
                Toast.makeText(
                        requireContext(),
                        "Annotation clicked: " + point.latitude() + " " + point.longitude(),
                        Toast.LENGTH_SHORT
                ).show();
                showInfoSheet(circleAnnotation);
                return true;
            };
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

                    Location location = new Location("");
                    location.setLongitude(point.longitude());
                    location.setLatitude(point.latitude());

                    mViewModel.setLocation(location);

                    mViewModel.getPOIList(location).observe(
                            getViewLifecycleOwner(), this::addMapMarkers);
                }

                private void addMapMarkers(List<MapPOI> POIList) {

                    if (mViewModel.lastPOILen != POIList.size()) {

                        AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
                        assert annotationPlugin != null;
                        CircleAnnotationManager circleAnnotationManager =
                                (CircleAnnotationManager) annotationPlugin.createAnnotationManager(
                                        AnnotationType.CircleAnnotation,
                                        new AnnotationConfig()
                                );

                        List<CircleAnnotation> circleAnnotations = circleAnnotationManager.getAnnotations();
                        circleAnnotationManager.addClickListener(poiClickListener);

                        for (MapPOI mapPOI : POIList) {
                            // Create the annotation to display on the map and include the arbitrary data
                            // (hash) as JSON data
                            CircleAnnotationOptions circleAnnotationOptions =
                                    new CircleAnnotationOptions()
                                            .withData(mapPOI.getJsonData())
                                            .withPoint(mapPOI.getPoint())
                                            .withCircleRadius(8.0)
                                            .withCircleColor("#ee4e8b")
                                            .withCircleStrokeWidth(2.0)
                                            .withCircleStrokeColor("#ffffff");

                            circleAnnotationManager.create(circleAnnotationOptions);
                        }
                        Log.d(TAG, "Points drawn ");
                        mViewModel.dataLoaded = true;           // TODO: use setter
                        return;
                    }

                    Log.d(TAG, "Didn't draw the points");

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

    private Location lastKnownLocation;
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
    @NonNull
    @Contract(" -> new")
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
        username = Preferences.loadUserName(requireContext());
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
                new CameraOptions.Builder().zoom(14.0).pitch(40.0).build()
        );
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                style -> {
                    initLocationComponent();
                    setupGesturesListener();
                });

        // https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#java
        // https://stackoverflow.com/a/41451143 by harshithdwivedi
        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentTheme) {
            case Configuration.UI_MODE_NIGHT_NO:
                mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                        style -> {
                            initLocationComponent();
                            setupGesturesListener();
                        });
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                mapView.getMapboxMap().loadStyleUri(Style.DARK,
                        style -> {
                            initLocationComponent();
                            setupGesturesListener();
                        });
                break;
        }
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
        if (mViewModel.dataLoaded) {
            addMapMarkers(mViewModel.getPOIList(), true);
        }
        setButtonsActions();
    }

    protected void addMapMarkers(@NonNull List<MapPOI> POIList, Boolean forceRedraw) {

        if (forceRedraw) {
            AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
            assert annotationPlugin != null;
            CircleAnnotationManager circleAnnotationManager =
                    (CircleAnnotationManager) annotationPlugin.createAnnotationManager(
                            AnnotationType.CircleAnnotation,
                            new AnnotationConfig()
                    );

            List<CircleAnnotation> circleAnnotations = circleAnnotationManager.getAnnotations();
            circleAnnotationManager.addClickListener(poiClickListener);

            for (MapPOI mapPOI : POIList) {
                // Converting a map point's qr code hash to json
                // https://stackoverflow.com/a/12155874 by Ankur
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("sha256", mapPOI.getHash());

                // Parsing json
                // https://howtodoinjava.com/gson/gson-jsonparser/
                JsonElement dataJson = new Gson().toJsonTree(dataMap);


                // Create the annotation to display on the map and include the arbitrary data
                // (hash) as JSON data
                CircleAnnotationOptions circleAnnotationOptions =
                        new CircleAnnotationOptions()
                                .withData(dataJson)
                                .withPoint(mapPOI.getPoint())
                                .withCircleRadius(8.0)
                                .withCircleColor("#ee4e8b")
                                .withCircleStrokeWidth(2.0)
                                .withCircleStrokeColor("#ffffff");

                circleAnnotationManager.create(circleAnnotationOptions);
            }
        }
    }

    protected void addMapMarkers(@NonNull List<MapPOI> POIList) {

        if (mViewModel.lastPOILen != POIList.size()) {

            AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
            assert annotationPlugin != null;
            CircleAnnotationManager circleAnnotationManager =
                    (CircleAnnotationManager) annotationPlugin.createAnnotationManager(
                            AnnotationType.CircleAnnotation,
                            new AnnotationConfig()
                    );

            List<CircleAnnotation> circleAnnotations = circleAnnotationManager.getAnnotations();
            circleAnnotationManager.addClickListener(poiClickListener);

            for (MapPOI mapPOI : POIList) {
                // Converting a map point's qr code hash to json
                // https://stackoverflow.com/a/12155874 by Ankur
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("sha256", mapPOI.getHash());

                // Parsing json
                // https://howtodoinjava.com/gson/gson-jsonparser/
                JsonElement dataJson = new Gson().toJsonTree(dataMap);


                // Create the annotation to display on the map and include the arbitrary data
                // (hash) as JSON data
                CircleAnnotationOptions circleAnnotationOptions =
                        new CircleAnnotationOptions()
                                .withData(dataJson)
                                .withPoint(mapPOI.getPoint())
                                .withCircleRadius(8.0)
                                .withCircleColor("#ee4e8b")
                                .withCircleStrokeWidth(2.0)
                                .withCircleStrokeColor("#ffffff");

                circleAnnotationManager.create(circleAnnotationOptions);
            }
            Log.d(TAG, "Points drawn ");
            return;
        }

        Log.d(TAG, "Didn't draw the points");
    }


    /**
     * Based on: https://material.io/components/sheets-bottom/android#using-bottom-sheets
     *
     * @param circleAnnotation
     */
    private void showInfoSheet(@NonNull CircleAnnotation circleAnnotation) {
        JsonObject annotationData = circleAnnotation.getData().getAsJsonObject();
        String hash = annotationData.get("sha256").getAsString();
        int intPoints = annotationData.get("points").getAsInt();

        NavController navController = Navigation.findNavController(requireView());
        Bundle bundle = new Bundle();
        bundle.putString("sha", hash);
        bundle.putInt("points", intPoints);
        navController.navigate(R.id.navigation_qr_code_details, bundle);
    }


    @Override
    public void onResume() {
        super.onResume();

        try {
            Location location = mViewModel.getLastKnownLocation().getValue();
            if (location != null) {
                mViewModel.getPOIList(location).observe(getViewLifecycleOwner(),
                        this::addMapMarkers);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }


    /**
     * Stop all defined listeners from listening, such as location and gestures.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

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


    /**
     * Respond to new system settings
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onMapReady();
    }

}