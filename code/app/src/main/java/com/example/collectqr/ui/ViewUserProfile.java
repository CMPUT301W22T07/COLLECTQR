package com.example.collectqr.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectqr.EditProfileDialogFragment;
import com.example.collectqr.GenerateQRCodeActivity;
import com.example.collectqr.R;
import com.example.collectqr.adapters.HistoryAdapter;
import com.example.collectqr.data.HistoryController;
import com.example.collectqr.utilities.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewUserProfile extends Fragment {
    private HistoryController controller;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String username;
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
        Button deleteButton = view.findViewById(R.id.user_profile_delete);

        //get the username passed in to the fragment via a bundle
        username = getArguments().getString("username");

        //decide whether or not to display the delete button, based on if the user is
        //an admin or not
        boolean admin = Preferences.loadAdminStatus(getContext());
        //hide the button if the user is not an admin, or if the admin is viewing themselves
        if(!admin || username.equals(Preferences.loadUserName(getContext()))) {
            deleteButton.setClickable(false);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        //set up the controller for the recycler view, which pulls data
        //from firebase and uses it to fill the screen
        controller = new HistoryController(username);
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
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    total_points.setText(String.valueOf(snapshot.get("total_points"))+"\nTotal Points");
                    best_code.setText(String.valueOf(snapshot.get("best_code"))+"\nBest Code");
                    num_codes.setText(String.valueOf(snapshot.get("num_codes"))+"\nCodes Scanned");
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            //before deleting the user, create a popup dialog to confirm they want
            //to delete them
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(getContext(),
                        com.google.android.material.R.style.ThemeOverlay_Material3_Dialog)
                        // https://stackoverflow.com/a/19064968 by Singhak
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //confirmation given, so delete the user from the database
                                db.collection("Users").document(username)
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                                NavController navController =  Navigation.findNavController(getView());
                                navController.popBackStack();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        //Return and Inflate the layout for this fragment
        return view;
    }

}
