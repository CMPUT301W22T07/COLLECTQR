package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.collectqr.model.QRCode;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A Controller which handles communications between the app and firebase, with special
 * focus on the QRCode class
 */
public class QRCodeController {
    /**
     * Takes a given QR code, and writes all its relevant contents to firestore.
     *
     * @param code the QR code to be stored to firestore
     */
    public void writeToFirestore(QRCode code) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference codeReference = db.collection("QRCodes");

        HashMap<String, String> data = new HashMap<>();
        data.put("sha256", code.getSha256());
        data.put("geohash", code.getGeoHash());
        data.put("latitude", code.getLatitudeAsString());
        data.put("longitude", code.getLongitudeAsString());
        data.put("qr_image", code.getQr_image());
        data.put("points", code.getPoints().toString());

        codeReference
                .document(code.getSha256())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));

        final CollectionReference scannedByReference = db.collection("QRCodes").document(code.getSha256()).collection("ScannedBy");

        for(Map.Entry<String, String> scans : code.getScanned_by().entrySet()) {
            HashMap<String, String> scannedBy = new HashMap<>();
            String key = scans.getKey();
            String value = scans.getValue();
            scannedBy.put(key, value);

            scannedByReference
                    .document(key)
                    .set(scannedBy)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                    .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
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
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                    .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
        }

    }
}
