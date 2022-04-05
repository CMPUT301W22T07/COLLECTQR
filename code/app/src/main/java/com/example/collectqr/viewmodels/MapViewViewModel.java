package com.example.collectqr.viewmodels;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
 * View model that maintains primarily an observable of Points of Interests to display,
 * but also provides location updates making use of the application context.
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
    public final Double MAX_RADIUS = 50000.0;    // 500KM search radius max
    public final String COLLECTION = "QRCodes";  // Collection to query
    public final String ORDERING = "geohash";    // How to order the documents
    public final String LOGGER_TAG = "MapViewController";

    // Class Variables
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<MapPOI> POIList = new ArrayList<>();
    private MutableLiveData<Location> locationLiveData;
    private MutableLiveData<List<MapPOI>> qrGeoLocations;
    public int lastPOILen = 0;
    public Boolean dataLoaded = false;           // Boolean to decide if data has already been
    // downloaded.


    public MapViewViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * Return an observable List of Points of Interests, which can be used to annotate a map.
     *
     * @param location The player's current location to search for nearby QR codes from
     * @return An observable list of map points-of-interests
     */
    public LiveData<List<MapPOI>> getPOIList(Location location) {
        // If observing for the first time, instantiate our observable and retrieve the data
        if (qrGeoLocations == null && location != null) {
            qrGeoLocations = new MutableLiveData<>();
            loadGeoLocations(location);
        }
        return qrGeoLocations;
    }


    /**
     * Generate the bounds of our search area and query Firestore for nearby QR codes.
     *
     * @param location The location the player wants to search
     */
    private void loadGeoLocations(Location location) {
        // Preparing the query with our search area as a geohash
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
                    // Generate GeoJson Points compatible with Mapbox in
                    generatePoints(matchingDocs);
                });
    }


    /**
     * Generate an observable list of GeoJson Points.
     *
     * @param matchingDocs A list of DocumentSnapshots that matched the search area criteria
     */
    private void generatePoints(@NonNull List<DocumentSnapshot> matchingDocs) {
        int listSize = matchingDocs.size();

        // Clear the POIList if populated.
        POIList.clear();

        // Iterate through every doc, get the necessary data to make a Point and add to List
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


    /**
     * Force setting the current location (like on map click).
     *
     * @param location The location the player wants to set
     */
    public void setLocation(Location location) {
        if (locationLiveData == null) {
            locationLiveData = new MutableLiveData<>();
        }

        locationLiveData.setValue(location);
    }


    /**
     * Get the last known location that was set manually or through a location manager.
     *
     * @return An observable data type with the last known location
     */
    public LiveData<Location> getLastKnownLocation() {
        return locationLiveData;
    }

    @Nullable
    public List<MapPOI> getPOIList() {
        return qrGeoLocations.getValue();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        POIList.clear();
    }
}


