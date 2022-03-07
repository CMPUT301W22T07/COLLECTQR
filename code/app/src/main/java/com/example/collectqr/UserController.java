package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    public void writeToFirestore(User user) {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference userReference = db.collection("Users");

        HashMap<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());

        //Move stats from HashMap to db
        for(Map.Entry<String, Integer> stats : user.getStats().entrySet()) {
            String key = stats.getKey();
            String value = stats.getValue().toString();
            data.put(key, value);
        }

        // The set method sets a unique id for the document
        userReference
                .document(user.getUsername())
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

        final CollectionReference codesReference = db.collection("Users").document(user.getUsername()).collection("ScannedCodes");

        //document per qrcode scanned
        for(Map.Entry<String, HashMap<String, String>> stats : user.getCodes_scanned().entrySet()) {
            HashMap<String, String> qrdata = new HashMap<>();
            String key = stats.getKey();
            HashMap<String, String> value = stats.getValue();
            qrdata.put("hash", key);
            qrdata.put("points", value.get("points"));
            qrdata.put("latitude", value.get("latitude"));
            qrdata.put("longitude", value.get("longitude"));
            qrdata.put("date", value.get("date"));

            codesReference
                    .document(key)
                    .set(qrdata)
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
