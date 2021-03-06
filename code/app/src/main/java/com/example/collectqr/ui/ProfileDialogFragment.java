package com.example.collectqr.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.collectqr.EditProfileDialogFragment;
import com.example.collectqr.GenerateQRCodeActivity;
import com.example.collectqr.R;
import com.example.collectqr.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

// Modified from Lab 3 Instructions - Fragments.pdf


/**
 * The class Profile dialog fragment extends dialog fragment
 */
public class ProfileDialogFragment extends DialogFragment {
    private String email;
    private String phone;

    // https://developer.android.com/guide/topics/ui/dialogs#DialogFragment
    @NonNull
    @Override

/**
 *
 * On create dialog
 *
 * @param Bundle  the bundle
 * @return Dialog
 */
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_dialog_fragment, null);

        TextView usernameView = rootView.findViewById(R.id.profile_username);
        TextView emailView = rootView.findViewById(R.id.profile_email);
        TextView phoneView = rootView.findViewById(R.id.profile_phone);
        TextView exportButton = rootView.findViewById(R.id.profile_export_profile);
        TextView shareButton = rootView.findViewById(R.id.profile_share_profile);
        TextView editButton = rootView.findViewById(R.id.profile_edit_profile);
        TextView achievementsButton = rootView.findViewById(R.id.profile_achievements);

        String username = Preferences.loadUserName(getContext());
        usernameView.setText(username);
        //https://firebase.google.com/docs/firestore/query-data/listen
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(username);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override


/**
 *
 * On event
 *
 * @param DocumentSnapshot  the document snapshot
 * @param @Nullable  the @ nullable
 */
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    if (!snapshot.getString("email").isEmpty()) {
                        email = snapshot.getString("email");
                        emailView.setText(email);
                    }
                    if (!snapshot.getString("phone").isEmpty()) {
                        phone = snapshot.getString("phone");
                        phoneView.setText(phone);
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                // Modified from Lab 3 Participation Exercise Hints
                Bundle args = new Bundle();
                args.putString("email", email);
                args.putString("phone", phone);
                EditProfileDialogFragment profileFragment = new EditProfileDialogFragment();
                profileFragment.setArguments(args);
                profileFragment.show(getActivity().getSupportFragmentManager(), "EDIT_PROFILE");
            }
        });

        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                // Modified from Lab 3 Participation Exercise Hints
                AchievementDialogFragment achievmentFragment = new AchievementDialogFragment();
                achievmentFragment.show(getActivity().getSupportFragmentManager(), "ACHIEVEMENTS");
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), GenerateQRCodeActivity.class);
                intent.putExtra("qrGen", 0);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override

/**
 *
 * On click
 *
 * @param view  the view
 */
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), GenerateQRCodeActivity.class);
                intent.putExtra("qrGen", 1);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Dialog dialog = builder.setView(rootView).create();
        /*
        StackOverflow, Author: Mohamed AbdelraZek
        https://stackoverflow.com/a/67540989
         */
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_rectangle, getActivity().getTheme()));
        return dialog;
    }

}
