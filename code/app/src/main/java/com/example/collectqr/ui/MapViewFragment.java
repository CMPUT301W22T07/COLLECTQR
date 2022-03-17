package com.example.collectqr.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.collectqr.EnterQrInfoActivity;
import com.example.collectqr.MainAppActivity;
import com.example.collectqr.R;
import com.example.collectqr.ScanQRCodeActivity;
import com.example.collectqr.data.MapViewController;
import com.example.collectqr.databinding.FragmentMapViewBinding;
import com.example.collectqr.utilities.Preferences;
import com.example.collectqr.viewmodels.MapViewViewModel;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

/**
 * A class for displaying the main map, specifically with configuring it, and
 * setting it to be centered around the users location
 */
public class MapViewFragment extends Fragment implements LocationListener {

    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";
    final private int LOCATION_REQUEST_CODE = 1;
    // From osmdroid example under Apache 2.0 License
    // https://github.com/osmdroid/osmdroid
    // Constants
    private final String TAG = "MapViewFragment";
    private final String PREFS_NAME = "com.example.collectqr.prefs";
    // TODO: Test cases for the map bounds
    private MapViewViewModel mViewModel;
    private SharedPreferences mPrefs;
    private FragmentMapViewBinding binding;
    // Map vars
    private MapView mMapView;
    private IMapController mapController;
    private MyLocationNewOverlay mLocationOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    // Location Vars
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean locationPermissionsGranted;
    private Location lastKnownLocation;
    private Location currentLocation;
    private GeoPoint userPosition;

    private String username;

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

        username = Preferences.loadPreferences(container.getContext());
        mPrefs = requireContext().getSharedPreferences("", Context.MODE_PRIVATE);
        org.osmdroid.config.Configuration.getInstance().load(requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext()));
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        new MapViewController(new GeoLocation(53.5260000, -113.5250000), 500.0).makeQuery();

        setupMap();
        restoreViewState();
        return view;
    }

    /**
     * Initialises a map on the user's current location and requests the necessary permissions
     * to do so.
     */
    private void setupMap() {
        //  mMapView = binding.mapView;
        //  mMapView.setTileSource(TileSourceFactory.MAPNIK);

        // Setup map
        mMapView = binding.mapView;
        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView");
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mMapView.getOverlayManager().getTilesOverlay().setVerticalWrapEnabled(false);
        mMapView.setMultiTouchControls(true);

        // Set map default start point
        mapController = mMapView.getController();
        // mapController.setZoom(9.5);
        // GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        // mapController.setCenter(startPoint);

        /*
         * Location button functions and more based off osmdroid example, Apache-2.0 License
         * https://github.com/osmdroid/osmdroid
         */

        // Enable Location Overlay
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mMapView);
        this.mLocationOverlay.enableMyLocation();
        this.mLocationOverlay.enableFollowLocation();
        mapController.setZoom(17.0);
        mMapView.setTilesScaledToDpi(true);
        mMapView.getOverlays().add(this.mLocationOverlay);

        // Adding scale bar
        final DisplayMetrics dm = requireContext().getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    LOCATION_REQUEST_CODE
            );
        }

        // https://developer.android.com/guide/topics/ui/look-and-feel/darktheme#java
        // https://stackoverflow.com/a/41451143 by harshithdwivedi
        int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentTheme) {
            case Configuration.UI_MODE_NIGHT_NO:
                mMapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(R.color.md_theme_light_background);
                mMapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(R.color.black);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                mMapView.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                mMapView.getOverlayManager().getTilesOverlay().setLoadingBackgroundColor(R.color.md_theme_dark_background);
                mMapView.getOverlayManager().getTilesOverlay().setLoadingLineColor(R.color.black);
                break;
        }
    }

    /**
     * Business Talk
     */
    private void getDeviceLocation() {
        Context locCtx = requireContext();
        try {
            if (locationPermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(locCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(locCtx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            userPosition = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            mapController.setZoom(17.0);
                            mMapView.getController().animateTo(userPosition, 17.0, 1L);
                        } else {
                            Log.d(TAG, "Current location null");
                            Log.e(TAG, "Exception: %s", task.getException());
                            GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
                            mapController.setCenter(startPoint);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Sets actions on buttons making use of view binding.
     */
    private void setButtonsActions() {
        binding.fabLaunchQRScanner.setOnClickListener(view -> startScanner());

        binding.fabGpsLockLocation.setOnClickListener(view -> setMapToCurrentLocation());
    }

    /**
     * Animates the map to the user's current location.
     */
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
            locationPermissionsGranted = true;
            getDeviceLocation();
            // binding.fabGpsLockLocation.setSize(FloatingActionButton.SIZE_MINI);
            // https://stackoverflow.com/a/42001431 by EckoTan
            if (lastKnownLocation != null) {
                userPosition = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMapView.getController().animateTo(userPosition);
                binding.fabGpsLockLocation.setImageResource(R.drawable.ic_baseline_gps_fixed);
            }
        } else {
            Snackbar.make(requireView(), "Cannot access location", Snackbar.LENGTH_LONG)
                    .setAction("Enable Location", view -> requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            LOCATION_REQUEST_CODE
                    )).show();
        }
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
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                if (data != null){
                    Intent intent = new Intent(this.getActivity(), EnterQrInfoActivity.class);
                    intent.putExtra("sha", data.getStringExtra("sha"));
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * Restore the view to the last-saved state when reconstructing the fragment
     */
    private void restoreViewState() {
        final double latitude = Double.parseDouble(mPrefs.getString(PREFS_LATITUDE_STRING, "50"));
        final double longitude = Double.parseDouble(mPrefs.getString(PREFS_LONGITUDE_STRING, "100"));
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        final float zoom = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 9);

        // No animateTo(). Go instantly.
        mMapView.setExpectedCenter(new GeoPoint(latitude, longitude));
        mMapView.getController().setZoom(zoom);
        mMapView.setMapOrientation(orientation, false);
    }


    @Override
    public void onPause() {
        // https://github.com/osmdroid/osmdroid From the example application.
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mMapView.getMapOrientation());
        // Perhaps geohash this instead
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mMapView.getZoomLevelDouble());
        edit.apply();

        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupMap();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}