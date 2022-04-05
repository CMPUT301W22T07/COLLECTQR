package com.example.collectqr.data;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.QRCodeController;
import com.example.collectqr.adapters.HistoryAdapter;
import com.example.collectqr.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Controls and manages the data needed for HistoryFragement
 */
public class HistoryController {
    private String username;
    private FirebaseFirestore db;
    private ArrayList<QRCode> qrData;
    private String currentSort = "date_descend";
    private HistoryAdapter adapter;


    /**
     * HistoryController controller
     * @param username  the username
     */
    public HistoryController(String username) {
        this.username = username;
        db = FirebaseFirestore.getInstance();
        qrData = new ArrayList<>();
        adapter = new HistoryAdapter(qrData);
        this.startDataDownloader();
    }

    /**
     * Returns an adapter for a RecyclerView
     * @return HistoryAdapter
     *      THis is the adapter to be used to control the RecyclerView on the HistoryFragment
     */
    public HistoryAdapter getAdapter() {
        return adapter;
    }


    /**
     * Returns the data to be displayed
     * @return the data
     */
    public ArrayList<QRCode> getData() {
        return qrData;
    }

    /**
     * Downloads the user's document from the database
     * and sets up a snapshot listener to keep the data up to date
     * Assigns data from total_points and num_codes fields to the TextViews provided
     * @param totalPoints
     * @param numCodes
     */
    public void setStatsBarData(TextView totalPoints, TextView numCodes) {
        //https://firebase.google.com/docs/firestore/query-data/listen
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
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

    /**
     * Downloads all the QR codes scanned by the user from the database
     * and sets up a snapshot listener to keep the data up to date
     * Calls a function to sort the list and notifies the adapter using the list as its data
     */
    private void startDataDownloader() {
        // Code from Lab 5
        HistoryController controller = this;
        db.collection("Users").document(username).collection("ScannedCodes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        qrData.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("sha")));
                            String imageName = doc.getString("image");
                            Integer points = Integer.parseInt(doc.get("points").toString());
                            String sha = doc.getString("sha");
                            Date date = doc.getDate("date");
                            qrData.add(new QRCode(sha, points, date, imageName)); // Adding the cities and provinces from FireStore
                        }
                        controller.sortQrData(currentSort);
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    /**
     * Sorts an ArrayList with elements of type QRCode into a specific order
     * @param sortBy
     *      This is the string representing the order in which the list should be sorted
     */
    public void sortQrData(String sortBy) {
        /*
        https://www.geeksforgeeks.org/how-to-sort-an-arraylist-of-objects-by-property-in-java/
        Article Contributed By: sparshgupta
        https://youtu.be/Mguw_TQBExo
        YouTube video, Author: RAJASEKHAR REDDY
         */
        if (sortBy.equals("points_ascend")) {
            qrData.sort(new Comparator<QRCode>() {
                @Override
                public int compare(QRCode qrcode1, QRCode qrcode2) {
                    return qrcode1.getPoints() - qrcode2.getPoints();
                }
            });
        } else if (sortBy.equals("points_descend")) {
            qrData.sort(new Comparator<QRCode>() {
                @Override
                public int compare(QRCode qrcode1, QRCode qrcode2) {
                    return qrcode2.getPoints() - qrcode1.getPoints();
                }
            });
        } else if (sortBy.equals("date_descend")) {
            qrData.sort(new Comparator<QRCode>() {
                @Override
                public int compare(QRCode qrcode1, QRCode qrcode2) {
                    return qrcode2.getDate().compareTo(qrcode1.getDate());
                }
            });
        }
        adapter.notifyDataSetChanged();
        currentSort = sortBy;
    }


    /**
     * Deletes the code from history
     * @param code  the code
     */
    public void deleteCode(QRCode code) {
        int secondBest = 0;
        for (int i=0; i<qrData.size(); i++) {
            if (qrData.get(i).getSha256()!=code.getSha256() && qrData.get(i).getPoints() > secondBest) {
                secondBest = qrData.get(i).getPoints();
            }
        }
        new QRCodeController().deleteCodeFromAccount(code.getSha256(), code.getPoints(), secondBest, username);
    }
}
