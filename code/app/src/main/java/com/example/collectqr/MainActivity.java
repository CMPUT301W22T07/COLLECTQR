package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup view binding in this activity: see dev docs
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        // Launch main application activity
        startActivity(new Intent(this, MainAppActivity.class));
    }

}