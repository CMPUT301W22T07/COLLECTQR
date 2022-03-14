package com.example.collectqr.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.geofire.GeoLocation;

import java.util.List;

/**
 * A class which contains information about map markers, which indicate possible QR codes
 * to be scanned
 */
public class MapViewViewModel extends ViewModel {
    /*
     Sources:
     https://github.com/osmdroid/osmdroid/wiki/Markers,-Lines-and-Polygons-(Java) for Map markers
     https://developer.android.com/topic/libraries/architecture/viewmodel for the architecture
     */
    private MutableLiveData<List<GeoLocation>> qrGeoLocations;
    public LiveData<List<GeoLocation>> getGeoLocations() {
        if (qrGeoLocations == null) {
            qrGeoLocations = new MutableLiveData<>();
            loadGeoLocations();
        }
        return qrGeoLocations;
    }

    private void loadGeoLocations() {
        qrGeoLocations.getValue().add(new GeoLocation(53.5260000, -113.5250000));
    }
}