package com.example.collectqr.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

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