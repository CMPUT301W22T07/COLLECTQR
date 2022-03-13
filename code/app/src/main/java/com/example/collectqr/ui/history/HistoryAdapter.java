package com.example.collectqr.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.collectqr.R;
import java.util.ArrayList;


// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> qrHistoryData;
    final private String username;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView pointsView;
        private TextView dateView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            imageView = view.findViewById(R.id.history_card_image);
            pointsView = view.findViewById(R.id.history_card_points);
            dateView = view.findViewById(R.id.history_card_date);
        }

        public TextView getPointsView() {return pointsView;}
        public TextView getDateView() {return dateView;}
        public ImageView getImageView() {return  imageView;}
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param username String the username of the user whose history will be retrieved
     * @param qrHistoryList ArrayList<HistoryItem> containing the data to populate views to be used
     * by RecyclerView.
     */
    public HistoryAdapter(String username, ArrayList<HistoryItem> qrHistoryList) {
        qrHistoryData = qrHistoryList;
        this.username = username;
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
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        HistoryItem currentItem = qrHistoryData.get(position);
        viewHolder.getPointsView().setText(currentItem.getPoints()+" points");
        viewHolder.getImageView().setImageResource(currentItem.getImageResource());
        String scannedOn = currentItem.getDate().toString();
        viewHolder.getDateView().setText(String.format("%s %s %s",
                scannedOn.substring(11,16),
                scannedOn.substring(4,10),
                scannedOn.substring(23)));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return qrHistoryData.size();
    }
}


