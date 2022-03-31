package com.example.collectqr.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final LocationRequest locationRequest;


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


    @SuppressLint("MissingPermission")
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


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback(), null);
    }

}


//public class bruh() {
//    /**
//     * Gets the last known location of the device using Google Play Service's Fused Location
//     * Provider Client.
//     *
//     * @return A Location
//     * @deprecated Use LocationRepository instead (TODO: Work in progress)
//     */
//    @Deprecated
//    public Location getLastKnownLocation() {
//        Context context = requireContext();
//        AtomicReference<Location> location = new AtomicReference<>();
//
//        /* https://developer.android.com/training/location/retrieve-current#java
//           https://stackoverflow.com/a/57237566 by rivaldi */
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
//
//            locationResult.addOnCompleteListener(requireActivity(), task -> {
//
//                if (task.isSuccessful()) {
//                    location.set(task.getResult());
//                    Log.d(TAG, location.get().toString());
//                } else {
//                    location.set(null);
//                    Log.e(TAG,"FusedLocationProviderClient Failed to get Location");
//                }
//
//            });
//        }
//
//        return location.get();
//    }
//}