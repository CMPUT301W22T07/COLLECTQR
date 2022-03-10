package com.example.collectqr;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import android.os.Handler;
import android.view.WindowManager;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, QRCodeHomeActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);


    }
}