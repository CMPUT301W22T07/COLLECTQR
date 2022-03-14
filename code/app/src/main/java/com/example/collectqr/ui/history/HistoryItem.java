package com.example.collectqr.ui.history;

import com.example.collectqr.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class HistoryItem {
    private int points;
    private StorageReference imageReference;
    private String hash;
    private Date date;

    public HistoryItem(QueryDocumentSnapshot doc) {
        String imageName = doc.getString("image");
        points = Integer.parseInt(doc.get("points").toString());
        hash = doc.getString("hash");
        date = doc.getDate("date");
        // https://firebase.google.com/docs/storage/android/download-files#downloading_images_with_firebaseui
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://collectqr7.appspot.com");
        StorageReference storageRef = storage.getReference();
        imageReference = storageRef.child(imageName);
    }

    public int getPoints() {
        return points;
    }

    public StorageReference getImageReference() { return imageReference; }

    public String getHash() {
        return hash;
    }

    public Date getDate() {
        return date;
    }
}
