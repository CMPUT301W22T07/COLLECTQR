package com.example.collectqr;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.collectqr.databinding.ActivityAppBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The main page of the app, through the use of various fragments,
 * it displays pieces of the UI, such as the map, and bottom bar
 */
public class MainAppActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ActivityAppBinding binding;

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
        //load username from SharedPreferences
        //Preferences.savePreferences(this, "localusername");

        // Preferences.deletePreferences(this);
        String username = Preferences.loadPreferences(this);

        //if username is null, this is the user should be prompted to create a username
        if (username == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            /*
                Launch the login activity as the base of the stack
                https://stackoverflow.com/a/16388608 by Cynichniy Bandera
             */
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    /**
     * Keep the monkeys out
     */
    public void noMonkeys() {
        if (ActivityManager.isUserAMonkey()) {
            new MaterialAlertDialogBuilder(this,
                    com.google.android.material.R.style.ThemeOverlay_Material3_Dialog)
                    // https://stackoverflow.com/a/19064968 Singhak
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