package com.example.collectqr;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.collectqr.utilities.Preferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {
    private Solo solo;
    FirebaseFirestore db;

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(MainAppActivity.class, true, true);

    @Before
    public void setup() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Preferences.deletePreferences(rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkLoginSuccess() {
        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        //solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "111testinguser111");
        solo.clickOnButton("Login");
        solo.sleep(10000); //wait 10 seconds for firebase and sharedpreferences to update
        String addedName = Preferences.loadUserName(rule.getActivity());
        assertEquals("111testinguser111", addedName);
    }

    @Test
    public void checkLoginFail() {
        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        //solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "GeneralEd");
        solo.clickOnButton("Login");
        solo.sleep(10000); //wait 10 seconds for firebase and sharedpreferences to update
        String addedName = Preferences.loadUserName(rule.getActivity());
        //user already in db, so it shouldn't be added to shared preferences
        assertNotEquals("GeneralEd", addedName);
    }

    @Test
    public void checkShuffleButton() {
        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        //solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Give me a name!");
        TextView textBox = (TextView) solo.getView(R.id.usernameEditText);
        assertFalse("strings not equal", textBox.getText().toString().equals(""));
    }

    @After
    public void tearDown() throws Exception {
        Preferences.deletePreferences(rule.getActivity());
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document("111testinguser111")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        solo.finishOpenedActivities();
    }
}
