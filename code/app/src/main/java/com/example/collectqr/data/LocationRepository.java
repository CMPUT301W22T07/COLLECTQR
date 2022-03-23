package com.example.collectqr.data;

import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.List;

public final class LocationRepository extends LiveData<Location> {
    /* Sources
       https://developer.android.com/jetpack/guide for the architecture
       https://stackoverflow.com/a/47676353 by Damia Fuentes for tracking location in a repository
       https://stackoverflow.com/a/65507431 by ChristianB for architecture/passing a context
       https://youtu.be/VgZZemAwLTk by Brandan Jones for using observables

       Knowing that passing a context is asking for memory leaks (we're following MVVM somewhat),
       we use the application context instead.
     */

    // https://www.geeksforgeeks.org/singleton-class-java/ Pavan Gopal Rayapati
    // Naive singleton. Honestly trash for use in Android in particular but works for now
    private static volatile LocationRepository sSoleInstance = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;


    public LocationRepository(Context context) {
        int ONE_MINUTE = 60000;
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(ONE_MINUTE);
        locationRequest.setFastestInterval(ONE_MINUTE / 4);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static LocationRepository locationRepository(Context context) {
        if (sSoleInstance == null) {
            sSoleInstance = new LocationRepository(context);
        }
        return sSoleInstance;
    }


    @Override
    protected void onActive() {
        super.onActive();
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this::setLocationData);
        startLocationUpdates();
    }


    @Override
    protected void onInactive() {
        super.onInactive();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback());
    }

    private LocationCallback locationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for (Location location : locationResult.getLocations()) {
                    setLocationData(location);
                }
            }
        };
    }


    private void setLocationData(Location location) {
        setValue(location);
    }


    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback(), null);
    }

}
