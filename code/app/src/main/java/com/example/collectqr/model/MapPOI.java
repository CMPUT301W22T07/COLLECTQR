package com.example.collectqr.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;

import java.util.HashMap;
import java.util.Map;

/**
 * Point of interest (POI) for the map, that includes a Point type and its related
 * document information from Firestore. This can then be used to annotate
 * the map.
 */
public class MapPOI {
    // Constants
    // TODO: Consider moving these DB query field constants to a "Constants" class or maintain in
    //       *Controller classes.
    private final String HASH_FIELD = "sha";
    private final String POINTS_FIELD = "points";
    private final String LOGGING_TAG = "MapPOI";

    // Class variables
    private final Point point;
    private final DocumentSnapshot document;
    private String hash;
    private int intPoints;
    private JsonElement jsonData;

    /**
     * Construct a map point-of-interest with a Point location
     *
     * @param point    the point
     * @param document the document snapshot
     */

    public MapPOI(Point point, @NonNull DocumentSnapshot document) {

        this.point = point;
        this.document = document;
        this.hash = document.getString(HASH_FIELD);
        try {
            this.intPoints = document.getLong(POINTS_FIELD).intValue();
        } catch (Exception e) {
            Log.e(LOGGING_TAG, e.toString());
            this.intPoints = 0;
        }
    }


    /**
     * Construct a map point-of-interest with a latitude a longitude
     *
     * @param longitude the longitude
     * @param latitude  the latitude
     * @param document  the document snapshot
     */
    public MapPOI(double longitude, double latitude, @NonNull DocumentSnapshot document) {

        this.point = Point.fromLngLat(longitude, latitude);
        this.document = document;
        this.hash = document.getId();
        try {
            this.intPoints = document.getLong(POINTS_FIELD).intValue();
        } catch (Exception e) {
            Log.e(LOGGING_TAG, e.toString());
            this.intPoints = 0;
        }

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(HASH_FIELD, this.hash);
        dataMap.put(POINTS_FIELD, String.valueOf(this.intPoints));

        this.jsonData = new Gson().toJsonTree(dataMap);
    }


    /**
     * Return all data about this map POI as JSON
     *
     * @return JsonElement of map data
     */
    public JsonElement getJsonData() {
        return jsonData;
    }

    public Point getPoint() {
        return point;
    }


    /**
     * Return an integer of the point value of this POI
     *
     * @return an int of QR code score
     */
    public int getPoints() {
        return intPoints;
    }


    /**
     * Return the hash of value of the QR code, used for searching Firestore
     *
     * @return a string of the QR code's hash
     */
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
