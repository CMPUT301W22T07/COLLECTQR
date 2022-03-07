package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class QRCodeController {
    public void writeToFirestore(QRCode code) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference codeReference = db.collection("QRCodes");

        HashMap<String, String> data = new HashMap<>();
        data.put("sha256", code.getSha256());
        data.put("latitude", code.getLatitude());
        data.put("longitude", code.getLongitude());
        data.put("qr_image", code.getQr_image());
        data.put("points", code.getPoints().toString());

        codeReference
                .document(code.getSha256())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data has been added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    }
                });

        final CollectionReference scannedByReference = db.collection("QRCodes").document(code.getSha256()).collection("ScannedBy");

        for(Map.Entry<String, String> scans : code.getScanned_by().entrySet()) {
            HashMap<String, String> scannedBy = new HashMap<>();
            String key = scans.getKey();
            String value = scans.getValue();
            scannedBy.put(key, value);

            scannedByReference
                    .document(key)
                    .set(scannedBy)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Data has been added successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Data could not be added!" + e.toString());
                        }
                    });
        }

        final CollectionReference commentsReference = db.collection("QRCodes").document(code.getSha256()).collection("Comments");

        for(Map.Entry<String, String> comments : code.getComments().entrySet()) {
            HashMap<String, String> comment = new HashMap<>();
            String key = comments.getKey();
            String value = comments.getValue();
            comment.put(key, value);

            commentsReference
                    .document(key)
                    .set(comment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Data has been added successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Data could not be added!" + e.toString());
                        }
                    });
        }

    }
}
