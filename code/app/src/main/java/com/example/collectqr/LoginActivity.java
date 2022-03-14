package com.example.collectqr;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectqr.data.UserController;
import com.example.collectqr.model.User;
import com.example.collectqr.utilities.Preferences;
import com.github.javafaker.Faker;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity handles the creation of a new user when first logging into
 * the app. All the user has to do is enter a valid username, then their profile
 * is created and added to both the firestore database and their shared preferences,
 * for future logins
 */
public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ExtendedFloatingActionButton loginButton = findViewById(R.id.loginButton);
        ExtendedFloatingActionButton shuffleButton = findViewById(R.id.shuffleButton);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(view -> {
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
                        .addOnCompleteListener(task -> {
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
                                    // https://developer.android.com/guide/components/activities/tasks-and-back-stack
                                    //finishActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Intent intent = new Intent (this, MainAppActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    //user already exists
                                    toast.cancel(); //cancel the old toast
                                    Toast toast1 = Toast.makeText(context, "User already exists!", Toast.LENGTH_SHORT);
                                    toast1.show();
                                    //re-enable the button so that the user can try again
                                    loginButton.setClickable(true);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });
            }
        });

        shuffleButton.setOnClickListener(view -> {
            Faker faker = new Faker();
            String randomName = faker.superhero().prefix()+faker.name().firstName();
            usernameEditText.setText(randomName);
        });
    }
}