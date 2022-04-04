package com.example.collectqr;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * The class Dummy activity extends application compat activity
 */
public class DummyActivity extends AppCompatActivity {

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
