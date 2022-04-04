package com.example.collectqr.data;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.example.collectqr.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A Controller which handles communications between the app and firebase, with special
 * focus on the user class
 */
public class UserController {
    /**
     * Takes a given user, and writes all its relevant contents to firestore.
     *
     * @param user the user to be stored to firestore
     */
    public void writeToFirestore(User user) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference userReference = db.collection("Users");

        HashMap<String, Object> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("devices", user.getDevices());
        data.put("scan_1_code", user.isScan_1_code());
        data.put("scan_10_codes", user.isScan_10_codes());
        data.put("scan_50_codes", user.isScan_50_codes());
        data.put("scan_10_points", user.isScan_10_points());
        data.put("scan_100_points", user.isScan_100_points());
        data.put("scan_300_points", user.isScan_300_points());

        //Move stats from HashMap to db
        for (Map.Entry<String, Integer> stats : user.getStats().entrySet()) {
            String key = stats.getKey();
            int value = stats.getValue();
            data.put(key, value);
        }

        // The set method sets a unique id for the document
        userReference
                .document(user.getUsername())
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));

        final CollectionReference codesReference = db.collection("Users").document(user.getUsername()).collection("ScannedCodes");

        //document per qrcode scanned
        for (Map.Entry<String, HashMap<String, Object>> stats : user.getCodes_scanned().entrySet()) {
            HashMap<String, Object> qrdata = new HashMap<>();
            String key = stats.getKey();
            HashMap<String, Object> value = stats.getValue();
            qrdata.put("hash", key);
            qrdata.put("points", value.get("points"));
            qrdata.put("date", value.get("date"));
            qrdata.put("image", value.get("image"));

            codesReference
                    .document(key)
                    .set(qrdata)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                    .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
        }

    }

    /**
     * Takes a given user's device_id, and adds them as an admin user
     *
     * @param device_id the user's device_id to be added
     */
    public void addAdminUser(String device_id) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        final CollectionReference userReference = db.collection("Admins");

        HashMap<String, String> data = new HashMap<>();
        data.put("device_id", device_id);

        //Add the information to the database
        userReference
                .document(device_id)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Data has been added successfully!"))
                .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
    }
}
