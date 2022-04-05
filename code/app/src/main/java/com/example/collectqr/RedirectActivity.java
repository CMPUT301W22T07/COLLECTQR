package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * The class Dummy activity extends application compat activity
 */
public class RedirectActivity extends AppCompatActivity {

    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_activity);

        Intent intent = getIntent();
        data = intent.getStringExtra("data");

        ////System.out.println(data);
    }
}
