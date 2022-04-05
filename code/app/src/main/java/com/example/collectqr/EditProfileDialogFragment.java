package com.example.collectqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.collectqr.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


// Modified from Lab 3 Instructions - Fragments.pdf

/**
 * The class Edit profile dialog fragment extends dialog fragment
 */
public class EditProfileDialogFragment extends DialogFragment {
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
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.edit_profile_dialog_fragment, null);

        EditText emailField = rootView.findViewById(R.id.edit_email);
        emailField.setText(getArguments().getString("email"));
        EditText phoneField = rootView.findViewById(R.id.edit_phone);
        phoneField.setText(getArguments().getString("phone"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        AlertDialog dialog = builder
                .setView(rootView)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        /*
        StackOverflow, Author: Mohamed AbdelraZek
        https://stackoverflow.com/a/67540989
         */
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.white_rounded_rectangle));

        /*
        StackOverflow, Author: Tom Bollwitt
        https://stackoverflow.com/a/7636468
         */
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String email = emailField.getText().toString();
                        String phone = phoneField.getText().toString();
                        if (validate(phone, email, rootView)) {
                            save(phone, email);
                            dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    boolean validate(String phone, String email, View rootview) {
        boolean valid = true;
        String message = null;

        if (!email.contains("@")) {
            // https://developer.android.com/guide/topics/ui/notifiers/toasts
            message = "Invalid Email";
            valid = false;
        }
        if (phone.isEmpty()) {
            // TODO: add more phone number checks
            // https://developer.android.com/guide/topics/ui/notifiers/toasts
            if (!valid) {
                message = "Invalid Email and Phone Number";
            } else {
                message = "Invalid Phone Number";
                valid = false;
            }
        }
        if (!valid) {
            Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
            toast.show();
        }
        return valid;
    }


    /**
     *
     * Gets the toast position Y
     *
     * @param rootView  the root view
     * @return the toast position Y
     */
    private int getToastPositionY(View rootView) {

        /*
        StackOverflow, Author:Alexander Knauf
        https://stackoverflow.com/a/64440541
         */
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(rootView.getRootView());
        if (insets.isVisible(WindowInsetsCompat.Type.ime())) {
            return insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
        } else {
            return 0;
        }
    }

    void save(String phone, String email) {
        String username = Preferences.loadUserName(getContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(username);
        documentReference.update("email", email);
        documentReference.update("phone", phone);
    }

}
