package com.example.collectqr.ui.history;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Comparator;

public class HistoryController {
    private String username;
    private FirebaseFirestore db;
    private ArrayList<HistoryItem> qrData;
    private String currentSort = "date_descend";
    private HistoryAdapter adapter;

    public HistoryController(String username) {
        this.username = username;
        db = FirebaseFirestore.getInstance();
        qrData = new ArrayList<>();
        adapter = new HistoryAdapter(qrData);
        this.startDataDownloader();
    }

    public HistoryAdapter getAdapter() {
        return adapter;
    }

    public void setStatsBarData(TextView totalPoints, TextView numCodes) {
        //https://firebase.google.com/docs/firestore/query-data/listen
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    totalPoints.setText(snapshot.get("total_points") + "\nTotal Points");
                    numCodes.setText(snapshot.get("num_codes") + "\nQR Codes");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void startDataDownloader() {
        // Code from Lab 5
        HistoryController controller = this;
        db.collection("Users").document(username).collection("ScannedCodes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        // Clear the old list
                        qrData.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("hash")));
                            qrData.add(new HistoryItem(doc)); // Adding the cities and provinces from FireStore
                        }
                        controller.sortQrData(currentSort);
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    public void sortQrData(String sortBy) {
        /*
        https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
        Article Contributed By: sparshgupta
        https://youtu.be/Mguw_TQBExo
        YouTube video, Author: RAJASEKHAR REDDY
         */
        if (sortBy.equals("points_ascend")) {
            qrData.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return historyItem.getPoints() - t1.getPoints();
                }
            });
        } else if (sortBy.equals("points_descend")) {
            qrData.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return t1.getPoints() - historyItem.getPoints();
                }
            });
        } else if (sortBy.equals("date_descend")) {
            qrData.sort(new Comparator<HistoryItem>() {
                @Override
                public int compare(HistoryItem historyItem, HistoryItem t1) {
                    return t1.getDate().compareTo(historyItem.getDate());
                }
            });
        }
        adapter.notifyDataSetChanged();
        currentSort = sortBy;
    }
}
