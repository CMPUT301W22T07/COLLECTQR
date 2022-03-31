package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.collectqr.databinding.ActivityAppBinding;
import com.example.collectqr.utilities.Preferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

/**
 * The main page of the app, through the use of various fragments,
 * it displays pieces of the UI, such as the map, and bottom bar
 */
public class MainAppActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityAppBinding binding;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* Start with a splashscreen
           https://developer.android.com/reference/androidx/core/splashscreen/SplashScreen
         */
        SplashScreen.installSplashScreen(this);
        noMonkeys();
        doesUserExist();

        super.onCreate(savedInstanceState);
        //setupWindowAttributes();

        // Setup view binding in this activity; see dev docs
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* Bottom Navigation Boilerplate from Android Studio
           Supplementary source: https://developer.android.com/guide/navigation/navigation-ui#java
         */
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_leaderboard, R.id.navigation_map, R.id.navigation_history).build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_container_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomnavContainer, navController);

    }


    /**
     * Check if a user already exists on the device. If not, start Login activity
     */
    public void doesUserExist() {
        //check if the current device id exists within the db
        @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .whereArrayContainsAny("devices", Arrays.asList(device_id))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                //device doesn't exist within the db, so go to login activity
                                Intent intent = new Intent(context, LoginActivity.class);
                                /*
                                Launch the login activity as the base of the stack
                                https://stackoverflow.com/a/16388608 by Cynichniy Bandera
                                */
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //device does exist within db, save username to shared preferences
                                    //for future use in other parts of the application
                                    Preferences.saveUserName(context, document.getId());
                                    System.out.println(Preferences.loadUserName(context));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    /**
     * Keep the monkeys out
     */
    public void noMonkeys() {
        if (ActivityManager.isUserAMonkey()) {
            new MaterialAlertDialogBuilder(this,
                    com.google.android.material.R.style.ThemeOverlay_Material3_Dialog)
                    // https://stackoverflow.com/a/19064968 by Singhak
                    .setCancelable(false)
                    .setMessage("Cease your monkeying")
                    .setPositiveButton("Sorry, I'll leave", (dialogInterface, i) -> {
                        // https://stackoverflow.com/a/27765687 by sivi
                        finishAndRemoveTask();
                    })
                    .show();
        }
    }


    /**
     * Setup window attributes and decorations at startup
     */
    public void setupWindowAttributes() {
        /* Swapping the eye-searing container colour
           https://trendyprogrammer.blogspot.com/2020/01/how-to-show-content-behind-status-bar.html
         */
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}