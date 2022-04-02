package com.example.collectqr.model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mapbox.geojson.Point;

/**
 * Point of interest (POI) for the map, that includes a Point type and its related
 * document information from Firestore. This can then be used to annotate
 * the map.
 */
public class MapPOI {
    // Constants
    // TODO: Consider moving these DB query field constants to a "Constants" class or maintain in
    //       *Controller classes.
    private final String HASH_FIELD = "sha256";
    private final String POINTS_FIELD = "points";

    // Class variables
    private final Point point;
    private final DocumentSnapshot document;
    private String hash;

    public MapPOI(Point point, @NonNull DocumentSnapshot document) {
        this.point = point;
        this.document = document;
        this.hash = document.getString(HASH_FIELD);
    }

    public MapPOI(double longitude, double latitude, @NonNull DocumentSnapshot document) {
        this.point = Point.fromLngLat(longitude, latitude);
        this.document = document;
        this.hash = document.getString(HASH_FIELD);
    }

    public Point getPoint() {
        return point;
    }

    public Boolean containsGeoPoint(Point pointCompare) {
        // probs going to fail
        return point == pointCompare;
    }

    public int getPoints() {
        int intPoints = 0;
        String strPoints = document.getString(POINTS_FIELD);

        // Don't convert if the field is null
        if (strPoints != null) {
            intPoints = Integer.parseInt(strPoints);
        }

        return intPoints;
    }

    public String getHash() {
        return hash;
    }

    @NonNull
    @Override
    public String toString() {
        return "Map Point of Interest: \n{" +
                "point=" + point +
                "and contains a Firestore document" +
                '}';
    }
}