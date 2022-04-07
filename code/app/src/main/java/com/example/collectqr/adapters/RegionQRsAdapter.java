package com.example.collectqr.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.example.collectqr.model.QRCode;

import java.util.ArrayList;

// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter
/**
 * A custom adapter, specifically for displaying the views in the RecyclerView
 * which are displayed in the leaderboard screen
 * extends RecyclerView.Adapter
 */
public class RegionQRsAdapter extends RecyclerView.Adapter<RegionQRsAdapter.ViewHolder> {

    private ArrayList<QRCode> data;
    private ViewGroup viewGroup;
    private String category;

    private RegionQRsAdapter.OnRecyclerItemClickListener listener;

    /*
    YouTube Video, Author: Coding in Flow
    https://youtu.be/bhhs4bwYyhc
     */
    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position, String category);
    }


    /**
     * Sets the on item click listener
     * @param listener  the listener
     */
    public void setOnItemClickListener(RegionQRsAdapter.OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView codePoints;
        private TextView codeDistance;
        private TextView codeRank;


        /**
         * View holder
         * @param view  the view
         * @param listener  the listener
         * @param category  the category
         * @return public
         */
        public ViewHolder(View view, OnRecyclerItemClickListener listener, String category) {
            super(view);
            codePoints = view.findViewById(R.id.username_text);
            codeDistance = view.findViewById(R.id.score_text);
            codeRank = view.findViewById(R.id.rank_text);

            // Define click listener for the ViewHolder's View
            view.setOnClickListener(new View.OnClickListener() {
                @Override
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
         * Gets the user name
         * @return the user name
         */
        public TextView getPoints() { return codePoints; }


        /**
         * Gets the user score
         * @return the user score
         */
        public TextView getDistance() { return codeDistance; }


        /**
         * Gets the user rank
         * @return the user rank
         */
        public TextView getRank() { return codeRank; }
    }


    /**
     * Initializes the dataset of the Adapter.
     * @param data
     * @param category
     */
    public RegionQRsAdapter(ArrayList<QRCode> data, String category) {
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
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        QRCode code = data.get(position);
        // set TextView for each list element
        TextView userName = viewGroup.findViewById(R.id.username_text);
        TextView userScore = viewGroup.findViewById(R.id.score_text);
        TextView userRank = viewGroup.findViewById(R.id.rank_text);

        viewHolder.getPoints().setText(code.getPoints().toString()+" points");
        viewHolder.getDistance().setText("lat: "+code.getLatitudeAsString()+", lon: "+code.getLongitudeAsString());
        // index of user in sorted list plus 1 = rank
        viewHolder.getRank().setText(Integer.toString(data.indexOf(code)+1));
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



