package com.example.collectqr.utilities;

import com.firebase.geofire.GeoLocation;
import com.google.common.hash.Hashing;

import org.osmdroid.util.GeoPoint;

import java.nio.charset.StandardCharsets;

/**
 * An abstract class which contains methods for hashing
 */
public abstract class HashConversion {

    /**
     * Returns the SHA-256 hash of a given object
     *
     * @return SHA-256 hash String of an Object
     */
    public String convertToSHA256(String string) {
        // https://www.baeldung.com/sha-256-hashing-java
        return Hashing.sha256()
                .hashString(string, StandardCharsets.UTF_8)
                .toString();
    }


    /**
     * Convert a Firebase GeoLocation to an osmdroid GeoPoint
     * @param geolocation   A location with a latitude and longitude
     * @return              A location as a point
     */
    public GeoPoint convertLocationToOSMPoint(GeoLocation geolocation) {
        return new GeoPoint(geolocation.latitude, geolocation.longitude);
    }


    /**
     * Convert a Firebase GeoLocation to a Firebase GeoPoint
     * @param location   A location with a latitude and longitude
     * @return           A location as a point
     */
    public com.google.firebase.firestore.GeoPoint convertLocationToFBPoint(GeoLocation location) {
        return new com.google.firebase.firestore.GeoPoint(location.latitude, location.longitude);
    }


    /**
     * Convert an osmdroid GeoPoint to a Firebase GeoLocation
     * @param geoPoint      A location with a latitude and longitude
     * @return              A location as a GeoLocation
     */
    public GeoLocation convertPointToLocation(GeoPoint geoPoint) {
        return new GeoLocation(geoPoint.getLatitude(), geoPoint.getLongitude());
    }


    /**
     * Convert a Firebase GeoPoint to a Firebase GeoLocation
     * @param geoPoint      A location with a latitude and longitude
     * @return              A location as a GeoLocation
     */
    public GeoLocation convertPointToLocation(com.google.firebase.firestore.GeoPoint geoPoint) {
        return new GeoLocation(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

}
