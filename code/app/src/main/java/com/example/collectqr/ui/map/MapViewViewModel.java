package com.example.collectqr.ui.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class MapViewViewModel extends ViewModel {
    /*
     Sources:
     https://github.com/osmdroid/osmdroid/wiki/Markers,-Lines-and-Polygons-(Java) for Map markers
     https://developer.android.com/topic/libraries/architecture/viewmodel for the architecture
     */
    private MutableLiveData<ArrayList<GeoPoint>> qrGeoPoints;
}