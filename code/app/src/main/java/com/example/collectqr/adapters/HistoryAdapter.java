package com.example.collectqr.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.collectqr.model.QRCode;
import com.example.collectqr.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.context.AttributeContext;

import java.util.ArrayList;
import java.util.ResourceBundle;


// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter

/**
 * A custom adapter, specifically for storing and displaying the views in the RecyclerView
 * which represent the user's QR code history
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<QRCode> qrHistoryData;
    private ViewGroup viewGroup;
    private OnRecyclerItemClickListener listener;
    private View previousSelected = null;

    /*
    YouTube Video, Author: Coding in Flow
    https://youtu.be/bhhs4bwYyhc
     */
    public interface OnRecyclerItemClickListener {
        void onRecyclerItemClick(int position, View view);
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView pointsView;
        private TextView dateView;

        public ViewHolder(View view, OnRecyclerItemClickListener listener) {
            super(view);
            // Define click listener for the ViewHolder's View

            imageView = view.findViewById(R.id.history_card_image);
            pointsView = view.findViewById(R.id.history_card_points);
            dateView = view.findViewById(R.id.history_card_date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRecyclerItemClick(position, view);
                        }
                    }
                }
            });
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
    public HistoryAdapter(ArrayList<QRCode> qrHistoryData) {
        this.qrHistoryData = qrHistoryData;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        this.viewGroup = viewGroup;
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.history_list_item, viewGroup, false);
        return new ViewHolder(view, this.listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        QRCode currentItem = qrHistoryData.get(position);
        viewHolder.getPointsView().setText(currentItem.getPoints() + " points");
        String scannedOn = currentItem.getDate().toString();
        viewHolder.getDateView().setText(String.format("%s %s %s",
                scannedOn.substring(11, 16),
                scannedOn.substring(4, 10),
                scannedOn.substring(23)));
        // https://firebase.google.com/docs/storage/android/download-files#downloading_images_with_firebaseui
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://collectqr7.appspot.com");
        StorageReference storageReference = storage.getReferenceFromUrl("gs://collectqr7.appspot.com/"+currentItem.getQr_image());
        Glide.with(viewGroup.getContext())
                .load(storageReference)
                .placeholder(R.drawable.ic_baseline_image_not_supported_24)
                .into(viewHolder.getImageView());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return qrHistoryData.size();
    }
}


