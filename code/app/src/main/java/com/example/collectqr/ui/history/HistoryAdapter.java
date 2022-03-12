package com.example.collectqr.ui.history;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


// https://developer.android.com/guide/topics/ui/layout/recyclerview#implement-adapter

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<HistoryItem> qrHistoryData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String username;

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
        // TODO: figure out how to access data in inner class and fix this method
        /*
        final ArrayList<Integer> size = new ArrayList<>();
        size.add(3);
        // https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        size.set(0, Integer.parseInt(document.get("num_codes").toString()));
                        size.set(0, 5);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
         */
        return 7;
    }
}


