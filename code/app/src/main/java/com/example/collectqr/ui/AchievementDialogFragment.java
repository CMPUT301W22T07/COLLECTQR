package com.example.collectqr.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.collectqr.R;
import com.example.collectqr.adapters.AchievementsAdapter;
import com.example.collectqr.model.AchievementItem;
import com.example.collectqr.utilities.Preferences;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;


// Modified from Lab 3 Instructions - Fragments.pdf

/**
 * The class Edit profile dialog fragment extends dialog fragment
 */
public class AchievementDialogFragment extends DialogFragment {
    AchievementsAdapter adapter;
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
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_achievements, null);

        String username = Preferences.loadUserName(this.getContext());
        ListView achievementsList = rootView.findViewById(R.id.user_achievements_list);
        ArrayList achievementDataList = new ArrayList<>();


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        AlertDialog dialog = builder
                .setView(rootView)
                .setTitle("Achievements")
                .setPositiveButton("Done", null)
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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Users").document(username);
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        //fetch all achievement data from firestore
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            //for each achievement, add it to the list of achievements the user has, to be displayed
                            Map<String, Object> data = snapshot.getData();
                            if((boolean) data.get("scan_1_code")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "Scanned your first code!"));
                            }
                            if((boolean) data.get("scan_10_codes")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "Scanned 10 codes!"));
                            }
                            if((boolean) data.get("scan_50_codes")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "You scanned 50 codes, wow!"));
                            }
                            if((boolean) data.get("scan_10_points")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "You scanned a code only worth 10 points, tough luck."));
                            }
                            if((boolean) data.get("scan_100_points")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "You scanned a code worth 100 points! Lucky!"));
                            }
                            if((boolean) data.get("scan_300_points")) {
                                achievementDataList.add(new AchievementItem(R.drawable.achievement_icon, "A code worth 300 points? That must be a world record..."));
                            }

                            //finally, set the array adapter for the list to create the final product
                            adapter = new AchievementsAdapter(getContext(), achievementDataList);
                            achievementsList.setAdapter(adapter);

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
