package com.example.collectqr.model;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final String HASH_FIELD = "sha256";
    private final String POINTS_FIELD = "points";
    private final String COMMENTS_COLLECTION = "Comments";
    private final String SCANNED_BY_COLLECTION = "ScannedBy";

    // Class variables
    private final Point point;
    private final DocumentSnapshot document;
    private DocumentReference documentReference;
    private String hash;
    Map<String, String> allComments = new ArrayMap<>();
    ArrayList<String> allScannedBy = new ArrayList<>();
    // ArrayMap<String, String> allScannedBy = new ArrayMap<>();
    private JsonElement dataJson;

    public MapPOI(Point point, @NonNull DocumentSnapshot document) {
        this.point = point;
        this.document = document;
        this.hash = document.getString(HASH_FIELD);

    }

    public MapPOI(double longitude, double latitude, @NonNull DocumentSnapshot document) {
        this.point = Point.fromLngLat(longitude, latitude);
        this.document = document;
        this.documentReference = document.getReference();
        this.hash = document.getString(HASH_FIELD);

        addDataToPoints(this.point, this.document);
    }


    private void addDataToPoints(Point point, DocumentSnapshot document) {
        CollectionReference commentsCollection = documentReference.collection(COMMENTS_COLLECTION);
        CollectionReference scannedByCollection = documentReference.collection(SCANNED_BY_COLLECTION);

        getComments(commentsCollection);
        getAllScannedBy(scannedByCollection);

//         Map<String, Map<String, String>> allData = new HashMap<>();
         Map<String, Object> allData = new HashMap<>();
         allData.put(COMMENTS_COLLECTION, allComments);
         allData.put(SCANNED_BY_COLLECTION, allScannedBy);

         convertDataToJson(allData);
    }


    private void convertDataToJson(Map<String, Object> allData) {
        // Converting a map point's qr code hash to json
        // https://stackoverflow.com/a/12155874 by Ankur
         dataJson = new Gson().toJsonTree(allComments);

        // https://stackoverflow.com/a/24635655 by matt burns
         Gson gson = new GsonBuilder().enableComplexMapKeySerialization()
                 .setPrettyPrinting().create();

        Log.d("JSON", allComments.toString());
        Log.d("JSON", gson.toJson(allComments));
    }


    private void getAllScannedBy(@NonNull CollectionReference scannedByCollection) {
        scannedByCollection.addSnapshotListener((value, error) -> {
            assert value != null;
            for (QueryDocumentSnapshot d : value) {
                Log.d("BRUHSCAN", "Scanned by: " + d.getId());
                String docID = d.getId();
                // String date = (String) d.get(d.getId());
                allScannedBy.add(docID);
            }
        });
    }


    private void getComments(@NonNull CollectionReference commentsCollection) {
        commentsCollection.addSnapshotListener((value, error) -> {
            assert value != null;
            for (QueryDocumentSnapshot d : value) {
                Log.d("BRUH", document.getId() + ": " + d.getId() +
                        ": " + d.get(d.getId()));

                String docId = d.getId();
                String comment = (String) d.get(docId);
                if (comment != null) {
                    allComments.put(docId, comment);
                }
            }
        });

    }


    public JsonElement getDataJson() {
        return dataJson;
    }


    public Point getPoint() {
        return point;
    }


    public String getHash() {
        return hash;
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


    @NonNull
    @Override
    public String toString() {
        return "Map Point of Interest: \n{" +
                "point=" + point +
                "and contains a Firestore document" +
                '}';
    }


    private class MapPOIJson {

    }
}
