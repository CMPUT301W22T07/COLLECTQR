package com.example.collectqr.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.EditProfileDialogFragment;
import com.example.collectqr.GenerateQRCodeActivity;
import com.example.collectqr.R;
import com.example.collectqr.adapters.HistoryAdapter;
import com.example.collectqr.data.HistoryController;
import com.example.collectqr.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewUserProfile extends Fragment {
    private HistoryController controller;
    private RecyclerView.LayoutManager layoutManager;
    // https://developer.android.com/guide/topics/ui/dialogs#DialogFragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the view for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_user_profile, container, false);

        //get all the necessary UI elements to be worked with
        TextView usernameText = view.findViewById(R.id.user_profile_username);
        TextView best_code = view.findViewById(R.id.user_profile_best_code);
        TextView total_points = view.findViewById(R.id.user_profile_total_points);
        TextView num_codes = view.findViewById(R.id.user_profile__num_codes);
        RecyclerView recyclerView = view.findViewById(R.id.user_profile_recycler_view);

        //get the username passed in to the fragment via a bundle
        String username = getArguments().getString("username");

        controller = new HistoryController(username);
        //controller.setStatsBarData(totalPoints, numCodes);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(controller.getAdapter());

        //set the username text field
        usernameText.setText(username);

        //https://firebase.google.com/docs/firestore/query-data/listen
        //get data to fill in TextViews from the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "AOSHASOIAHSIBUDUGDWQUDWUIWBIUDWIUDWUIDWUIBDWUBIDWUOIDWBIODWBDWBYDWBYDBWDBDBW");
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    total_points.setText(String.valueOf(snapshot.get("total_points"))+"\nTotal Points");
                    best_code.setText(String.valueOf(snapshot.get("best_code"))+"\nBest Code");
                    num_codes.setText(String.valueOf(snapshot.get("num_codes"))+"\nCodes Scanned");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        //Return and Inflate the layout for this fragment
        return view;
    }

}
