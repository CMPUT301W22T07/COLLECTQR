package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.collectqr.data.UserController;
import com.example.collectqr.databinding.ActivityAppBinding;
import com.example.collectqr.model.User;
import com.example.collectqr.ui.ProfileDialogFragment;
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

        setUpTopAppBar();

        /* Bottom Navigation Boilerplate from Android Studio
           Supplementary source: https://developer.android.com/guide/navigation/navigation-ui#java
         */
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_leaderboard, R.id.navigation_map, R.id.navigation_history).build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_container_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomnavContainer, navController);
    }

    /*
    YouTube Video
    Author: Philipp Lackner
    https://youtu.be/CRmfdVYWOhc
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }


    /**
     * Check if a user already exists on the device. If not, start Login activity
     */
    public void doesUserExist() {
        //initially write false to users admin status to prevent any bugs
        Preferences.saveAdminStatus(context, false);
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
                                    //check if the user has admin permissions
                                    checkIfAdmin(device_id);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Check if a user should have admin permissions by checking the database, if the user should or
     * shouldn't have admin permissions, write this data to shared preferences for future use
     */
    public void checkIfAdmin(String device_id) {
        db = FirebaseFirestore.getInstance();
        //search firebase to see if username is already in db
        db.collection("Admins")
                .whereEqualTo("device_id", device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            //user doesn't exist within admin document, so they are not an admin
                            //write this to shared preferences
                            Preferences.saveAdminStatus(context, false);
                        } else {
                            //user is an admin, write this to shared preferences
                            Preferences.saveAdminStatus(context, true);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
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

    private void setUpTopAppBar() {
        /*
        https://developer.android.com/training/appbar/setting-up#add-toolbar
        https://stackoverflow.com/a/42837106
        StackOverflow, Author tahsinRupam
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        /*
        https://material.io/components/app-bars-top/android#regular-top-app-bar
        StackOverflow, Author: reVerse
        https://stackoverflow.com/a/27490705
         */
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.user_search:
                        System.out.println("clicked user search");
                        return true;
                    case R.id.user_profile:
                        new ProfileDialogFragment().show(getSupportFragmentManager(), "DISPLAY_PROFILE");
                        return true;
                    case R.id.sort_history:
                        System.out.println("clicked sort history");
                        return true;
                }
                return false;
            }
        });
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