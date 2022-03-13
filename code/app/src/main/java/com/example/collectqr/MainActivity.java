package com.example.collectqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get reference to database
        db = FirebaseFirestore.getInstance();

        //load username from SharedPreferences

        Preferences.savePreferences(this, "PZ");
        String username = Preferences.loadPreferences(this);

        //if username is null, this is the user should be prompted to create a username
        if(username == null) {
            //TODO: Intent to Login
        }

        //if not null, the user can just be redirected to the main activity
        //TODO: Intent to map activity

        /*db.collection("Users")
                .whereEqualTo("username", "penguin")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()) {
                                System.out.println("empty!!!!!!");
                            } else {
                                System.out.println("notempty!!!!!!!!");
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.get("username"));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/

    };
}