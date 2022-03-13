package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch main application activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //get reference to database
        db = FirebaseFirestore.getInstance();

        //load username from SharedPreferences

        //Preferences.savePreferences(this, "localusername");
        Preferences.deletePreferences(this);
        String username = Preferences.loadPreferences(this);

        //if username is null, this is the user should be prompted to create a username
        if(username == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //if not null, the user can just be redirected to the main activity
        //TODO: Intent to map activity
        //Intent intent = new Intent(this, //TODO.class);
        //startActivity(intent);

    }

}