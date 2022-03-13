package com.example.collectqr;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disable the button so that the user can't spam click it while the query runs
                loginButton.setClickable(false);

                String username = usernameEditText.getText().toString();

                //make sure username isn't empty
                if (username.equals("")) {
                    Toast toast = Toast.makeText(context, "Empty Username!", Toast.LENGTH_SHORT);
                    toast.show();
                    loginButton.setClickable(true);
                } else {
                    //toast to let user know that firebase is running a query
                    Toast toast = Toast.makeText(context, "Checking Username", Toast.LENGTH_LONG);
                    toast.show();

                    //search firebase to see if username is already in db
                    db.collection("Users")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            //user doesn't exist
                                            //hide the toast
                                            toast.cancel();

                                            //create user, and store to db
                                            User user = new User(username);
                                            UserController controller = new UserController();
                                            controller.writeToFirestore(user);

                                            //write the user to shared preferences
                                            Preferences.savePreferences(context, username);

                                            //TODO: go to map activity
                                            //Intent intent = new Intent(context, MainActivity.class);
                                            //startActivity(intent);
                                        } else {
                                            //user already exists
                                            toast.cancel(); //cancel the old toast
                                            Toast toast = Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT);
                                            toast.show();
                                            //re-enable the button so that the user can try again
                                            loginButton.setClickable(true);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });
    }
}