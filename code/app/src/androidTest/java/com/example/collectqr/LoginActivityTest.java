package com.example.collectqr;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;
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


/**
 * The class Login activity test
 * @NOTE: This should only be run after you have deleted your own
 * account from the database, to prevent any logging out/logging in issues
 * or, ideally, run on a separate device specifically for testing
 */
public class LoginActivityTest {
    private Solo solo;
    FirebaseFirestore db;
    private static boolean setupDone = false;

    @Rule
    public ActivityTestRule rule = new ActivityTestRule(MainAppActivity.class, true, true);

    @Before

/**
 *
 * Setup
 *
 * @param Exception  the exception
 * @throws   Exception
 */
    public void setup() throws Exception {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        Preferences.deletePreferences(rule.getActivity());
    }

    @Test

/**
 *
 * Start
 *
 * @param Exception  the exception
 * @throws   Exception
 */
    public void start() throws Exception {

        Activity activity = rule.getActivity();
    }

    @Test

/**
 *
 * Check login success
 *
 */
    public void checkLoginSuccess() {

        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "111testinguser111");
        solo.clickOnButton("Login");
        solo.sleep(10000); //wait 10 seconds for firebase and sharedpreferences to update
        String addedName = Preferences.loadUserName(rule.getActivity());
        assertEquals("111testinguser111", addedName);
    }

    @Test

/**
 *
 * Check login fail
 *
 */
    public void checkLoginFail() {

        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "GeneralEd");
        solo.clickOnButton("Login");
        solo.sleep(10000); //wait 10 seconds for firebase and sharedpreferences to update
        String addedName = Preferences.loadUserName(rule.getActivity());
        //user already in db, so it shouldn't be added to shared preferences
        assertNotEquals("GeneralEd", addedName);
    }

    @Test

/**
 *
 * Check shuffle button
 *
 */
    public void checkShuffleButton() {

        solo.assertCurrentActivity("Wrong Activity", MainAppActivity.class);
        //solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.clickOnButton("Give me a name!");
        solo.sleep(1000); //sleep to help with syncing
        TextView textBox = (TextView) solo.getView(R.id.usernameEditText);
        assertFalse("strings not equal", textBox.getText().toString().equals(""));
    }

    @After

/**
 *
 * Tear down
 *
 * @param Exception  the exception
 * @throws   Exception
 */
    public void tearDown() throws Exception {

        Preferences.deletePreferences(rule.getActivity());
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document("111testinguser111")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override

/**
 *
 * On success
 *
 * @param aVoid  the a void
 */
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override

/**
 *
 * On failure
 *
 * @param Exception  the exception
 */
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Error deleting document", e);
                    }
                });
        solo.finishOpenedActivities();
    }
}
