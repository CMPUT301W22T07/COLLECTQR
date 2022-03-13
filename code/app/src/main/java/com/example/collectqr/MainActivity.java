package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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