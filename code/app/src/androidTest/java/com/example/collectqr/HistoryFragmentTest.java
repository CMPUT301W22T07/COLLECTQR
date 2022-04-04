package com.example.collectqr;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static java.util.EnumSet.allOf;

import android.util.ArrayMap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest


/**
 * The class History fragment test
 */
public class HistoryFragmentTest {
    @Rule
    public ActivityScenarioRule<MainAppActivity> activityRule =
            new ActivityScenarioRule<>(MainAppActivity.class);

    @Before

/**
 *
 * Setup
 *
 */
    public void setup() {

        onView(withId(R.id.navigation_history)).perform(click());
    }

    @Test

/**
 *
 * Check fragment
 *
 */
    public void checkFragment() {

        onView(withText(endsWith("Codes"))).check(matches(isDisplayed()));
        onView(withText(endsWith("Total Points"))).check(matches(isDisplayed()));
    }

    @Test

/**
 *
 * Check fab
 *
 */
    public void checkFab() {

        onView(withId(R.id.sort_history)).perform(click());
        onView(withText("Sort by:")).check(matches(isDisplayed()));
    }
}
