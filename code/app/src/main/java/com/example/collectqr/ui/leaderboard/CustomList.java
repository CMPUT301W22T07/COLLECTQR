package com.example.collectqr.ui.leaderboard;

import static com.google.common.collect.Iterables.size;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.R;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Collections;

public class CustomList extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;

    public CustomList(Context context, ArrayList<User> users){
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }

        // sort array list based on total points
        Collections.sort(users, (user1, user2) -> user1.getStats().get("total_points").compareTo(user2.getStats().get("total_points")));
        // reverse to decreasing order (most points first)
        Collections.reverse(users);

        User user = users.get(position);

        TextView userName = view.findViewById(R.id.username_text);
        TextView userScore = view.findViewById(R.id.score_text);
        TextView userRank = view.findViewById(R.id.rank_text);

        userName.setText(user.getUsername());
        userScore.setText(user.getStats().get("total_points") + "points");
        // index of user in sorted list plus 1 = rank
        userRank.setText("#" + users.indexOf(user)+1);

        return view;
    }
}
