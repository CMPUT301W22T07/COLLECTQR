package com.example.collectqr.ui.leaderboard;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LeaderboardController {
    private String username;
    private FirebaseFirestore db;
    private Leaderboard leaderboard;
    private Integer score;

    public LeaderboardController(String username){
        this.username = username;
        db = FirebaseFirestore.getInstance();

    }

    public ArrayList<User> createLeaderboardArray(Context context) {
        // https://stackoverflow.com/questions/46706433/firebase-firestore-get-data-from-collection
        // Author: https://stackoverflow.com/users/1830590/slaven-petkovic
        // answered by: https://stackoverflow.com/users/324977/sam-stern
        ArrayList<User> users = new ArrayList<User>();
        db.collection("Users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<User> userObjectsList = documentSnapshots.toObjects(User.class);

                            // Add all to your list
                            users.addAll(userObjectsList);
                            Log.d(TAG, "onSuccess: " + users);
                        }
                    }})
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error getting data!!!", Toast.LENGTH_LONG).show();
                        }
                    });

    }

    public Integer getPersonalScore(String personalUsername) {
        final DocumentReference usersReference = db.collection("Users").document(personalUsername);
        usersReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    score = (Integer) snapshot.get("total_points");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        return score;
    }
}
