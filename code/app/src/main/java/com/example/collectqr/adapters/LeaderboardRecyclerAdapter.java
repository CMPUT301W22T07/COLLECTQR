package com.example.collectqr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.example.collectqr.model.User;

import java.util.ArrayList;
import java.util.HashMap;

// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter
/**
 * A custom RecyclerView.Adapter for the leaderboard screen
 */
public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.ViewHolder> {

    private ArrayList<User> data;
    private ViewGroup viewGroup;
    private String category;
    private LeaderboardRecyclerAdapter.OnRecyclerItemClickListener listener;

    /*
    YouTube Video, Author: Coding in Flow
    https://youtu.be/bhhs4bwYyhc
     */
    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position, String category);
    }


    /**
     *
     * Sets the on item click listener
     *
     * @param listener  the listener
     */
    public void setOnItemClickListener(LeaderboardRecyclerAdapter.OnRecyclerItemClickListener listener) {

        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userScore;
        private TextView userRank;


        /**
         *
         * View holder
         *
         * @param view  the view
         * @param listener  the listener
         * @param category  the category
         * @return public
         */
        public ViewHolder(View view, OnRecyclerItemClickListener listener, String category) {

            super(view);
            // Define click listener for the ViewHolder's View

            userName = view.findViewById(R.id.username_text);
            userScore = view.findViewById(R.id.score_text);
            userRank = view.findViewById(R.id.rank_text);

            view.setOnClickListener(new View.OnClickListener() {
                @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
                public void onClick(View view) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRecyclerItemClick(position, category);
                        }
                    }
                }
            });
        }


        /**
         *
         * Gets the user name
         *
         * @return the user name
         */
        public TextView getUserName() {

            return userName;
        }


        /**
         *
         * Gets the user score
         *
         * @return the user score
         */
        public TextView getUserScore() {

            return userScore;
        }


        /**
         *
         * Gets the user rank
         *
         * @return the user rank
         */
        public TextView getUserRank() {

            return userRank;
        }
    }

    /**
     * Initializes the dataset of the Adapter.
     *
     * @param data
     * @param category
     */
    public LeaderboardRecyclerAdapter(ArrayList<User> data, String category) {

        this.data = data;
        this.category = category;
    }

    /**
     * Creates new views (invoked by the layout manager)
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        // Create a new view, which defines the UI of the list item
        this.viewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.content, viewGroup, false);
        return new ViewHolder(view, this.listener, this.category);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        User user = data.get(position);

        viewHolder.getUserName().setText(user.getUsername());
        if (category.equals("most_points")) {
            // gets users total points
            viewHolder.getUserScore().setText(user.getStats().get("total_points") + " points");
        } else if (category.equals("most_codes")) {
            // gets users number of codes scanned
            viewHolder.getUserScore().setText(user.getStats().get("num_codes") + " codes");
        } else if (category.equals("best_code")) {
            // gets users best single code score
            viewHolder.getUserScore().setText(user.getStats().get("best_code") + " points");
        } else if (category.equals("region_best")) {
            // gets users highest scoring code from region
            viewHolder.getUserScore().setText(user.getStats().get("region_best") + " points");
        }
        // index of user in sorted list plus 1 = rank
        String rankStr = Integer.toString(data.indexOf(user)+1);
        viewHolder.getUserRank().setText("#" + rankStr);
    }

    /**
     * Returns the size of your dataset (invoked by the layout manager)
     * @return
     */
    @Override
    public int getItemCount() {

        return data.size();
    }
}



