package com.example.collectqr.ui.leaderboard;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.User;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
     * Takes app context and creates an ArrayList of User objects
     * to be used by the leaderboard for getting info and stats.
     * Saves all "Users" documents as User objects
     *
     * @param context
     * @return ArrayList<User> users
     */
    public ArrayList<User> createLeaderboardArray(Context context) {
        ArrayList<User> users = new ArrayList<User>();
        LeaderboardController controller = this;
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        // Clear the old list
                        // users.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("username")));
                            String name = String.valueOf(doc.getData().get("username"));
                            Integer totalPoints = Integer.parseInt(String.valueOf(doc.get("total_points")));
                            Integer numCodes = Integer.parseInt(String.valueOf(doc.get("num_codes")));
                            User userObj = new User(name);
                            userObj.updateScore(numCodes, totalPoints);
                            users.add(userObj);
                        }
                    }
                });

        // sort array list based on total points
        Collections.sort(users, Comparator.comparing(user -> user.getStats().get("total_points")));
        // reverse to decreasing order (most points first)
        Collections.reverse(users);
        return users;
    }

    /**
     * Using a username passed into the method, finds user in Firestore
     * and gets their score
     * @return score
     */
    public void getPersonalScore(TextView score) {
        // https://firebase.google.com/docs/firestore/query-data/listen
        final DocumentReference usersReference = db.collection("Users").document(username);
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
                    score.setText(snapshot.get("total_points")+ " points");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    /**
     * Using a username passed into the method, finds User object in
     * ArrayList also passed to the method, and returns their rank/index
     * in the sorted list
     * @param username
     * @param users
     * @return (i + 1) index + 1 of user in list
     */
    public void getUserRank(String username, ArrayList<User> users, TextView rank){
        // find user in ArrayList and return their index in list + 1 (rank)
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).getUsername() == username){
                rank.setText((i + 1));
            } else {
                rank.setText("ERR");
            }
        }
    }
}
