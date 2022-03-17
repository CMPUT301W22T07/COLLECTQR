package com.example.collectqr.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class which contains information about map markers, which indicate possible QR codes
 * to be scanned
 */
public class MapViewViewModel extends ViewModel {
    /*
     Sources:
     https://docs.mapbox.com/android/maps/guides/annotations/annotations/
     https://developer.android.com/topic/libraries/architecture/viewmodel for the architecture
     https://firebase.google.com/docs/firestore/solutions/geoqueries#java_1 for geo queries
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MutableLiveData<List<GeoLocation>> qrGeoLocations;
    public LiveData<List<GeoLocation>> getGeoLocations() {
        if (qrGeoLocations == null) {
            qrGeoLocations = new MutableLiveData<>();
            loadGeoLocations();
        }
        return qrGeoLocations;
    }

    private void loadGeoLocations() {
        //qrGeoLocations.getValue().add(new GeoLocation(53.5260000, -113.5250000));
        GeoLocation location = new GeoLocation(53.260, -113.525);
        String hash = GeoFireUtils.getGeoHashForLocation(location);
        double radiusInM = 50 * 1000;
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(location, radiusInM);
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("QRCodes")
                    .orderBy("geohash")
                    .startAt(b.startHash)
                    .endAt(b.endHash);
            tasks.add(q.get());
        }

        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                        for (Task<QuerySnapshot> task : tasks) {
                            QuerySnapshot snap = task.getResult();
                            for (DocumentSnapshot doc : snap.getDocuments()) {
                                String lat_str = doc.getString("latitude");
                                String lng_str = doc.getString("longitude");

                                assert lat_str != null;
                                assert lng_str != null;

                                if (!lat_str.equals("") && !lng_str.equals("")) {
                                    double lat = Double.parseDouble(lat_str);
                                    double lng = Double.parseDouble(lng_str);

                                    matchingDocs.add(doc);
                                }
                            }
                        }
                        generatePoints(matchingDocs);
                    }
                });
    }

    private void generatePoints(List<DocumentSnapshot> matchingDocs) {
    }


}