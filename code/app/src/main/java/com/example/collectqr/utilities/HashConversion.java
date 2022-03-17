package com.example.collectqr.utilities;

import com.firebase.geofire.GeoLocation;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.GeoPoint;

import java.nio.charset.StandardCharsets;

/**
 * A class that contains methods for hashing and hashing-related tasks
 */
public class HashConversion {

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
     *
     * @param geolocation A location with a latitude and longitude
     * @return A location as a point
     */
    public GeoPoint convertLocationToOSMPoint(GeoLocation geolocation) {
        return new GeoPoint(geolocation.latitude, geolocation.longitude);
    }


    /**
     * Convert a Firebase GeoLocation to a Firebase GeoPoint
     *
     * @param location A location with a latitude and longitude
     * @return A location as a point
     */
    public com.google.firebase.firestore.GeoPoint convertLocationToFBPoint(GeoLocation location) {
        return new com.google.firebase.firestore.GeoPoint(location.latitude, location.longitude);
    }

}
