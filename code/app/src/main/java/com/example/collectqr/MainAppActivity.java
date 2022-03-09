package com.example.collectqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.collectqr.databinding.ActivityAppBinding;
import com.example.collectqr.databinding.ActivityMainBinding;

public class MainAppActivity extends AppCompatActivity {
    private ActivityAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app);

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
}