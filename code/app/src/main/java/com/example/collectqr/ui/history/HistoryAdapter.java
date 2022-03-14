package com.example.collectqr.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter

/**
 * A custom adapter, specifically for storing and displaying items from
 * the users QR code history
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> qrHistoryData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            imageView = (ImageView) view.findViewById(R.id.history_card_image);
            textView = (TextView) view.findViewById(R.id.history_card_points);
        }

        public TextView getTextView() {return textView;}
        public ImageView getImageView() {return  imageView;}
    }

    public HistoryAdapter() {
        // TODO: add collection reference and its size as paramaters
        qrHistoryData = new ArrayList<HistoryItem>();
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param qrHistoryList ArrayList<HistoryItem> containing the data to populate views to be used
     * by RecyclerView.
     */
    public HistoryAdapter(ArrayList<HistoryItem> qrHistoryList) {
        qrHistoryData = qrHistoryList;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.history_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (position>=qrHistoryData.size()) {
            // TODO: create query to get new items from database
            qrHistoryData.add(new HistoryItem(700, R.drawable.ic_baseline_map, "example hash"));

        }
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        HistoryItem currentItem = qrHistoryData.get(position);
        viewHolder.getTextView().setText(Integer.toString(currentItem.getPoints())+" points");
        viewHolder.imageView.setImageResource(currentItem.getImageResource());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        // TODO: get the size from the user document in firestore
        return 15;
    }
}


