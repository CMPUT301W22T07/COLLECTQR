package com.example.collectqr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.collectqr.R;
import com.example.collectqr.model.User;


import java.util.ArrayList;

/**
 * Custom list for leaderboard UI
 */
public class LeaderboardAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;

    /**
     * creates custom list from ArrayList of user objects
     * @param context
     * @param users
     */
    public LeaderboardAdapter(Context context, ArrayList<User> users){
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /**
     * Sorts ArrayList by total points and sets views for
     * custom list components
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent, false);
        }

        User user = users.get(position);

        // set TextView for each list element
        TextView userName = view.findViewById(R.id.username_text);
        TextView userScore = view.findViewById(R.id.score_text);
        TextView userRank = view.findViewById(R.id.rank_text);

        userName.setText(user.getUsername());
        userScore.setText(user.getStats().get("total_points") + " points");
        // index of user in sorted list plus 1 = rank
        userRank.setText("#" + Integer.toString(users.indexOf(user)+1));

        return view;
    }
}
