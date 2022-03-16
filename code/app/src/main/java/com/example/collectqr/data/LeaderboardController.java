package com.example.collectqr.data;

import static android.content.ContentValues.TAG;

import android.util.ArrayMap;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.collectqr.adapters.LeaderboardRecyclerAdapter;
import com.example.collectqr.model.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * LeaderboardController class
 * reads users and their desired fields from Firestore,
 * specifically for user by the leaderboard
 */
public class LeaderboardController {
    private String username;
    private FirebaseFirestore db;
    private String currentCategory="most_points";

    /**
     * saves instance of Firestore and current user's username
     *
     * @param username of the current user
     */
    public LeaderboardController(String username){
        this.username = username;
        db = FirebaseFirestore.getInstance();

    }

    public void setCurrentCategory(String category) {
        this.currentCategory = category;
    }
    /**
     * Takes an empty ArrayList and the adapter of the ListView
     * Downloads the data into the ArrayList sorts it and notifies the adapter
     * Updates the views that represent the current users rank and points based on the updates in the data
     *
     * @param dataLists
     *      this is a map of lists
     * @param adapters
     *      this is a map of adapters
     * @param score
     *      this is the view that will display the user's score
     * @param rank
     *      this is the view that will display the user's rank
     */
    public void downloadData(ArrayMap<String, ArrayList<User>> dataLists, ArrayMap<String, LeaderboardRecyclerAdapter> adapters, TextView score, TextView rank) {
        LeaderboardController controller = this;
        db.collection("Users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {
                        dataLists.get("most_points").clear();
                        dataLists.get("most_codes").clear();
                        dataLists.get("best_code").clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Log.d(TAG, String.valueOf(doc.getData().get("username")));
                            String name = String.valueOf(doc.getData().get("username"));
                            Integer totalPoints = Integer.parseInt(String.valueOf(doc.get("total_points")));
                            Integer numCodes = Integer.parseInt(String.valueOf(doc.get("num_codes")));
                            Integer bestCode = Integer.parseInt(String.valueOf(doc.get("best_code")));
                            User userObj = new User(name);
                            userObj.updateScore(numCodes, totalPoints, bestCode);
                            dataLists.get("most_points").add(userObj);
                            dataLists.get("most_codes").add(userObj);
                            dataLists.get("best_code").add(userObj);
                        }
                        controller.sortLists(dataLists);

                        for (int i=0; i<dataLists.get(currentCategory).size(); i++) {
                            User item = dataLists.get(currentCategory).get(i);
                            if (item.getUsername().equals(username)) {
                                if (currentCategory.equals("most_points")) {
                                    score.setText(item.getStats().get("total_points") + " points");
                                    rank.setText(Integer.toString(i+1));
                                } else if (currentCategory.equals("most_codes")) {
                                    score.setText(item.getStats().get("num_codes") + " codes");
                                    rank.setText(Integer.toString(i+1));
                                } else {
                                    score.setText(item.getStats().get("best_code") + " points");
                                    rank.setText(Integer.toString(i+1));
                                }
                            }
                        }

                        adapters.get("most_points").notifyDataSetChanged();
                        adapters.get("most_codes").notifyDataSetChanged();
                        adapters.get("best_code").notifyDataSetChanged();

                    }
                });
    }

    /**
     * Sorts the arrays used in the leaderboard by their category
     * @param dataLists
     *      this is a map of lists to be sorted
     */
    private void sortLists(ArrayMap<String, ArrayList<User>> dataLists) {
        dataLists.get("most_points").sort(new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return t1.getStats().get("total_points")-user.getStats().get("total_points");
            }
        });
        dataLists.get("most_codes").sort(new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return t1.getStats().get("num_codes")-user.getStats().get("num_codes");
            }
        });
        dataLists.get("best_code").sort(new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return t1.getStats().get("best_code")-user.getStats().get("best_code");
            }
        });
    }


}
