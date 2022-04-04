package com.example.collectqr.data;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates a wrapper to query Firebase documents with the geohash field and return documents
 * within a given radius in metres.
 */
public class MapViewController {
    /* Based off Geo queries sample at:
       https://firebase.google.com/docs/firestore/solutions/geoqueries#java */

    // Constants
    public final Double MAX_RADIUS = 5000.0;    // 50KM search radius max
    public final String COLLECTION = "QRCodes"; // Collection to query
    public final String ORDERING = "geohash"; // How to order the documents
    public final String LOGGER_TAG = "MapViewController";

    // Class Variables
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<DocumentSnapshot> matchedDocs = new ArrayList<>();
    private final List<Point> POIList = new ArrayList<>();


    /**
     * Return a list of Points to display on a map when given a location.
     *
     * @param location The location to search for nearby QR codes
     */
    public List<Point> getNearbyQRCodes(@NonNull Location location) {

        double lat = location.getLatitude();
        double lon = location.getLongitude();
        GeoLocation searchLocation = new GeoLocation(lat, lon);

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(searchLocation, MAX_RADIUS);
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        queryDocuments(bounds, tasks);
        generateMapPoints(matchedDocs);

        return POIList;
    }


    /**
     * Query Firestore for documents in a collection in a given set of bounds.
     *
     * @param bounds The bounds of the area to search, based off the geo-hash
     * @param tasks  The List of Tasks to append tasks to.
     */

    private void queryDocuments(@NonNull List<GeoQueryBounds> bounds,
                                List<Task<QuerySnapshot>> tasks) {


        // Query and create tasks
        for (GeoQueryBounds bound : bounds) {
            Query query = db.collection(COLLECTION)
                    .orderBy(ORDERING)
                    .startAt(bound.startHash)
                    .endAt(bound.endHash);
            tasks.add(query.get());
        }

        // Add matching documents to list on tasks completing
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(t -> {
                    for (Task<QuerySnapshot> task : tasks) {
                        QuerySnapshot snap = task.getResult();
                        matchedDocs.addAll(snap.getDocuments());
                    }
                    Log.d(LOGGER_TAG, Arrays.toString(matchedDocs.toArray()));
                });

    }


    /**
     * Create a List of Points which can be displayed on a map.
     *
     * @param matchingDocuments A List of matching Firestore Documents to make Points from
     */
    private void generateMapPoints(@NonNull List<DocumentSnapshot> matchingDocuments) {

        for (DocumentSnapshot doc : matchingDocuments) {
            // Get the latitude from a document as a String
            try {
                String lat_str = doc.getString("latitude");
                String lng_str = doc.getString("longitude");
                double lat;
                double lng;

                // We got the stored hash, but we'll make a Point with the latitude and longitude
                assert lat_str != null;
                assert lng_str != null;

                if (!lat_str.equals("") && !lng_str.equals("")) {
                    lat = Double.parseDouble(lat_str);
                    lng = Double.parseDouble(lng_str);

                    // Create points to add here
                    Point point = Point.fromLngLat(lng, lat);
                    POIList.add(Point.fromLngLat(lng, lat));

                    Log.d(LOGGER_TAG, point.toString());
                }
            } catch (Exception e) {
                Log.e(LOGGER_TAG, e.toString());
            }
        }

    }

}
