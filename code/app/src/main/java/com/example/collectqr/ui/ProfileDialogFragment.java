package com.example.collectqr.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.collectqr.DummyActivity;
import com.example.collectqr.EditProfileDialogFragment;
import com.example.collectqr.LoginActivity;
import com.example.collectqr.R;
import com.example.collectqr.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

// Modified from Lab 3 Instructions - Fragments.pdf

public class ProfileDialogFragment extends DialogFragment {
    private String email;
    private String phone;

    // https://developer.android.com/guide/topics/ui/dialogs#DialogFragment
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_dialog_fragment, null);
        //if (getDialog()!=null && getDialog().getWindow()!=null) {
         //   getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_rectangle));
        //}

        TextView usernameView = rootView.findViewById(R.id.profile_username);
        TextView emailView = rootView.findViewById(R.id.profile_email);
        TextView phoneView = rootView.findViewById(R.id.profile_phone);
        TextView exportButton = rootView.findViewById(R.id.profile_export_profile);
        TextView shareButton = rootView.findViewById(R.id.profile_share_profile);
        TextView editButton = rootView.findViewById(R.id.profile_edit_profile);

        String username = Preferences.loadUserName(getContext());
        usernameView.setText(username);
        //https://firebase.google.com/docs/firestore/query-data/listen
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

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: change dummy activity to an activity that generates qr code to export user
                Intent intent = new Intent(getContext(), DummyActivity.class);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: change dummy activity to an activity that generates qr code to share user
                Intent intent = new Intent(getContext(), DummyActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Dialog dialog = builder.setView(rootView).create();
        /*

         */
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_rectangle));
        return dialog;
    }

}
