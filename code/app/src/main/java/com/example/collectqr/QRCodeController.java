package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.collectqr.model.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A Controller which handles communications between the app and firebase, with special
 * focus on the QRCode class
 */
public class QRCodeController {
    private FirebaseFirestore db;

    public QRCodeController() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Takes a given QR code, and writes all its relevant contents to firestore.
     *
     * @param code the QR code to be stored to firestore
     */
    public void writeToFirestore(QRCode code) {
        final CollectionReference codeReference = db.collection("QRCodes");

        HashMap<String, Object> data = new HashMap<>();
        data.put("sha", code.getSha256());
        if (code.getLatitude()!=null) {
            data.put("geohash", code.getGeoHash());
        } else {
            data.put("geohash", "");
        }
        data.put("latitude", code.getLatitudeAsString());
        data.put("longitude", code.getLongitudeAsString());
        data.put("qr_image", code.getQr_image());
        data.put("points", code.getPoints());

        codeReference
                .document(code.getSha256())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));

        final CollectionReference scannedByReference = db.collection("QRCodes").document(code.getSha256()).collection("ScannedBy");

        for(Map.Entry<String, Date> scans : code.getScanned_by().entrySet()) {
            HashMap<String, Date> scannedBy = new HashMap<>();
            String key = scans.getKey();
            Date value = scans.getValue();
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

    /**
     * Uploads a QR code to a user's history and updates stats
     * @param qrCode
     * @param username
     */
    public void writeToUserFirestore(QRCode qrCode, String username) {
        DocumentReference docRef = db.collection("Users").document(username).collection("ScannedCodes").document(qrCode.getSha256());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        final DocumentReference userCodeReference = db.collection("Users").document(username).collection("ScannedCodes").document(qrCode.getSha256());
                        ArrayMap<String, Object> data = new ArrayMap<>();
                        data.put("sha", qrCode.getSha256());
                        data.put("date", qrCode.getDate());
                        data.put("points", qrCode.getPoints());
                        data.put("image", qrCode.getQr_image());
                        userCodeReference.set(data);
                        db.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        Integer totalPoints = Integer.parseInt(document.get("total_points").toString());
                                        Integer numCodes = Integer.parseInt(document.get("num_codes").toString());
                                        Integer bestCode = Integer.parseInt(document.get("best_code").toString());
                                        DocumentReference documentReference = db.collection("Users").document(username);
                                        documentReference.update("total_points", totalPoints + qrCode.getPoints());
                                        documentReference.update("num_codes", numCodes + 1);
                                        if (bestCode<qrCode.getPoints()) {
                                            documentReference.update("best_code", qrCode.getPoints());
                                        }
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Uploads a QR code to the QRCodes collection
     * @param qrCode
     * @param username
     */
    public void writeToCodesFirestore(QRCode qrCode, String username) {
        DocumentReference docRef = db.collection("QRCodes").document(qrCode.getSha256());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            final CollectionReference commentsReference = db.collection("QRCodes").document(qrCode.getSha256()).collection("Comments");

                            for(Map.Entry<String, String> comments : qrCode.getComments().entrySet()) {
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
                        } else {
                            Log.d(TAG, "No such document");
                            writeToFirestore(qrCode);
                        }
                        final DocumentReference scannedByReference = db.collection("QRCodes").document(qrCode.getSha256()).collection("ScannedBy").document(username);
                        ArrayMap<String, Object> data = new ArrayMap<>();
                        data.put("username", username);
                        data.put("date", qrCode.getDate());
                        scannedByReference.set(data);
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
    }

    public void deleteCodeFromAccount(QRCode code, int secondBest, String username) {
        // https://firebase.google.com/docs/firestore/manage-data/delete-data#delete_documents
        db.collection("Users").document(username).collection("ScannedCodes").document(code.getSha256())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        db.collection("Users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Integer totalPoints = Integer.parseInt(document.get("total_points").toString());
                        Integer numCodes = Integer.parseInt(document.get("num_codes").toString());
                        Integer bestCode = Integer.parseInt(document.get("best_code").toString());
                        DocumentReference documentReference = db.collection("Users").document(username);
                        documentReference.update("total_points", totalPoints - code.getPoints());
                        documentReference.update("num_codes", numCodes - 1);
                        if (bestCode == code.getPoints()) {
                            if (secondBest!=code.getPoints()) {
                                documentReference.update("best_code", secondBest);
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
