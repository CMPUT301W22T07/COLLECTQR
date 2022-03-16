package com.example.collectqr.data;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.collectqr.adapters.LeaderboardAdapter;
import com.example.collectqr.model.Leaderboard;
import com.example.collectqr.model.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * LeaderboardController class
 * reads users and their desired fields from Firestore,
 * specifically for user by the leaderboard
 */
public class LeaderboardController {
    private String username;
    private FirebaseFirestore db;
    private Leaderboard leaderboard;
    private Integer score;

    /**
     * saves instance of Firestore and current user's username
     *
     * @param username of the current user
     */
    public LeaderboardController(String username){
        this.username = username;
        db = FirebaseFirestore.getInstance();

    }

    /**
     * Takes an empty ArrayList and the adapter of the ListView
     * Downloads the data into the ArrayList sorts it and notifies the adapter
     * Updates the views that represent the current users rank and points based on the updates in the data
     *
     * @param users
     * @param adapter
     * @param personalScore
     * @param personalRank
     */
    public void downloadData(ArrayList<User> users, LeaderboardAdapter adapter, TextView personalScore, TextView personalRank) {
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("username")));
                            String name = String.valueOf(doc.getData().get("username"));
                            Integer totalPoints = Integer.parseInt(String.valueOf(doc.get("total_points")));
                            Integer numCodes = Integer.parseInt(String.valueOf(doc.get("num_codes")));
                            Integer bestCode = Integer.parseInt(String.valueOf(doc.get("best_code")));
                            for (int i=0; i< users.size(); i++) {
                                if (users.get(i).getUsername().equals(name)) {
                                    users.remove(i);
                                }
                            }
                            User userObj = new User(name);
                            userObj.updateScore(numCodes, totalPoints, bestCode);
                            users.add(userObj);

                        }
                        users.sort(new Comparator<User>() {
                            @Override
                            public int compare(User user, User t1) {
                                return t1.getStats().get("total_points")-user.getStats().get("total_points");
                            }
                        });
                        adapter.notifyDataSetChanged();
                        for (int i=0;i<users.size();i++) {
                            if (users.get(i).getUsername().equals(username)) {
                                personalScore.setText(users.get(i).getStats().get("total_points").toString());
                                personalRank.setText(Integer.toString(i+1));
                            }
                        }
                    }
                });
    }
}
