package com.example.collectqr.data;

import android.util.Log;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.firebase.geofire.core.GeoHashQuery;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryGeoHash {
    public final Double MAX_RADIUS = 5000.0;    // 50KM search radius max
    /* Based off Geo queries sample at:
       https://firebase.google.com/docs/firestore/solutions/geoqueries#java */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final GeoLocation currentLocation;
    private final Double searchRadiusInM;
    private List<DocumentSnapshot> matchingDocsResult;

    /**
     * Creates a wrapper to query Firebase documents with the "geohash" field and return documents
     * within a given radius in metres.
     *
     * @param currentLocation The player's current location
     * @param searchRadiusInM The radius that the player wants to search
     */
    public QueryGeoHash(GeoLocation currentLocation, Double searchRadiusInM) {
        this.currentLocation = currentLocation;
        this.searchRadiusInM = searchRadiusInM;
    }

    /**
     * Query the database for a list of QR codes in a given search radius under a maximum radius.
     */
    public void makeQuery() {
        // Define the area we want to search
        List<GeoQueryBounds> bounds =
                GeoFireUtils.getGeoHashQueryBounds(currentLocation, searchRadiusInM);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        Log.d("DEBUG", Arrays.toString(bounds.toArray()));

        // Create a query of QRCode documents ordered by geohash
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("QRCodes")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }


//        Tasks.whenAllComplete(tasks)
//                .addOnCompleteListener(t -> {
//                    List<DocumentSnapshot> matchingDocs = new ArrayList<>();
//
//                    for (Task<QuerySnapshot> task : tasks) {
//                        QuerySnapshot snap = task.getResult();
//                        for (DocumentSnapshot doc : snap.getDocuments()) {
//                            double latitude = doc.getDouble("latitude");
//                            double longitude = doc.getDouble("longitude");
//
//                            // Filtering false-positives
//                            GeoLocation docLocation = new GeoLocation(latitude, longitude);
//                            double distanceInM = GeoFireUtils
//                                    .getDistanceBetween(docLocation, currentLocation);
//
//                            if ((distanceInM <= searchRadiusInM)
//                                    && (searchRadiusInM <= MAX_RADIUS)) {
//                                matchingDocs.add(doc);
//                            }
//                        }
//                    }
//
//                    // TODO: Do away with this odd reassignment
//                    matchingDocsResult = matchingDocs;
//                });
//        return matchingDocsResult;
//    }

    }
}