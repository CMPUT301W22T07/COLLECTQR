package com.example.collectqr.viewmodels;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.collectqr.data.MapViewController;
import com.example.collectqr.model.MapPOI;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class which contains information about map markers, which indicate possible QR codes
 * to be scanned
 */
public class MapViewViewModel extends AndroidViewModel {
    /*
     Sources:
     https://docs.mapbox.com/android/maps/guides/annotations/annotations/ for creating map markers
     https://developer.android.com/topic/libraries/architecture/viewmodel for the architecture
     https://firebase.google.com/docs/firestore/solutions/geoqueries#java_1 for geo queries
     https://developer.android.com/training/location/retrieve-current#java for location
     */

    // Constants
    public final Double MAX_RADIUS = 5000.0;    // 50KM search radius max
    public final String COLLECTION = "QRCodes"; // Collection to query
    public final String ORDERING = "geohash";   // How to order the documents
    public final String LOGGER_TAG = "MapViewController";

    // Class Variables
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<MapPOI> POIList = new ArrayList<>();

    private MapViewController mMapController;
    private MutableLiveData<List<MapPOI>> qrGeoLocations;
    public int lastPOILen = 0;

    public MapViewViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<MapPOI>> getGeoLocations(Location location) {
        if (qrGeoLocations == null && location != null) {
            qrGeoLocations = new MutableLiveData<>();
            loadGeoLocations(location);
        }
        return qrGeoLocations;
    }


    private void loadGeoLocations(Location location) {
        GeoLocation searchLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
        String hash = GeoFireUtils.getGeoHashForLocation(searchLocation);
        double radiusInM = MAX_RADIUS;

        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(searchLocation, radiusInM);
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        for (GeoQueryBounds b : bounds) {
            Query q = db.collection(COLLECTION)
                    .orderBy(ORDERING)
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(t -> {
                    List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                    for (Task<QuerySnapshot> task : tasks) {
                        QuerySnapshot snap = task.getResult();
                        matchingDocs.addAll(snap.getDocuments());
                    }
                    Log.d(LOGGER_TAG, Arrays.toString(matchingDocs.toArray()));
                    generatePoints(matchingDocs);
                });
    }


    private void generatePoints(@NonNull List<DocumentSnapshot> matchingDocs) {
        int listSize = matchingDocs.size();

        POIList.clear();

        for (DocumentSnapshot doc : matchingDocs) {
            String lat_str = doc.getString("latitude");
            String lng_str = doc.getString("longitude");
            double lat;
            double lng;

            assert lat_str != null;
            assert lng_str != null;

            if (!lat_str.equals("") && !lng_str.equals("")) {
                lat = Double.parseDouble(lat_str);
                lng = Double.parseDouble(lng_str);

                MapPOI mapPOI = new MapPOI(lng, lat, doc);

                // Create points to add here
                POIList.add(mapPOI);
                Log.d(LOGGER_TAG, mapPOI.toString());
            }
        }
        qrGeoLocations.setValue(POIList);
        lastPOILen = listSize;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        POIList.clear();
    }
}


