package com.example.collectqr.ui.history;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.collectqr.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;


// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> qrHistoryData;
    private ViewGroup viewGroup;

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

        public TextView getPointsView() {
            return pointsView;
        }

        public TextView getDateView() {
            return dateView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public HistoryAdapter(ArrayList<HistoryItem> qrHistoryData) {
        this.qrHistoryData = qrHistoryData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        this.viewGroup = viewGroup;
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
        viewHolder.getPointsView().setText(currentItem.getPoints() + " points");
        String scannedOn = currentItem.getDate().toString();
        viewHolder.getDateView().setText(String.format("%s %s %s",
                scannedOn.substring(11, 16),
                scannedOn.substring(4, 10),
                scannedOn.substring(23)));
        // https://firebase.google.com/docs/storage/android/download-files#downloading_images_with_firebaseui
        Glide.with(viewGroup.getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/collectqr7.appspot.com/o/test.jpg?alt=media&token=ed2687e8-daaf-4bdd-b04c-c36b10141a1f")
                .into(viewHolder.getImageView());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return qrHistoryData.size();
    }
}


