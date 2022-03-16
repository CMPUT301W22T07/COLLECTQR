package com.example.collectqr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collectqr.R;
import com.example.collectqr.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A custom RecyclerView.Adapter for the leaderboard screen
 */
public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.ViewHolder> {

    private ArrayList<User> data;
    private ViewGroup viewGroup;
    private String category;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userScore;
        private TextView userRank;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            userName = view.findViewById(R.id.username_text);
            userScore = view.findViewById(R.id.score_text);
            userRank = view.findViewById(R.id.rank_text);
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getUserScore() {
            return userScore;
        }

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
        return new ViewHolder(view);
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

        // set TextView for each list element
        TextView userName = viewGroup.findViewById(R.id.username_text);
        TextView userScore = viewGroup.findViewById(R.id.score_text);
        TextView userRank = viewGroup.findViewById(R.id.rank_text);

        viewHolder.getUserName().setText(user.getUsername());
        if (category.equals("most_points")) {
            viewHolder.getUserScore().setText(user.getStats().get("total_points") + " points");
        } else if (category.equals("most_codes")) {
            viewHolder.getUserScore().setText(user.getStats().get("num_codes") + " codes");
        } else if (category.equals("best_code")) {
            viewHolder.getUserScore().setText(user.getStats().get("best_code") + " points");
        }
        // index of user in sorted list plus 1 = rank
        viewHolder.getUserRank().setText(Integer.toString(data.indexOf(user)+1));
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



