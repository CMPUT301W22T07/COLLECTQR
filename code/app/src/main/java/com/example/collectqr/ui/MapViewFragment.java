package com.example.collectqr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CameraState;
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
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener;
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
 *
 * @see Fragment
 */
public class MapViewFragment extends Fragment {

    private final String TAG = "MapViewFragment";
    // Store reference and override the circle annotation click listener
    private final OnCircleAnnotationClickListener poiClickListener =
            circleAnnotation -> {
                Point point = circleAnnotation.getPoint();
                showInfoSheet(circleAnnotation);
                return true;
            };
    private FragmentMapViewBinding binding;
    private BottomSheetDialogFragment infoSheet;
    // Map Variables
    private MapView mapView;
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
    // Store reference and override the map click listener
    private final OnMapLongClickListener mapClickListener =
            new OnMapLongClickListener() {
                @Override
                public boolean onMapLongClick(@NonNull Point point) {
                    mViewModel.clearQrGeoLocations();

                    Location location = new Location("");
                    location.setLatitude(point.latitude());
                    location.setLongitude(point.longitude());

                    mViewModel.getPOIList(location).observe(getViewLifecycleOwner(),
                            mapPOIS -> {
                                addMapMarkers(mapPOIS, true);
                            });
                    Toast.makeText(requireContext(),
                            "Searching in a 500KM radius",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
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
    private MapViewFragment permManager;
    private CameraState cameraState;
    /* Permissions callback because onRequestPermissionsResult is deprecated...
       https://stackoverflow.com/a/63546099 by Ace
       https://stackoverflow.com/a/66552678 by Daniel.Wang */
    private final ActivityResultLauncher requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    checkPermissions();
                } else {
                    Toast.makeText(requireContext(),
                            "Location is used to search for nearby QR codes",
                            Toast.LENGTH_LONG).show();
                    setupDegradedGesturesListener();
                }
            });
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

        if (savedInstanceState != null) {
            cameraState = (CameraState) savedInstanceState.getSerializable("CAMERA_STATE");
        }
        if (mViewModel != null && mViewModel.getCameraStateBundle() != null) {
            cameraState = (CameraState) mViewModel.getCameraStateBundle().getSerializable("CAMERA_STATE");
        }

        username = Preferences.loadUserName(requireContext());
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mapView = binding.mapView;
        setupDegradedGesturesListener();
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
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new MaterialAlertDialogBuilder(requireContext(),
                    com.google.android.material.R.style.ThemeOverlay_Material3_Dialog)
                    // https://stackoverflow.com/a/19064968 by Singhak
                    .setCancelable(false)
                    .setMessage("We rely on your current location to search for nearby QR codes " +
                            "seamlessly. Recording your location is optional.")
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        // https://stackoverflow.com/a/27765687 by sivi
                        requestPermissionLauncher.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION);
                    })
                    .show();

            setupDegradedGesturesListener();
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
            );
        }

    }


    /**
     * Setup the map's style, camera, and location/gesture listeners.
     */
    private void onMapReady() {

        if (cameraState != null) {
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(cameraState.getCenter())
                    .padding(cameraState.getPadding())
                    .zoom(cameraState.getZoom())
                    .bearing(cameraState.getBearing())
                    .pitch(cameraState.getPitch())
                    .build();

            stopLocationListeners();
            mapView.getMapboxMap().setCamera(cameraOptions);
            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        } else {
            mapView.getMapboxMap().setCamera(
                    new CameraOptions.Builder().zoom(14.0).pitch(40.0).build()
            );

            mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS,
                    style -> {
                        initLocationComponent();
                        setupGesturesListener();
                    });
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


    private void setupDegradedGesturesListener() {
        gesturesPlugin = mapView.getPlugin(Plugin.Mapbox.MAPBOX_GESTURES_PLUGIN_ID);
        assert gesturesPlugin != null;
        gesturesPlugin.addOnMapLongClickListener(mapClickListener);
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


    /**
     * Handles returning from the scanner activity and send to enter qr info activity
     * https://www.tutorialspoint.com/how-to-send-data-to-previous-activity-in-android
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
                    if (!data.getStringExtra("sha").isEmpty()) {
                        Intent intent = new Intent(this.getActivity(), EnterQrInfoActivity.class);
                        intent.putExtra("sha", data.getStringExtra("sha"));
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        String returnValue = data.getStringExtra("user_to_view");
                        System.out.println("RETURN VALUE: " + returnValue);
                        NavController navController = Navigation.findNavController(getView());
                        Bundle bundle = new Bundle();
                        bundle.putString("username", returnValue);
                        navController.navigate(R.id.navigation_user_profile, bundle);
                    }
                }
            }
        }
    }


    /**
     * Move the map's camera to the player's current location.
     */
    private void onCameraTrackingRequested() {
        checkPermissions();
        Toast.makeText(requireContext(), "Moving to your location", Toast.LENGTH_SHORT).show();

        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        LocationComponentPlugin locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert gesturesPlugin != null;
        assert locationComponentPlugin != null;

        // Listen for changes to the player's location and touch events on the map
        locationComponentPlugin.addOnIndicatorPositionChangedListener(posChangedListener);
        gesturesPlugin.addOnMoveListener(onMoveListener);
    }


    /**
     * When the map camera moves, stop the camera from tracking the player's movement.
     */
    private void onCameraTrackingDismissed() {
        Toast.makeText(requireContext(), "Camera tracking disabled", Toast.LENGTH_SHORT).show();

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


    /**
     * Force a draw annotations to map with serialised JSON data regardless of prior draw states
     *
     * @param POIList     List of Points of Interests
     * @param forceRedraw Boolean to control redraws when called within the package
     */
    protected void addMapMarkers(@NonNull List<MapPOI> POIList, Boolean forceRedraw) {

        if (forceRedraw) {
            AnnotationPlugin annotationPlugin = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
            assert annotationPlugin != null;
            CircleAnnotationManager circleAnnotationManager =
                    (CircleAnnotationManager) annotationPlugin.createAnnotationManager(
                            AnnotationType.CircleAnnotation,
                            new AnnotationConfig()
                    );

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
        }
    }


    /**
     * Create and draw annotations to map with serialised JSON data.
     *
     * @param POIList List of Point of Interests
     */
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
                dataMap.put("sha", mapPOI.getHash());

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
     * Launches a fragment to show the information of a QR code displayed on the map.
     *
     * @param circleAnnotation The annotation tapped on the map, with position and JSON-formatted
     *                         data.
     */
    private void showInfoSheet(@NonNull CircleAnnotation circleAnnotation) {
        // Deserialize the data back into their original types
        JsonObject annotationData = circleAnnotation.getData().getAsJsonObject();
        String hash = annotationData.get("sha").getAsString();
        int intPoints = annotationData.get("points").getAsInt();

        // Pass the deserialized data as a bundle to the QR Code details fragment
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

    private void stopGestureListeners() {
        GesturesPlugin gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        assert gesturesPlugin != null;
        gesturesPlugin.removeOnMoveListener(onMoveListener);
        gesturesPlugin.removeOnMapLongClickListener(mapClickListener);
    }

    private void stopLocationListeners() {
        LocationComponentPlugin locationComponentPlugin =
                mapView.getPlugin(Plugin.Mapbox.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);

        assert locationComponentPlugin != null;

        locationComponentPlugin.removeOnIndicatorPositionChangedListener(posChangedListener);
    }

    private void stopAllListeners() {
        stopGestureListeners();
        stopLocationListeners();
    }


    /**
     * Stop all defined listeners from listening, such as location and gestures.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAllListeners();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("CAMERA_STATE", mapView.getMapboxMap().getCameraState());
    }


    @Override
    public void onPause() {
        super.onPause();
        mViewModel.setCameraStateBundle(mapView.getMapboxMap().getCameraState());
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
        checkPermissions();
    }


    /**
     * Respond to new system settings
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        checkPermissions();
    }

}