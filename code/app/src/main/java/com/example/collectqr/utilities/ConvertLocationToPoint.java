package com.example.collectqr.utilities;

import com.firebase.geofire.GeoLocation;

import org.osmdroid.util.GeoPoint;

public class ConvertLocationToPoint {
    public GeoPoint convert(GeoLocation geoLocation) {
        return new GeoPoint(geoLocation.latitude, geoLocation.longitude);
    }
}
