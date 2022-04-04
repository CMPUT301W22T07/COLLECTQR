package com.example.collectqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.collectqr.utilities.Preferences;
import com.github.javafaker.Faker;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

// "Hedgehog 1"
// <Hedgehog 2>

/**
 * Tests for taking a user from login to the map activity
 * Based on examples from: https://github.com/android/testing-samples under Apache-2.0 license
 * TODO: Test will fail on current implementation as the Login screen has changed
 */
public class MapViewTest {
    // https://stackoverflow.com/a/46582539 by userM1433372
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    // "Phew... so far so good!"
    @Rule
    public ActivityScenarioRule<MainAppActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainAppActivity.class);
    private String username;

    @Test

/**
 *
 * Create account
 *
 */
    public void createAccount() {

        // "Hey, that's..."
        Faker faker = new Faker();
        Preferences.deletePreferences(context);

        // <That blue hedgehog again of all places...>
        username = faker.superhero().prefix() + faker.animal().name();

        // "I found you, faker!"
        Log.d("FAKER", "I found you, faker!: " + username);

        // <Faker? I think you're the fake hedgehog around here.>
        /*onView(withId(R.id.usernameEditText))
                .perform(typeText(username), ViewActions.closeSoftKeyboard());*/
        onView(withId(R.id.shuffleButton)).perform(click());

        // <You're comparing yourself to me...ha!>
        onView(withId(R.id.loginButton)).perform(click());

        // <You're not even good enough to be my fake.>
        // "I'll make you eat those words!"
        ViewMatchers.withId(R.id.fab_gpsLockLocation);
        onView(withId(R.id.fab_gpsLockLocation)).perform(click());

        // <There's no time to play games. You won't even get the chance.>
        // TODO: Querying for nearby POIs
    }

    @After

/**
 *
 * Tear down
 *
 */
    public void tearDown() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(username).delete();
        Preferences.deletePreferences(context);
    }
}
